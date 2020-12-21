package poker.spark

import org.apache.spark.sql.SparkSession

import scala.math.random
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import collection.JavaConverters.asJavaCollection
import scala.collection.JavaConverters._
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer

object SparkExample {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("Simple Application")
        .config("spark.master", "local").getOrCreate()

    val logData = spark.read.textFile("./README.md").cache()
    val numAs = logData.filter(line => line.contains("a")).count()

    println(s"Lines with a: $numAs")
    // ...
    spark.stop()
  }

}

object SparkPi {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("SparkPi")
      .config("spark.master", "local").getOrCreate()

    val slices = Int.MaxValue / 100000
    val count = spark.sparkContext.parallelize(1 to 100000 * slices , slices).map { i =>
      val x = random * 2 - 1 // random number -1 until 1
      val y = random * 2 - 1
      if (x * x + y * y <= 1) 1 else 0
    }.reduce(_+_)
    println(s"Pi is roughly ${4.0 * count / (100000 * slices)}")

    // ...
    spark.stop()
  }

}
// Pi is roughly 3.13726 (2 slices)
// Pi is roughly 3.141094 (20 slices)
// Pi is roughly 3.1418888 (200 slices)
// Pi is roughly 3.1415790053087456 (int.MaxValue / 100_000) in 193.291849seconds

//   val TOPIC = "test"
//  val KEY = "message"
object SparkStream {
  def main(args:Array[String]):Unit = {
    val spark = SparkSession.builder.appName("Spark Streaming").config("spark.master", "local").getOrCreate()

    val scalaKafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val kafkaParams=scalaKafkaParams.asJava
    val sc = new StreamingContext(spark.sparkContext, Seconds(1))

    val topics = asJavaCollection(Array("test", "topicB"))
    val offset = Map[TopicPartition, java.lang.Long]((new TopicPartition("partition",1),1L)).asJava

    val stream = KafkaUtils.createDirectStream[String, String](sc, PreferConsistent, Subscribe[String, String](topics, kafkaParams, offset))

    stream.foreachRDD(rdd => println( rdd.toString()))

    // spark.stop()
  }
}
