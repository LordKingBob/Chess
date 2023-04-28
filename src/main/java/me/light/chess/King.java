package me.light.chess;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class King extends Piece{

    public boolean canCastle = true;
    public King(char color) {
        super(color);
        this.icon = 0;
        this.piece = 'K';
    }


}