package io.prediction.recommendation.social

import scala.collection.immutable.HashMap

case class Features (
  /*
    categorcial features
    similarity is 
    the same or different
    eg gender
  */
  val cfList: List[Int],
  /*
    numerical features
    similarity is
    based on the difference
    eg age
  */
  val nfList: List[Double],
  /*
    list features
    similarity is
    based on the overlap
    eg keywords
  */
  val lfList: List[HashMap[Int, Double]]
) extends Serializable
