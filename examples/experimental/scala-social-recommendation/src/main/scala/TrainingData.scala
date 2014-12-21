package io.prediction.recommendation.social

import org.apache.spark.rdd.RDD

class TrainingData (
  val userProfiles: RDD[(Int, Features)],
  val userInteractions: RDD[((Int, (Int, Interactions)],
  val sourceProfiles: RDD[(Int, Features)],
  val trainingRecords: RDD[Record]
) extends Serializable
