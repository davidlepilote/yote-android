package com.monirapps.yote.engine.player;

import android.graphics.drawable.GradientDrawable;

import com.monirapps.yote.engine.Board;
import com.monirapps.yote.engine.Game;

import java.util.List;
import java.util.Random;

/**
 * Created by David et Monireh on 06/06/2017.
 */

public class MinimaxPlayer extends Player {

  private class EvaluatedMove {
    public Move move;
    public double eval;

    public EvaluatedMove(Move move, double eval) {
      this.move = move;
      this.eval = eval;
    }

    @Override
    public String toString() {
      return "EvaluatedMove{" +
          "move=" + move +
          ", eval=" + eval +
          '}';
    }
  }

  private static final double BIG_NUMBER = 1_000_000_000.0;

  private int depth;

  public MinimaxPlayer(Board.Blot.BlotColor color) {
    super(color);
    init();
  }

  @Override
  public void init() {
    super.init();
    depth = 3;
  }

  @Override
  public Move play(Game game) {
    return playMinimax(game, depth).move;
  }

  private Random random = new Random();

  private EvaluatedMove playMinimax(Game game, int depth) {

    EvaluatedMove bestMove = new EvaluatedMove(null, -BIG_NUMBER + (random.nextDouble() / 1000.));

    final List<Move> moves = game.legalMoves();

    for (Move currentMove : moves) {
      game.play(currentMove);
      double moveEval = minimax(game, depth - 1, -BIG_NUMBER, BIG_NUMBER, false);
      game.undo();
      if(moveEval > bestMove.eval) {
        bestMove.move = currentMove;
        bestMove.eval = moveEval;
      }
    }

    return bestMove;
  }

  private double minimax(Game game, int depth, double alpha, double beta, boolean isCurrentPlayerTurn) {
    if (depth == 0) {
      return evaluate(game, isCurrentPlayerTurn);
    }

    double bestMove = (isCurrentPlayerTurn ? -1 : 1) * BIG_NUMBER;

    final List<Move> moves = game.legalMoves();

    for (Move currentMove : moves) {
      game.play(currentMove);
      double currentEvalMove = minimax(game, depth - 1, alpha, beta, !isCurrentPlayerTurn);
      game.undo();
      if (isCurrentPlayerTurn) {
        bestMove = Math.max(bestMove, currentEvalMove);
        alpha = Math.max(alpha, bestMove);
        if (beta <= alpha) {
          return bestMove;
        }
      } else {
        bestMove = Math.min(bestMove, currentEvalMove);
        beta = Math.min(beta, bestMove);
        if (beta <= alpha) {
          return bestMove;
        }
      }
    }
    return bestMove;

  }

  private double evaluate(Game game, boolean isCurrentPlayerTurn) {
    return (isCurrentPlayerTurn ? 1.0 : -1.0) * (game.blotsLeft(game.currentPlayer()) - (game.blotsLeft(game.opponentPlayer())));
  }
}
