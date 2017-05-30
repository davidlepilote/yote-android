package com.monirapps.yote.engine;

import com.monirapps.yote.engine.player.Player;

import java.util.Observable;

/**
 * Created by David et Monireh on 29/05/2017.
 */
public class Game extends Observable {

  public interface Turn {
    Player.Move play(Player currentPlayer, Board board, boolean opponentHasNonPlayedBlots);
  }

  public interface PlayMoveListener {
    void play();
  }

  private Board board = new Board();

  private Player[] players = new Player[2];

  private boolean player1Turn = true;

  private int turn = 0;

  private Player.Move currentMove;

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

  public boolean isOver() {
    return players[0].hasLost(board) || players[1].hasLost(board);
  }

  public Player currentPlayer() {
    return players[player1Turn ? 0 : 1];
  }

  public Player opponentPlayer() {
    return players[player1Turn ? 1 : 0];
  }

  public void play(Turn turn) {
    currentMove = turn.play(currentPlayer(), board, opponentPlayer().hasNonPlayedBlots());
    board.playMove(currentPlayer(), opponentPlayer(), currentMove);
    player1Turn = !player1Turn;
    setChanged();
    notifyObservers(getBoardJSString());
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(String.format("### TURN %d ###\n\n", turn++));
    builder.append(board.toString());
    builder.append("\n");
    if (currentMove != null) {
      builder.append(String.format("Player %d plays : %s\n", player1Turn ? 2 : 1, currentMove.toString()));
    }
    if (isOver()) {
      builder.append(String.format("Player %d has won", players[0].hasLost(board) ? 2 : 1));
    } else {
      builder.append(String.format("Player 1 has %d blots left\nPlayer 2 has %d blots left", players[0].blotsLeft(board), players[1].blotsLeft(board)));
    }
    builder.append("\n");
    return builder.toString();
  }

  public String getBoardJSString() {
    return board.toString().replaceAll("\n", "<br>");
  }
}
