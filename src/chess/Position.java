package chess;

public class Position 
{
    public static final String START_POS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    
    private String move;
    private String piecePlacement;
    private char activeColor;
    private String castling;
    private String enPassant;
    private int halfMoveClock;
    private int fullMoveClock;
    
    public Position(){
        piecePlacement = START_POS;
        activeColor = 'w';
        castling = "KQkq";
        enPassant = "-";
        halfMoveClock = 0;
        fullMoveClock = 0;
        move = "-";
    }
    
    /**
     * @param fen
     * @param move 
     */
    public Position(String fen, String move){
        String[] arr = fen.split("\\s");
        piecePlacement = arr[0].trim();
        activeColor = arr[1].trim().charAt(0);
        castling = arr[2].trim();
        enPassant = "-";
        halfMoveClock = Integer.parseInt(arr[3]);
        fullMoveClock = Integer.parseInt(arr[4]);
        this.move = move;
    }
    
    /**
     * 
     * @param piecePlacement
     * @param activeColor
     * @param castling
     * @param enPassant
     * @param halfMoveClock
     * @param fullMoveClock 
     * @param move 
     */
    public Position(String piecePlacement, char activeColor, String castling, String enPassant, int halfMoveClock, int fullMoveClock,  String move){
        this.piecePlacement = piecePlacement;
        this.activeColor = activeColor;
        this.castling = castling;
        this.enPassant = enPassant;
        this.halfMoveClock = halfMoveClock;
        this.fullMoveClock = fullMoveClock;
        this.move = move;
    }
    
    public void setPlacement(String placement){
        String str = placement;
        if(!placement.contains("-")){
        } else {
            while(str.contains("-")){
                boolean counting = false;
                int startIndex = 0;
                int count = 0;
                for(int i = 0; i < str.length(); i++){
                    if(str.charAt(i) == '-' && !counting){
                        counting = true;
                        count++;
                        startIndex = i;
                    }else if(counting && str.charAt(i) != '-'){
                        String firstPart = (i == 0)? "" : str.substring(0, startIndex);
                        String middlePart = ""+count;
                        String lastPart = str.substring(i);
                        str = firstPart + middlePart + lastPart;
                        count = 0;
                        break;
                    }else if(counting && str.charAt(i) == '-'){
                        count++;
                    }if (counting && i == str.length()-1 ){
                        String firstPart = str.substring(0, startIndex);
                        String lastPart = ""+count;
                        str = firstPart + lastPart;
                    }
                }
            }
        }
        this.piecePlacement = str;
    }
    
    
    
    public void setActiveColor(char color){
        this.activeColor = color;
    }
    
    public void setCastling(){
        this.castling = "KQkq";
    }
    
    public void setCastling(String castle){
        this.castling = castle;
    }
    
    public void setEnpassant(){
        this.enPassant = "-";
    }
    
    public void setEnpassant(String enPassant){
        this.enPassant = enPassant;
    }
    
    public void setHalfCounter(int i){
        this.halfMoveClock = i;
    }
    
    public void setFullCounter(int i){
        this.fullMoveClock = i;
    }
    
    
    public void setMove(String from, String to){
        this.move = from+to;
    }
    
    public void setMove(String move){
        this.move = move;
    }
    
    public void setMove(){
        this.move = "-";
    }
    
    public String getPlacement(){
        return this.piecePlacement;
    }
    
    public String getPlacementExtended(){
        String str = piecePlacement;
//        System.out.println(piecePlacement);
        
//        for(int i = 0; i < piecePlacement.length(); i++){
//            if(Character.isDigit(piecePlacement.charAt(i))){
//                int num = Character.getNumericValue(piecePlacement.charAt(i));
//                int index = str.length() - piecePlacement.length() + i;
//                String firstPart = (i == 0)? "" : str.substring(0,index);
//                String lastPart = (i == piecePlacement.length()-1) ? "": str.substring(index+1);
//                String dashes = "";
//                for(int j = 0; j < num; j++){
//                    dashes += "-";
//                }
//                str = firstPart + dashes + lastPart;
//            }
//        }        
//        System.out.println(str);
        return str;
    }
    
    public String getFen(){
        String str = "";
        str += piecePlacement + " ";
        str += activeColor + " ";
        str += castling + " ";
        str += enPassant + " ";
        str += halfMoveClock + " ";
        str += fullMoveClock + " ";
        return str;
    }
    
    public char getColor(){
        return this.activeColor;
    }
    
    public String getCastling(){
        return this.castling;
    }
    
    public String getEnPassant(){
        return this.enPassant;
    }
    
    public int getHalfClock(){
        return this.halfMoveClock;
    }
    
    public int getFullClock(){
        return this.fullMoveClock;
    }
    
    public String getMove(){
        return this.move;
    }
    
    public boolean isCheck(){
        return move.contains("+");
    }
    
    public boolean isCheckmate(){
        return move.contains("#");
    }
    
    public boolean isStalemate(){
        return move.contains("p");
    }
    
    public boolean isPromotion(){
        return move.contains("=");
    }
    
    @Override
    public String toString(){
        String str = "";
        str += piecePlacement + " ";
        str += activeColor + " ";
        str += castling + " " ;
        str += enPassant + " ";
        str += halfMoveClock + " ";
        str += fullMoveClock + " ";
        str += move + " ";
      
        return str;
    }
    
    public boolean equals(Position pos){
        return this.toString().equals(pos.toString());
    }
}
