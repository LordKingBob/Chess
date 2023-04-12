package me.light.chess;

import javafx.scene.layout.GridPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class GameBoard extends GridPane {
    public GameBoard(String str){
      for (int y = 0; y < 8; ++y){
        for (int x = 0; x < 8; ++x){
          this.add(new GameCell((x + y) % 2 == 0 ? Color.WHITE : Color.BROWN, null), x, y);
        }
      }
        this.getSquare(1,1).setPiece(new Queen("White"));
    }

    public GameCell getSquare(int row, int column){
        return ((GameCell)this.getChildren().get(row * 8 + column));
    }
}