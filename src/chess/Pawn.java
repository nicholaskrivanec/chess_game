package chess;

import chess.Enums.PieceColor;

public class Pawn extends Piece {
    public static int worth = 1;
    private boolean passing;
    private boolean promoting;
    
    Pawn(PieceColor color){
       this.color = color;
       if( color.equals(WHITE) ){
           name="♙";
           directions = new int[]{ N, NE, NW };
       } else {
           name = "♟";
           directions = new int[]{ S, SE, SW };
       }
       passing = false;
       moveLimit = 2;
    }
    
    @Override
    public int[] getAttackDirections(){
        return new int[]{directions[1], directions[2]};
    }
    
    public void setPromoting(){
        promoting = true;
    }
    
    public boolean promoting(){
        return this.promoting;
    }
    
    public void setPassing(){
        passing = true;
    }
    
    public boolean passing(){
        return this.passing;
    }
    
    public void setPassed(){
        passing = false;
    }
    
    @Override
    public int getLimit(){
        return 1;
    }
    
    @Override
    protected boolean equals(Piece piece) {
        return (piece instanceof Pawn && piece.getColor() == this.color);
    }

}
