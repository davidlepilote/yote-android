package com.monirapps.yote.engine.player;

import android.support.v4.util.Pair;

import com.monirapps.yote.engine.Board;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by David et Monireh on 15/11/2016.
 */
public abstract class Player implements Playable {

    public static class Move {

        public Move(MoveType type, Board.Case... cases) {
            this.type = type;
            this.cases = cases;
        }

        public enum MoveType implements Comparable<MoveType> {
            JUMP,
            SLIDE,
            ADD;
        }

        public final MoveType type;

        public final Board.Case[] cases;

        @Override
        public String toString() {
            switch (type){
                case ADD:
                    return type + " : " + cases[0].line + "," + cases[0].column;
                case SLIDE:
                    return type + " : " + cases[0].line + "," + cases[0].column + " -> " + cases[1].line + "," + cases[1].column;
                case JUMP:
                    return type + " : " + cases[0].line + "," + cases[0].column + " -> " + cases[2].line + "," + cases[2].column;
            }
            return null;
        }
    }

    public static final int NB_BLOTS = 12;

    public final Board.Blot.BlotColor color;

    private final Deque<Board.Blot> blots = new ArrayDeque<>(NB_BLOTS);

    public Player(Board.Blot.BlotColor color) {
        this.color = color;
        init();
    }

    @Override
    public void init() {
        this.blots.clear();
        for (int nbBlot = 0; nbBlot < NB_BLOTS; nbBlot++) {
            blots.add(new Board.Blot(this.color));
        }
    }

    public Board.Blot takeBlot(){
        return blots.pop();
    }

    public void addBlot(Board.Blot blot) {
        blots.push(blot);
    }

    public int blotsLeft() {
        return blots.size();
    }

    public boolean hasNonPlayedBlots() {
        return !blots.isEmpty();
    }

}
