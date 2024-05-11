package chess;

import chess.Enums.SquareColor;

public final class Square 
{
    private int rank , file;
    private final char[] name = new char[2];
    private Piece piece;
    private SquareColor color;

    public static final String[] LIGHT_SQS = new String[]{
        "a8",   "c8",   "e8",   "g8",
        "b7",   "d7",   "f7",   "h7",
        "a6",   "c6",   "e6",   "g6",
        "b5",   "d5",   "f5",   "h5",
        "a4",   "c4",   "e4",   "g4",
        "b3",   "d3",   "f3",   "h3",
        "a2",   "c2",   "e2",   "g2",
        "b1",   "d1",   "f1",   "h1"
    };
    
    public static final String[] DARK_SQS = new String[]{
        "b8",   "d8",   "f8",   "h8",
        "a7",   "c7",   "e7",   "g7",
        "b6",   "d6",   "f6",   "h6",
        "a5",   "c5",   "e5",   "g5",
        "b4",   "d4",   "f4",   "h4",
        "a3",   "c3",   "e3",   "g3",
        "b2",   "d2",   "f2",   "h2",
        "a1",   "c1",   "e1",   "g1",
    };
    
    public static final char[] FILE = {'a','b','c','d','e','f','g','h'};
    public static final char[] RANK = {'8','7','6','5','4','3','2','1'};
    public static final int[] EN_PASSANT_RANK = {3, 4};
    public static final int[] PROMOTION_RANKS = {0, 7};
    
    public Square(int file, int rank){
        this.file = file;
        this.rank = rank;
        name[0] = FILE[file];
        name[1] = RANK[rank];
        this.piece = null;
    };
    
    public Square(){};
    
    public Square(String str){
        file = getFile(str.charAt(0));
        rank = getRank(str.charAt(1));
        color = getColor();
    }
    
    public SquareColor getColor(){ 
        if ((file + rank) % 2 == 0)
            return SquareColor.LIGHT;
        return SquareColor.DARK;
    }
    
    public static int getRank(char r){
        return Integer.parseInt("-" + r) + 8;
    }
    
    public static int getFile( char f ){
        return f - 'a';
    }
    
    public int getRank(){
        return this.rank;
    }
    
    public int getFile(){
        return this.file;
    }
    
    public void setPiece(Piece piece){
        this.piece = piece;
    }
    
    public Piece getPiece(){
        return this.piece;
    }
    
    public boolean isEmpty(){
        return( this.piece == null );
    }
    
    public void clear(){
        this.piece = null;
    }
    
    public static boolean isValidIndex(int index){
        return (index > 0 && index < 7);
    }
    
    public static int getRankName(int index){
        return 8 - index;
    }
    
    public static char getFileName(int r){
        return (char)('a' + r );
    }
    
    @Override
    public String toString(){
      String str = ""+ name[0] + name[1];
      return str;
    }
}
