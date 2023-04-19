package me.light.chess;

public class Pawn extends Piece{
    public boolean hasMoved, inPassant;
    public Pawn(char color) {
        super(color);
        this.icon = 5;
        this.piece = 'P';
    }


}