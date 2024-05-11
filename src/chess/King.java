package chess;

import chess.Enums.PieceColor;

public class King extends Piece {
    King(PieceColor color){
        this.color = color;
        this.moveLimit = 1;
        this.value = 0;
        this.name = (color.equals(WHITE)) ? "♔" : "♚";
        this.directions = new int[] { N, S, E, W, NE, NW, SE, SW };
    }
    
    @Override
    protected boolean equals(Piece p) {
        return (p instanceof King && p.color == this.color);
    }
}
