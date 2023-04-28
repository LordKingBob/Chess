package me.light.chess;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;

public class GameBoard extends GridPane {
    private int currentTurn;

    private GameCell selectedPiece;
    public ArrayList<GameCell> blackPieces = new ArrayList<>();
    public ArrayList<GameCell> whitePieces = new ArrayList<>();
    private ArrayList<GameCell> moves;

    private boolean lockPieceCheck;
    public Pair<GameCell, Character> passantPawn;

    public GameBoard(Stage primaryStage, String str){
        this.currentTurn = 0;
      for (int r = 0; r < 8; ++r){
        for (int c = 0; c < 8; ++c){
          this.add(new GameCell(this, (r + c) % 2 == 0 ? Color.WHITE : Color.BROWN, null, r, c, false), c, r);
        }
      }
      if(str.equals("new_game"))
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
                if(!hasMoves()) {
                    Dialog win = new Dialog();
                    Image image = new Image(GameBoard.class.getResourceAsStream("/me/light/chess/Crown.jpg"), 50, 50, true, true);
                    // Citation: Crown picture by Piotr Siedlecki https://www.publicdomainpictures.net/en/view-image.php?image=118566&picture=beautiful-royal-crown
                    ImageView imageView = new ImageView(image);
                    win.setTitle("Congratulations " + (getTurn() == 'W' ? "Black" : "White"));
                    win.setContentText("Congratulations " + (getTurn() == 'W' ? "Black" : "White") + " You won in "
                            + (currentTurn - 1) + " moves!");
                    win.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                    win.setGraphic(imageView);
                    win.showAndWait();
                }
            }
        }
    }

    public boolean hasMoves(){
        for(int j = 0; j < (getTurn() == 'W' ? whitePieces : blackPieces).size(); ++j) {
            if(getMovesForPiece((getTurn() == 'W' ? whitePieces : blackPieces).get(j)).size() > 0)
                return true;
        }
        return false;
    }


    public ArrayList<GameCell> getMoves(char color){
        ArrayList<GameCell> cells = new ArrayList<>();
        for(int j = 0; j < (color == 'W' ? whitePieces : blackPieces).size(); ++j) {
           cells.addAll(getMovesForPiece((color == 'W' ? whitePieces : blackPieces).get(j)));
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
        Piece p1 = cell1.getPiece();
        Piece p2 = cell2.getPiece();
        cell1.setPiece(null);
        cell2.setPiece(p1);
        lockPieceCheck = true;
        reverseBoard();
        boolean inCheck = inCheck();
        reverseBoard();
        lockPieceCheck = false;
        cell1.setPiece(p1);
        cell2.setPiece(p2);
        return inCheck;
    }

    public ArrayList<GameCell> getMovesForPiece(GameCell cell){
        ArrayList<GameCell> cells = new ArrayList<>();
        Piece piece = cell.getPiece();
        if (piece.piece == 'P') {
            Pawn pawn = (Pawn)piece;
            if(checkIfValid(cell, cell.r - 1, cell.c) && this.getSquare(cell.r - 1, cell.c).getPiece() == null) {
                cells.add(this.getSquare(cell.r - 1, cell.c));
                if (!pawn.hasMoved && checkIfValid(cell, cell.r - 2, cell.c) && this.getSquare(cell.r - 2, cell.c).getPiece() == null)
                    cells.add(this.getSquare(cell.r - 2, cell.c));
            }
            if(checkIfValid(cell, cell.r - 1, cell.c + 1) &&
                    (this.getSquare(cell.r - 1, cell.c + 1).getPiece() != null || passantPawn != null && passantPawn.getKey() == this.getSquare(cell.r, cell.c + 1)))
                cells.add(this.getSquare(cell.r - 1, cell.c + 1));
            if(checkIfValid(cell, cell.r - 1, cell.c - 1) &&
                    (this.getSquare(cell.r - 1, cell.c - 1).getPiece() != null || passantPawn != null && passantPawn.getKey() == this.getSquare(cell.r, cell.c - 1)))
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
                    cells.add(this.getSquare(cell.r + x[i], cell.c + y[i]));
                }
            }
        } else if (piece.piece == 'R') {
            for(int x = -1; x <= 1;x += 2){
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
            if(((King)piece).canCastle){
                for(int j = 0; j < (getTurn() == 'W' ? whitePieces : blackPieces).size(); ++j) {
                    GameCell c = (getTurn() == 'W' ? whitePieces : blackPieces).get(j);
                    if (c.getPiece().piece == 'R' && ((Rook) c.getPiece()).canCastle && !lockPieceCheck) {
                        boolean empty = true;
                        int modifier = c.c == 0 ? -1 : 1;
                        for (int i = cell.c + modifier; i > 0 && i < 7; i += modifier) {
                            if (this.getSquare(cell.r, i).getPiece() == null) {
                                empty = simulateMoveForCheck(cell, this.getSquare(cell.c, i));
                            } else {
                                empty = false;
                                break;
                            }
                        }
                        if (empty)
                            cells.add(this.getSquare(cell.r, (int) (((double) cell.c + c.c) / 2 + .5)));
                    }
                }
            }
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
        if(passantPawn != null && !lockPieceCheck && passantPawn.getValue() == getTurn())
            passantPawn = null;
        if(p2.getPiece().piece == 'P') {
            ((Pawn) p2.getPiece()).hasMoved = true;
            if(p1.r - p2.r == 2) {
                passantPawn = new Pair<>(getSquare(7 - p2.r, 7 - p2.c), getTurn());
            }
            if(checkIfValid(p2, p2.r + 1, p2.c) && Math.abs(p1.c - p2.c) == 1 && passantPawn.getKey() == getSquare(p2.r + 1, p2.c))
                passantPawn.getKey().setPiece(null);
            if(p2.r == 0){
                Dialog promote = new Dialog();
                promote.initStyle(StageStyle.UNDECORATED);
                promote.getDialogPane().setPrefWidth(0);
                HBox hBox = new HBox();
                hBox.setMaxWidth(220);
                Piece[] pieces = {new Rook(getTurn()), new Knight(getTurn()), new Bishop(getTurn()), new Queen(getTurn())};
                for(Piece piece : pieces) {
                    Button button = new Button(null, new GameCell(this, Color.BROWN, piece, 0, 0, true));
                    button.setOnMouseClicked(e -> {
                        p2.setPiece(((GameCell)button.getGraphic()).getPiece());
                        promote.setResult(Boolean.TRUE);
                        promote.close();
                    });
                    hBox.getChildren().add(button);
                }
                promote.setGraphic(hBox);
                promote.showAndWait();
            }
        }
        if(p2.getPiece().piece == 'K'){
            ((King)p2.getPiece()).canCastle = false;
            if(p2.c - p1.c > 1)
                movePiece(getSquare(p2.r, 7), getSquare(p2.r, p2.c - 1));
            else if (p2.c - p1.c < -1)
                movePiece(getSquare(p2.r, 0), getSquare(p2.r, p2.c + 1));
        }
        if(p2.getPiece().piece == 'R')
            ((Rook)p2.getPiece()).canCastle = false;
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