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
  public final int r, c;

  private GameBoard board;

  public GameCell(GameBoard board, Color color, Piece piece, int r, int c) {
    this.piece = piece;
    this.color = color;
    this.r = r;
    this.c = c;
    this.setHeight(50);
    this.setWidth(50);
    this.board = board;
    Canvas canvas = new Canvas(50, 50);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(color);
    gc.fillRect(0, 0, this.getWidth(), this.getHeight());
    this.getChildren().add(canvas);
    if(piece != null)
      this.getChildren().add(piece.getShape());
    this.setOnMouseClicked(e -> { board.takeTurn(this); });
  }

  public Piece getPiece() {
    return piece;
  }

  public void setPiece(Piece piece) {
    if(this.piece != null) {
      if(this.piece.color == 'W')
        board.whitePieces.remove(this);
      else
        board.blackPieces.remove(this);
    }
    if(this.getChildren().size() >= 2)
      this.getChildren().remove(1);
    this.piece = piece;
    if(this.piece != null) {
//      Node node = null;
//      if(this.getChildren().size() == 2) {
//        node = this.getChildren().get(1);
//        this.getChildren().remove(1);
//      }
      this.getChildren().add(this.piece.getShape());
//      if(node != null)
//        this.getChildren().add(node);
      if(this.piece.color == 'W')
        board.whitePieces.add(this);
      else
        board.blackPieces.add(this);

    }
  }

  public void highlightCell(){
    if(this.getChildren().size() == 2){
      Canvas canvas = new Canvas(this.getHeight(), this.getWidth());
      GraphicsContext gc = canvas.getGraphicsContext2D();
      gc.setLineWidth(5);
      gc.strokeRect(0, 0, this.getHeight()-2, this.getWidth()-2);
      this.getChildren().add(canvas);
    } else {
      this.getChildren().remove(2);
    }
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