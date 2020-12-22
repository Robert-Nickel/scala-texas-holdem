package poker.spark

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import poker.names

import scala.collection.JavaConverters.{asJavaCollection, _}
import scala.math.random
object EquityConsumer {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder.appName("Spark with Kafka").config("spark.master", "local").getOrCreate()
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

    println("Start polling")
    println()

     // stream.foreachRDD(rdd => rdd.foreach(playerRecord => println("Your Equity: " + playerRecord.value)))  // Prints results

    stream.foreachRDD(rdd => {
      if (!rdd.isEmpty) {
        println(s"Total: ${rdd.count()}")
        val equityResult = rdd
          .filter( player => player.topic() == "You")
          .map( playerRecord => playerRecord.value().toDouble)
          .reduce((_+_)) / rdd.count()
        println("Your average Equity: " + equityResult)
      }
    }) // Prints result count

    streamingContext.start
    streamingContext.awaitTermination
  }
}
