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

function isFlush(cards, suitType){
    var regex = '[id$=' + suitType + ']'
    $(regex).css('opacity', '1.0')
    if((cards.split(suitType).length - 1 ) >= 5)
        return true;
    else
        return false;
}

function isStraight(cards,valueType){
    var regex = '[id^=' + valueType + ']'
    $(regex).css('opacity', '1.0')

    if(cards.search(valueType) >= 0)
    {
        var value = card_score[valueType]
        var left = 0; var right = 0;
        for(var i=value;i<=14;i++)
        {
            if(cards.search(i+1)  != -1)
            {
                var regex = '[id^=' + eval(i+1) + ']'
                $(regex).css('opacity', '1.0')
                right+=1
            }
            else
                break;
        }
        for(var i=value;i>=2;i--)
        {
            if(cards.search(i-1) != -1)
            {
                var regex = '[id^=' + eval(i-1) + ']'
                $(regex).css('opacity', '1.0')
                left+=1
            }
            else
                break;
        }
        if(right + left >= 4)
            return true;
    }
    else
        return false;
}

function challengeWon(cards, bet)  {

    var betArr = bet.split("_")
    var handType = betArr[0]
    var valueType = betArr[1].toLowerCase()
    var value2Type = betArr[2].toLowerCase()
    var suitType = suit_map[betArr[3]]

    if(handType == "High Card")
    {
        var regex = '[id^=' + valueType + ']'
        $(regex).css('opacity', '1.0')
        if(cards.search(valueType) >=0)
        {
            return true;
        }
    }
    else if(handType == "Pair")
    {
        var regex = '[id^=' + valueType + ']'
        $(regex).css('opacity', '1.0')
        if((cards.split(valueType).length -1) >= 2)
            return true;
    }
    else if(handType == "Trio")
    {
        var regex = '[id^=' + valueType + ']'
        $(regex).css('opacity', '1.0')
        if((cards.split(valueType).length -1) >= 3)
            return true;

    }
    else if(handType == "Four of a Kind")
    {
        var regex = '[id^=' + valueType + ']'
        $(regex).css('opacity', '1.0')
        if((cards.split(valueType).length -1) == 4)
            return true;
    }
    else if(handType == "Flush")
    {
        return isFlush(cards, suitType);
    }
    else if(handType == "Double Pair")
    {
        var regex = '[id^=' + valueType + ']'
        $(regex).css('opacity', '1.0')
        regex = '[id^=' + value2Type + ']'
        $(regex).css('opacity', '1.0')

        if((cards.split(valueType).length -1) >=2 &&(cards.split(value2Type).length -1) >= 2)
            return true;
    }
    else if(handType == "Full House")
    {
        var regex = '[id^=' + valueType + ']'
        $(regex).css('opacity', '1.0')
        regex = '[id^=' + value2Type + ']'
        $(regex).css('opacity', '1.0')

        if((cards.split(valueType).length -1) >= 3 &&(cards.split(value2Type).length -1) >=2)
            return true;

    }
    else if(handType == "Straight")
    {
        isStraight(cards, valueType);
    }
    else if(handType == "Straight Flush")
    {
        if(isStraight(cards, valueType) && isFlush(cards, suitType))
            return true;
        else
            return false;
    }
    else if(handType == "Royal Flush")
    {
        if(isStraight(cards,valueType) && isFlush(cards, suitType) && cards.search('a' + suitType) >=0)
            return true;
        else
            return false;
    }
    else
    {
        console.log("Such a bet doesn't exist from challenge Result file!")
    }
}