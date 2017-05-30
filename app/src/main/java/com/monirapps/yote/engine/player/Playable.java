package com.monirapps.yote.engine.player;

import com.monirapps.yote.engine.Board;

/**
 * Created by David et Monireh on 15/11/2016.
 */
public interface Playable{
    public Player.Move play(Board board, boolean opponentHasNonPlayedBlots);

    public void init();
}
