package util

/**
 * Created by saheb on 8/31/15.
 */

// Range of id is from 0 to 51
class Card(val id : Int) {

  val suitMap : Map[Int,String] = Map(0 -> "h", 1-> "c", 2-> "d", 3 -> "s")

  val valueMap : Map[Int, String] = Map(0 -> "a", 10 -> "j", 11 -> "q", 12 -> "k")

  def getId = id

  def getSuit = suitMap.getOrElse(id/13,None)

  def getValue  = {
    if(id%13==0)
      valueMap.get(0)
    if(id % 13 < 10)
      (id%13 + 1).toString()
    else
      valueMap.getOrElse(id%13, None)
  }
}

object Card extends App {
  for(i<- 0 to 51){
    val card = new Card(i)
    println(i + "=> Suit = " + card.getSuit + " and Value = " + card.getValue )
  }
}
