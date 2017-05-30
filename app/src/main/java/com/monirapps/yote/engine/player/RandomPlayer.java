package com.monirapps.yote.engine.player;

import com.monirapps.yote.engine.Board;

import java.util.List;
import java.util.Random;

/**
 * Created by David et Monireh on 29/05/2017.
 */
public class RandomPlayer extends Player {

    Random random;

    public RandomPlayer(Board.Blot.BlotColor color) {
        super(color);
    }

    @Override
    public Move play(Board board, boolean opponentHasNonPlayedBlots) {
        final List<Move> moves = legalMoves(board, opponentHasNonPlayedBlots);
        return moves.get(random.nextInt(moves.size()));
    }

    @Override
    public void init() {
        super.init();
        random = new Random();
    }
}
