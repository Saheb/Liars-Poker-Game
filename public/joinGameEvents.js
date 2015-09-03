/**
 * Created by saheb on 9/3/15.
 */

function joinClicked(btn){
    console.log(btn)
    var player = {
        id : Number(store.getItem("loginId")),
        name : store.getItem("loginName"),
        email : store.getItem("loginEmail")
    }

    var gameIdJoined = Number(btn.parentElement.getAttribute("name"))

    // TODO : Figure out some way to stop a user from joining his own game!

    var jqxhr = $.ajax({
        type : "PUT",
        url : "/joinGame/"+gameIdJoined,
        data : JSON.stringify(player),
        contentType : "application/json",
        success : function(response) {
            console.log(response)
            location.href = "/createGame/" + btn.parentElement.getAttribute("name")
        }
    })

}
