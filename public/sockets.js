/**
 * Created by saheb on 9/2/15.
 */

store.setItem("handType", "NA")
store.setItem("valueType", "NA")
store.setItem("value2Type", "NA")
store.setItem("suitType", "NA")
store.setItem("turn_number", "NA")
//store.setItem("round_number", "0")

var player_id = Number(store.getItem("loginId"))
var loc = window.location, new_uri;
if (loc.protocol === "https:") {
    new_uri = "wss:";
} else {
    new_uri = "ws:";
}
new_uri += "//" + loc.host;
//new_uri += loc.pathname;
var ws = new ReconnectingWebSocket(new_uri + "/gamePlay/"+ GAME_ID + "/" + player_id +  "/play")
ws.debug = true;

ws.onopen = function()
{
    // Web Socket is connected, send data using send()
    var player = {
        "id" : Number(store.getItem("loginId")),
        "name" : store.getItem("loginName"),
        "email" : store.getItem("loginEmail")
    }
    var json = {"action" : "GameStatus", "player" : player}
    ws.send(JSON.stringify(json));
    console.log("Message is sent...");
};

ws.onmessage = function (evt)
{
    var gameStatusOrBet = JSON.parse(evt.data);
    console.log(gameStatusOrBet);
    if(gameStatusOrBet.length > 1) // This is check for socket message is game status or a bet!!
    {
        if(gameStatusOrBet[0].hasOwnProperty("hand"))// Challenge Response or GameStatus check!
        {
            $('#playerCardsTable td').remove();
            //TODO : Animation before showing final cards!
            //deck.mount($('#container2')[0]);
            //deck.poker();
            var allCards = "";
            for(var i=0;i<gameStatusOrBet.length;i++)
            {
                var p = gameStatusOrBet[i]
                var name = store.getItem(p.player_id)
                var cards = p.hand.split(",")
                var trStr = '<tr style="height: 100px"> <td class="col-md-1">' + name + '</td><td class="col-md-5">' ;//<td>' + p.num_of_cards + '</td> <td>'+ p.position +'</td></tr>'
                for(var c=0; c< cards.length;c++)
                {
                    allCards += (cards[c] + ",")
                    var srcString = "/assets/cards/images/" + cards[c] + ".png";
                    //var img = $('<img id="dynamic" class="card" width="80" height="120" hspace="5">'); //Equivalent: $(document.createElement('img'))
                    //img.attr('src', srcString);
                    trStr = trStr + '<img id="'+ cards[c] +'" width="70" height="100" style="opacity:0.3" hspace="5" src=' + srcString + '>'
                }
                trStr = trStr + '</td>';
                $('#playerCardsTable tr:last').after(trStr);
            }

            console.log(allCards);
            console.log(store.getItem("previousBet"));
            var result = ""
            if(challengeWon(allCards, store.getItem("previousBet")))
            {
                $("#roundResultTitle")[0].innerHTML = "Round " + store.getItem("round_number") + " : " + store.getItem(store.getItem("player_challenge_id")) +" lost challenge against " +  store.getItem(store.getItem("previousBetPlayerId"))
                result = "LOST"
            }
            else
            {
                $("#roundResultTitle")[0].innerHTML = "Round " + store.getItem("round_number") + " : " + store.getItem(store.getItem("player_challenge_id")) +" won challenge against " +  store.getItem(store.getItem("previousBetPlayerId"))
                result = "WON"
            }
            console.log(result);
            var betString = store.getItem('previousBet').replace(/_/g, ' ').replace(/NA/g, ' ')
            $('#betString').text("Bet challenged was : " + betString);
            $("#roundResultModal").modal("show")
            if(store.getItem("player_challenge_id") == store.getItem("loginId"))
            {
                var roundResult = {
                    game_id : GAME_ID,
                    round_number : Number(store.getItem("round_number")),
                    player_challenge_id : Number(store.getItem("player_challenge_id")), // player id of the player who challenged the bet
                    player_bet_id : Number(store.getItem("previousBetPlayerId")), // player id of the player whose bet has been challenged
                    bet_challenged : store.getItem("previousBet"),
                    result : result
                }
                var json = {
                    "action": "RoundResult",
                    "roundResult": roundResult
                }
                ws.send(JSON.stringify(json))
                //var json = {
                //    "action": "Close"
                //}
                //ws.send(JSON.stringify(json));
                //ws.close();
                //console.log("Closing Connection!");
            }
        }
        else // Game Status!
        {
            $('#playerStatusTable td').remove();
            var playerPositionMap = {}
            var positionPlayerMap = {}
            var positionNameMap = {}
            var num_of_players = gameStatusOrBet.length
            for(var i=0;i<num_of_players;i++)
            {
                var p = gameStatusOrBet[i]
                playerPositionMap[p.player_id] = p.position
                positionPlayerMap[p.position] = p.player_id
                positionNameMap[p.position] = p.name
                store.setItem(p.player_id, p.name)
                $('#playerStatusTable tr:last').after('<tr> <td>' + p.name + '</td> <td>' + p.num_of_cards + '</td> <td>'+ p.position +'</td></tr>');
            }

            store.setItem("positionNameMap", JSON.stringify(positionNameMap))
            store.setItem("playerPositionMap", JSON.stringify(playerPositionMap))
            store.setItem("positionPlayerMap", JSON.stringify(positionPlayerMap))
            store.setItem("num_of_players", num_of_players)
            var myPosition = playerPositionMap[Number(store.getItem("loginId"))]
            store.setItem("myPosition", myPosition)

            if(typeof myPosition == 'undefined')
            {
                $('#okdeal').text("Okay! Let me watch")
                drawTable();
            }
            else
            {
                var validPositions = Object.keys(positionPlayerMap);
                var myIndex = validPositions.indexOf(myPosition.toString())
                if(myIndex != 0)
                    store.setItem("leftPlayerId", positionPlayerMap[validPositions[myIndex-1]])
                else
                    store.setItem("leftPlayerId", positionPlayerMap[validPositions[num_of_players-1]])
                drawTable();
                if(myPosition == validPositions[0])
                {
                    $("#betBtn").prop('disabled', false)
                    $("#challengeBtn").prop('disabled', false)
                }
                else
                {
                    $("#betBtn").prop('disabled', true)
                    $("#challengeBtn").prop('disabled', true)
                }
            }
            $("#gameStatusModal").modal("show");
        }
    }
    else
    {
        if(gameStatusOrBet.hasOwnProperty("bet"))// Check for bet or roundResult
        {
            store.setItem("previousBet", gameStatusOrBet.bet);
            store.setItem("previousBetPlayerId",gameStatusOrBet.player_id)
            var bet = gameStatusOrBet.bet.split("_")
            $("#cu_handType").text(bet[0]);
            $("#cu_valueType").text(bet[1]);
            $("#cu_value2Type").text(bet[2]);
            $("#cu_suitType").text(bet[3]);
            if(gameStatusOrBet.player_id == Number(store.getItem("leftPlayerId")))
            {
                $("#betBtn").prop('disabled', false)
                $("#challengeBtn").prop('disabled', false)
            }
            var playerPositionMap = JSON.parse(store.getItem("playerPositionMap"))
            var positionPlayerMap = JSON.parse(store.getItem("positionPlayerMap"))

            var betterPosition = playerPositionMap[gameStatusOrBet.player_id]
            var previousBetterName = store.getItem(positionPlayerMap[betterPosition])
            var validPositions = Object.keys(positionPlayerMap);
            var betterIndex = validPositions.indexOf(betterPosition.toString())
            var currentPosition = betterIndex + 1
            var currentBetterName = "";
            if(currentPosition < Number(store.getItem("num_of_players")))
                currentBetterName = store.getItem(positionPlayerMap[validPositions[currentPosition]])
            else
                currentBetterName = store.getItem(positionPlayerMap[validPositions[0]])
            $('#previousBetter').text(previousBetterName)
        }
        else if(gameStatusOrBet.hasOwnProperty('player_challenge_id'))// round Result
        {
            store.setItem("player_challenge_id", gameStatusOrBet.player_challenge_id);
            //var json = {
            //    "action": "Close"
            //}
            //ws.send(JSON.stringify(json));
            //ws.close();
            //console.log("Closing Connection!");
        }
        else if(gameStatusOrBet.hasOwnProperty(('action')))
        {
            console.log(gameStatusOrBet.message)
            console.log(gameStatusOrBet.player.name)
            $('#chatMessages').append('<b>' + gameStatusOrBet.player.name + '</b><br/>');
            $('#chatMessages').append(gameStatusOrBet.message + '<br/>');
        }
        else // Game Result!
        {
            $('#finalStandTable td').remove();
            $.ajax({
                type : "GET",
                url : "/gamePlay/"+GAME_ID + "/finalStandings",
                contentType : "application/json",
                success : function(response) {
                    console.log(response);
                    $('#winner').text(gameStatusOrBet[0].name + " has won the Game!");
                    for(var i=0;i<response.length;i++)
                    {
                        $('#finalStandTable tr:last').after('<tr> <td>' + response[i].name + '</td> <td>' + response[i].position + '</td></tr>');
                    }
                    $("#gameResultModal").modal("show");
                }
            });
        }
    }
};

ws.onclose = function()
{
    // websocket is closed.
    console.log("Connection is closed...");
};
