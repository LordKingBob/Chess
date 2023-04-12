package me.light.chess;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Piece {
    public int piece;
    public String color;
    public Piece(String color){
        this.color = color;
    }

    public ImageView getShape(){
        Image base = new Image(King.class.getResourceAsStream("/me/light/chess/Chess_Pieces_Sprite.png"));
        ImageView img = new ImageView(base);
        int x = color.equals("Black") ? 45 : 0;
        img.setViewport(new Rectangle2D(piece*45, x, 45, 45));
        return img;
    }
}