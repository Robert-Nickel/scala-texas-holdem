package poker.kafka

import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

object EquityProducer {

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", classOf[StringSerializer])
  props.put("value.serializer", classOf[StringSerializer])
  val producer = new KafkaProducer[String, String](props)

  def produceEquity(playerName: String, equity: Double) = {
    val record = new ProducerRecord(playerName, "equity", equity.toString)
    producer.send(record)
  }

  // producer.close()
}