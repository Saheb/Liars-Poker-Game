/**
 * Created by saheb on 8/17/15.
 */

var cb = new Codebird;
cb.setConsumerKey("3tZ6woyPs0eYLLkCETRDVIICO", "zozuay9xan5IRXdiViE6mpi71JcutREwfWikTZpCPXsWm9RCfE");


window.fbAsyncInit = function() {
    FB.init({
        appId: '962083260497512',
        xfbml: true,
        version: 'v2.4'
    });
};

(function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if(d.getElementById(id)) {
        return;
    }
    js = d.createElement(s);
    js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

var googleUser = {};
var startApp = function() {
    gapi.load('auth2', function() {
        // Retrieve the singleton for the GoogleAuth library and set up the client.
        auth2 = gapi.auth2.init({
            client_id: '116777801623-0l4ivhohb1ob3l7le4n5bdi09q7udeqq.apps.googleusercontent.com',
            cookiepolicy: 'single_host_origin',
            // Request scopes in addition to 'profile' and 'email'
            //scope: 'additional_scope'
        });
        attachSignin(document.getElementById('google-login-btn'));
        //auth2.attachClickHandler('google-login-btn', {});
    });
};

startApp()

//attachSignin(document.getElementById('google-login-btn'));

function attachSignin(element) {
    auth2.attachClickHandler(element, {},
        function(googleUser) {
            document.getElementById('loginName').innerText =
                googleUser.getBasicProfile().getName();

            var player = {
                id: 0,
                name: googleUser.getBasicProfile().getName(),
                email: googleUser.getBasicProfile().getEmail()
            }

            var jqxhr = $.ajax({
                type: "PUT",
                url: "/persistLoginInfo",
                contentType: "application/json",
                data: JSON.stringify(player),
                success: function(response) {
                    console.log(response)
                    window.sessionStorage.setItem("loginId", response)
                    window.sessionStorage.setItem("loginName", player.name)
                    window.sessionStorage.setItem("loginEmail", player.email)
                }
            })

            $('#loginModal').modal('hide');
        },
        function(error) {
            alert(JSON.stringify(error, undefined, 2));
        });
}

document.getElementById('fb-login-btn').onclick = function()
{
    var fbCallback = function() {
        FB.api('/me', function(response) {
            console.log(response);
            var userId = response.id
            FB.api('/' + userId, {
                fields: ['id', 'email', 'name']
            }, function(response) {
                console.log(response);
                document.getElementById('loginName').innerText = response.name

                var player = {
                    id: 0,
                    name: response.name,
                    email: response.email
                }

                var jqxhr = $.ajax({
                    type: "PUT",
                    url: "/persistLoginInfo",
                    contentType: "application/json",
                    data: JSON.stringify(player),
                    success: function(response) {
                        console.log(response)
                        window.sessionStorage.setItem("loginId", response)
                        window.sessionStorage.setItem("loginName", player.name)
                        window.sessionStorage.setItem("loginEmail", player.email)
                    }
                })

                $('#loginModal').modal('hide');
            });
        });
    }
    FB.getLoginStatus(function(response) {
        if(response.status === 'connected') {
            console.log('Logged in.');
            fbCallback()
        }
        else
        {
            FB.login(function(response) {
                console.info('FB.login response', response);
                fbCallback()
            }, {
                scope: 'email'
            });
        }
    });
}
//              document.getElementById('google-login-btn').onclick = function(googleUser) {
//                // Useful data for your client-side scripts:
//                var profile = googleUser.getBasicProfile();
//                console.log("ID: " + profile.getId()); // Don't send this directly to your server!
//                console.log("Name: " + profile.getName());
//                console.log("Image URL: " + profile.getImageUrl());
//                console.log("Email: " + profile.getEmail());
//
//                // The ID token you need to pass to your backend:
//                var id_token = googleUser.getAuthResponse().id_token;
//                console.log("ID Token: " + id_token);
//              };

document.getElementById('twitter-login-btn').onclick = function() {

    // the below is equivalent to document.getElementById
    $("#login-btns").get(0).style.display = "none";
    $("#pin").get(0).style.display = "inline-block";
    // gets a request token
    cb.__call(
        "oauth_requestToken",
        {
            oauth_callback: "oob"
        },
        function(reply) {
            // stores it
            cb.setToken(reply.oauth_token, reply.oauth_token_secret);

            // gets the authorize screen URL
            cb.__call(
                "oauth_authorize",
                {},
                function(auth_url) {
                    window.codebird_auth = window.open(auth_url);
                }
            );
        }
    );
}

document.getElementById('submitPin').onclick = function() {
    cb.__call(
        "oauth_accessToken",
        {
            oauth_verifier: document.getElementById("pin").value
        },
        function(reply) {
            // store the authenticated token, which may be different from the request token (!)
            cb.setToken(reply.oauth_token, reply.oauth_token_secret);

            // if you need to persist the login after page reload,
            // consider storing the token in a cookie or HTML5 local storage

            document.getElementById('loginName').innerText = reply.screen_name
            console.log(reply.screen_name)
            console.log(reply.user_id)

            var player = {
                id: 0,
                name: reply.screen_name,
                email: reply.screen_name + "@twitter.com"
            }

            var jqxhr = $.ajax({
                type: "PUT",
                url: "/persistLoginInfo",
                contentType: "application/json",
                data: JSON.stringify(player),
                success: function(response) {
                    console.log(response)
                    store.setItem("loginId", response)
                    store.setItem("loginName", player.name)
                    store.setItem("loginEmail", player.email)
                }
            })

            $('#loginModal').modal('hide');
        }
    );
}

$("#createGameBtn").get(0).onclick = function() {
    var isLoggedIn = $("#loginName").text() != "Login"
    if(isLoggedIn)
    {
        var game = {
            "id": 0,
            "name": $("#loginName").text() + "'s Game",
            "admin_player": Number(store.getItem("loginId")),
            "joined_players": 1,
            "max_players": 5,
            "winner_player": -1,
            "status": -1
        }

        var jqxhr = $.ajax(
            {
                type: "PUT",
                url: "/createGame",
                contentType: "application/json",
                data: JSON.stringify(game),
                success: function(response) {
                    console.log(response)
                    location.href = "/createGame/" + response
                }
            })
        store.setItem("isAdmin", true);
    }
    else
        alert("You need to login to Join or Create a Game!")

}

$("#joinGameBtn").get(0).onclick = function() {
    var isLoggedIn = $("#loginName").text() != "Login"
    if(isLoggedIn)
    {
        var jqxhr = $.ajax({
            type: "GET",
            url: "/joinGame",
            dataType: "html",
            success: function(response) {
                console.log(response)
                location.href = "/joinGame"
            }
        })
        store.setItem("isAdmin", false);
    }
    else
        alert("You need to login to Join or Create a Game!")
}

$("#watchGameBtn").get(0).onclick = function() {
    //var isLoggedIn = $("#loginName").text() != "Login"
        var jqxhr = $.ajax({
            type: "GET",
            url: "/watchGame",
            dataType: "html",
            success: function(response) {
                console.log(response)
                location.href = "/watchGame"
            }
        })
}