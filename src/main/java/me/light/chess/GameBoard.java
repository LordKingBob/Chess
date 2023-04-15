package me.light.chess;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;

public class GameBoard extends GridPane {
    private int currrentPlayer;
    public GameBoard(Stage primaryStage, String str){
        this.currrentPlayer = 0;
      for (int y = 0; y < 8; ++y){
        for (int x = 0; x < 8; ++x){
          this.add(new GameCell((x + y) % 2 == 0 ? Color.WHITE : Color.BROWN, null), x, y);
        }
      }
      if(str.equals("single"))
          newGame();
      if(str.equals("load"))
        loadGame();
      this.takeTurn();
      primaryStage.setOnCloseRequest(event -> saveGame());
      ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
          double newVal = Math.min(primaryStage.getHeight()/8.8, primaryStage.getWidth()/8.3);
          for(Node gc : this.getChildren())
              gc.resize(newVal, newVal);
      };
      primaryStage.widthProperty().addListener(stageSizeListener);
      primaryStage.heightProperty().addListener(stageSizeListener);
    }

    public void takeTurn(){
        this.currrentPlayer = (this.currrentPlayer + 1)%2;
        this.reverseBoard();
    }

    public GameCell getSquare(int row, int column){
        return ((GameCell)this.getChildren().get(row * 8 + column));
    }

    public void loadGame(){
        try {
            BufferedReader file = new BufferedReader(new FileReader("src/main/java/me/light/chess/GameData.txt"));
            this.currrentPlayer = Integer.parseInt(file.readLine());
            int count = -1;
            for(String str : file.lines().toList()){
                for(String s : str.split("\\s+")) {
                    ++count;
                    if(s.charAt(0) == '-')
                        continue;
                    this.getSquare(count / 8, count % 8).setPiece(Piece.piece(s.charAt(0), s.charAt(1)));
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (NumberFormatException e){
            System.out.println("Failed to Load Game");
        }
    }

    public void reverseBoard(){
        for(int i = 0; i < this.getChildren().size() / 2; ++i){
            Piece temp = this.getSquare(i / 8,i % 8).getPiece();
            this.getSquare(i / 8,i % 8).setPiece(this.getSquare((63 - i) / 8,(63 - i) % 8).getPiece());
            this.getSquare((63 - i) / 8,(63 - i) % 8).setPiece(temp);
        }

    }

    public void newGame(){
        for(int c = 0; c < 8; ++c){
            char[] seq = {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'};
            this.getSquare(1, c).setPiece(new Pawn('B'));
            this.getSquare(6, c).setPiece(new Pawn('W'));
            this.getSquare(0, c).setPiece(Piece.piece('B', seq[c]));
            this.getSquare(7, c).setPiece(Piece.piece('W', seq[c]));
        }
    }

    public void saveGame() {
        try {
            BufferedWriter file = new BufferedWriter(new FileWriter("src/main/java/me/light/chess/GameData.txt"));
            file.write(this.currrentPlayer + "\n");
            for (int r = 0; r < 8; ++r) {
                String row = "";
                for (int c = 0; c < 8; ++c) {
                    Piece piece = this.getSquare(r, c).getPiece();
                    row += piece != null ? "" + piece.color + piece.piece + " " : "- ";
                }
                file.write(row + "\n");
            }
            file.flush();
            file.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}