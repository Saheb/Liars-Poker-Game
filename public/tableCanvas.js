/**
 * Created by saheb on 9/6/15.
 */
    window.globals = {}

    window.globals.loadTable = function(){
        //paper.view.setCenter(window.screen.availWidth/ , window.screen.availHeight/8)
        var circleStyle = {
            strokeColor: '#000000',
            strokeWidth: 3
        };
        var positionNameMap = JSON.parse(store.getItem("positionNameMap"));
        var num_of_players = Number(store.getItem("num_of_players"));
        var myPosition = Number(store.getItem("myPosition"));
        var angle = 360/num_of_players;
        var players = new Array();
        paper.view.setViewSize(450,450)
        var path = new Path.Circle(view.center, 220);
        path.fillColor = 'green'
        var po = new Point(view.center.x,view.center.y + 175);
        var vec = po - view.center;
        var validPositions = Object.keys(positionNameMap);
        for (var i = myPosition; i < validPositions.length + 1; i++)
        {
            var temp = new Point(vec.x + view.center.x, vec.y+ view.center.y)
            players[i] = new Path.Circle(temp, 25);
            players[i].fillColor = 'yellow';
            players[i].style = circleStyle;
            var name = positionNameMap[i];
            players[i].name = name;
            text = new PointText(new Point(temp.x-(3*name.length),temp.y+40));
            text.content = name;
            vec.angle += angle;
        }

        for (var i = 1; i < myPosition; i++)
        {
            var temp = new Point(vec.x + view.center.x, vec.y+ view.center.y)
            players[i] = new Path.Circle(temp, 25);
            players[i].fillColor = 'yellow';
            players[i].style = circleStyle;
            var name = positionNameMap[i];
            players[i].name = name;
            text = new PointText(new Point(temp.x-(3*name.length),temp.y+40));
            text.content = name;
            vec.angle += angle;
        }

        var currentBetterName = positionNameMap[1]
        project.activeLayer._namedChildren[currentBetterName][0].fillColor = 'red'
    }

    window.globals.watchTable = function(){
        //paper.view.setCenter(window.screen.availWidth/ , window.screen.availHeight/8)
        var circleStyle = {
            strokeColor: '#000000',
            strokeWidth: 3
        };
        var positionNameMap = JSON.parse(store.getItem("positionNameMap"));
        var num_of_players = Number(store.getItem("num_of_players"));
        var angle = 360/num_of_players;
        var players = new Array();
        paper.view.setViewSize(450,450)
        var path = new Path.Circle(view.center, 220);
        path.fillColor = 'green'
        var po = new Point(view.center.x,view.center.y + 175);
        var vec = po - view.center;

        for (var i = 1; i < num_of_players + 1; i++)
        {
            var temp = new Point(vec.x + view.center.x, vec.y+ view.center.y)
            players[i] = new Path.Circle(temp, 25);
            players[i].fillColor = 'yellow';
            players[i].style = circleStyle;
            var name = positionNameMap[i];
            players[i].name = name;
            text = new PointText(new Point(temp.x-(3*name.length),temp.y+40));
            text.content = name;
            vec.angle += angle;
        }

        var currentBetterName = positionNameMap[1]
        project.activeLayer._namedChildren[currentBetterName][0].fillColor = 'red'
    }

