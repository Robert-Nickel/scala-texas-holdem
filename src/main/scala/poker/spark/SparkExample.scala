package poker.spark

import org.apache.spark.sql.SparkSession

object SparkExample {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("Simple Application").config("spark.master", "local").getOrCreate()
    // ...
    spark.stop()
  }

}

