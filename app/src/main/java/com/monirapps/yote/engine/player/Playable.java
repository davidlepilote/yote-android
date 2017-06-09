package com.monirapps.yote.engine.player;

import com.monirapps.yote.engine.Board;
import com.monirapps.yote.engine.Game;

/**
 * Created by David et Monireh on 15/11/2016.
 */
public interface Playable{
    public Player.Move play(Game game);

    public void init();
}
