/**
 * Created by saheb on 9/2/15.
 */

$(".btn-group-vertical > .btn").click(function() {
    $(this).addClass("active").siblings().removeClass("active");
});

$(".btn-group > .btn").click(function() {
    $(this).addClass("active").siblings().removeClass("active");
});

function dealAndGetCards(){
    //var action = "GetCards";//(store.getItem("isAdmin")==="true")?"Deal":"GetCards";
    // TODO: Whoever clicks okdeal first, dealing of card will happen.
    var nextRoundNumber = 1;
    if(typeof store.getItem("round_number") != 'undefined')
        nextRoundNumber = Number(store.getItem("round_number"));

    var isPlaying = (store.getItem('myPosition') != 'undefined')
    var player = {
        "id" : Number(store.getItem("loginId")),
        "name" : store.getItem("loginName"),
        "email" : store.getItem("loginEmail")
    }
    if(isPlaying){
        $.ajax({
            url : '/gamePlay/' + GAME_ID + '/' + nextRoundNumber + '/' + 'dealCards',
            data : JSON.stringify(player),
            type : 'GET',
            success : function(response) {
                console.dir(response)
                $.ajax({
                    url : '/gamePlay/' + GAME_ID + '/' + 'getCards',
                    data : JSON.stringify(player),
                    type : 'POST',
                    contentType : 'application/json',
                    success : function(response) {
                        console.dir("Received your cards!");
                        console.dir(response)
                        deck.mount($("#container")[0]);
                        deck.intro(); deck.flip(); deck.shuffle(); deck.shuffle();
                        setTimeout(function(){
                            deck.unmount();
                            for(var i=0;i<cards.length;i++)
                            {
                                var srcString = "/assets/cards/images/" + cards[i] + ".png";
                                var img = $('<img id="dynamic" width="80" height="120" hspace="5" vspace="5">'); //Equivalent: $(document.createElement('img'))
                                img.attr('src', srcString);
                                img.appendTo('#cards');
                            }
                        }, 3300);
                        var cards = response[0].hand.split(",")
                        // set round_number and turn_number in WSS
                        store.setItem("round_number", response[0].round_number)
                        store.setItem("turn_number", 1)
                    }
                })
            }
        })
    }
    else // Watching the game!
    {
        // Reforming the layout!
        $('#betPanel').remove();
        $('#cards').remove();
        $('#challengeBtn').remove();
        $('#betBtn').remove();
        $('#allHandsTable').fadeIn(1000);
        $.ajax({
            url : '/gamePlay/' + GAME_ID + '/' + nextRoundNumber + '/' + 'dealCards',
            data : JSON.stringify(player),
            type : 'GET',
            success : function(response) {
                console.dir(response)
                $.ajax({
                    url : '/gamePlay/' + GAME_ID + '/getAllHands',
                    data : JSON.stringify(player),
                    type : 'GET',
                    contentType : 'application/json',
                    success : function(response) {
                        console.dir("Received all hands!");
                        console.dir(response);
                        $('#allHandsTable td').remove();
                        //deck.mount($("#allHands")[0]);
                        //deck.sort(); deck.shuffle(); deck.shuffle();
                        setTimeout(function(){
                            ///deck.unmount();
                            // show all hands with player names in a div with id "allHands"
                            for(var i=0;i<response.length;i++)
                            {
                                var p = response[i]
                                var name = store.getItem(p.player_id)
                                var cards = p.hand.split(",")
                                var trStr = '<tr style="height: 100px"> <td class="col-md-1">' + name + '</td><td class="col-md-5">' ;//<td>' + p.num_of_cards + '</td> <td>'+ p.position +'</td></tr>'
                                for(var c=0; c< cards.length;c++)
                                {
                                    var srcString = "/assets/cards/images/" + cards[c] + ".png";
                                    trStr = trStr + '<img id="dynamic" width="70" height="100" hspace="5" src=' + srcString + '>'
                                }
                                trStr = trStr + '</td>';
                                $('#allHandsTable tr:last').after(trStr);
                            }
                        }, 0);
                        store.setItem("round_number", response[0].round_number)
                    }
                })
            }
        })
    }

    console.dir("Message is sent! JSON->" + JSON.stringify(player));
}
function getGameStatus(){
    var xhr = $.ajax({
        url : "/gamePlay/" + GAME_ID,
        type : "GET",
        dataType: "html",
        success : function(response) {
            location.href = "/gamePlay/"+GAME_ID
        }
    })
}
$("#okdeal").get(0).onclick = dealAndGetCards
$("#ok").get(0).onclick = getGameStatus

$("#handType").get(0).onclick = function(btn) {
    var handType = btn.target.textContent;
    store.setItem("handType", handType);
    switch(handType) {
        case "High Card":
            $("#value2Type > button").prop('disabled', true);
            $("#suitType > button").prop('disabled', true);
            break;
        case "Pair" :
            $("#value2Type > button").prop('disabled', true);
            $("#suitType > button").prop('disabled', true);
            break;
        case "Double Pair":
            $("#value2Type > button").prop('disabled', false);
            $("#suitType > button").prop('disabled', true);
            break;
        case "Straight":
            $("#value2Type > button").prop('disabled', true);
            $("#suitType > button").prop('disabled', true);
            break;
        case "Trio":
            $("#value2Type > button").prop('disabled', true);
            $("#suitType > button").prop('disabled', true);
            break;
        case "Flush" :
            $("#value2Type > button").prop('disabled', true);
            $("#suitType > button").prop('disabled', false);
            break;
        case "Full House":
            $("#value2Type > button").prop('disabled', false);
            $("#suitType > button").prop('disabled', true);
            break;
        case "Four of a Kind":
            $("#value2Type > button").prop('disabled', true);
            $("#suitType > button").prop('disabled', true);
            break;
        case "Straight Flush":
            $("#value2Type > button").prop('disabled', true);
            $("#suitType > button").prop('disabled', false);
            break;
        case "Royal Flush":
            $("#value2Type > button").prop('disabled', true);
            $("#suitType > button").prop('disabled', false);
            break;
    }
}

$("#valueType").get(0).onclick = function(btn) {
    store.setItem("valueType", btn.target.textContent)
}

$("#value2Type").get(0).onclick = function(btn) {
    store.setItem("value2Type", btn.target.textContent)
}

$("#suitType").get(0).onclick = function(btn) {
    store.setItem("suitType", btn.target.textContent)
}

$("#betBtn").get(0).onclick = function() {
    // validate the bet by comparing with previous bet from SS.
    if(call()){
        var handType = store.getItem("handType");
        switch(handType) {
            case "High Card":
                $("#value2Type > button").removeClass('active');
                $("#suitType > button").removeClass('active');
                store.setItem('value2Type', "NA");
                store.setItem('suitType', "NA");
                break;
            case "Pair" :
                $("#value2Type > button").removeClass('active');
                $("#suitType > button").removeClass('active');
                store.setItem('value2Type', "NA");
                store.setItem('suitType', "NA");
                break;
            case "Double Pair":
                $("#value2Type > button").prop('disabled', false);
                $("#suitType > button").removeClass('active');
                store.setItem('suitType', "NA");
                break;
            case "Straight":
                $("#value2Type > button").removeClass('active');
                $("#suitType > button").removeClass('active');
                store.setItem('value2Type', "NA");
                store.setItem('suitType', "NA");
                break;
            case "Trio":
                $("#value2Type > button").removeClass('active');
                $("#suitType > button").removeClass('active');
                store.setItem('value2Type', "NA");
                store.setItem('suitType', "NA");
                break;
            case "Flush" :
                $("#value2Type > button").removeClass('active');
                $("#suitType > button").prop('disabled', false);
                store.setItem('value2Type', "NA");
                break;
            case "Full House":
                $("#value2Type > button").prop('disabled', false);
                $("#suitType > button").removeClass('active');
                store.setItem('suitType', "NA");
                break;
            case "Four of a Kind":
                $("#value2Type > button").removeClass('active');
                $("#suitType > button").removeClass('active');
                store.setItem('value2Type', "NA");
                store.setItem('suitType', "NA");
                break;
            case "Straight Flush":
                $("#value2Type > button").removeClass('active');
                $("#suitType > button").prop('disabled', false);
                store.setItem('value2Type', "NA");
                break;
            case "Royal Flush":
                $("#value2Type > button").removeClass('active');
                $("#suitType > button").prop('disabled', false);
                store.setItem('value2Type', "NA")
                break;
        }
        var player = {
            "id": Number(store.getItem("loginId")),
            "name": store.getItem("loginName"),
            "email": store.getItem("loginEmail")
        }
        var bet = {
            "game_id": GAME_ID,
            "round_number": Number(store.getItem("round_number")),
            "turn_number": Number(store.getItem("turn_number")),
            "player_id": player.id,
            "bet": store.getItem("handType") + "_" + store.getItem("valueType") + "_" + store.getItem("value2Type") + "_" + store.getItem("suitType")
        }
        var json = {
            "action": "Bet",
            "player": player,
            "bet": bet
        }
        ws.send(JSON.stringify(json));
        $("#cu_handType").text(store.getItem("handType"));
        $("#cu_valueType").text(store.getItem("valueType"));
        $("#cu_suitType").text(store.getItem("suitType"));
        $("#cu_value2Type").text(store.getItem("value2Type"));

        store.setItem("previousBet", bet.bet);
        store.setItem("previousBetPlayerId",bet.player_id)

        console.dir("Bet is sent...");
        // update WSS
        store.setItem("turn_number", Number(store.getItem("turn_number"))+1)
        $("#betBtn").prop('disabled', true)
        $("#challengeBtn").prop('disabled', true)
        var playerPositionMap = JSON.parse(store.getItem("playerPositionMap"))
        var positionPlayerMap = JSON.parse(store.getItem("positionPlayerMap"))
        var betterPosition = playerPositionMap[Number(store.getItem("loginId"))]
        var validPositions = Object.keys(positionPlayerMap);
        var betterIndex = validPositions.indexOf(betterPosition.toString())
        var currentPosition = betterIndex + 1
        var currentBetterName = "";
        if(currentPosition < Number(store.getItem("num_of_players")))
            currentBetterName = store.getItem(positionPlayerMap[validPositions[currentPosition]])
        else
            currentBetterName = store.getItem(positionPlayerMap[validPositions[0]])

        $('#previousBetter').text(player.name)
        drawTable();
    }
    else
    {
        alert("You can't bet lower than previous bet!");
    }

}

$("#challengeBtn").get(0).onclick = function(){
    var player = {
        "id": Number(store.getItem("loginId")),
        "name": store.getItem("loginName"),
        "email": store.getItem("loginEmail")
    }

    var roundResult = {
        game_id : GAME_ID,
        round_number : Number(store.getItem("round_number")),
        player_challenge_id : player.id, // player id of the player who challenged the bet
        player_bet_id : Number(store.getItem("previousBetPlayerId")), // player id of the player whose bet has been challenged
        bet_challenged : store.getItem("previousBet"),
        result : "NA"
    }

    var json = {
        "action": "Challenge",
        "player": player,
        "roundResult": roundResult
    }
    ws.send(JSON.stringify(json));
    console.dir("Challenge details are sent...");
}

$("#playAgain").get(0).onclick = function(){
    location.href = "/"
}

$("#chatBox").keyup(function(event){
    if(event.keyCode == 13){
        var message = event.target.value;
        if(message != "")
        {
            var player = {
                "id": Number(store.getItem("loginId")),
                "name": store.getItem("loginName"),
                "email": store.getItem("loginEmail")
            }

            var json = {
                "action": "Chat",
                "player": player,
                "message": message
            }
            ws.send(JSON.stringify(json));
            $('#chatMessages').append('<b>me</b><br/>');
            $('#chatMessages').append(message + '<br/>');
            event.target.value = ""
        }
    }
});
