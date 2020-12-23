package poker.spark

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import poker.names

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
        calculateAverageEquity(rdd, "You")
        // This does not work yet:
        rdd.saveAsTextFile("rddTextFile")
      }
    })
    streamingContext.start
    streamingContext.awaitTermination
  }

  def calculateAverageEquity(rdd: RDD[ConsumerRecord[String, String]], name: String) = {
    rdd
      .filter(player => player.topic() == name)
      .map(playerRecord => playerRecord.value().toDouble)
      .reduce(_ + _) / rdd.count()
  }
}
