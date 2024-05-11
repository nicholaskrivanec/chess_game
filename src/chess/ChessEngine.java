package chess;

import chess.Enums.PieceColor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class ChessEngine {
    public static final int DRAW = 0;
    public static final int WHITE = 1;
    public static final int BLACK = -1;
    public static final int WHITE_RESIGNS = 2;
    public static final int BLACK_RESIGNS = -2;
    public static final String FEN_URL = "https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation";
    private int fiftyMoveCounter;
    private Board b;
    private BoardAnalyzer analyzer;
    private HashMap<String, ArrayList<ArrayList<String>>> pointersHashMap;
    private HashMap<String, ArrayList<String>> validMoveList_w;
    private HashMap<String, ArrayList<String>> validMoveList_b;
    private Stack<Position> positionStack;
    private Position lastMove;
    private Position currMove;
    private StockFishExecutor fish; 
    private PieceColor cpuColor;
    
    public ChessEngine() {
        fish = new StockFishExecutor();
        try{
            fish.startEngine(); 
        } catch (Exception ex){
            System.out.println("Couldn't start fish.");
            throw ex;
        }
        positionStack = new Stack<>();
        lastMove = new Position();
        lastMove.setActiveColor('w');
        cpuColor = PieceColor.BLACK;
        positionStack.add(lastMove);
        this.validMoveList_b = new HashMap<>();
        this.validMoveList_w = new HashMap<>();
        this.pointersHashMap = new HashMap<>(); 
        fiftyMoveCounter = 0;
        b = new Board();
        b.showPieces();
        analyzer = new BoardAnalyzer(b);
    }
    
    public void newGame(String strPc){
        PieceColor pc = strPc.equals(Symbol.BLACK) ? PieceColor.BLACK : PieceColor.WHITE;
        fish.killFish();
        try{
            fish.startEngine();
        } catch(Exception ex){
            System.out.println("Couldn't start fish.");
            throw ex;
        }
        positionStack = new Stack<>();
        lastMove = new Position();
        lastMove.setActiveColor('w');
        cpuColor = pc;
        fiftyMoveCounter = 0;
        b = new Board();
        analyzer = new BoardAnalyzer(b);
        if(cpuColor == PieceColor.WHITE)
            currMove = new Position();
    }

    /**
     * Takes the last move played as a String, and amends to it any special 
     * symbols (check +, checkmate #, stalemate $,  
     * @param move
     * @return 
     */
    private String manageMoveListMove(String move) { 
        char activeColor = currMove.getColor();
        PieceColor currTurn = (activeColor=='w')? PieceColor.WHITE : PieceColor.BLACK;
        
        if(isCheck(currTurn)){
            if(inCheckMate(currTurn)){
                move += Symbol.CHECK_MATE;
                if(currTurn == PieceColor.BLACK){
                    move += Symbol.WHITE_WINS;
                } else {
                    move += Symbol.BLACK_WINS;
                }
            } else {
                move += Symbol.CHECK;
            }
        } else if (isDraw()){
            move += Symbol.DRAW_GAME;
        }
        return move;
    }
    
    private boolean enPassantPossible(Square to, PieceColor pc) {
        int fEast = to.getFile() + 1;
        int fWest = fEast - 2;
        if (Square.isValidIndex(fEast)) {
            Square sqEast = b.getSquare(fEast, to.getRank());
            if (!sqEast.isEmpty()) {
                Piece piece = sqEast.getPiece();
                if (piece instanceof Pawn && piece.getColor() != pc) {
                    return true;
                }
            }
        }
        if (Square.isValidIndex(fWest)) {
            Square sqWest = b.getSquare(fWest, to.getRank());
            if (!sqWest.isEmpty()) {
                Piece piece = sqWest.getPiece();
                if (piece instanceof Pawn && piece.getColor() != pc)
                    return true;
            }
        }
        return false;
    }

    private boolean inCheckMate(PieceColor pc) {
        Square kingSq = b.getKingSquare(pc);
        if(analyzer.getValidMoves(kingSq.toString()).isEmpty()){
            runEval();
            HashMap<String, ArrayList<String>> validMoves;
            validMoves = (pc == PieceColor.WHITE)? validMoveList_w : validMoveList_b;
            
            for(Map.Entry<String, ArrayList<String>> entry: validMoves.entrySet())
                if(validMoves.get(entry.getKey()).isEmpty() == false)
                    return false;
            
            return true;
        }
        return false;
    }
    
    private boolean isCheck(PieceColor pc){
        Square kingSq = b.getKingSquare(pc);
        ArrayList<String> arr;
        arr = analyzer.getPointers(kingSq, pc.Opposite());
        if(arr.size() > 0 ){
            System.out.println("Pieces checking the king on " + kingSq.toString() + ":");
            arr.forEach((str) -> {
                System.out.print(str + "\t");
            });
            return true;
        }
        return false;
    }
    
    /**
     *  Conditions for a Draw
     *      On a Player's turn, 
     *      their king is not in check, 
     *      and none of their pieces can legally move.
     *  OR
     *      All the pawns, queens, rooks, 
     *      and either 2 knights and a bishop or 
     *      2 bishops and a knight have been captured.
     *  OR
     *      Fifty moves have been played without a capture or pawn move.
     *  OR
     *      The same position has been reached on 3 seperate occasions AND 
     *      a player claims the draw}
     * @return 
     */
    private boolean isDraw() {
        //one move happens after each player takes a turn moving, therefore when the counter = 100
        if (fiftyMoveCounter == 100) {
            System.out.println("50 moves since a piece has been taken, or a pawn has advanced. :_(");
            return true;
        }
        return false;
    }
    
    private boolean positionHasBeenReachedThreeTimes() {
        Position position = positionStack.lastElement();
        int count = 0;
        Iterator<Position> iter = positionStack.iterator();

        while (iter.hasNext()) 
            if (position.equals(iter.next()))
                if (++count == 3) 
                    return true;
        return false;
    }
    
    private boolean isDiagonallyTouching(Square sq1, Square sq2) {
        int r1 = sq1.getRank();
        int r2 = sq2.getRank();
        int f1 = sq1.getFile();
        int f2 = sq2.getFile();

        if (Math.abs(f2 - f1) == 1 && Math.abs(r2 - r1) == 1) {
            return true;
        }

        return false;
    }

    private boolean isDoubleMove(Square from, Square to) {
        int rankDiff = Math.abs(from.getRank() - to.getRank());
        if (rankDiff == 2) {
            return true;
        }
        return false;
    }

    private void runEval() {
        // make sure lists are clear
        if (!validMoveList_w.isEmpty()) {
            validMoveList_w.clear();
        }
        if (!validMoveList_b.isEmpty()) {
            validMoveList_b.clear();
        }
        if(!pointersHashMap.isEmpty()){
            pointersHashMap.clear();
        }
        ////////////////////////Maybe Thread these, First manageMoveListMove if a data collision is possible////////////////////////////////////
        //get eval lists from the analyzer
        validMoveList_w = analyzer.getValidMoveHashMap(PieceColor.WHITE);
        validMoveList_b = analyzer.getValidMoveHashMap(PieceColor.BLACK);
        pointersHashMap = analyzer.getPointersHashMap();
    }

    private boolean hasPiece(String sqName) {
        return (Board.isOnTheBoard(sqName) && !b.getSquare(sqName).isEmpty());
    }
    
    public boolean isWhitesTurn() {
        char color = lastMove.getColor();
        return (color == 'w');
    }

    public boolean isPawnPromotion(String from, String to) {
        // if from has exists, and to exists, and from has a pawn
        if (hasPiece(from) && Board.isOnTheBoard(to)) {
            Square fromSq = b.getSquare(from);
            Square toSq = b.getSquare(to);
            Piece piece = fromSq.getPiece();
            PieceColor pc = piece.getColor();
            int rankFrom = fromSq.getRank();
            int rankTo = toSq.getRank();

            //if piece is a pawn moving to the end of the board, return true
            if (piece instanceof Pawn) {
                if (pc == PieceColor.WHITE && rankFrom == 1 && rankTo == 0) {
                    return true;
                } else if (rankFrom == 6 && rankTo == 7) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean threePositions() {
        return positionHasBeenReachedThreeTimes();
    }
    
    public String getPosition() {
        return b.getPosition();
    }

    public String getStartPosition(boolean fen){
        if(fen){
            return positionStack.get(0).getPlacement();
        }else{
            return Position.START_POS;
        }
    }
    
    public String getStartPosition(){
        return getStartPosition(false);
    }
    
    public String getPosition(String i){
        try{
            return positionStack.get(Integer.parseInt(i)).getPlacement();
        }catch(NumberFormatException ex){
            System.out.println("Exception in ChessEngine.getPosition()");
            System.out.println("i = " + i + " is not a valid index");
            return positionStack.lastElement().getPlacement();
        }
    }
    
    public String getPosition(int i){
        if(!positionStack.empty()){
            int index = positionStack.size()-i;
            if (index >= 0 && index < positionStack.size()){
                return positionStack.get(index).getPlacementExtended();
            }
            else if(index > positionStack.size()){
                return positionStack.get(positionStack.size()-1).getPlacement();
            }
        }
        
        return getStartPosition();
    };
    
    public int getNumberOfMoves() {
        if (positionStack.isEmpty()) {
            return 0;
        }
        return positionStack.size();
    }

    public void promote( String toStr, String pieceType) {
        Square toSq = b.getSquare(toStr);
        
        String move = positionStack.get(positionStack.size()-1).getMove();
        lastMove.setMove(move + "=" + pieceType.toUpperCase());
        
        //promote the pawn
        b.promote( toSq, pieceType);

        //update the analyzer, moveList, and positions, change players turn
        analyzer.setBoard(b);

        //reset 50 move counter
        fiftyMoveCounter = 0;
    }

    public void undoMove() {
        //if stack is clear, return 
        if (positionStack.size() == 1) {
            lastMove = positionStack.lastElement();
            
            return;
        }
        System.out.println("Move Undone*************************************");
        //get the position from the stack, set the board, decrement 50 move counter 
        positionStack.pop();
        lastMove = positionStack.lastElement();
        b.setPieces(lastMove.getPlacement());
        analyzer.setBoard(b);
    }
    
    public String getLastMove() {
        if(positionStack.isEmpty()){
            return "";
        }
        return lastMove.getMove();
    }

    /**
     * Moves the piece from first square to the second. If a piece is on the second
     * square, or a passing pawn is taken en passant, the related piece is captured.
     * If castling, the relative rook is also moved. 
     * 
     * This method also takes care of updating game variables 
     * (e.g. moveList, positionStack...etc)
     * 
     * This method assumes the move is valid.
     * @param from
     * @param to 
     */
    public void move(String from, String to) {
        currMove = new Position();
       
        char currTurn;
        String castle = lastMove.getCastling();
        int fullMove = lastMove.getFullClock();
        int halfMove = lastMove.getHalfClock();
        
        String enPassant = lastMove.getEnPassant();
        String moveStr = "";
        
        if(lastMove.getColor() == 'w'){
            currTurn = 'b';
            fiftyMoveCounter = lastMove.getHalfClock();
        }else{
            currTurn = 'w';
            fiftyMoveCounter = lastMove.getFullClock();
        }
        
        
        Square sq1 = b.getSquare(from);
        if (sq1.isEmpty()) 
            return;
        
        Piece p1 = sq1.getPiece();
        PieceColor pc = p1.getColor();
        Square sq2 = b.getSquare(to);
        boolean uncommonMove = false;
        
        //PAWN MOVES////////////////////////////////////////////////////////////
        if (p1 instanceof Pawn) {
            Pawn pawn = (Pawn)p1;
            fiftyMoveCounter = 0; 

            //en passant 
            if (!enPassant.contains("-")) {
                Pawn passer = (Pawn)(b.getSquare(enPassant).getPiece());
                passer.setPassed();
                if (isDiagonallyTouching(sq1, sq2) && sq2.isEmpty()) {
                    b.takePassedPawn(from, to);
                    moveStr = (pawn.toString() + Symbol.CAPTURE + to);
                    uncommonMove = true;
                }
            }
            //pawn double move
            if (isDoubleMove(sq1, sq2) && enPassantPossible(sq2, pc)) {
                pawn.setPassing();
                enPassant = sq2.toString();
            } else {
                enPassant = "-";
            }   
            
        } 
        //KING MOVES////////////////////////////////////////////////////////////
        else if (p1 instanceof King) {
            if (pc == PieceColor.WHITE) {
                if (to.equals("g1") && from.equals("e1")) {
                    castle = "--" + castle.substring(2);
                    b.castleOOWhite();     
                    moveStr = (Symbol.CASTLE_KING_SIDE);
                    uncommonMove = true;
                } else if (to.equals("c1") && from.equals("e1")) {
                    castle = "--" + castle.substring(2);
                    b.castleOOOWhite();     
                    moveStr = (Symbol.CASTLE_QUEEN_SIDE);
                    uncommonMove = true;
                }
            } else if (to.equals("g8") && from.equals("e8")) {
                castle = castle.substring(0,1) + "--";
                b.castleOOBlack();
                moveStr = (Symbol.CASTLE_KING_SIDE);
                uncommonMove = true;
            } else if (to.equals("c8") && from.equals("e8")) {
                castle = castle.substring(0,1) + "--";
                b.castleOOOBlack();
                moveStr = (Symbol.CASTLE_QUEEN_SIDE);
                uncommonMove = true;
            }
        }

        //OTHER MOVES///////////////////////////////////////////////////////////
        if (!uncommonMove) {
            if (!sq2.isEmpty()) {
                fiftyMoveCounter = 0;
                moveStr = (p1.toString() + Symbol.CAPTURE + to);
            } else {
                fiftyMoveCounter++;
                moveStr = (p1.toString() + to);
            }
            b.move(from, to, !sq2.isEmpty());
        }
        
        //Adjust analyzer, whitesTurn, and positionStack variables to the move
        analyzer.setBoard(b);
        currMove.setPlacement(b.getPosition());
        currMove.setActiveColor(currTurn);
        currMove.setCastling(castle);
        currMove.setEnpassant(enPassant);
        currMove.setHalfCounter(halfMove);
        currMove.setFullCounter(fullMove);
        currMove.setMove(manageMoveListMove(moveStr));
        positionStack.add(currMove);
        System.out.println(currMove.toString());
        lastMove = positionStack.lastElement();
    }
    
    public void setCPUColor(String strColor){
        String str = strColor.toLowerCase().trim();
        
        this.cpuColor = 
                ( str.equals("black")) ? PieceColor.BLACK : 
                ( str.equals("white")) ? PieceColor.WHITE : null;
    }
    
    public PieceColor getCPUColor(){
        return this.cpuColor;
    }
    
    public String getCPUMove(){
        String cpuMove = fish.getBestMove(currMove.getFen());
        return cpuMove;
    }
    
    public boolean isCPUTurn(){
        
        
        Position last = positionStack.lastElement();
        if(last.getColor() == 'b' && cpuColor == PieceColor.BLACK){
            return true;
        }else if(last.getColor() == 'w' && cpuColor == PieceColor.WHITE){
            return true;
        }
        return false;
    }
    
    public void killFish(){
        fish.killFish();
    }

    public ArrayList<String> getMoves(String str) {
        ArrayList<String> moves = new ArrayList<>();
        try{
            Square square = b.getSquare(str);
            
            if (square.isEmpty()) {
                return moves;
            }
            PieceColor pc = square.getPiece().getColor();
            if ((pc == PieceColor.BLACK && isWhitesTurn()) || (pc == PieceColor.WHITE && !isWhitesTurn())) {
                return moves;
            }
            return analyzer.getValidMoves(str);
        } catch(Exception ex){
            System.out.println("Exception in ChessEngine.getMoves( " + str + " )");
            System.out.println(ex.toString());
            for(StackTraceElement ste : ex.getStackTrace()){
                System.out.println("\t"+ ste.toString());
            }
            return moves;
        }
    }

    public ArrayList<String> getPointers(String str, PieceColor pc){
        return analyzer.getPointers(b.getSquare(str), pc);
    }
    
    public void resign() {
        String move = (isWhitesTurn()) ? Symbol.BLACK_WINS : Symbol.WHITE_WINS;
        lastMove.setMove(move);
    }

    public static void displayHashMap(HashMap<String, ArrayList<String>> map) {
        Iterator<Map.Entry<String, ArrayList<String>>> iter = map.entrySet().iterator();
        String key;
        ArrayList<String> value;
        Map.Entry<String, ArrayList<String>> entry;

        while (iter.hasNext()) {
            entry = iter.next();
            key = entry.getKey();
            value = entry.getValue();
            System.out.print("\n" + key + ": " + value.toString());
        }
    }

    public static int[] splitDigits(int num) {
        //figure out how many digits are in the number
        int numDigits = 1;
        int temp = num;
        while (temp >= 10) {
            temp /= 10;
            numDigits++;
        }

        int[] arr = new int[numDigits];
        int divisor = (int) Math.pow(10, numDigits - 1);
        temp = num;
        for (int i = 0; i < numDigits; i++) {
            arr[i] = temp / divisor;
            temp %= divisor;
            divisor /= 10;
        }
        return arr;
    }

    public static int combineDigits(int[] arr) {
        int num = (int) Math.pow(10, arr.length - 1);
        int combined = 0;
        for (int i = 0; i < arr.length; i++) {
            combined += arr[i] * num;
            num /= 10;
        }
        return combined;
    }
}
