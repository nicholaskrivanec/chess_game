package chess.Enums;

public enum PieceColor {
    WHITE
    ,BLACK;
    
    public char GetName(){
        return (this == WHITE) ? 'w' : (this == BLACK) ? 'b' : '-';
    }
    
    public PieceColor Opposite(){
        return (this == WHITE) ? BLACK : WHITE;
    }
    
    @Override
    public String toString(){
      switch(this){
            case WHITE: return "White"; 
            case BLACK: return "Black"; 
            default: return "-"; 
      }
    }
}
