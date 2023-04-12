package me.light.chess;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GameCell extends StackPane {
  private Piece piece;

  public GameCell(Color color, Piece piece) {
    this.piece = piece;
    this.setHeight(50);
    this.setWidth(50);
    Canvas canv = new Canvas(50, 50);
    GraphicsContext gc = canv.getGraphicsContext2D();
    gc.setFill(color);
    gc.fillRect(0, 0, this.getWidth(), this.getHeight());
    this.getChildren().add(canv);
    if(piece != null)
      this.getChildren().add(piece.getShape());
  }

  public Piece getPiece() {
    return piece;
  }

  public void setPiece(Piece piece) {
    if(this.getChildren().size() == 2)
      this.getChildren().remove(1);
    this.piece = piece;
    this.getChildren().add(piece.getShape());
  }
}