package me.light.chess;

public class Rook extends Piece{

    public boolean canCastle = true;
    public Rook(char color) {
        super(color);
        this.icon = 4;
        this.piece = 'R';
    }
}