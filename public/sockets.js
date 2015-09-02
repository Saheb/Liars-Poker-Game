/**
 * Created by saheb on 9/2/15.
 */

window.sessionStorage.setItem("handType", "NA")
window.sessionStorage.setItem("valueType", "NA")
window.sessionStorage.setItem("value2Type", "NA")
window.sessionStorage.setItem("suitType", "NA")
window.sessionStorage.setItem("turn_number", "NA")
window.sessionStorage.setItem("round_number", "NA")

var player_id = Number(window.sessionStorage.getItem("loginId"))
var ws = new WebSocket("ws://localhost:9000/gamePlay/"+ GAME_ID + "/" + player_id +  "/play")
ws.onopen = function()
{
    // Web Socket is connected, send data using send()
    var player = {
        "id" : Number(window.sessionStorage.getItem("loginId")),
        "name" : window.sessionStorage.getItem("loginName"),
        "email" : window.sessionStorage.getItem("loginEmail")
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
            for(var i=0;i<gameStatusOrBet.length;i++)
            {
                var p = gameStatusOrBet[i]
                var name = window.sessionStorage.getItem(p.player_id)
                var cards = p.hand.split(",")
                var trStr = '<tr style="height: 100px"> <td class="col-md-1">' + name + '</td><td class="col-md-5">' ;//<td>' + p.num_of_cards + '</td> <td>'+ p.position +'</td></tr>'
                for(var c=0; c< cards.length;c++)
                {
                    var srcString = "/assets/cards/images/" + cards[c] + ".png";
                    //var img = $('<img id="dynamic" class="card" width="40" height="60" hspace="5">'); //Equivalent: $(document.createElement('img'))
                    //img.attr('src', srcString);
                    trStr = trStr + '<img id="dynamic" class="card" width="70" height="100" hspace="5" src=' + srcString + '>'
                }
                trStr = trStr + '</td>';
                $('#playerCardsTable tr:last').after(trStr);
            }
            $("#roundResultModal").modal("show")
        }
        else
        {
            $('#playerStatusTable td').remove();
            for(var i=0;i<gameStatusOrBet.length;i++)
            {
                var p = gameStatusOrBet[i]
                window.sessionStorage.setItem(p.player_id, p.name)
                $('#playerStatusTable tr:last').after('<tr> <td>' + p.name + '</td> <td>' + p.num_of_cards + '</td> <td>'+ p.position +'</td></tr>');
            }
            $("#gameStatusModal").modal("show")
        }
    }
    else
    {
        window.sessionStorage.setItem("previousBet", gameStatusOrBet.bet);
        window.sessionStorage.setItem("previousBetPlayerId",gameStatusOrBet.player_id)
        var bet = gameStatusOrBet.bet.split("_")
        $("#cu_handType").text(bet[0]);
        $("#cu_valueType").text(bet[1]);
        $("#cu_suitType").text(bet[3]);
        $("#cu_value2Type").text(bet[2]);
    }
};

ws.onclose = function()
{
    // websocket is closed.
    console.log("Connection is closed...");
};
