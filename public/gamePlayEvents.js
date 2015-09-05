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
    var isAdmin = store.getItem("isAdmin")==="true"
    var player = {
        "id" : Number(store.getItem("loginId")),
        "name" : store.getItem("loginName"),
        "email" : store.getItem("loginEmail")
    }
    if(isAdmin){
        $.ajax({
            url : '/gamePlay/' + GAME_ID + '/' + 'dealCards',
            data : JSON.stringify(player),
            type : 'GET',
            success : function(response) {
                console.log("Dealt the cards!");
                console.log(response)
                $.ajax({
                    url : '/gamePlay/' + GAME_ID + '/' + 'getCards',
                    data : JSON.stringify(player),
                    type : 'POST',
                    contentType : 'application/json',
                    success : function(response) {
                        console.log("Received your cards!");
                        console.log(response)
                        var cards = response[0].hand.split(",")
                        for(var i=0;i<cards.length;i++)
                        {
                            var srcString = "/assets/cards/images/" + cards[i] + ".png";
                            var img = $('<img id="dynamic" class="card" width="80" height="120" hspace="5">'); //Equivalent: $(document.createElement('img'))
                            img.attr('src', srcString);
                            img.appendTo('#cards');
                        }
                        // set round_number and turn_number in WSS
                        store.setItem("round_number", response[0].round_number)
                        store.setItem("turn_number", 1)
                    }
                })
            }
        })
    }
    else
    {
        $.ajax({
            url : '/gamePlay/' + GAME_ID + '/' + 'getCards',
            data : JSON.stringify(player),
            type : 'POST',
            contentType : 'application/json',
            success : function(response) {
                console.log("Received your cards!");
                console.log(response);
                var cards = response[0].hand.split(",")
                for(var i=0;i<cards.length;i++)
                {
                    var srcString = "/assets/cards/images/" + cards[i] + ".png";
                    var img = $('<img id="dynamic" width="80" height="120" hspace="5">'); //Equivalent: $(document.createElement('img'))
                    img.attr('src', srcString);
                    img.appendTo('#cards');
                }
                // set round_number and turn_number in WSS
                store.setItem("round_number", response[0].round_number)
                store.setItem("turn_number", 1)
                if(Number(store.getItem("myPosition"))==1)
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
        })
    }
    console.log("Message is sent! JSON->" + JSON.stringify(player));
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
    store.setItem("handType", btn.target.textContent)
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

        console.log("Bet is sent...");
        // update WSS
        store.setItem("turn_number", Number(store.getItem("turn_number"))+1)
        $("#betBtn").prop('disabled', true)
        $("#challengeBtn").prop('disabled', true)
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
    console.log("Challenge details are sent...");
}
