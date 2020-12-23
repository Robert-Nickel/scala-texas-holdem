package poker.kafka

import java.util.{Properties, UUID}

import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer

object PlayerDataProducer {

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", classOf[StringSerializer])
  props.put("value.serializer", classOf[StringSerializer])
  val producer = new KafkaProducer[String, String](props)

  def publishPreflopEquity(playerName: String, equity: Double, roundId: UUID) = {
    val record = new ProducerRecord(playerName, "equity_preflop", roundId + "|" + equity.toString)
    producer.send(record)
  }

  def publishFlopEquity(playerName: String, equity: Double, roundId: UUID) = {
    val record = new ProducerRecord(playerName, "equity_flop", roundId + "|" + equity.toString)
    producer.send(record)
  }

  def publishTurnEquity(playerName: String, equity: Double, roundId: UUID) = {
    val record = new ProducerRecord(playerName, "equity_turn", roundId + "|" + equity.toString)
    producer.send(record)
  }

  def publishRiverEquity(playerName: String, equity: Double, roundId: UUID) = {
    val record = new ProducerRecord(playerName, "equity_river", roundId + "|" + equity.toString)
    producer.send(record)
  }

  def produceWinLoss(playerName: String, winLoss: Int, roundId: UUID) = {
    val record = new ProducerRecord(playerName, "winLoss", roundId + "|" + winLoss.toString)
    producer.send(record)
  }

  // producer.close()
}