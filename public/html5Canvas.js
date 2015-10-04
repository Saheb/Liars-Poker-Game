/**
 * Created by saheb on 9/22/15.
 */

var canvas = document.getElementById("htmlCanvas");
var ctx = canvas.getContext("2d");

var radius = canvas.height / 2;
radius = radius * 0.85;

var directionPoll;

function drawTable() {
    ctx.restore();
    clearInterval(directionPoll);
    ctx.clearRect(0,0,canvas.width, canvas.height);
    ctx.arc(canvas.width/2, canvas.height/2, radius, 0 , 2*Math.PI);
    ctx.fillStyle = "green";
    ctx.fill();
    if(store.getItem("myPosition") != 'undefined')
    {
        drawPlayers();
        //drawDirection(ctx,radius)
        directionPoll = setInterval(drawDirection, 1000,ctx,radius);
    }
    else
    {
        watchPlayers();
        directionPoll = setInterval(drawDirection, 1000,ctx,radius);
    }
}

function drawPlayers() {
    var num;
    var positionNameMap = JSON.parse(store.getItem("positionNameMap"));
    var validPositions = Object.keys(positionNameMap);
    var myPosition = Number(store.getItem("myPosition"));
    var myIndex = validPositions.indexOf(myPosition.toString());
    ctx.font = radius*0.07 + "px arial";
    ctx.fillStyle = 'red';
    ctx.textBaseline="middle";
    ctx.textAlign="center";
    ctx.save();
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
        x = playerDist * Math.sin(-1*angle);
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
        x = playerDist * Math.sin(-1*angle);
        y = playerDist * Math.cos(angle);
        angle += angle;
    }
}

function watchPlayers(){
    var positionNameMap = JSON.parse(store.getItem("positionNameMap"));
    var validPositions = Object.keys(positionNameMap);
    ctx.font = radius*0.07 + "px arial";
    ctx.fillStyle = 'red';
    ctx.textBaseline="middle";
    ctx.textAlign="center";
    ctx.save();
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


function drawDirection(ctx, radius){
    var now = new Date();
    var hour = now.getHours();
    var minute = now.getMinutes();
    var second = now.getSeconds();
    //hour
    hour=hour%12;
    hour=(hour*Math.PI/6)+
        (minute*Math.PI/(6*60))+
        (second*Math.PI/(360*60));
    //drawHand(ctx, hour, radius*0.5, radius*0.07);
    //minute
    minute=(minute*Math.PI/30)+(second*Math.PI/(30*60));
    //drawHand(ctx, minute, radius*0.8, radius*0.07);
    // second
    second=(second*Math.PI/30);
    drawHand(ctx, second, radius*0.6, radius*0.01);
}

function drawHand(ctx, pos, length, width) {
    ctx.beginPath();
    ctx.lineWidth = width;
    ctx.lineCap = "round";
    ctx.moveTo(0,0);
    ctx.rotate(pos);
    ctx.lineTo(0, -length);
    ctx.stroke();
    ctx.rotate(-pos);
}
