package com.monirapps.yote.engine;

import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutCompat.OrientationMode;

import com.google.gson.annotations.SerializedName;
import com.monirapps.yote.engine.player.Player;
import org.json.JSONArray;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Created by David et Monireh on 15/11/2016.
 */
public final class Board implements Iterable<Board.Case>{

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int line = 0; line < HEIGHT; line++) {
            for (int column = 0; column < WIDTH; column++) {
                if(cases[line][column].isEmpty()){
                    builder.append("¤");
                } else if(cases[line][column].blot.color == Blot.BlotColor.WHITE){
                    builder.append("W");
                } else{
                    builder.append("B");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public Iterator<Case> iterator() {
        return new Iterator<Case>() {

            private int line = 0;

            private int column = 0;

            @Override
            public boolean hasNext() {
                return line < HEIGHT;
            }

            @Override
            public Case next() {
                final Case aCase = cases[line][column];
                if(column < WIDTH - 1){
                    column++;
                } else {
                    column = 0;
                    line++;
                }
                return aCase;
            }
        };
    }

    /**
     * Returns the adjacent case if it is reachable (not out of the board) and it is empty, null otherwise
     * @param aCase The starting case
     * @param direction The direction to move to
     * @return The adjacent case if it is reachable and empty, null otherwise
     */
    public Case moveTo(Case aCase, Direction direction) {
        Case newCase = null;
        final int newLine = aCase.line + direction.line;
        final int newColumn = aCase.column + direction.column;
        if(newLine >= 0
                && newLine < HEIGHT
                && newColumn >= 0
                && newColumn < WIDTH
                && cases[newLine][newColumn].isEmpty()){
            newCase = cases[newLine][newColumn];
        }
        return newCase;
    }

    /**
     * Returns the opponent case and the jumped case which is two cases away
     * if it is reachable (not out of the board), it is empty
     * and the middle case is occupied by an opponent blot, null otherwise
     * @param aCase The starting case
     * @param direction The direction to move to
     * @return The opponent case and the jumped case which is two cases away if it is reachable,
     * empty and the middle is occupied by an opponent's blot, null otherwise
     */
    public Pair<Case, Case> jumpTo(Case aCase, Direction direction) {
        Pair<Case, Case> newCase = null;
        final int opponentLine = aCase.line + direction.line;
        final int opponentColumn = aCase.column + direction.column;
        final int jumpedLine = aCase.line + direction.line * 2;
        final int jumpedColumn = aCase.column + direction.column * 2;
        if(jumpedLine >= 0
                && jumpedLine < HEIGHT
                && jumpedColumn >= 0
                && jumpedColumn < WIDTH
                && cases[jumpedLine][jumpedColumn].isEmpty()
                && !aCase.isEmpty()
                && cases[opponentLine][opponentColumn].isOpponentColor(aCase.blot.color)){
            newCase = new Pair<>(cases[opponentLine][opponentColumn], cases[jumpedLine][jumpedColumn]);
        }
        return newCase;
    }

    public void playMove(Player player, Player opponent, Player.Move move) {
        switch (move.type){
            case ADD:
                move.cases[0].setBlot(player.takeBlot());
                break;
            case SLIDE:
                move.cases[1].setBlot(move.cases[0].takeBlot());
                break;
            case JUMP:
                final Case startingCase = move.cases[0];
                final Case opponentCase = move.cases[1];
                final Case jumpingCase = move.cases[2];
                final Case otherOpponentCase = move.cases[3];
                jumpingCase.setBlot(startingCase.takeBlot());
                deadBlots.add(opponentCase.takeBlot());
                if(otherOpponentCase != null){
                    deadBlots.add(otherOpponentCase.takeBlot());
                } else {
                    deadBlots.add(opponent.takeBlot());
                }
                break;
        }
    }

    public void clear() {
        for (Case aCase : this) {
            aCase.blot = null;
        }
        deadBlots.clear();
    }

    private Case getCase(Case aCase){
        return cases[aCase.line][aCase.column];
    }

    public static enum Direction {
        UP(-1, 0),
        DOWN(1, 0),
        RIGHT(0, 1),
        LEFT(0, -1);

        public final int line;

        public final int column;

        Direction(int line, int column) {
            this.column = column;
            this.line = line;
        }
    }

    public static class Blot {

        public enum BlotColor{
            @SerializedName("white")
            WHITE("white", 1),
            @SerializedName("black")
            BLACK("black", -1);

            public final String namedColor;

            public final int value;

            BlotColor(String namedColor, int value)
            {
                this.namedColor = namedColor;
                this.value = value;
            }

            public BlotColor opposite(){
                switch (this){
                    case WHITE:
                        return BLACK;
                    case BLACK:
                        return WHITE;
                    default: // SHOULD NOT HAPPEN
                        throw new IllegalStateException();
                }
            }


        }

        public Blot(BlotColor color) {
            this.color = color;
        }

        public final BlotColor color;
    }

    public static class Case {

        private Blot blot;

        public final int line;

        public final int column;

        public Case(int line, int column) {
            this.line = line;
            this.column = column;
        }

        public Blot getBlot() {
            return blot;
        }

        public void setBlot(Blot blot) {
            if(blot != null){
                this.blot = blot;
            }
        }

        public Blot takeBlot(){
            final Blot blot = this.blot;
            this.blot = null;
            return blot;
        }

        public boolean isEmpty(){
            return blot == null;
        }

        /**
         * CAREFUL, NOT THE CONTRARY OF isOpponentColor
         * @param color the color to check
         * @return true IF AND ONLY IF a blot is there and has the same color
         */
        public boolean isSameColor(Blot.BlotColor color){
            return !isEmpty() && blot.color == color;
        }

        /**
         * CAREFUL, NOT THE CONTRARY OF isSameColor
         * @param color the color to check
         * @return true IF AND ONLY IF a blot is there and has a different color
         */
        public boolean isOpponentColor(Blot.BlotColor color){
            return !isEmpty() && blot.color != color;
        }
    }

    public static final int WIDTH = 6;

    public static final int HEIGHT = 5;

    // Blots that are no longer in game
    public final Deque<Blot> deadBlots = new ArrayDeque<>();

    public final Case[][] cases = new Case[HEIGHT][WIDTH];

    public Board() {
        for (int line = 0; line < HEIGHT; line++) {
            for (int column = 0; column < WIDTH; column++) {
                cases[line][column] = new Case(line, column);
            }
        }
    }

    public JSONArray toJson() {
        final JSONArray boardJson = new JSONArray();
        for (Case[] caseRow : cases)
        {
            final JSONArray rowJson = new JSONArray();
            for (Case aCase : caseRow)
            {
                rowJson.put(aCase.blot == null ? 0 : aCase.blot.color.value);
            }
            boardJson.put(rowJson);
        }
        return boardJson;
    }
}
