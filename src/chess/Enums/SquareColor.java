package chess.Enums;

public enum SquareColor {
    LIGHT 
    ,DARK;
   
    @Override
    public String toString(){
      return ( this == SquareColor.LIGHT ) ? "□ " : "■ ";
    }
}
