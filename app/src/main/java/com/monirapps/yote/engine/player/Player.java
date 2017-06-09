package com.monirapps.yote.engine.player;

import android.support.v4.util.Pair;

import com.google.gson.annotations.SerializedName;
import com.monirapps.yote.engine.Board;
import com.monirapps.yote.engine.Board.Blot.BlotColor;
import com.monirapps.yote.engine.Board.Case;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David et Monireh on 15/11/2016.
 */
public abstract class Player
        implements Playable
{

    public static class Move
    {
        public Move(MoveType type, Board.Case... cases)
        {
            this.type = type;
            this.cases = cases;
        }

        public enum MoveType
                implements Comparable<MoveType>
        {
            @SerializedName("JUMP")
            JUMP,
            @SerializedName("SLIDE")
            SLIDE,
            @SerializedName("ADD")
            ADD;
        }

        public final MoveType type;

        public final Board.Case[] cases;

        @Override
        public String toString()
        {
            switch (type)
            {
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

    public static final String COLOR = "color";

    public static final String VALUE = "value";

    public static final String BLOTS = "blots";

    public final Board.Blot.BlotColor color;

    private final Deque<Board.Blot> blots = new ArrayDeque<>(NB_BLOTS);

    public Player(Board.Blot.BlotColor color)
    {
        this.color = color;
        init();
    }

    public BlotColor getColor()
    {
        return color;
    }

    @Override
    public void init()
    {
        this.blots.clear();
        for (int nbBlot = 0; nbBlot < NB_BLOTS; nbBlot++)
        {
            blots.add(new Board.Blot(this.color));
        }
    }

    public Board.Blot takeBlot()
    {
        return blots.pop();
    }

    public int blotsLeft(Board board) {
        int nbBlots = blots.size();
        for (Board.Case[] cases : board.cases) {
            for (Board.Case aCase : cases) {
                if (aCase.isSameColor(color)) {
                    nbBlots++;
                }
            }
        }
        return nbBlots;
        //return blots.size() + (int) Stream.of(board.cases).flatMap(Stream::of).filter(aCase -> aCase.isSameColor(color)).count();
    }

  public void addBlot(Board.Blot blot) {
    blots.push(blot);
  }

  public int blotsLeft() {
    return blots.size();
  }

    public boolean hasNonPlayedBlots()
    {
        return !blots.isEmpty();
    }

    /**
     * Lists all the legal moves that can be :
     * - add a blot to the given case
     * - move a blot from a given case to an adjacent case
     * - jump over an opponent blot, and taking another opponent's blot (one move by other blot)
     *
     * @param board
     * @return all legal moves for the given player and board
     */
    public List<Move> legalMoves(Board board, boolean opponentHasNonPlayedBlots) {
        List<Move> moves = new ArrayList<>();
        for (Board.Case aCase : board) {
            // Try to add a ADD Move
            if (aCase.isEmpty() && !blots.isEmpty()) {
                moves.add(new Move(Move.MoveType.ADD, aCase));
            } else if (aCase.isSameColor(color)) {
                for (Board.Direction direction : Board.Direction.values()) {
                    // Try to add a SLIDE Move
                    Board.Case slidingCase = board.moveTo(aCase, direction);
                    if (slidingCase != null) {
                        moves.add(new Move(Move.MoveType.SLIDE, aCase, slidingCase));
                    }
                    // Try to add a JUMP Move
                    Pair<Board.Case, Board.Case> newCase = board.jumpTo(aCase, direction);
                    if (newCase != null) {
                        final Board.Case opponentCase = newCase.first;
                        final Board.Case jumpedCase = newCase.second;
                        for (Board.Case otherOpponentCase : board) {
                            if(otherOpponentCase.isOpponentColor(color) && otherOpponentCase != opponentCase){
                                moves.add(new Move(Move.MoveType.JUMP, aCase, opponentCase, jumpedCase, otherOpponentCase));
                            }
                        }
                        // The opponent other blot is taken from his own stack
                        if (opponentHasNonPlayedBlots) {
                            moves.add(new Move(Move.MoveType.JUMP, aCase, opponentCase, jumpedCase, null));
                        }
                    }
                }
            }
        }
        return moves;
    }

    public boolean hasLost(Board board) {
        return blotsLeft(board) == 0;
        //return blots.size() == 0 && Stream.of(board.cases).flatMap(Stream::of).filter(aCase -> aCase.isSameColor(color)).count() == 0;
    }
}
