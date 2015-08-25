package util

/**
 * Created by saheb on 8/25/15.
 */
package main.scala

import scala.util.Random
import scala.collection.immutable.ListSet

// default size 52.
// cards from 0 to 51

//TODO : Remove variables and do it using immutables

class Deck(val numOfCards : Int = 52) {

  val generator:Random = new Random()
  var deck = ListSet[Int]()

  def initialize = {
    for(i<- 0 until numOfCards)
      deck = deck + i
    shuffle
  }

  def pop = {
    val top = deck.head
    deck = deck - top
    top
  }

  def shuffle: Unit = {
    deck = Random.shuffle(deck)
  }

  def size = {
    deck.size
  }
}

object test extends App{
  val deck = new Deck()
  deck.initialize
  for(i<- 0 until deck.numOfCards)
    println(deck.pop)
}
