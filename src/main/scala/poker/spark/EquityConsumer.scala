package poker.spark

import java.io.{FileReader, FileWriter, PrintWriter}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import poker.names

import scala.io.Source

object EquityConsumer {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.appName("Spark with Kafka").config("spark.master", "local[*]").getOrCreate()
    val streamingContext = new StreamingContext(spark.sparkContext, Seconds(1))
    spark.sparkContext.setLogLevel("ERROR")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "something"
    )

    val topics = names.toArray
    val kafkaRawStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams))

    // Assumption value arrives like this: [ROUNDID | double.toString]
    val kafkaStream: DStream[(String, String, String, String, String)] = kafkaRawStream
      .map( consumerRecord => {
        val values =  consumerRecord.value().split("[|]")
        consumerRecord.key() match {
          case "win_loss" =>  (consumerRecord.topic(), consumerRecord.key(), values(0), 0.toString,values(1))
          case _ => (consumerRecord.topic(), consumerRecord.key(), values(0), values(1), 0.toString)
        }
      })


    import spark.implicits._
    import org.apache.spark.sql.functions.{when, lower}

    var dfSchema = Array("topic", "key", "roundId", "equity", "winnings")
    var resultDfSchema = Array("topic", "key", "winnings", "expectedPreflop")
    var dataFrame = Seq.empty[(String,String,String,Double,Double)].toDF(dfSchema:_*)
    var resultDataFrame = Seq.empty[(String, String, Long, Long)].toDF(resultDfSchema:_*)

    kafkaStream.foreachRDD( rdd => {
      if(!rdd.isEmpty()) {
        // adding the new data to the dataFrame
        val newRow = rdd.toDF(dfSchema:_*)
        // only add the row if it has not previously been added (no duplicates...)
        dataFrame = newRow.union(newRow)
        dataFrame = dataFrame.dropDuplicates()

        val winlossRDDs = rdd.filter( data => data._2 == "win_loss")
        if(!winlossRDDs.isEmpty()) {

          // calculate preflop equity and actual winnings for each player
          // FIXME: I think we receive a win_loss for every player? then just do everything for the player instead of iterating of every name.

          names.foreach( name => {
            val handCount = rdd
              .filter(playerRecord => playerRecord._1 == name)
              .filter(playerRecord => playerRecord._2.equals("equity_preflop"))
              .count()

            val winningsPerHand = rdd
              .filter(playerRecord => playerRecord._1 == name)
              .filter(playerRecord => playerRecord._2 == "win_loss")
              .map(playerRecord => playerRecord._5.toInt)
              .reduce(_ + _) / handCount

            val allWinnings = rdd
              .filter(playerRecord => playerRecord._2 == "win_loss")
              .filter(playerRecord => playerRecord._5.toInt > 0)
              .map(playerRecord => playerRecord._5.toInt)
              .reduce(_+_)

            val equitiesPreflop = rdd
              .filter(playerRecord => playerRecord._1 == name)
              .filter(playerRecord => playerRecord._2 == "equity_preflop")
              .map(playerRecord => playerRecord._4.toDouble)
              .reduce(_ + _)

            val expectedWinningsWithPreflopEquity = ((equitiesPreflop / 100) / handCount) * allWinnings
            val resultRow = Seq((
              name,
              "result-preflop",
              winningsPerHand,
              BigDecimal(expectedWinningsWithPreflopEquity).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble)
            ).toDF(resultDfSchema:_*)

            resultDataFrame = resultDataFrame.union(resultRow)
          })
          resultDataFrame.show()
        }
        dataFrame.show()
      }
    })

    streamingContext.start
    streamingContext.awaitTermination
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
