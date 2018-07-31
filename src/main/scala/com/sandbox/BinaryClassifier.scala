import ml.dmlc.xgboost4j.scala.{ XGBoost, DMatrix, Booster }

import breeze.linalg.{ DenseVector, DenseMatrix }

object ArrayConversions {
  implicit class Array1Bonus(arr: Array[Float]) {
    val vector = DenseVector(arr)

    def toDenseVector = vector

    def describe(str: String*) = {
      val message = str.mkString(" / ")
      println(s"$message: ${vector.length}")
    }
  }

  implicit class Array2Bonus(arr: Array[Array[Float]]) {
    val matrix = DenseMatrix(arr)

    def toDenseMatrix = matrix

    def describe(str: String*) = {
      val message = str.mkString(" / ")
      println(s"$message: ${matrix.rows} x ${matrix.cols}")
    }
  }

  implicit class DMatrixBonus(data: DMatrix) {
    def describe(str: String*) = {
      val message = str.mkString(" / ")
      data.getLabel.describe(str :+ "label": _*)
      data.getWeight.describe(str :+ "weight": _*)
      data.getBaseMargin.describe(str :+ "baseMargin": _*)
      println(s"$message / rowNum : ${data.rowNum}")
    }
  }
}

object BinaryClassifier {
  import ArrayConversions._

  val workingDir = "src/main/scala/resources"

  def exo1(): Unit = {
    val dtrain: DMatrix = new DMatrix(s"$workingDir/agaricus.txt.train")
    dtrain.describe("Binary Classifier", "dtrain")

    val params: Map[String, Any] = Map(
      "eta" -> 0.1,
      "max_depth" -> 2,
      "objective" -> "binary:logistic"
    )

    val round: Int = 2

    val model: Booster = XGBoost.train(dtrain, params, round)

    val predictions = model.predict(dtrain)
    predictions.describe("Binary Classifier", "predictions")

    model.saveModel(s"$workingDir/agaricus.model")
  }

  def exo2() = {
    val dtrain: DMatrix = new DMatrix(s"$workingDir/agaricus.txt.train")
    val dtest: DMatrix = new DMatrix(s"$workingDir/agaricus.txt.test")

    dtrain.describe("DART Booster", "dtrain")
    dtest.describe("DART Booster", "dtest")

    val params: Map[String, Any] = Map(
      "booster" -> "dart",
      "max_depth" -> 5,
      "learning_rate" -> 0.1,
      "objective" -> "binary:logistic",
      "silent" -> true,
      "sample_type" -> "uniform",
      "normalize_type" -> "tree",
      "rate_drop" -> 0.1,
      "skip_drop" -> 0.5
    )

    val round: Int = 500

    val model: Booster = XGBoost.train(dtrain, params, round)

    val predictions: Array[Array[Float]] = model.predict(dtest, false, round)
    predictions.describe("DART Booster", "predictions")
  }

  def main(args: Array[String]): Unit = {
    exo1()

    exo2()
  }
}
