/**
 * Created by saheb on 9/3/15.
 */
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

var suit_map = {
    "Spades"   : "s",
    "Diamonds" : "d",
    "Clubs"    : "c",
    "Hearts"   : "h"
};

var bet = store.getItem("previousBet")

function challengeWon(cards, bet)  {

    var betArr = bet.split("_")
    var handType = betArr[0]
    var valueType = betArr[1].toLowerCase()
    var suitType = betArr[2]
    var value2Type = betArr[3].toLowerCase()

    if(handType == "High Card")
    {
        if(cards.search(valueType) >=0)
            return true;
    }
    else if(handType == "Pair")
    {
        if((cards.split(valueType).length -1) >= 2)
            return true;
    }
    else if(handType == "Trio")
    {
        if((cards.split(valueType).length -1) >= 3)
            return true;
    }
    else if(handType == "Four of a Kind")
    {
        if((cards.split(valueType).length -1) == 4)
            return true;
    }
    else if(handType == "Flush")
    {
        if((cards.split(suit_map[suitType]) - 1 ) >= 5)
            return true;
    }
    else if(handType == "Double Pair")
    {
        if((cards.split(valueType).length -1) >=2 &&(cards.split(value2Type).length -1) >= 2)
            return true;
    }
    else if(handType == "Full House")
    {
        if((cards.split(valueType).length -1) >= 3 &&(cards.split(value2Type).length -1) >=2)
            return true;
    }
    else if(handType == "Straight")
    {
        if(cards.search(valueType) >= 0)
        {
            var value = card_score[valueType]
            var left = 0; var right = 0;
            for(var i=value;i<=14;i++)
            {
                if(cards.search(i+1))
                    right+=1
                else
                    break;
            }
            for(var i=value;i>=2;i--)
            {
                if(cards.search(i-1))
                    left+=1
                else
                    break;
            }
            if(right + left >= 4)
                return true;
        }
        else
            return false;
    }
    else if(handType == "Straight Flush")
    {

    }
    else if(handType == "Royal Flush")
    {

    }
    else
    {
        console.log("Such a bet doesn't exist from challenge Result file!")
    }
}