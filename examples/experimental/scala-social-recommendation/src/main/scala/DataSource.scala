package io.prediction.recommendation.social

import scala.collection.immutable.HashMap

import io.prediction.controller.PDataSource
import io.prediction.controller.EmptyEvaluationInfo
import io.prediction.controller.EmptyActualResult
import io.prediction.controller.Params

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

import grizzled.slf4j.Logger

class DataSource(val dsp: DataSourceParams)
  extends PDataSource[TrainingData,
    EmptyEvaluationInfo, Query, EmptyActualResult] {

  @transient lazy val logger = Logger[this.type]

  override
  def readTraining(sc: SparkContext): TrainingData = {
    val userBasics = sc.textFile(dsp.userProfileFile).map {
      line =>
      val data = line.split("\\t")
      val id = data(0).toInt
      val birthYear = data(1).toInt
      val gender = data(2).toInt
      val numTweet = data(3).toInt
      var tags = HashMap[Int, Double]()
      data(4).split(";").foreach {
        v => 
        tags += (v.toInt -> 1.0)
      }
      val cfList = List(gender)
      val nfList = List(birthYear, numTweet)
      val lfList = List(tags)
      (id, (cfList, nfList, lfList))
    }
    val userKeywords = sc.textFile(dsp.userKeywordFile).map {
      line =>
      val data = line.split("\\t")
      val id = data(0).toInt
      var keywords = HashMap[Int, Double]()
      data(1).split(";").foreach {
        part =>
        val pair = part.split(":").map(v => v.toInt)
        keywords += (pair(0) -> pair(1))
      }
      val lfList = List(keywords)
      (id, (List(), List(), lfList))
    }
    val userProfiles = userBasics.join(userKeywords).map {
      data =>
      val id = data._1
      val cfList = data._2._1._1 ++ data._2._2._1
      val nfList = data._2._1._2 ++ data._2._2._2
      val lfList = data._2._1._3 ++ data._2._2._3
      (id, new Features(cfList, nfList, lfList))
    }

    val userActions = sc.textFile(dsp.userActionFile).map {
      line =>
      val data = line.split("\\t").map(v => v.toInt)
      val id1 = data(0)
      val id2 = data(1)
      val numAt = data(2)
      val numRetweet = data(3)
      val numComment = data(4)
      val niList = List(numAt, numRetweet, numComment)
      ((id1, id2), (null, niList))
    }
    val userSnss = sc.textFile(dsp.userSnsFile).map {
      line =>
      val data = line.split("\\t").map(v => v.toInt)
      val id1 = data(0)
      val id2 = data(1)
      val ciList = List(1)
      ((id1, id2), (ciList, null))
    }
    val userIteractions = (userActions ++ userSnss).foldByKey((null, null)) {
      (interaction1, iteraction2) =>
      var ciList = null
      if (interaction1._1 != null) {
        ciList = interaction1._1
      } else if (interaction2._1 != null) {
        ciList = interaction2._1
      }
      var niList = null
      if (interaction1._2 != null) {
        niList = interaction1._2
      } else if (interaction2._2 != null) {
        niList = interaction2._2
      }
      (ciList, niList)
    }.map {
      pair =>
      val id1 = pair._1._1
      val id2 = pair._1._2
      var ciList = pair._2._1
      if (ciList == null) {
        ciList = List(0)
      }
      var niList = pair._2._2
      if (niList == null) {
        niList = List(0, 0, 0)
      }
      (id1, (id2, new Interactions(ciList, niList)))
    }.distinct().groupByKey()

    val sourceProfiles = sc.textFile(dsp.itemFile).map {
      line =>
      val data = line.split("\\t")
      val id = data(0).toInt
      val cats = data(1).split(".").map(v => v.toInt).toList
      var keywords = HashMap[Int, Double]()
      data(2).split(";").foreach {
        v =>
        tags += (v.toInt -> 1.0)
      }
      val cfList = List() ++ cats
      val lfList = List(keywords)
      (id, new Features(cfList, List(), lfList))
    }

    val trainingRecords = ssc.textFile(dsp.recordFile).map {
      line =>
      val data = line.split("\\t")
      val id1 = data(0).toInt
      val id2 = data(1).toInt
      val accept = data(2).toInt
      val time = data(3).toLong
      val neList = List(time)
      new TrainingRecord(id1, id2, accept, List(), neList)
    }

    new TrainingData(userProfiles, userIteractions, sourceProfiles, trainingRecords)
  }
}

case class DataSourceParams(
  val userProfileFile: String,
  val itemFile: String,
  val userActionFile: String,
  val userSnsFile: String,
  val userKeywordFile: String,
  val recordFile: String
) extends Params
