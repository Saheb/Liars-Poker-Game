/**
 * Created by saheb on 9/1/15.
 */

var hand_score = {
    "Royal Flush"    :  10,
    "Straight Flush" :  9,
    "Four of a Kind"    :  8,
    "Full House"     :  7,
    "Flush"          :  6,
    "Trio"    :  5,
    "Straight"       :  4,
    "Double Pair"   :  3,
    "Pair"           :  2,
    "High Card"      :  1
};
var card_score = {
    "A"   : 14,
    "K"  : 13,
    "Q" : 12,
    "J"  : 11,
    "10"    : 10,
    "9"     : 9,
    "8"     : 8,
    "7"     : 7,
    "6"     : 6,
    "5"     : 5,
    "4"     : 4,
    "3"     : 3,
    "2"     : 2
};
var suit_score = {
    "Spades"   : 4,
    "Diamonds" : 3,
    "Clubs"    : 2,
    "Hearts"   : 1
};

function compare(current_bet,last_bet)
{
    if(hand_score[current_bet.Hand] > hand_score[last_bet.Hand])
        return true;
    else if (hand_score[current_bet.Hand] == hand_score[last_bet.Hand])
    {
        if (current_bet.Hand=="Full House" || current_bet.Hand =="Double Pair")
        {
            if(card_score[current_bet.Card1] > card_score[last_bet.Card1])
                return true;
            else if(current_bet.Card1 == current_bet.Card1)
            {
                if(card_score[current_bet.Card2] > card_score[last_bet.Card2])
                    return true;
                else
                    return false;
            }
            else
                return false;
        }
        else if (current_bet.Hand=="Flush" || current_bet.Hand=="Straight Flush" || current_bet.Hand=="Royal Flush")
        {
            if(current_bet.NumberOfCards > last_bet.NumberOfCards)
                return true;
            else if(current_bet.NumberOfCards==last_bet.NumberOfCards)
            {
                if(suit_score[current_bet.Suit] > suit_score[last_bet.Suit])
                    return true;
                else if (current_bet.Suit == last_bet.Suit)
                {
                    if(card_score[current_bet.Card1] > card_score[last_bet.Card1])
                        return true;
                    else
                        return false;
                }
                else
                    return false;
            }
            else
                return false;
        }
        else if(current_bet.Hand=="Four of a Kind" || current_bet.Hand=="Pair" || current_bet.Hand=="Trio")
        {
            if(card_score[current_bet.Card1] > card_score[last_bet.Card1])
                return true;
            else
                return false;
        }
        else
        {
            if(card_score[current_bet.Card1] > card_score[last_bet.Card1])
                return true;
            else
                return false;
        }
    }
    else
        return false;
}



function call()
{
    var current = new Object();
    current.Hand = window.sessionStorage.getItem("handType")
    current.Card1 = window.sessionStorage.getItem("valueType")
    current.Card2 = window.sessionStorage.getItem("value2Type")
    current.Suit = window.sessionStorage.getItem("suitType")
    current.NumberOfCards = 5;
    var previous = new Object();
    previous.Hand = $("#cu_handType").text();
    previous.Card1 = $("#cu_valueType").text();
    previous.Card2 = $("#cu_value2Type").text();
    previous.Suit =  $("#cu_suitType").text();
    previous.NumberOfCards = 5;

    if(previous.Hand == "Hand" || compare(current,previous))
    {
        console.log(previous);
        console.log(current);
        console.log("Current Bet is Accepted");
        return true;
    }
    else
    {
        return false;
    }

}
