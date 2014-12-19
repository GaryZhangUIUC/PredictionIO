package io.prediction.recommendation.social

import scala.collection.immutable.HashMap

import org.apache.spark.rdd.RDD

class TrainingData (
  val userProfiles: RDD[(Int, Features)],
  val userInteractions: RDD[((Int, (Int, Interactions)],
  val sourceProfiles: RDD[(Int, Features)],
  val trainingRecords: RDD[TrainingRecord]
) extends Serializable

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

case class TrainingRecord (
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
