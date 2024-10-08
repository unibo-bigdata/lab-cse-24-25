package lab101

import org.apache.spark.sql.SparkSession
import utils._

object ExampleJob {

  val inputFile = "/datasets/capra.txt"
  val outputDir = "/output/exampleJobOutput2"

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.appName("Sample Spark job").getOrCreate()

    if(args.length == 0){
      println("The first parameter should indicate the deployment mode (\"local\" or \"remote\")")
      return
    }

    val deploymentMode = args(0)

    val myRdd = spark.sparkContext.textFile(Commons.getDatasetPath(deploymentMode, inputFile))
    // The output directory should NOT exist
    myRdd.flatMap(_.split(" ")).map(_.toUpperCase).saveAsTextFile(Commons.getDatasetPath(deploymentMode,outputDir))
  }

}