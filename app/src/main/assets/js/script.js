
 $(function() {


    $('body').on('click', '.tile', function (event)
    {
          $(".tile").removeClass("clickedTile");
          $(this).toggleClass('clickedTile');


            var test = new Case(4,5);

            var aCase = {};
            var aBlot = {};

            aCase.line = event.currentTarget.id.charAt(0);
            aCase.column = event.currentTarget.id.charAt(1);


            if ($("span:first", $(this)).hasClass("whiteBlot"))
            {
                aBlot.color = "white";
            }
            else if ($("span:first", $(this)).hasClass("blackBlot"))
            {
                aBlot.color = "black";
            }

            aCase.blot = aBlot;



            if (typeof aCase.blot.color !== 'undefined')
            {
                android.getLegalMoves(JSON.stringify(aCase));
                console.log("clicked on  Tile : " + event.currentTarget.id + "+ with Case[line: "
                            +aCase.line + ", column: " + aCase.column + ", Blot[color: " + aCase.blot.color + "]]");
            }
    });

 });

function play()
{
    android.play();
}

function showLegalMoves(legalMoves)
{
    var jsonArray = JSON.parse(legalMoves);

    console.log("okokok " + JSON.stringify(jsonArray));

    $(".tile span").removeClass("previsionBlot");

    for (var move = 0; move < jsonArray.length; move++)
    {
        console.log("okokok " + jsonArray[move].cases[1].line + "|" + jsonArray[move].cases[1].column);

        if (jsonArray[move].type == "JUMP")
        {
            var selectedId = "" + jsonArray[move].cases[2].line + jsonArray[move].cases[2].column;
        }
        else
        {
            var selectedId = "" + jsonArray[move].cases[1].line + jsonArray[move].cases[1].column;
        }


        $('#' + selectedId+" span").addClass('previsionBlot');

    }

}
 function update(board)
 {
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
