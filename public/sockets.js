/**
 * Created by saheb on 9/2/15.
 */

store.setItem("handType", "NA")
store.setItem("valueType", "NA")
store.setItem("value2Type", "NA")
store.setItem("suitType", "NA")
store.setItem("turn_number", "NA")
store.setItem("round_number", "NA")

var player_id = Number(store.getItem("loginId"))
var ws = new WebSocket("ws://localhost:9000/gamePlay/"+ GAME_ID + "/" + player_id +  "/play")
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
                    //var img = $('<img id="dynamic" class="card" width="40" height="60" hspace="5">'); //Equivalent: $(document.createElement('img'))
                    //img.attr('src', srcString);
                    trStr = trStr + '<img id="dynamic" class="card" width="70" height="100" hspace="5" src=' + srcString + '>'
                }
                trStr = trStr + '</td>';
                $('#playerCardsTable tr:last').after(trStr);
            }

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
            }
        }
        else
        {
            $('#playerStatusTable td').remove();
            var positionMap = {}
            for(var i=0;i<gameStatusOrBet.length;i++)
            {
                var p = gameStatusOrBet[i]
                positionMap[p.id] = p.position
                store.setItem(p.player_id, p.name)
                $('#playerStatusTable tr:last').after('<tr> <td>' + p.name + '</td> <td>' + p.num_of_cards + '</td> <td>'+ p.position +'</td></tr>');
            }
            $("#gameStatusModal").modal("show")
            store.setItem("positionMap", JSON.stringify(positionMap))
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
            $("#cu_suitType").text(bet[3]);
            $("#cu_value2Type").text(bet[2]);
        }
        else // round Result
        {
            store.setItem("player_challenge_id", gameStatusOrBet.player_challenge_id)
        }
    }
};

ws.onclose = function()
{
    // websocket is closed.
    console.log("Connection is closed...");
};