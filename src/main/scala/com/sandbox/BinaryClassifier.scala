import ml.dmlc.xgboost4j.scala.{ XGBoost, DMatrix, Booster }

import breeze.linalg.{ DenseVector, DenseMatrix }

object ArrayConversions {
  private def oneLine(str: String*) = str.mkString(" / ")

  implicit class Array1Bonus(arr: Array[Float]) {
    val vector = DenseVector(arr)

    def toDenseVector = vector

    def describe(str: String*) = {
      val headers = oneLine(str: _*)
      println(s"$headers: ${vector.length}")
    }
  }

  implicit class Array2Bonus(arr: Array[Array[Float]]) {
    val matrix = DenseMatrix(arr)

    def toDenseMatrix = matrix

    def describe(str: String*) = {
      val headers = oneLine(str: _*)
      println(s"$headers: ${matrix.rows} x ${matrix.cols}")
    }
  }

  implicit class DMatrixBonus(data: DMatrix) {
    def describe(str: String*) = {
      val headers = oneLine(str: _*)
      data.getLabel.describe(str :+ "label": _*)
      data.getWeight.describe(str :+ "weight": _*)
      data.getBaseMargin.describe(str :+ "baseMargin": _*)
      println(s"$headers / rowNum: ${data.rowNum}")
    }
  }

  implicit class MapBonus(mymap: Map[String, Any]) {
    def describe(str: String*) = {
      val headers = oneLine(str: _*)
      println(s"$headers: $mymap")
    }
  }
}

object BinaryClassifier {
  import ArrayConversions._

  val workingDir = "src/main/scala/resources"

  val dtrain: DMatrix = new DMatrix(s"$workingDir/agaricus.txt.train")
  val dtest: DMatrix = new DMatrix(s"$workingDir/agaricus.txt.test")

  def exo1(): Unit = {
    dtrain.describe("Basic Model", "dtrain")

    val params: Map[String, Any] = Map(
      "eta" -> 0.1,
      "max_depth" -> 2,
      "objective" -> "binary:logistic",
      "nthread" -> 2
    )

    val round: Int = 2

    val model: Booster = XGBoost.train(dtrain, params, round)

    val predictions = model.predict(dtrain)
    predictions.describe("Basic Model", "predictions")

    model.saveModel(s"$workingDir/agaricus.model")
  }

  def exo2() = {
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
      "skip_drop" -> 0.5,
      "nthread" -> 2
    )

    val round: Int = 500

    val model: Booster = XGBoost.train(dtrain, params, round)

    val predictions: Array[Array[Float]] = model.predict(dtest, false, round)
    predictions.describe("DART Booster", "predictions")
  }

  def exo3() = {
    dtrain.describe("Monotonic", "dtrain")
    dtest.describe("Monotonic", "dtest")

    val params1: Map[String, Any] = Map(
      "booster" -> "dart",
      "max_depth" -> 5,
      "learning_rate" -> 0.1,
      "objective" -> "binary:logistic",
      "silent" -> true,
      "sample_type" -> "uniform",
      "normalize_type" -> "tree",
      "rate_drop" -> 0.1,
      "skip_drop" -> 0.5,
      "nthread" -> 2,
      "tree_method" -> "exact",
      "max_bin" -> 10
    )

    params1.describe("Monotonic", "params")
    val round: Int = 1000
    val model1: Booster = XGBoost.train(dtrain, params1, round, earlyStoppingRound = 10)
    // evals = evallist

    val predictions1 = model1.predict(dtest, false, round)
    predictions1.describe("Monotonic", "predictions1")

    val params2 = params1 + ("monotone_constraints" -> Seq(1, -1))
    val model2: Booster = XGBoost.train(dtrain, params2, round, earlyStoppingRound = 10)
    // evals = evallist

    val predictions2 = model2.predict(dtest, false, round)
    predictions2.describe("Monotonic", "predictions2")
  }

  def main(args: Array[String]): Unit = {
    exo1()
    exo2()
    exo3()
  }
}
