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

import scala.collection.JavaConverters.{asJavaCollection, _}
import scala.math.random

object SparkExample {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("Simple Application")
      .config("spark.master", "local").getOrCreate()

    val logData = spark.read.textFile("./README.md").cache()
    val numAs = logData.filter(line => line.contains("a")).count()

    println(s"Lines with a: $numAs")
    spark.stop()
  }

}

object SparkPi {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("SparkPi")
      .config("spark.master", "local").getOrCreate()

    val slices = 200 // Int.MaxValue / 100000
    val count = spark.sparkContext.parallelize(1 to 100000 * slices, slices).map { i =>
      val x = random * 2 - 1 // random number -1 until 1
      val y = random * 2 - 1
      if (x * x + y * y <= 1) 1 else 0
    }.reduce(_ + _)
    println(s"Pi is roughly ${4.0 * count / (100000 * slices)}")
    // Pi is roughly 3.13726 (2 slices)
    // Pi is roughly 3.141094 (20 slices)
    // Pi is roughly 3.1418888 (200 slices)
    // Pi is roughly 3.1415790053087456 (int.MaxValue / 100_000) in 193.291849seconds
    spark.stop()
  }
}

object SparkStream {
  def main(args: Array[String]): Unit = {
    val TOPIC = "test"
    val spark = SparkSession.builder.appName("Spark with Kafka").config("spark.master", "local").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    val streamingContext = new StreamingContext(spark.sparkContext, Seconds(1))
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "something"
    )

    val topics = Array(TOPIC)
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    println(s"Subscribed to topic: '$TOPIC'")
    println("Start polling")
    println()
    println()

     stream.foreachRDD(rdd => rdd.foreach(cr => println(cr.value)))  // Prints results
    /*stream.foreachRDD(rdd => {
      if (!rdd.isEmpty) {
        println(s"Total: ${rdd.count()}")
        rdd.map(cr => cr.value)
          .groupBy(value => value.split("\\|").last) // a|b|c -> c     c => List(1,2,3)
          .foreach(x => println(s"City: ${x._1} => ${x._2.count(_ => true)}"))
      }
    })*/ // Prints result count

    streamingContext.start
    streamingContext.awaitTermination
  }
}
