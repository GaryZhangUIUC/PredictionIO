package io.prediction.recommendation.social

case class Record (
  val fromId: Int,
  val toId: Int,
  val result: Int,
  /*
    extra information of a record
    that can be used to weight the record
  */
  val ceList: List[Int],
  val neList: List[Double]
) extends Serializable
