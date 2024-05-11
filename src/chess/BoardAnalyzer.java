package chess;

import chess.Enums.Dir;
import chess.Enums.PieceColor;
import static chess.ChessEngine.combineDigits;
import static chess.ChessEngine.splitDigits;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BoardAnalyzer {
    private Board board;

    private int pinDirection = 0;

    public BoardAnalyzer(Board board) {
        //Count the total pieces
        this.board = board;
    }

    public HashMap<String, ArrayList<String>> getValidMoveHashMap(PieceColor pc) {
        HashMap<String, ArrayList<String>> validMoves = new HashMap<>();
        //get the king square for the color
        Square kingSq = (pc == PieceColor.WHITE) ? board.getKingSquare_w() : board.getKingSquare_b();

        //get all the isCheck squares with all the opposite color of pc
        ArrayList<Square> checkSquares = board.getCheckSquaresWithPieces(pc, kingSq);

        //get all the squares that have a piece with Shade pc
        ArrayList<String> squares = board.getSquares(pc);
        squares.removeAll(checkSquares);

        ArrayList<String> moves;
        Iterator<String> iter = squares.iterator();
        while (iter.hasNext()) {
            String currSquare = iter.next();
            moves = getValidMoves(currSquare);
            if (!moves.isEmpty()) {
                validMoves.put(currSquare, moves);
            }
        }
        return validMoves;
    }

    /**
     * takes a square and Shade. Returns true if the piece on that square
 is pinned by the opposite given Shade
     *
     * @param sq
     * @param pc
     * @return
     */
    public boolean isPinned(Square sq, PieceColor pc) {
        //Conditions for pinning: 
        //if sq has a piece with that color,
        //and the square is a potential isCheck Square
        //and an enemy piece is pointing at the piece 
        //and the enemy piece has a moveLimit > 1

        pinDirection = 0;
        //if(square doesn't have a piece or the piece on the square isn't the color of the king
        if ( sq.isEmpty() || sq.getPiece().getColor() != pc || !isCheckSquare(sq.toString(), pc) || sq.getPiece() instanceof King )
            return false;

        int ourFile = sq.getFile();
        int ourRank = sq.getRank();
        Square kingSq = board.getKingSquare(pc);
        int kingFile = kingSq.getFile();
        int kingRank = kingSq.getRank();

        //get the direction moving from the king to our square
        int dir = getLineDirection(kingSq, sq); //direction from the king's perspective
        for (int i : new Knight(PieceColor.WHITE).getAttackDirections())
            if (i == dir)
                return false;
        
        int[] arr = board.getNextSquare(kingFile, kingRank, dir);
        Square currSq = board.getSquare(arr[0], arr[1]);

        //check the squares from the king to our square for allies blocking or forks
        while ((arr[0] != ourFile) || (arr[1] != ourRank) && Board.isOnTheBoard(arr[0], arr[1])) {
            if (!currSq.isEmpty()) 
                return false;
            arr = board.getNextSquare(arr[0], arr[1], dir);
            currSq = board.getSquare(arr[0], arr[1]);
        }
        arr = board.getNextSquare(ourFile, ourRank, dir);

        //check from our square to the next piece or end of board for enemies
        while (Board.isOnTheBoard(arr[0], arr[1])) {
            currSq = board.getSquare(arr[0], arr[1]);
            if (!currSq.isEmpty()) {
                Piece piece = currSq.getPiece();
                PieceColor pColor = piece.getColor();

                if (pColor != pc) {
                    if (piece instanceof Queen) {
                        pinDirection = dir;
                        System.out.println("Queen pinning from "+ currSq.toString());
                        return true;
                    } else if (piece instanceof Rook) {
                        if (dir == Dir.N
                                || dir == Dir.S
                                || dir == Dir.E
                                || dir == Dir.W) {
                            pinDirection = dir;
                            System.out.println("Rook pinning from " + currSq.toString());
                            return true;
                        }
                    } else if (piece instanceof Bishop) {
                        if (dir == Dir.NE
                                || dir == Dir.NW
                                || dir == Dir.SE
                                || dir == Dir.SW) {
                            pinDirection = dir;
                            System.out.println("Bishop pinning from " + currSq.toString());
                            return true;
                        }
                    }
                }
                return false;
            }
            arr = board.getNextSquare(arr[0], arr[1], dir);
        }
        return false;
    }
    
    public HashMap<String, ArrayList<ArrayList<String>>> getPointersHashMap() {
        HashMap<String, ArrayList<ArrayList<String>>> pointers = new HashMap<>();
        ArrayList<ArrayList<String>> lists = new ArrayList<>();

        Iterator<Square> iter = board.boardIterator();
        /*for each square on the board, get 2 lists
            first list: all the white-piece squares pointing at the square
            Second list: all the black-piece squares pointing at the square
         */
        while (iter.hasNext()) {
            ArrayList<String> list_w = new ArrayList<>();
            ArrayList<String> list_b = new ArrayList<>();
            Square currSquare = iter.next();
            String squareName = currSquare.toString();
            list_w = getPointers(currSquare, PieceColor.WHITE);
            list_b = getPointers(currSquare, PieceColor.BLACK);
            lists.add( list_w );
            lists.add( list_b );
            pointers.put(squareName, lists);
        }

        return pointers;
    }

    public ArrayList<String> getPinMoves(Square ourSq, Piece piece, PieceColor pc) {
        //get the direction of the line between ourSq and the kingSq
        int[] ourDirections = piece.getMovementType();
        boolean sameDirection = false;
        ArrayList<String> pinMoves = new ArrayList<>();
        //if the line extended from the king to our piece is equal to the pin direction and our move direction, 
        // then we can move

        for (int i : ourDirections) {
            if (i == pinDirection) {
                sameDirection = true;
                break;
            }
        }
        int dir = pinDirection;

        if (sameDirection) {
            int ourFile = ourSq.getFile();
            int ourRank = ourSq.getRank();
            Square kingSq = board.getKingSquare(pc);
            int kingFile = kingSq.getFile();
            int kingRank = kingSq.getRank();

            int[] arr = board.getNextSquare(ourFile, ourRank, dir);

            //If pinned piece is a pawn
            if (piece instanceof Pawn) {
                //white pawns can only move North, North East, North West
                if (pc == PieceColor.WHITE) {
                    switch (dir) {
                        case Dir.N:
                            if (pawnCanAdvance(ourSq, pc)) {
                                pinMoves.add(board.getSquare(arr[0], arr[1]).toString());
                                if (pawnCanDoubleMove(ourSq, pc)) 
                                    pinMoves.add(board.getSquare(ourFile, ourRank - 2).toString());
                            }
                            return pinMoves;
                        case Dir.NE:
                            if (pawnCanCapture(ourSq, pc, true)) 
                                pinMoves.add(board.getSquare(arr[0], arr[1]).toString());
                            return pinMoves;
                        case Dir.NW:
                            if (pawnCanCapture(ourSq, pc, false)) 
                                pinMoves.add(board.getSquare(arr[0], arr[1]).toString());
                            return pinMoves;
                    }
                } else {
                    switch (dir) {
                        case Dir.S:
                            if (pawnCanAdvance(ourSq, pc)) {
                                pinMoves.add(board.getSquare(arr[0], arr[1]).toString());
                                if (pawnCanDoubleMove(ourSq, pc))
                                    pinMoves.add(board.getSquare(ourFile, ourRank + 2).toString());
                            }
                            return pinMoves;
                        case Dir.SE:
                            if (pawnCanCapture(ourSq, pc, true)) 
                                pinMoves.add(board.getSquare(arr[0], arr[1]).toString());
                            return pinMoves;
                        case Dir.SW:
                            if (pawnCanCapture(ourSq, pc, false)) 
                                pinMoves.add(board.getSquare(arr[0], arr[1]).toString());
                            return pinMoves;
                    }
                }
                return pinMoves;
            }//end if pawn
            else {
                //moving toward pinner from our square
                while (Board.isOnTheBoard(arr[0], arr[1])) {
                    Square sq = board.getSquare(arr[0], arr[1]);
                    if (!sq.isEmpty()) {
                        Piece p2 = sq.getPiece();
                        if (p2.getColor() != pc)
                            pinMoves.add(sq.toString());
                        break;
                    } else 
                        pinMoves.add(sq.toString());
                    arr = board.getNextSquare(arr[0], arr[1], dir);
                }

                //moving toward us from the king square
                arr = board.getNextSquare(kingFile, kingRank, dir);
                while (arr[0] != ourFile || arr[1] != ourRank) {   
                    Square sq = board.getSquare(arr[0], arr[1]);
                    pinMoves.add(sq.toString());
                    arr= board.getNextSquare(arr[0], arr[1], dir);
                }
            }
        }//end same direction
        return pinMoves;
    }

    /**
     * Returns the direction of the line connecting the two squares from a to board
 i.e. the slope
     *
     * @param a
     * @param b
     * @return
     */
    public int getLineDirection(Square a, Square b) {
        int aFile = a.getFile();
        int aRank = a.getRank();
        int bFile = b.getFile();
        int bRank = b.getRank();
        int dir = 0;

        if (aFile == bFile)
           dir = (aRank < bRank) ? Dir.S : Dir.N;    
        else if (aRank == bRank) 
           dir = (aFile < bFile) ? Dir.E : Dir.W;
        else if(aRank == bRank-2 && aFile == bFile+1)
           dir = Dir.SSW;
        else if(aRank == bRank-2 && aFile == bFile-1)
           dir = Dir.SSE;
        else if(aRank == bRank+2 && aFile == bFile+1)
           dir = Dir.NNW;
        else if(aRank == bRank+2 && aFile == bFile-1)
           dir = Dir.NNE;
        else if (aRank == bRank-1 && aFile == bFile+2)
           dir = Dir.SWW;
        else if (aRank == bRank-1 && aFile == bFile-2)
           dir = Dir.SEE;
        else if (aRank == bRank+1 && aFile == bFile+2)
           dir = Dir.NWW;
        else if (aRank == bRank+1 && aFile == bFile-2)
           dir = Dir.NEE;
        else if (aRank > bRank) 
           dir = (aFile > bFile) ? Dir.NW : Dir.NE;
        else if (aFile > bFile) 
           dir = Dir.SW;
        else 
           dir = Dir.SE;
        return dir;
    }

    public ArrayList<String> getValidMoves(String sqName) {
        ArrayList<String> moves = new ArrayList<>();
        //check if the square is clear 
        Square sq = board.getSquare(sqName);
        if (sq.isEmpty()) {
            return moves;
        }

        Piece p = board.getSquare(sqName).getPiece();
        PieceColor pc = p.getColor();
        Square kingSq = board.getKingSquare(pc);
        ArrayList<String> checkers = getPointers(kingSq, pc.Opposite());
        boolean inCheck = (!checkers.isEmpty());
        boolean doubleCheck = (checkers.size() >= 2);
        
        //if in double isCheck, the square trying to move must be the king
        if(doubleCheck && !(sqName.equals(kingSq.toString()))){
            return moves;
        }
        
        //if in Check, only moves are to take the checker, interpose, or move king
        if(inCheck){
            return getValidCheckMoves(sq, kingSq, checkers);
        }
        
        //if the piece is pinned, return pinMoves
        if (isPinned(sq, pc)) {
            //check if piece can move to or away from the pinner
            System.out.println(sq.toString() + pc.toString() + " is pinned");
            return getPinMoves(sq, p, pc);
        }
        

        if (p instanceof Pawn) {
            return getValidPawnMoves(board.getSquare(sqName), pc);
        } //check if piece is a king that can castle;
        //if it's a king and can castle, Move is to squares east or west of king
        else if (p instanceof King) {
            if (pc == PieceColor.WHITE) {
                if (canCastle(true, true)) {
                    moves.add("g1");
                }
                if (canCastle(true, false)) {
                    moves.add("c1");
                }
            } else {
                if (canCastle(false, true)) {
                    moves.add("g8");
                }
                if (canCastle(false, false)) {
                    moves.add("c8");
                }
            }
            //check in each direction for pointers 
            int[] kingDirections = p.getMovementType();
            int kingFile = sq.getFile();
            int kingRank = sq.getRank();
            PieceColor enemyColor = p.getColor().Opposite();
            
            for(int i: kingDirections){
                int[] arr = board.getNextSquare(kingFile, kingRank, i);
                if(Board.isOnTheBoard(arr[0], arr[1])){
                    Square currSq = board.getSquare(arr[0], arr[1]);
                    if(!currSq.isEmpty() && currSq.getPiece().getColor() != enemyColor)
                        continue;
                    ArrayList<String> list = getPointers(currSq, enemyColor);
                    if(list.isEmpty())
                        moves.add(currSq.toString());
                }
            }
            
            return moves;
        }//end if king
        
        int[] movementType = p.getMovementType();
        int moveLimit = p.getLimit();
        //move through each direction until a piece is in the way or the end of the board is hit
        for (int i = 0; i < movementType.length; i++) {
            boolean canMove = true;
            Square s = sq;
            int file = s.getFile();
            int rank = s.getRank();
            int moveCount = 0;

            while (canMove == true) {
                //move one square
                int[] fr = new int[]{file, rank};
                int currSquare = combineDigits(fr);
                currSquare += movementType[i];
                fr = splitDigits(currSquare);
                if (currSquare < 10) {
                    file = 0;
                    rank = fr[0];
                } else {
                    file = fr[0];
                    rank = fr[1];
                }

                //check if square is off the board, or has a piece there
                if (!Board.isOnTheBoard(currSquare)) {
                    canMove = false;
                } else if (!board.getSquare(file, rank).isEmpty()) {
                    Piece targetP = board.getSquare(file, rank).getPiece();
                    PieceColor targetPc = targetP.getColor();

                    //if piece is not our color, then you can move there, but not past
                    if (pc != targetPc) {
                        moves.add(board.getSquare(file, rank).toString());
                    }
                    canMove = false;
                } else {
                    moves.add(board.getSquare(file, rank).toString());
                    moveCount++;
                    if (moveCount >= moveLimit) {
                        canMove = false;
                    }
                }
            }//end while
        }// end for

        return moves;
    }

    public ArrayList<String> getValidCheckMoves(Square ourSq, Square kingSq, ArrayList<String> checkers) {
        ArrayList<String> list = new ArrayList<>();
        PieceColor enemyColor = kingSq.getPiece().getColor().Opposite();
        Square enemy = board.getSquare(checkers.get(0));
        Piece ourPiece = ourSq.getPiece();
        Piece enemyPiece = enemy.getPiece();
        int[] ourDirections = ourPiece.getMovementType();
        
        if(ourSq == kingSq){
            //get valid kingMoves
            int file = kingSq.getFile();
            int rank = kingSq.getRank();
            int enemyDir = getLineDirection(kingSq, enemy);
            int[] kingDirections = kingSq.getPiece().getMovementType();
            
            /*
                If a square exists in that direction, and no enemies are pointing at it,
                add the square to the list.
                The King cannot move in the same direction as the checking direction, unless taking the checker
            */
            for(int i : kingDirections){
                int[] index = board.getNextSquare(file, rank, i);
                
                if(-i == enemyDir){
                     continue;
                }
                if(Board.isOnTheBoard(index[0], index[1])){
                    Square currSq = board.getSquare(index[0], index[1]);
                    if(!currSq.isEmpty() && currSq.getPiece().getColor() == ourPiece.getColor()){
                        continue;
                    }
                    if(getPointers(currSq, enemyColor).isEmpty()){
                        list.add(currSq.toString());
                    }
                }
            }
             
        } else {//For pieces that are not the king
            //get the line from the kingSq to the checker
            //this is the direction the piece can point
            int dir = getLineDirection(kingSq, enemy);
            int kingFile = kingSq.getFile();
            int kingRank = kingSq.getRank();
            int enemyFile = enemy.getFile();
            int enemyRank = enemy.getRank();
            int ourFile = ourSq.getFile();
            int ourRank = ourSq.getRank();
            int[] arr = board.getNextSquare(kingFile, kingRank, dir);
            ArrayList<Square> squareArr = new ArrayList<>();
            
            //if being checked by a knight, only moves are to take the attacker or move the king
            squareArr.add(enemy);
            
            if(!(enemyPiece instanceof Knight)) { //not a knight, get all the square between the king and the checker
                while(arr[0] != enemyFile || arr[1] != enemyRank){
                    Square sq = board.getSquare(arr[0], arr[1]);
                    squareArr.add(sq);
                    arr = board.getNextSquare(arr[0], arr[1], dir);
                }
            }
            
            //if we're a pawn
            if(ourPiece instanceof Pawn){
                PieceColor ourColor = ourPiece.getColor();
                //white pawn
                if(ourColor == PieceColor.WHITE){
                    arr = board.getNextSquare(ourFile, ourRank, Dir.NE);
                    if(arr[0] == enemyFile && arr[1] == enemyRank){
                        list.add(enemy.toString());
                    }
                    arr = board.getNextSquare(ourFile, ourRank, Dir.NW);
                    if(arr[0] == enemyFile && arr[1] == enemyRank){
                        list.add(enemy.toString());
                    }
                    arr = board.getNextSquare(ourFile, ourRank, Dir.N);
                    if(arr[0] == enemyFile && arr[1] == enemyRank){
                        return list;
                    }
                    if(Board.isOnTheBoard(arr[0], arr[1])){
                        Square currSq = board.getSquare(arr[0], arr[1]);
                        if(squareArr.contains(currSq)){
                            list.add(currSq.toString());
                        }
                    }
                    arr = board.getNextSquare(arr[0], arr[1], Dir.N);
                    if(pawnCanDoubleMove(ourSq, PieceColor.WHITE)){
                        Square currSq = board.getSquare(arr[0], arr[1]);
                        if(squareArr.contains(currSq)){
                            list.add(currSq.toString());
                        }
                    }
                        return list;
                } else{ // black pawn
                    arr = board.getNextSquare(ourFile, ourRank, Dir.SE);
                    if(arr[0] == enemyFile && arr[1] == enemyRank){
                        list.add(enemy.toString());
                    }
                    arr = board.getNextSquare(ourFile, ourRank, Dir.SW);
                    if(arr[0] == enemyFile && arr[1] == enemyRank){
                        list.add(enemy.toString());
                    }
                    arr = board.getNextSquare(ourFile, ourRank, Dir.S);
                    if(arr[0] == enemyFile && arr[1] == enemyRank){
                        return list;
                    }
                    if(Board.isOnTheBoard(arr[0], arr[1])){
                        Square currSq = board.getSquare(arr[0], arr[1]);
                        if(squareArr.contains(currSq)){
                            list.add(currSq.toString());
                        }
                    }
                    arr = board.getNextSquare(arr[0], arr[1], Dir.S);
                    if(pawnCanDoubleMove(ourSq, PieceColor.BLACK)){
                        Square currSq = board.getSquare(arr[0], arr[1]);
                        if(squareArr.contains(currSq)){
                            list.add(currSq.toString());
                        }
                    }
                } 
                return list;
            } else { 
            //we're any piece but a pawn or king
                //get all moves where we can take the checker or block the checker
                int moveLimit = ourPiece.getLimit();
                   
                //move in each direction of the piece, if a square is between the checker and king,
                    //add the square to the move list.
                for (int i = 0; i < ourDirections.length; i++) {
                    arr = board.getNextSquare(ourFile, ourRank, ourDirections[i]);
                    while(Board.isOnTheBoard(arr[0], arr[1])){
                        Square sq = board.getSquare(arr[0], arr[1]);
                        if(squareArr.contains(sq)){
                            list.add(sq.toString());
                            break;
                        }
                        if(!sq.isEmpty()){
                            break;
                        }
                        if(moveLimit == 1)
                            break;
                        arr = board.getNextSquare(arr[0], arr[1], ourDirections[i]);
                    }
                }// end for
            }
        }
        return list;
    }
    
    public ArrayList<String> getPointers(Square sq, Piece pointingPiece){
        ArrayList<String> pointers = new ArrayList<>();
        int file = sq.getFile();
        int rank = sq.getRank();
        int[] dirs = pointingPiece.getAttackDirections();
        int moveLimit = pointingPiece.getLimit();
        
        for(int i : dirs){
            int[] arr = board.getNextSquare(file, rank, -i);
            int count = 0;
            while(Board.isOnTheBoard(arr[0], arr[1]) && count < moveLimit){
                Square currSq = board.getSquare(arr[0], arr[1]);
                if(!currSq.isEmpty() ){
                    if(currSq.getPiece().equals(pointingPiece)){
                        pointers.add(currSq.toString());  
                    }
                    break;
                }
                count++;
                arr = board.getNextSquare(arr[0], arr[1], -i);
            }
        }
        
        return pointers;
    }
    
    public ArrayList<String> getPointers(Square sq, PieceColor pointingColor) {
        ArrayList<String> pointers = new ArrayList<>();
        Piece[] pieceArr = new Piece[]{
            new King(pointingColor),
            new Queen(pointingColor),
            new Pawn(pointingColor),
            new Bishop(pointingColor),
            new Rook(pointingColor),
            new Knight(pointingColor)
        };
        
        for(Piece p : pieceArr){
           pointers.addAll(getPointers(sq, p)); 
        }

//        int[] knightDirs = n.getMovementType();
//        for(int i : knightDirs){
//            int[] arr = board.getNextSquare(file, rank, i);
//            if(Board.isOnTheBoard(arr[0], arr[1])){
//                Square currSq = board.getSquare(arr[0], arr[1]);
//                if(currSq.isOccupied()){
//                    Piece piece = currSq.getPiece();
//                    if(piece instanceof Knight && piece.getColor() == pointingColor){
//                        pointers.add(currSq.toString());
//                    }
//                }
//            }
//        }
        return pointers;
    }
    
    public boolean canCastle(boolean white, boolean kingside) {
        Square kingSq, rookSq, between1, between2, between3;
        
        if (white) {
            //white king
            kingSq = board.getSquare("e1");
            if (kingSq.isEmpty()) 
                return false;
            
            Piece p = kingSq.getPiece();
            if (!(p instanceof King) || p.getColor() != PieceColor.WHITE || hasMoved(p)) 
                return false;
            
            if (kingside) {
                //white rook kingside
                rookSq = board.getSquare("h1");
                if (rookSq.isEmpty()) 
                    return false;
                Piece rk = rookSq.getPiece();
                if (!(rk instanceof Rook) || rk.getColor() != PieceColor.WHITE || hasMoved(rk)) 
                    return false;
                between1 = board.getSquare("f1");
                if (!between1.isEmpty() || !getPointers(between1, PieceColor.BLACK).isEmpty()) 
                    return false;
                between2 = board.getSquare("g1");
                if (!between2.isEmpty() || !getPointers(between2, PieceColor.BLACK).isEmpty()) 
                    return false;
            } else {
                //white rook queenside
                rookSq = board.getSquare("a1");
                if (rookSq.isEmpty()) 
                    return false;
                Piece rk = rookSq.getPiece();
                if (!(rk instanceof Rook) || rk.getColor() != PieceColor.WHITE || hasMoved(rk)) 
                    return false;
                between1 = board.getSquare("d1");
                if (!between1.isEmpty() || !getPointers(between1, PieceColor.BLACK).isEmpty()) 
                    return false;
                between2 = board.getSquare("c1");
                if (!between2.isEmpty() || !getPointers(between2, PieceColor.BLACK).isEmpty()) 
                    return false;
                between3 = board.getSquare("b1");
                if (!between3.isEmpty()) 
                    return false;
            }
        } else {
            //black king
            kingSq = board.getSquare("e8");
            if (kingSq.isEmpty()) 
                return false;
            Piece p = kingSq.getPiece();
            if (!(p instanceof King) || p.getColor() != PieceColor.BLACK || hasMoved(p)) 
                return false;
            if (kingside) {
                //black rook kingside
                rookSq = board.getSquare("h8");
                if (rookSq.isEmpty()) 
                    return false;
                Piece rk = rookSq.getPiece();
                if (!(rk instanceof Rook) || rk.getColor() != PieceColor.BLACK || hasMoved(rk)) 
                    return false;
                between1 = board.getSquare("f8");
                if (!between1.isEmpty() || !getPointers(between1, PieceColor.WHITE).isEmpty()) 
                    return false;
                between2 = board.getSquare("g8");
                if (!between2.isEmpty() || !getPointers(between2, PieceColor.WHITE).isEmpty()) 
                    return false;
            } else {
                //black rook queenside
                rookSq = board.getSquare("a8");
                if (rookSq.isEmpty()) 
                    return false;
                Piece rk = rookSq.getPiece();
                if (!(rk instanceof Rook) || rk.getColor() != PieceColor.BLACK || hasMoved(rk)) 
                    return false;
                between1 = board.getSquare("d8");
                if (!between1.isEmpty() || !getPointers(between1, PieceColor.WHITE).isEmpty())
                    return false;
                between2 = board.getSquare("c8");
                if (!between2.isEmpty() || !getPointers(between2, PieceColor.WHITE).isEmpty()) 
                    return false;
                between3 = board.getSquare("b8");
                if (!between3.isEmpty()) 
                    return false;
            }
        }
        return true;
    }
    
    public ArrayList<String> getPawnAttackSquares(String sq) {
        ArrayList<String> moves = new ArrayList<>();
        Square square = board.getSquare(sq);
        int file = square.getFile();
        int rank = square.getRank();
        int rNorth = rank - 1;
        int rSouth = rank + 1;
        int fEast = file + 1;
        int fWest = file - 1;

        //make sure a piece is even on the square to get the pieceColor
        if (!square.isEmpty()) {
            Piece piece = square.getPiece();
            PieceColor pc = piece.getColor();

            //if white pawn, set northeast, northwest
            if (pc == PieceColor.WHITE) {
                if (Board.isOnTheBoard(fEast, rNorth)) 
                    moves.add(board.getSquare(fEast, rNorth).toString());
                if (Board.isOnTheBoard(fWest, rNorth)) 
                    moves.add(board.getSquare(fWest, rNorth).toString());
            } //if black pawn, set southeast, southwest
            else {
                if (Board.isOnTheBoard(fEast, rSouth)) 
                    moves.add(board.getSquare(fEast, rSouth).toString());
                if (Board.isOnTheBoard(fWest, rSouth)) 
                    moves.add(board.getSquare(fWest, rSouth).toString());
            }
        }
        return moves;
    }

    private ArrayList<String> getValidPawnMoves(Square sq, PieceColor pc) {
        ArrayList<String> moves = new ArrayList<>();
        int file = sq.getFile();
        int rank = sq.getRank();
        int fEast = file + 1;
        int fWest = file - 1;
        int rNorth = rank - 1;
        int rSouth = rank + 1;

        boolean canTakeRight = pawnCanCapture(sq, pc, true);
        boolean canTakeLeft = pawnCanCapture(sq, pc, false);
        boolean canAdvance = pawnCanAdvance(sq, pc);

        // if Shade is white, look north, northeast, northwest
        if (pc == PieceColor.WHITE) {
            if (canAdvance) {
                moves.add(board.getSquare(file, rNorth).toString());
                if (rank == 6 && pawnCanDoubleMove(sq, pc)) 
                    moves.add(board.getSquare(file, rNorth - 1).toString());
            }
            if (canTakeRight) 
                moves.add(board.getSquare(fEast, rNorth).toString());
            if (canTakeLeft)
                moves.add(board.getSquare(fWest, rNorth).toString());
            return moves;
        } //else look south, southeast, southwest
        else {
            if (canTakeRight) 
                moves.add(board.getSquare(fEast, rSouth).toString());
            if (canTakeLeft)
                moves.add(board.getSquare(fWest, rSouth).toString());
            if (canAdvance) {
                moves.add(board.getSquare(file, rSouth).toString());
                if (rank == 1 && pawnCanDoubleMove(sq, pc)) 
                    moves.add(board.getSquare(file, rSouth + 1).toString());
            }
        }
        return moves;
    }

    private boolean pawnCanCapture(Square sq, PieceColor pc, boolean east) {
        int file = (east) ? sq.getFile() + 1 : sq.getFile() - 1;
        int rankNorth = sq.getRank() - 1;
        int rankSouth = rankNorth + 2;

        if (pc == PieceColor.WHITE && squareExistsWithPieceColored(file, rankNorth, PieceColor.BLACK))
            return true;
            
        else if (pc == PieceColor.BLACK && squareExistsWithPieceColored(file, rankSouth, PieceColor.WHITE)) 
            return true;
        
        return passingPawnOnAdjacentFile(sq, pc, east);
    }

    private boolean passingPawnOnAdjacentFile(Square sq, PieceColor pc, boolean east) {
        PieceColor pc2;
        int adjFile = (east) ? sq.getFile() + 1 : sq.getFile() - 1;
        int rank = sq.getRank();

        if (pc == PieceColor.WHITE) {
            if (rank != Square.EN_PASSANT_RANK[0]) 
                return false; 
            pc2 = PieceColor.BLACK;
        } else {
            if (rank != Square.EN_PASSANT_RANK[1]) 
                return false;
            pc2 = PieceColor.WHITE;
        }

        //check if a passing pawn is on the adjacent adjFile;
        Pawn pwn;
        if (squareExistsWithPieceColored(adjFile, sq.getRank(), pc2)) {
            Piece piece = board.getSquare(adjFile, sq.getRank()).getPiece();
            if (piece instanceof Pawn) {
                pwn = (Pawn) piece;
                if (pwn.passing()) 
                    return true;
            }
        }
        return false;
    }

    private boolean pawnCanAdvance(Square sq, PieceColor pc) {
        boolean white = pc == PieceColor.WHITE;
        boolean northIsEmpty = board.getSquare(sq.getFile(),sq.getRank()-1).isEmpty();
        boolean southIsEmpty = board.getSquare(sq.getFile(),sq.getRank()+1).isEmpty();
        return (white && northIsEmpty) || (!white && southIsEmpty);
    }

    private boolean pawnCanDoubleMove(Square sq, PieceColor pc) {
        int r = sq.getRank();
        if(pc == PieceColor.BLACK)
            if(r != 1)
                return false;
        else 
            if(r != 6)
                return false;
        
        int f = sq.getFile();

        if (pc == PieceColor.BLACK)
            return pawnCanAdvance(new Square(f, 2), pc);
        else 
            return (pawnCanAdvance(new Square(f, 5), pc));
    }

    private boolean hasMoved(Piece p) {
        return p.getMoveCount() > 0;
    }

    private boolean isCheckSquare(String sqName, PieceColor pc) {
        for (Square[] sqArr : board.getCheckSquares(pc)) 
            for (Square sq : sqArr) 
                if (sq.toString().equals(sqName)) 
                    return true;
        return false;
    }
    
    private boolean squareExistsWithPieceColored(int file, int rank, PieceColor pc) {
        if (Board.isOnTheBoard(file, rank)) {
            Square sq = board.getSquare(file, rank);
            if (!sq.isEmpty() && sq.getPiece().getColor() == pc) 
                return true;
        }
        return false;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
