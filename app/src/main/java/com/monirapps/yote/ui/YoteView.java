package com.monirapps.yote.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.monirapps.yote.engine.Board;
import com.monirapps.yote.engine.Game;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by David et Monireh on 30/05/2017.
 */

public class YoteView extends WebView implements Observer {

  private Game.PlayMoveListener playMoveListener;

  public YoteView(Context context) {
    this(context, null);
  }

  public YoteView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup();
  }

  public YoteView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup();
  }

  public void update(String board) {
    final String format = String.format("javascript:update('%s')", board);
    System.out.println(format);
    loadUrl(format);
  }

  public void setOnPlayMoveListener(Game.PlayMoveListener playMoveListener) {
    YoteView.this.playMoveListener = playMoveListener;
  }

  private void setup() {
    getSettings().setJavaScriptEnabled(true);
    addJavascriptInterface(this, "android");
    loadUrl("file:///android_asset/yote.html");
  }

  @JavascriptInterface
  public void play() {
    if (playMoveListener != null) {
      playMoveListener.play();
    }
  }

  @Override
  public void update(Observable o, Object arg) {
    update(arg.toString());
  }
}
