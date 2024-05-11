package chess;

import chess.Enums.PieceColor;

public class Bishop extends Piece {
    Bishop(PieceColor color){
        this.color = color;
        this.value = 3;
        name = (color.equals(WHITE)) ? "♗" : "♝";
        directions = new int[]{ NE, NW, SE, SW };
        this.moveLimit = 8;
    }
    
    @Override
    protected boolean equals(Piece p) {
        return (p instanceof Bishop && p.color == this.color);
    }
}
