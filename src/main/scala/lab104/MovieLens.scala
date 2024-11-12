package lab104

import org.apache.spark.sql.SparkSession
import utils._
import org.apache.spark.sql.SaveMode
import org.apache.spark.HashPartitioner
import org.apache.spark.sql._

object MovieLens {

  val path_to_datasets = "/datasets/big/"

  val path_ml_movies = path_to_datasets + "ml-movies.csv"
  val path_ml_ratings = path_to_datasets + "ml-ratings.csv"
  val path_ml_tags = path_to_datasets + "ml-tags.csv"

  val path_output_avgRatPerMovie = "/output/avgRatPerMovie"

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.appName("MovieLens job").getOrCreate()
    val sqlContext = spark.sqlContext // needed to save as CSV
    import sqlContext.implicits._

    if(args.length < 2){
      println("The first parameter should indicate the deployment mode (\"local\" or \"remote\")")
      println("The second parameter should indicate the job (1 for join-and-agg, 2 for agg-and-join, 3 for agg-and-bjoin)")
      return
    }

    val deploymentMode = args(0)
    val job = args(1)

    // Initialize input
    val rddMovies = spark.sparkContext.
      textFile(Commons.getDatasetPath(deploymentMode, path_ml_movies)).
      flatMap(MovieLensParser.parseMovieLine)
    val rddRatings = spark.sparkContext.
      textFile(Commons.getDatasetPath(deploymentMode, path_ml_ratings)).
      flatMap(MovieLensParser.parseRatingLine)
    val rddTags = spark.sparkContext.
      textFile(Commons.getDatasetPath(deploymentMode, path_ml_tags)).
      flatMap(MovieLensParser.parseTagLine)


    val rddMoviesKV = rddMovies.map(x => (x._1,x._2))

    if (job=="1"){
      rddRatings.
        map(x => ((x._2),(x._3))).
        join(rddMoviesKV).
        map({case (m,(r,t)) => ((m,t),r)}).
        aggregateByKey((0.0,0.0))((a,v)=>(a._1+v, a._2+1),(a1,a2)=>(a1._1+a2._1,a1._2+a2._2)).
        map({case ((m,t),(sum,cnt)) => (m, t, sum/cnt, cnt)}).
        coalesce(1).
        toDF().write.format("csv").mode(SaveMode.Overwrite).
        save(Commons.getDatasetPath(deploymentMode,path_output_avgRatPerMovie))
    }
    else if (job=="2"){
      rddRatings.
        map(x => (x._2,x._3)).
        aggregateByKey((0.0,0.0))((a,v)=>(a._1+v, a._2+1),(a1,a2)=>(a1._1+a2._1,a1._2+a2._2)).
        mapValues({case (sum,cnt) => (sum/cnt, cnt)}).
        join(rddMoviesKV).
        map({case (m,((r,cnt),t)) => (m,t,r,cnt)}).
        coalesce(1).
        toDF().write.format("csv").mode(SaveMode.Overwrite).
        save(Commons.getDatasetPath(deploymentMode,path_output_avgRatPerMovie))
    }
    else if (job=="3"){
      val bRddMovies = spark.sparkContext.broadcast(rddMoviesKV.collectAsMap())
      rddRatings.
        map(x => ((x._2),(x._3))).
        aggregateByKey((0.0,0.0))((a,v)=>(a._1+v, a._2+1),(a1,a2)=>(a1._1+a2._1,a1._2+a2._2)).
        mapValues({case (sum,cnt) => (sum/cnt, cnt)}).
        map({case (m,(r,cnt)) => (m,bRddMovies.value.get(m),r,cnt)}).
        coalesce(1).
        toDF().write.format("csv").mode(SaveMode.Overwrite).
        save(Commons.getDatasetPath(deploymentMode,path_output_avgRatPerMovie))
    }
    else {
      println("Wrong job number")
    }

  }

}