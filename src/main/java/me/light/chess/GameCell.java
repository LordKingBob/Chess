package me.light.chess;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GameCell extends StackPane {
  private Piece piece;
  private Color color;

  public GameCell(Color color, Piece piece) {
    this.piece = piece;
    this.color = color;
    this.setHeight(50);
    this.setWidth(50);
    Canvas canvas = new Canvas(50, 50);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(color);
    gc.fillRect(0, 0, this.getWidth(), this.getHeight());
    this.getChildren().add(canvas);
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
    if(piece != null)
      this.getChildren().add(piece.getShape());
  }

  @Override
  public void resize(double width, double height){
    super.resize(width, height);
    ((Canvas)this.getChildren().get(0)).setWidth(width);
    ((Canvas)this.getChildren().get(0)).setHeight(height);
    GraphicsContext gc = ((Canvas)this.getChildren().get(0)).getGraphicsContext2D();
    gc.setFill(color);
    gc.fillRect(0, 0, this.getWidth(), this.getHeight());
    if(this.getChildren().size() >= 2) {
      ((ImageView) this.getChildren().get(1)).setFitHeight(height);
      ((ImageView) this.getChildren().get(1)).setFitWidth(width);
    }
  }
}