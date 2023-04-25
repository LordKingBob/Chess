package me.light.chess;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class GameBoard extends GridPane {
    private int currentTurn;

    private GameCell selectedPiece;
    public ArrayList<GameCell> blackPieces = new ArrayList<>();
    public ArrayList<GameCell> whitePieces = new ArrayList<>();
    private ArrayList<GameCell> moves;

    private boolean lockPieceCheck;

    public GameBoard(Stage primaryStage, String str){
        this.currentTurn = 0;
      for (int r = 0; r < 8; ++r){
        for (int c = 0; c < 8; ++c){
          this.add(new GameCell(this, (r + c) % 2 == 0 ? Color.WHITE : Color.BROWN, null, r, c), c, r);
        }
      }
      if(str.equals("single"))
          newGame();
      if(str.equals("load"))
        loadGame();
      primaryStage.setOnCloseRequest(event -> saveGame());
      ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
          double newVal = Math.min(primaryStage.getHeight()/8.8, primaryStage.getWidth()/8.3);
          for(Node gc : this.getChildren())
              gc.resize(newVal, newVal);
      };
      primaryStage.widthProperty().addListener(stageSizeListener);
      primaryStage.heightProperty().addListener(stageSizeListener);

    }

    public void takeTurn(GameCell cell){
        if(cell.getPiece() != null && (getTurn() == 'W' ? 'W' : 'B') == cell.getPiece().color) {
            if(selectedPiece == null){
                moves = getMovesForPiece(cell);
                System.out.println(moves.size());
                if (moves.size() > 0) {
                    selectedPiece = cell;
                    cell.highlightCell();
                }
            } else if (selectedPiece == cell) {
                selectedPiece = null;
                cell.highlightCell();
            }


        }
        else if(selectedPiece != null){
            if(moves.contains(cell)) {
                selectedPiece.highlightCell();
                movePiece(selectedPiece, cell);
                selectedPiece = null;
                // Transfers turn to next player
            this.currentTurn += 1;
            this.reverseBoard();
            }
        }
    }

    public ArrayList<GameCell> getMoves(char color){
        ArrayList<GameCell> cells = new ArrayList<>();
        for(GameCell cell : color == 'W' ? whitePieces : blackPieces){
           cells.addAll(getMovesForPiece(cell));
        }
        return cells;
    }

    public char getTurn(){
        return this.currentTurn % 2 == 0 ? 'W' : 'B';
    }

    public boolean inCheck(){
        ArrayList<GameCell> cells = getMoves(getTurn() == 'W' ? 'B' : 'W');
        for(GameCell cell : getTurn() == 'W' ? whitePieces : blackPieces){
            if(cell.getPiece().piece == 'K'){
                if(cells.contains(cell))
                    return true;
            }
        }
        return false;
    }

    public boolean simulateMoveForCheck(GameCell cell1, GameCell cell2){
        System.out.println(cell1.getPiece() + " " + cell2.getPiece());
        Piece p1 = cell1.getPiece();
        Piece p2 = cell2.getPiece();
        lockPieceCheck = true;
        cell1.setPiece(p2);
        cell2.setPiece(p1);
        reverseBoard();
        boolean inCheck = inCheck();
        reverseBoard();
        cell1.setPiece(p1);
        cell2.setPiece(p2);
        lockPieceCheck = false;
        return inCheck;
    }

    public ArrayList<GameCell> getMovesForPiece(GameCell cell){
        ArrayList<GameCell> cells = new ArrayList<>();
        Piece piece = cell.getPiece();
        if (piece.piece == 'P') {
            Pawn pawn = (Pawn)piece;
            if(checkIfValid(cell, cell.r - 1, cell.c) && this.getSquare(cell.r - 1, cell.c).getPiece() == null) {
                cells.add(this.getSquare(cell.r - 1, cell.c));
                if (!pawn.hasMoved && this.getSquare(cell.r - 2, cell.c).getPiece() == null)
                    cells.add(this.getSquare(cell.r - 2, cell.c));
            }
            if(checkIfValid(cell, cell.r - 1, cell.c + 1) && this.getSquare(cell.r - 1, cell.c + 1).getPiece() != null)
                cells.add(this.getSquare(cell.r - 1, cell.c + 1));
            if(checkIfValid(cell, cell.r - 1, cell.c - 1) && this.getSquare(cell.r - 1, cell.c - 1).getPiece() != null)
                cells.add(this.getSquare(cell.r - 1, cell.c - 1));
        } else if (piece.piece == 'Q') {
            for(int x = -1; x<=1;x+= 2) {
                for (int y = -1; y <= 1; y += 2)
                    cells.addAll(linearMoves(cell, x, y));
                cells.addAll(linearMoves(cell, x, 0));
                cells.addAll(linearMoves(cell, 0, x));
            }
        } else if (piece.piece == 'N') {
            int[] x = {-2, -2, -1, -1, 1, 1, 2, 2};
            int[] y = {1, -1, 2, -2, 2, -2, 1, -1};
            for(int i = 0; i < 8; ++i){
                if(checkIfValid(cell, cell.r + x[i], cell.c + y[i])) {
                    cells.add(this.getSquare(cell.c + y[i], cell.r + x[i]));
                }
            }
        } else if (piece.piece == 'R') {
            for(int x = -1; x<=1;x+= 2){
                cells.addAll(linearMoves(cell, x, 0));
                cells.addAll(linearMoves(cell, 0, x));
            }
        } else if (piece.piece == 'B') {
            for(int x = -1; x<=1;x+= 2)
                for(int y = -1; y<=1; y+= 2)
                    cells.addAll(linearMoves(cell, x, y));
        } else if (piece.piece == 'K') {
            for(int x = -1; x <= 1; ++x)
                for(int y = -1; y <= 1; ++y)
                    if(!(x == y && y == 0) && checkIfValid(cell, cell.r + x, cell.c + y))
                        cells.add(this.getSquare(cell.r + x, cell.c + y));
        }

        cells.removeIf(c -> !lockPieceCheck && simulateMoveForCheck(cell, c));
        return cells;
    }

    public ArrayList<GameCell> linearMoves(GameCell cell, int x, int y){
        ArrayList<GameCell> gameCell = new ArrayList<>();
        int r = cell.r; int c = cell.c;
        while (checkIfValid(cell, r+=x, c+=y)){
            gameCell.add(this.getSquare(r, c));
            if(this.getSquare(r, c).getPiece() != null)
                break;
        }
        return gameCell;
    }

    public boolean checkIfValid(GameCell cell, int r, int c){
        if(!(r< 8 && r >= 0 && c < 8 && c >= 0))
            return false;
        GameCell other = this.getSquare(r, c);
        boolean okSquare = true;
        if(other.getPiece() != null)
            okSquare = other.getPiece().color != cell.getPiece().color;
        return okSquare;
    }

    public void movePiece(GameCell p1, GameCell p2){
        p2.setPiece(p1.getPiece());
        p1.setPiece(null);
    }

    public GameCell getSquare(int row, int column){
        return ((GameCell)this.getChildren().get(row * 8 + column));
    }

    public void loadGame(){
        try {
            BufferedReader file = new BufferedReader(new FileReader("src/main/java/me/light/chess/GameData.txt"));
            this.currentTurn = Integer.parseInt(file.readLine());
            int count = -1;
            for(String str : file.lines().toList()){
                for(String s : str.split("\\s+")) {
                    ++count;
                    if(s.charAt(0) == '-')
                        continue;
                    GameCell cell = this.getSquare(count / 8, count % 8);
                    cell.setPiece(Piece.piece(s.charAt(0), s.charAt(1)));
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
            file.write(this.currentTurn + "\n");
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