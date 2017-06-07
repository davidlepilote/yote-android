var actualBlotLegalMoves = [];
var currentCase = {};
var currentJumpMove = {};

 $(function() {


    $('body').on('click', '.tile', function (event)
    {
          $(".tile").removeClass("clickedTile");

          $(this).addClass('clickedTile');

          if (isTileATarget($(this)))
          {
              console.log("makarena " + currentJumpMove.cases);
              var targetCase = getCaseFromTile($(this));
              currentJumpMove.cases.push(targetCase);
              sendMoveToApp(currentJumpMove);
          }
          else
          {
            console.log("removing target class");

            $(".tile span").removeClass("target");
          }

          if (isBlotPresentOnTile($(this)) && isTileATarget($(this)) == false)
          {
            clickedOnTiltWithBlot($(this), event);
          }
          else if (isTileCorrectMove($(this)))
          {
            if (isJumpMove($(this)))
            {

               var destination = getCaseFromTile($(this));
               var eatenCase = getEatenCase(currentCase, destination);

               var cases = [];
               cases.push(currentCase);
               cases.push(eatenCase);
               cases.push(destination);
               currentJumpMove = new Move(cases, "JUMP");

               var numberOfTargets = handleEatableOpponentBlots(eatenCase);
               console.log("Jumping " + numberOfTargets);

               if (numberOfTargets == 0)
               {
                    console.log("Jumping over a blot and taking from player");
                    currentJumpMove.cases.push(null);
                    sendMoveToApp(currentJumpMove);
               }
            }
            else
            {
                var destination = getCaseFromTile($(this));
                var cases = [];
                cases.push(currentCase);
                cases.push(destination);
                var move = new Move(cases, "SLIDE");
                sendMoveToApp(move);
            }

          }
          else if (isTileATarget($(this)) == false)
          {
            var cases = [];
            var aCase = getCaseFromTile($(this));
            cases.push(aCase);
            var move = new Move(cases, "ADD");
            sendMoveToApp(move);

            console.log("here we can doo the creation move");
          }

    });

 });

 function isTileATarget(tile)
 {
    return $("span", tile).hasClass("target");
 }
 function handleEatableOpponentBlots(eatenCase)
 {
    var numberOfTargets;
    if (eatenCase.blot.color == "white")
    {
        numberOfTargets = $('.tile span.whiteBlot').length-1;
        $(".tile span.whiteBlot").addClass("target");
    }
    else
    {
       numberOfTargets = $('.tile span.blackBlot').length-1;
       $(".tile span.blackBlot").addClass("target");
    }


    var eatenId = "" + eatenCase.line + eatenCase.column;

    $('#' + eatenId +" span").removeClass("target");

    return numberOfTargets;
 }

 function getEatenCase(origin, destination)
 {
    var eatenLine;
    var eatenColumn;

     console.log('id malou : origineline(' + origin.line + ')| destinationline(' + destination.line+')');
     console.log('id malou : originecolumn(' + origin.column + ')| destinationcolumn(' + destination.column+')');

    if (origin.line == destination.line)
    {
        if (destination.column > origin.column)
        {
            eatenColumn = parseFloat(origin.column) + 1;
        }
        else
        {
            eatenColumn = parseFloat(origin.column) - 1;
        }

        eatenLine = origin.line;
    }
    else
    {
        if (destination.line > origin.line)
        {
            eatenLine = parseFloat(origin.line) + 1;
        }
        else
        {
            eatenLine = parseFloat(origin.line) - 1;
        }

        eatenColumn = origin.column;
    }
    var id = "" + eatenLine + eatenColumn;

    var eatenCase = getCaseFromTile($('#' + id));
    return eatenCase;
 }

 function getCaseFromTile(tile)
 {
     var line =  tile.attr('id').charAt(0);
     var column =  tile.attr('id').charAt(1);

     console.log("onGetCaseFromTile line(" + line + ") column("  +column + ")");


     var aCase = new Case(line,column);

     var aBlot;

        if ($("span:first", tile).hasClass("whiteBlot"))
        {
            aBlot = new Blot("white");
        }
        else if ($("span:first", tile).hasClass("blackBlot"))
        {
            aBlot = new Blot("black");

        }

        aCase.blot = aBlot;

        return aCase;
 }


 function isBlotPresentOnTile(tile)
 {
    return $("span:first", tile).hasClass("blot");
 }

 function isTileCorrectMove(tile)
 {
     return $("span:first", tile).hasClass("previsionBlot");
 }

 function isJumpMove(tile)
  {
      return $("span:first", tile).hasClass("jumpMove");
  }

 function clickedOnTiltWithBlot(tile, event)
 {
    currentCase = getCaseFromTile(tile);


    if (typeof currentCase.blot.color !== 'undefined')
    {
        android.getLegalMoves(JSON.stringify(currentCase));
        console.log("clicked on  Tile : " + event.currentTarget.id + "+ with Case[line: "
                    +currentCase.line + ", column: " + currentCase.column + ", Blot[color: " + currentCase.blot.color + "]]");
    }
 }

function play()
{
    android.play();
}

function sendMoveToApp(move)
{
    console.log("id malou , json jump = " + JSON.stringify(move));
    android.playMove(JSON.stringify(move));
}

function showLegalMoves(legalMoves)
{
    actualBlotLegalMoves = JSON.parse(legalMoves);

    console.log("okokok " + JSON.stringify(actualBlotLegalMoves));

    $(".tile span").removeClass("previsionBlot");
    $(".tile span").removeClass("jumpMove");

    for (var move = 0; move < actualBlotLegalMoves.length; move++)
    {
        console.log("okokok " + actualBlotLegalMoves[move].cases[1].line + "|" + actualBlotLegalMoves[move].cases[1].column);

        if (actualBlotLegalMoves[move].type == "JUMP")
        {
            var selectedId = "" + actualBlotLegalMoves[move].cases[2].line + actualBlotLegalMoves[move].cases[2].column;
            $('#' + selectedId+" span").addClass('jumpMove');
        }
        else
        {
            var selectedId = "" + actualBlotLegalMoves[move].cases[1].line + actualBlotLegalMoves[move].cases[1].column;
        }

        $('#' + selectedId+" span").addClass('previsionBlot');

    }

}


 function update(board)
 {
    console.log("javascript side, updating array :" + board);
     var drawnBoard = "";
     var counter = 0;
     var line = 0;
     var column = 0;
     drawnBoard += '<div class="row">';

    for (var i = 0; i < board.length; i++)
    {
        var c = board.charAt(i);

        var id = "" + line + column;
        switch (c)
        {
            case '#':
                counter--;
                line++;
                column = -1;
                drawnBoard += '</div>';
                if (i != board.length - 1)
                {
                  drawnBoard += '<div class="row">';
                }
            break;
            case '¤':
                 drawnBoard += '<span class="tile" id="' +id+ '"><span/></span>';
            break;
            case 'W':
                drawnBoard += '<span class="tile" id="' +id+ '"><span class="blot whiteBlot"/></span>';
            break;
            case 'B':
                drawnBoard += '<span class="tile" id="' +id+ '"><span class="blot blackBlot"/></span>';
            break;
           default:
                drawnBoard += c;
           break;
        }
        counter++;
        column++;
    }

    $('#board').html(drawnBoard);

 }
