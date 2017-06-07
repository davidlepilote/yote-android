package com.monirapps.yote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.monirapps.yote.engine.Board;
import com.monirapps.yote.engine.Game;
import com.monirapps.yote.engine.player.Player;
import com.monirapps.yote.engine.player.RandomPlayer;
import com.monirapps.yote.ui.YoteView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public final class MainActivity extends AppCompatActivity implements View.OnClickListener, Game.PlayMoveListener
{

    private YoteView yoteUI;

    private Button start;

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        start.setOnClickListener(this);

        yoteUI.setOnPlayMoveListener(this);
    }

    private void findViews()
    {
        yoteUI = (YoteView) findViewById(R.id.webView);
        start = (Button) findViewById(R.id.start);
    }

    private void updateUI()
    {
        yoteUI.update(game.toJson().toString());
    }

    @Override
    public void onClick(View v)
    {
        if (v == start)
        {
            game = new Game(new RandomPlayer(Board.Blot.BlotColor.WHITE), new RandomPlayer(Board.Blot.BlotColor.BLACK));
            game.addObserver(yoteUI);
            updateUI();
        }
    }

    @Override
    public void play()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                game.play(new Game.Turn()
                {
                    @Override
                    public Player.Move play(Player currentPlayer, Board board, boolean opponentHasNonPlayedBlots)
                    {
                        return currentPlayer.play(board, opponentHasNonPlayedBlots);
                    }
                });
            }
        });
    }

    @Override
    public void playMove(final Player.Move move)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                game.playMove(move);
            }
        });

    }

    @Override
    public List<Player.Move> getLegalMoves(Board.Case aCase)
    {
        final List<Player.Move> legalMoves = game.getLegalMoves(aCase);
        return legalMoves;
    }
}
