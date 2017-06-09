package com.monirapps.yote.engine;

import android.support.v4.util.Pair;

import com.monirapps.yote.engine.player.Player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Observable;

/**
 * Created by David et Monireh on 29/05/2017.
 */
public class Game extends Observable {

  public interface Turn {
    Player.Move play(Player currentPlayer, Game game);
  }

  public interface PlayMoveListener {
    void play();
  }

  private Board board = new Board();

  private Player[] players = new Player[2];

  private boolean player1Turn = true;

  private int turn = 0;

  private Deque<Player.Move> moves = new ArrayDeque<>();

  public Game(Player player1, Player player2) {
    players[0] = player1;
    players[1] = player2;
  }

  public void startGame() {
    turn = 0;
    board.clear();
    player1Turn = true;
    for (Player player : players) {
      player.init();
    }
  }

  public Player.Move lastMove() {
    return moves.peek();
  }

  public boolean isOver() {
    return hasLost(players[0]) || hasLost(players[1]) || legalMoves().size() == 0;
  }

  public boolean isBeginning() {
    return moves.size() == 0;
  }

  public Player currentPlayer() {
    return players[player1Turn ? 0 : 1];
  }

  public Player opponentPlayer() {
    return players[player1Turn ? 1 : 0];
  }

  /**
   * Lists all the legal moves that can be :
   * - add a blot to the given case
   * - move a blot from a given case to an adjacent case
   * - jump over an opponent blot, and taking another opponent's blot (one move by other blot)
   *
   * @return all legal moves for the current player
   */
  public List<Player.Move> legalMoves() {
    List<Player.Move> moves = new ArrayList<>();
    for (Board.Case aCase : board) {
      // Try to add a ADD Move
      if (aCase.isEmpty() && currentPlayer().hasNonPlayedBlots()) {
        moves.add(new Player.Move(Player.Move.MoveType.ADD, aCase));
      } else if (aCase.isSameColor(currentPlayer().color)) {
        for (Board.Direction direction : Board.Direction.values()) {
          // Try to add a SLIDE Move
          Board.Case slidingCase = board.moveTo(aCase, direction);
          if (slidingCase != null) {
            moves.add(new Player.Move(Player.Move.MoveType.SLIDE, aCase, slidingCase));
          }
          // Try to add a JUMP Move
          Pair<Board.Case, Board.Case> newCase = board.jumpTo(aCase, direction);
          if (newCase != null) {
            final Board.Case opponentCase = newCase.first;
            final Board.Case jumpedCase = newCase.second;
            for (Board.Case otherOpponentCase : board) {
              if(otherOpponentCase.isOpponentColor(currentPlayer().color) && otherOpponentCase != opponentCase){
                moves.add(new Player.Move(Player.Move.MoveType.JUMP, aCase, opponentCase, jumpedCase, otherOpponentCase));
              }
            }
            // The opponent other blot is taken from his own stack
            if (opponentPlayer().hasNonPlayedBlots()) {
              moves.add(new Player.Move(Player.Move.MoveType.JUMP, aCase, opponentCase, jumpedCase, null));
            }
          }
        }
      }
    }
    return moves;
  }

  public boolean hasLost(Player player) {
    return blotsLeft(player) == 0;
  }

  public int blotsLeft(Player player) {
    return player.blotsLeft() + blotsLeftOnBoard(player);
  }

  private int blotsLeftOnBoard(Player player) {
    int nbBlots = 0;
    for (Board.Case[] cases : board.cases) {
      for (Board.Case aCase : cases) {
        if (aCase.isSameColor(player.color)) {
          nbBlots++;
        }
      }
    }
    return nbBlots;
  }

  public void play(Turn turn) {
    this.turn++;
    moves.push(turn.play(currentPlayer(), this));
    board.playMove(currentPlayer(), opponentPlayer(), moves.peek());
    player1Turn = !player1Turn;
    setChanged();
    notifyObservers(getBoardJSString());
  }

  public void play(Player.Move move) {
    this.turn++;
    moves.push(move);
    board.playMove(currentPlayer(), opponentPlayer(), moves.peek());
    player1Turn = !player1Turn;
    setChanged();
    notifyObservers(getBoardJSString());
  }

  public void undo() {
    turn--;
    Player.Move lastMove = moves.poll();
    player1Turn = !player1Turn;
    board.unplayMove(currentPlayer(), opponentPlayer(), lastMove);
    setChanged();
    notifyObservers(getBoardJSString());
  }

  public int getTurn() {
    return turn;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(String.format("### TURN %d ###\n\n", turn));
    builder.append(board.toString());
    builder.append("\n");
    if (moves.size() != 0) {
      builder.append(String.format("Player %d plays : %s\n", player1Turn ? 2 : 1, moves.peek().toString()));
    }
    if (isOver()) {
      builder.append(String.format("Player %d has won", hasLost(players[0]) ? 2 : 1));
    } else {
      builder.append(String.format("Player 1 has %d blots left\nPlayer 2 has %d blots left", players[0].blotsLeft() + blotsLeftOnBoard(players[0]), players[1].blotsLeft() + blotsLeftOnBoard(players[1])));
    }
    builder.append("\n");
    return builder.toString();
  }

  public String getBoardJSString() {
    return board.toString().replaceAll("\n", "<br>");
  }
}
