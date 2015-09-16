/**
 * Created by saheb on 9/3/15.
 */

if(store.getItem("isAdmin")==="true")
    $("#startGameBtn").get(0).style.display = "block";
else
{
    // will be doing polling every 30 seconds to know if game has started or not!
    function gameStartedPolling () {
        $.ajax({
            url : '/gamePlay/gameStarted/' + GAME_ID,
            type : 'GET',
            contentType: "application/json",
            success : function(response) {
                location.href = "/gamePlay/" + GAME_ID
                clearInterval();
            },
            error : function(response){
                console.log("Game hasn't started yet");
            }
        })
    }
    //gameStartedPolling()
    setInterval(gameStartedPolling, 5000)
}

$("#startGameBtn").get(0).onclick = function() {
    var xhr = $.ajax({
        url : "/gamePlay/" + GAME_ID,
        type : "GET",
        dataType: "html",
        success : function(response) {
            location.href = "/gamePlay/"+GAME_ID
        }
    })
}
