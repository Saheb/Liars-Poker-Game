/**
 * Created by saheb on 9/22/15.
 */

var canvas = document.getElementById("htmlCanvas");
var ctx = canvas.getContext("2d");
var radius = canvas.height / 2;
radius = radius * 0.95

function drawTable() {
    ctx.arc(canvas.width/2, canvas.height/2, radius, 0 , 2*Math.PI);
    ctx.fillStyle = "green";
    ctx.fill();
    if(store.getItem("myPosition") != 'undefined')
    {
        drawPlayers(ctx,radius);
    }
    else
    {
        watchPlayers(ctx,radius);
    }
}

function drawPlayers(ctx, radius) {
    var num;
    var positionNameMap = JSON.parse(store.getItem("positionNameMap"));
    var validPositions = Object.keys(positionNameMap);
    var myPosition = Number(store.getItem("myPosition"));
    var myIndex = validPositions.indexOf(myPosition.toString());
    ctx.font = radius*0.07 + "px arial";
    ctx.fillStyle = 'red';
    ctx.textBaseline="middle";
    ctx.textAlign="center";
    ctx.translate(canvas.width/2, canvas.height/2);
    var num_of_players = Number(store.getItem("num_of_players"));
    var angle = (2*Math.PI)/num_of_players;
    var playerDist = canvas.height/2 - 2*radius*0.1;
    var y = canvas.height/2 - 2*radius*0.1;
    var x = 0;
    for(num = myIndex; num < validPositions.length; num++){
        ctx.beginPath();
        ctx.fillStyle = 'red';
        ctx.arc(x, y, radius*0.1, 0 , 2*Math.PI);
        ctx.fill();
        ctx.beginPath();
        ctx.fillStyle = 'black';
        var name = positionNameMap[validPositions[num]];
        ctx.fillText(name,x,y+radius*0.15);
        x = playerDist * Math.sin(angle);
        y = playerDist * Math.cos(angle);
        angle += angle;
    }

    for(num = 0; num < myIndex; num++){
        ctx.beginPath();
        ctx.fillStyle = 'red';
        ctx.arc(x, y, radius*0.1, 0 , 2*Math.PI);
        ctx.fill();
        ctx.beginPath();
        ctx.fillStyle = 'black';
        var name = positionNameMap[validPositions[num]];
        ctx.fillText(name,x,y+radius*0.15);
        x = playerDist * Math.sin(angle);
        y = playerDist * Math.cos(angle);
        angle += angle;
    }
}

function watchPlayers(ctx, radius){
    var positionNameMap = JSON.parse(store.getItem("positionNameMap"));
    var validPositions = Object.keys(positionNameMap);
    ctx.font = radius*0.07 + "px arial";
    ctx.fillStyle = 'red';
    ctx.textBaseline="middle";
    ctx.textAlign="center";
    ctx.translate(canvas.width/2, canvas.height/2);
    var num_of_players = Number(store.getItem("num_of_players"));
    var angle = (2*Math.PI)/num_of_players;
    var playerDist = canvas.height/2 - 2*radius*0.1;
    var y = canvas.height/2 - 2*radius*0.1;
    var x = 0;
    for(num = 0; num < validPositions.length; num++){
        ctx.beginPath();
        ctx.fillStyle = 'red';
        ctx.arc(x, y, radius*0.1, 0 , 2*Math.PI);
        ctx.fill();
        ctx.beginPath();
        ctx.fillStyle = 'black';
        var name = positionNameMap[validPositions[num]];
        ctx.fillText(name,x,y+radius*0.15);
        x = playerDist * Math.sin(angle);
        y = playerDist * Math.cos(angle);
        angle += angle;
    }
}

function drawDirection(){

}
