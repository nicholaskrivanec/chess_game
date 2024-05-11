package chess;

import chess.Enums.Dir;
import chess.Enums.PieceColor;

public abstract class Piece extends Dir {
    protected static final PieceColor WHITE = PieceColor.WHITE;
    protected static final PieceColor BLACK = PieceColor.BLACK;
    protected PieceColor color;
    protected String name;
    protected int[] directions; 
    protected int moveLimit; // square range for one move.
    protected int moveCount = 0;
    protected Square square = null;
    protected int value;
    
    protected abstract boolean equals(Piece piece);
    
    public void setColor(PieceColor color){
        this.color = color;
    }
        
    public PieceColor getColor(){
        return this.color;
    }
    
    public void setSquare(Square square){
        this.square = square;
    }
    
    public Square getSquare(){
        return this.square;
    }
    
    public void capture(){
        this.square = null;
    }
    
    public int[] getMovementType(){
        return this.directions;
    }
    
    public int[] getAttackDirections(){
        return this.directions;
    }
    
    public int getLimit(){
        return this.moveLimit;
    }
    
    public void setLimit(int limit){
        this.moveLimit = limit;
    }
    
    public void incrementMoveCount(){
        moveCount++;
    }
    
    public void decrementMoveCount(){
        moveCount--;
    }
    
    public int getMoveCount(){
        return this.moveCount;
    }
    
    @Override
    public String toString(){
        return this.name;
    }
}
