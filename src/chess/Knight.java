package chess;

import chess.Enums.PieceColor;

public class Knight extends Piece
{
    public static int worth = 3;
    
    Knight(PieceColor color){
        this.color = color;
        name = color.equals(WHITE) ? "♘" : "♞";
        directions = new int[]{ NWW, NNW, NNE, NEE, SEE, SWW, SSE, SSW };
        moveLimit = 1;
    }
    
    @Override
    protected boolean equals(Piece p) {
        return (p instanceof Knight && p.color == this.color);
    }

}
