package chess;

import chess.Enums.PieceColor;

public class Rook extends Piece{
    public Rook(PieceColor color){
        this.color = color;
        value = 5;
        name = (color.equals(WHITE)) ?  "♖" : "♜";
        directions = new int[]{ N, S, E, W };
        this.moveLimit = 8;
    }

    @Override
    protected boolean equals(Piece p) {
       return (p instanceof Rook && p.color == this.color );
    }
}
