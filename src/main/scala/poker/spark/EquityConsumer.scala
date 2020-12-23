package poker.spark

import java.io.{FileReader, FileWriter, PrintWriter}

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import poker.names

import scala.io.Source

object EquityConsumer {

  def main(args: Array[String]): Unit = {
    val spark =
      SparkSession.builder
        .appName("Spark with Kafka")
        .config("spark.master", "local").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    val streamingContext = new StreamingContext(spark.sparkContext, Seconds(1))
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "something"
    )

    val topics = names.toArray
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.foreachRDD(rdd => {
      if (!rdd.isEmpty) {
        println(s"Your average equity: ${calculateGlobalAverageEquity(rdd, "You")}")
        println(s"Your average win/loss: ${calculateGlobalAverageWinLoss(rdd, "You")}")
        writeEquityWinLossQuotaFile(rdd, "You")
        rdd.saveAsTextFile("rdd")
      }
    })
    streamingContext.start
    streamingContext.awaitTermination
  }

  def writeEquityWinLossQuotaFile(rdd: RDD[ConsumerRecord[String, String]], name: String) = {
    // roundId | preflop equity | flop equity | turn equity | river equity | average equity | winLoss
    // uuidabc | 100            | 4           | 32          | 20           | 39             | -20


    val bufferedSource = Source.fromFile("equityWinLossQuota.txt")
    val lines = bufferedSource.getLines.toList
    bufferedSource.close()

    val roundIdsInFile = lines.map(line => line.split("[|]")(0).trim)

    rdd
      .filter(playerRecord => !roundIdsInFile.contains(playerRecord.value().split("[|]")(0))) // id doesn't exist yet
      .foreach(playerRecord => {
        val roundId = playerRecord.value().split("[|]")(0)
        new PrintWriter(new FileWriter("equityWinLossQuota.txt", true)) {
          write(roundId + " | - | - | - | - | - | - " + "\n")
          close()
        }
      })
    rdd
      .filter(playerRecord => roundIdsInFile.contains(playerRecord.value().split("[|]")(0))) // id exists already
      .foreach(playerRecord => {
        val equityTuple = playerRecord.key() match {
          case "equity_preflop" => (playerRecord.value().split("[|]")(1), 1)
          case "equity_flop" => (playerRecord.value().split("[|]")(1), 2)
          case "equity_turn" => (playerRecord.value().split("[|]")(1), 3)
          case "equity_river" => (playerRecord.value().split("[|]")(1), 4)
        }
        // TODO: Write to file (find the right cell and replace whatever is in there)
        // Alternative: Replace the whole file, manage a 2 dimensional list in memory (read it at the beginning)
      })
  }

  def calculateGlobalAverageEquity(rdd: RDD[ConsumerRecord[String, String]], name: String) = {
    val globalEquityCount = rdd
      .filter(playerRecord => playerRecord.topic() == name)
      .filter(playerRecord => playerRecord.key().startsWith("equity"))
      .count()

    rdd
      .filter(playerRecord => playerRecord.topic() == name)
      .filter(playerRecord => playerRecord.key().startsWith("equity"))
      .map(playerRecord => playerRecord.value().split("[|]")(0).toDouble)
      .reduce(_ + _) / globalEquityCount
  }

  def calculateGlobalAverageWinLoss(rdd: RDD[ConsumerRecord[String, String]], name: String) = {
    val winLossCount = rdd
      .filter(playerRecord => playerRecord.topic() == name)
      .filter(playerRecord => playerRecord.key() == "winLoss")
      .count()

    rdd
      .filter(playerRecord => playerRecord.topic() == name)
      .filter(playerRecord => playerRecord.key() == "winLoss")
      .map(playerRecord => playerRecord.value().split("[|]")(0).toInt)
      .reduce(_ + _) / winLossCount
  }
}
