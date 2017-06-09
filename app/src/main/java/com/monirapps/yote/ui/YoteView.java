package com.monirapps.yote.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.monirapps.yote.engine.Board;
import com.monirapps.yote.engine.Game;
import com.monirapps.yote.engine.player.Player;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by David et Monireh on 30/05/2017.
 */

public final class YoteView
        extends WebView
        implements Observer
{

    private Game.PlayMoveListener playMoveListener;

    public YoteView(Context context)
    {
        this(context, null);
    }

    public YoteView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setup();
    }

    public YoteView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public void update(String board)
    {
        final String format = String.format("javascript:update('%s')", board);
        System.out.println(format);
        loadUrl(format);
    }

    public void setOnPlayMoveListener(Game.PlayMoveListener playMoveListener)
    {
        YoteView.this.playMoveListener = playMoveListener;
    }

    private void setup()
    {
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(this, "android");
        loadUrl("file:///android_asset/yote.html");
    }

    // Not done in UI Thread
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void play()
    {
        if (playMoveListener != null)
        {
            playMoveListener.play();
        }
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void playMove(final String jsonString)
    {
        final Gson gson = new Gson();
        Player.Move move = gson.fromJson(jsonString, Player.Move.class);
        playMoveListener.playMove(move);
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void getLegalMoves(final String jsonString)
    {
        if (playMoveListener != null)
        {
            final Gson gson = new Gson();
            post(new Runnable()
            {
                @Override
                public void run()
                {
                    loadUrl(String.format("javascript:showLegalMoves('%s')", gson.toJson(playMoveListener.getLegalMoves(gson.fromJson(jsonString, Board.Case.class)))));
                }
            });
        }
    }

    @Override
    public void update(Observable o, Object arg)
    {
        update(arg.toString());
    }

}
