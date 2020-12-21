package poker.kafka

import java.util.Properties

import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

object Producer extends App {

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", classOf[StringSerializer])
  props.put("value.serializer", classOf[StringSerializer])

  val producer = new KafkaProducer[String, String](props)

  val TOPIC = "test"
  val KEY = "message"

  for (i <- 1 to 50) {
    val record = new ProducerRecord(TOPIC, KEY, s"hello $i")
    Thread.sleep(1000)
    println("Sending ..")
    producer.send(record)
  }

  val record = new ProducerRecord(TOPIC, KEY, "the end " + new java.util.Date)
  println("Sending final message..")
  producer.send(record)

  producer.close()
}
