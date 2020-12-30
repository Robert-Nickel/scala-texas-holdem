package poker.kafka

import java.util.{Properties, UUID}

import org.apache.kafka.clients.producer._

object PlayerDataProducer {

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("group.id","something")
  val producer = new KafkaProducer[String, String](props)

  // ["You","equity_preflop","22424-2adb-asdsa-21|0.5"]
  def publishPreflopEquity(playerName: String, equity: Double, roundId: UUID) = {
    val record = new ProducerRecord(playerName, "equity_preflop", roundId.toString + "|" + equity.toString)
    producer.send(record)
  }

  def publishFlopEquity(playerName: String, equity: Double, roundId: UUID) = {
    val record = new ProducerRecord(playerName, "equity_flop", roundId.toString + "|" + equity.toString)
    producer.send(record)
  }

  def publishTurnEquity(playerName: String, equity: Double, roundId: UUID) = {
    val record = new ProducerRecord(playerName, "equity_turn", roundId.toString + "|" + equity.toString)
    producer.send(record)
  }

  def publishRiverEquity(playerName: String, equity: Double, roundId: UUID) = {
    val record = new ProducerRecord(playerName, "equity_river", roundId.toString + "|" + equity.toString)
    producer.send(record)
  }

  def produceWinLoss(playerName: String, winLoss: Int, roundId: UUID) = {
    val record = new ProducerRecord(playerName, "win_loss", roundId.toString + "|" + winLoss.toString)
    producer.send(record)
  }

  // producer.close()
}