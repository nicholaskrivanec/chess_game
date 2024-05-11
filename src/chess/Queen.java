package chess;

import chess.Enums.PieceColor;

public class Queen extends Piece{
    Queen(PieceColor color){
        this.color = color;
        this.name = ( color.equals(WHITE)) ? "♕" : "♛";
        directions = new int[] { N, S, E, W, NE, NW, SE, SW };
        this.value = 9;
        this.moveLimit = 8;
    }

    @Override
    protected boolean equals(Piece p) {
        return (p instanceof Queen && p.color == this.color);
    }
}
