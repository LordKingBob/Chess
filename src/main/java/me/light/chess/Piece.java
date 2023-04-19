package me.light.chess;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

public class Piece {
    public int icon;
    public char piece;
    public final char color;
    private int x, y;

    public Piece(char color){
        this.color = color;
        this.piece = '-';

    }

    public static Piece piece(char color, char piece){
        return switch (piece) {
            case 'K' -> new King(color);
            case 'Q' -> new Queen(color);
            case 'B' -> new Bishop(color);
            case 'R' -> new Rook(color);
            case 'N' -> new Knight(color);
            case 'P' -> new Pawn(color);
            default -> null;
        };
    }

    public ImageView getShape(){
        Image base = new Image(Piece.class.getResourceAsStream("/me/light/chess/Chess_Pieces_Sprite.png"));
        ImageView img = new ImageView(base);
        int x = color == 'B' ? 45 : 0;
        img.setViewport(new Rectangle2D(icon*45, x, 45, 45));
        img.setPreserveRatio(true);
        return img;
    }


    public Pair<Integer, Integer> getCoords() {
        return new Pair<>(x, y);
    }

    public void setCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }
}