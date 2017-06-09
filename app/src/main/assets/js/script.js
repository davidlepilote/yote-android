var actualBlotLegalMoves = [];
var currentCase = {};
var currentJumpMove = {};
var whitePlayer = {};
var blackPlayer = {};
var currentPlayer = {};

 $(function() {


    $('body').on('click', '.tile', function (event)
    {
          $(".tile").removeClass("clickedTile");

          $(this).addClass('clickedTile');
          var line =  $(this).attr('id').charAt(0);
          var column =  $(this).attr('id').charAt(1);
          console.log("thomas tile clicked : " + line + ","+column);

          if (isTileATarget($(this)))
          {
              var targetCase = getCaseFromTile($(this));
              currentJumpMove.cases.push(targetCase);
              sendMoveToApp(currentJumpMove);
          }
          else
          {
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

               if (numberOfTargets == 0)
               {
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
          else if (isTileATarget($(this)) == false && (currentPlayer.blots - currentPlayer.blotsOnBoard) > 0)
          {
            var cases = [];
            var aCase = getCaseFromTile($(this));
            cases.push(aCase);
            var move = new Move(cases, "ADD");
            sendMoveToApp(move);
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
    }
 }

function play()
{
    android.play();
}

function sendMoveToApp(move)
{
    android.playMove(JSON.stringify(move));
}

function showLegalMoves(legalMoves)
{
    actualBlotLegalMoves = JSON.parse(legalMoves);


    $(".tile span").removeClass("previsionBlot");
    $(".tile span").removeClass("jumpMove");

    for (var move = 0; move < actualBlotLegalMoves.length; move++)
    {

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


 function update(stringBoard)
 {
     var jsonBoard = JSON.parse(stringBoard);

        console.log("thomas board received : " + jsonBoard.board);
    // We define the players here
     var currentPlayerColor = jsonBoard.currentPlayer;
     whitePlayer = new Player(jsonBoard.players[0].color, parseFloat(jsonBoard.players[0].value), parseFloat(jsonBoard.players[0].blots));
     blackPlayer = new Player(jsonBoard.players[1].color, parseFloat(jsonBoard.players[1].value), parseFloat(jsonBoard.players[1].blots));




     var board = jsonBoard.board;

    // Then, we draw the board
     var drawnBoard = "";
     var line = 0;
     var column = 0;
     drawnBoard += '<div class="row">';

    for (var i = 0; i < board.length; i++)
    {

        for (var j = 0; j < board[i].length; j++)
        {
           var id = "" + line + column;

            if (parseFloat(board[i][j]) == whitePlayer.value)
            {
              console.log("thomas, we draw a white blot");
              drawnBoard += '<span class="tile" id="' +id+ '"><span class="blot whiteBlot"/></span>';
            }
            else if (parseFloat(board[i][j]) == blackPlayer.value)
             {
               drawnBoard += '<span class="tile" id="' +id+ '"><span class="blot blackBlot"/></span>';
             }
             else
             {
               drawnBoard += '<span class="tile" id="' +id+ '"><span/></span>';
             }
             column++;
        }

        line++;
        column = 0;
        drawnBoard += '</div>';
        if (i != board.length - 1)
        {
          drawnBoard += '<div class="row">';
        }

    }

    $('#board').html(drawnBoard);


    // Dealing with showing actual score on players tab


    var whiteBlotsInGame = $('.whiteBlot').length
    var blackBlotsInGame = $(".blackBlot").length;

    whitePlayer.blotsOnBoard = whiteBlotsInGame;
    blackPlayer.blotsOnBoard = blackBlotsInGame;

    $("#whitePlayer").html("White " + (whitePlayer.blots - whitePlayer.blotsOnBoard));
    $("#blackPlayer").html("Black " + (blackPlayer.blots - blackPlayer.blotsOnBoard));

    if (currentPlayerColor == whitePlayer.color)
    {
    currentPlayer = whitePlayer;
    $("#whitePlayer").addClass("currentPlayer");
    $("#blackPlayer").removeClass("currentPlayer");
    }
    else
    {
     currentPlayer = blackPlayer;
     $("#whitePlayer").removeClass("currentPlayer");
     $("#blackPlayer").addClass("currentPlayer");
    }

    handleEndGame();

 }

 function handleEndGame()
 {
    console.log("babar 2");
    if ((currentPlayer.blots + currentPlayer.blotsOnBoard) == 0 )
    {
        if (currentPlayer.color == "white")
        {
           $("#board").html("<strong>Black has won ! </strong>");
        }
        else
        {
         $("#board").html("<strong>White has won ! </strong>");
        }
    }
 }
