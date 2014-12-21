package io.prediction.recommendation.social

case class Interactions (
  /*
    categorical interactions
    where the category matters
    eg following
  */
  val ciList: List[Int],
  /*
    numerical interactions
    where the count matters
    eg number of retweeting
  */
  val niList: List[Double]
) extends Serializable
