/**
 * Created by saheb on 9/10/15.
 */

function watchBtnClicked(btn) {
    console.log(btn)
    //var player = {
    //    id : Number(store.getItem("loginId")),
    //    name : store.getItem("loginName"),
    //    email : store.getItem("loginEmail")
    //}

    var gameIdWatch = Number(btn.parentElement.getAttribute("name"))

    // TODO : Figure out some way to stop a user from joining his own game!

    var jqxhr = $.ajax({
        type: "PUT",
        url: "/watchGame/" + gameIdWatch,
        //data : JSON.stringify(player), Not sending watcher data as of now!
        contentType: "html",
        success: function (response) {
            console.log(response)
            location.href = "/watchGame/" + gameIdWatch
        }
    })

}
