import ml.dmlc.xgboost4j.scala.DMatrix
import ml.dmlc.xgboost4j.scala.XGBoost

object BinaryClassifier {
  def main(args: Array[String]): Unit = {
    // available at github.com/xgboost/demo/data
    val workingDir = "src/main/scala/resources"
    val dtrain: DMatrix = new DMatrix(s"$workingDir/agaricus.txt.train")

    val params: Map[String, Any] = Map(
      "eta" -> 0.1,
      "max_depth" -> 2,
      "objective" -> "binary:logistic"
    )

    val round: Int = 2

    val model = XGBoost.train(dtrain, params, round)

    model.predict(dtrain)
    model.saveModel(s"$workingDir/agaricus.model")
  }
}
