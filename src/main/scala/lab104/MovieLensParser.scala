package lab104

import java.util.Calendar

object MovieLensParser {

  val noGenresListed = "(no genres listed)"
  val commaRegex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"
  val pipeRegex = "\\|(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"
  val quotes = "\""

  /** Convert from timestamp (String) to year (Int) */
  def yearFromTimestamp(timestamp: String): Int = {
    val cal = Calendar.getInstance()
    cal.setTimeInMillis(timestamp.trim.toLong * 1000L)
    cal.get(Calendar.YEAR)
  }

  /** Function to parse movie records
   *
   *  @param line line that has to be parsed
   *  @return tuple containing movieId, title and genres, none in case of input errors
   */
  def parseMovieLine(line: String): Option[(Long, String, String)] = {
    try {
      val input = line.split(commaRegex)
      var title = input(1).trim
      title = if(title.startsWith(quotes)) title.substring(1) else title
      title = if(title.endsWith(quotes)) title.substring(0, title.length - 1) else title
      Some(input(0).trim.toLong, title, input(2).trim)
    } catch {
      case _: Exception => None
    }
  }

  /** Function to parse rating records
   *
   *  @param line line that has to be parsed
   *  @return tuple containing userId, movieId, rating, and year none in case of input errors
   */
  def parseRatingLine(line: String): Option[(Long, Long, Double, Int)] = {
    try {
      val input = line.split(commaRegex)
      Some(input(0).trim.toLong, input(1).trim.toLong, input(2).trim.toDouble, yearFromTimestamp(input(3)))
    } catch {
      case _: Exception => None
    }
  }

  /** Function to parse tag records
   *
   *  @param line line that has to be parsed
   *  @return tuple containing userId, movieId, tag, and year, none in case of input errors
   */
  def parseTagLine(line: String) : Option[(Long, Long, String, Int)] = {
    try {
      val input = line.split(commaRegex)
      Some(input(0).trim.toLong, input(1).trim.toLong, input(2), yearFromTimestamp(input(3)))
    } catch {
      case _: Exception => None
    }
  }

}