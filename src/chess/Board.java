package chess;

import chess.Enums.Dir;
import chess.Enums.PieceColor;
import java.util.Iterator;
import java.util.ArrayList;

public class Board {
    private final Square[][] squares;
    private boolean flipBoard = false;

    public static final String[] SQRS = {
        "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
        "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
        "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
        "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
        "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
        "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
        "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
        "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",};

    public static final String[] PROMOTION_SQS_W = { "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8" };
    public static final String[] PROMOTION_SQS_B = { "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1" };
    private Piece w_King;
    private Piece b_King;

    private Square[][] checkSquares_w;
    private Square[][] checkSquares_b;

    //CONSTRUCTOR
    /**
     * Creates a board in an 8x8 2-dimensional array of squares
     */
    public Board() {
        squares = new Square[8][8];
        for (int rank = 0; rank < 8; rank++) 
            for (int file = 0; file < 8; file++) 
                squares[file][rank] = new Square(file, rank);
        
        w_King = new King(PieceColor.WHITE);
        b_King = new King(PieceColor.BLACK);
        setPieces();
    }

    //METHODS
    /**
     * shows the pieces and the clear squares using System.out.print() and UTF
     * character codes
     */
    public void showPieces() {
        // if board is normal
        if (!flipBoard) {
            for (int rank = 0; rank < 8; rank++) {
                System.out.print(Square.RANK[rank] + " ");
                for (int file = 0; file < 8; file++) {
                    String str;
                    if (getSquare(file, rank).isEmpty()) 
                      str = getSquare(file, rank).getColor().toString();
                    else 
                      str = getSquare(file, rank).getPiece().toString();
                    System.out.print(str + " ");
                }
                System.out.println();
            }
            System.out.println("  \u2006a\u2001b\u2001c\u2001d\u2001e\u2001f\u2001g\u2001h");
        } // if board is flipped
        else {
            for (int rank = 7; rank >= 0; rank--) {
                System.out.print(Square.RANK[rank] + " ");
                for (int file = 7; file >= 0; file--) {
                    String str;
                    if (getSquare(file, rank).isEmpty()) {
                        str = getSquare(file, rank).getColor().toString();
                    } else {
                        str = getSquare(file, rank).getPiece().toString();
                    }
                    System.out.print(str + " ");
                }
                System.out.println();
            }
            System.out.println("  \u2006h\u2001g\u2001f\u2001e\u2001d\u2001c\u2001b\u2001a");
        }
        System.out.println();
    }

    /**
     * toggles flipBoard
     */
    public void flip() {
        flipBoard = !flipBoard;
    }

    /**
     * Sets the pieces to their initial squares
     */
    private void setPieces() {
        setPieces(Position.START_POS);
//        setPiece(new Pawn(PieceColor.WHITE), "a2");
//        setPiece(new Pawn(PieceColor.WHITE), "b2");
//        setPiece(new Pawn(PieceColor.WHITE), "c2");
//        setPiece(new Pawn(PieceColor.WHITE), "d2");
//        setPiece(new Pawn(PieceColor.WHITE), "e2");
//        setPiece(new Pawn(PieceColor.WHITE), "f2");
//        setPiece(new Pawn(PieceColor.WHITE), "g2");
//        setPiece(new Pawn(PieceColor.WHITE), "h2");
//        setPiece(new Pawn(PieceColor.BLACK), "a7");
//        setPiece(new Pawn(PieceColor.BLACK), "b7");
//        setPiece(new Pawn(PieceColor.BLACK), "c7");
//        setPiece(new Pawn(PieceColor.BLACK), "d7");
//        setPiece(new Pawn(PieceColor.BLACK), "e7");
//        setPiece(new Pawn(PieceColor.BLACK), "f7");
//        setPiece(new Pawn(PieceColor.BLACK), "g7");
//        setPiece(new Pawn(PieceColor.BLACK), "h7");
//        setPiece(new Rook(PieceColor.WHITE), "a1");
//        setPiece(new Rook(PieceColor.WHITE), "h1");
//        setPiece(new Rook(PieceColor.BLACK), "a8");
//        setPiece(new Rook(PieceColor.BLACK), "h8");
//        setPiece(new Knight(PieceColor.WHITE), "b1");
//        setPiece(new Knight(PieceColor.WHITE), "g1");
//        setPiece(new Knight(PieceColor.BLACK), "b8");
//        setPiece(new Knight(PieceColor.BLACK), "g8");
//        setPiece(new Bishop(PieceColor.WHITE), "c1");
//        setPiece(new Bishop(PieceColor.WHITE), "f1");
//        setPiece(new Bishop(PieceColor.BLACK), "c8");
//        setPiece(new Bishop(PieceColor.BLACK), "f8");
//        setPiece(new Queen(PieceColor.WHITE), "d1");
//        setPiece(new Queen(PieceColor.BLACK), "d8");
//        setPiece(w_King, "e1");
//        setPiece(b_King, "e8");
    }

    /**
     * Sets the board up using UCI algebraic notation; moving from rank 8 to 1,
     * f a to h Uppercase = white lowercase = black delimiter between rows= '/'
     * integer = the number of clear squares example start position =
     * rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR where r = black rook n =
     * black knight b = black bishop q = black queen k = black king p = black
     * pawn R = white rook N = white knight B = white bishop Q = white Queen K =
     * white King P = white pawn
     *
     * @param str
     */
    public void setPieces(String str) {
        //parse the rows using delimiter '/'
        String[] rows = str.split("/");

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length(); j++) {
                char ch = rows[i].charAt(j);
                switch (ch) {
                    case '-': squares[j][i].clear(); break;
//                    case ' ': squares[j][i].clear(); break;
                    case 'r': squares[j][i].setPiece(new Rook(PieceColor.BLACK)); break;
                    case 'n': squares[j][i].setPiece(new Knight(PieceColor.BLACK)); break;
                    case 'b': squares[j][i].setPiece(new Bishop(PieceColor.BLACK)); break;
                    case 'q': squares[j][i].setPiece(new Queen(PieceColor.BLACK)); break;
                    case 'p': squares[j][i].setPiece(new Pawn(PieceColor.BLACK)); break;
          
                    
                    case 'R': squares[j][i].setPiece(new Rook(PieceColor.WHITE)); break;
                    case 'N': squares[j][i].setPiece(new Knight(PieceColor.WHITE)); break;
                    case 'B': squares[j][i].setPiece(new Bishop(PieceColor.WHITE)); break;
                    case 'Q': squares[j][i].setPiece(new Queen(PieceColor.WHITE)); break;
                    case 'P': squares[j][i].setPiece(new Pawn(PieceColor.WHITE)); break;
                    case 'K':
                        w_King = new King(PieceColor.WHITE);
                        w_King.setSquare(squares[j][i]);
                        squares[j][i].setPiece(w_King); break;
                   
                    case 'k':
                        b_King = new King(PieceColor.BLACK);
                        b_King.setSquare(squares[j][i]);
                        squares[j][i].setPiece(b_King); break;
                    default:
                        //replace the digit with the specified number of spaces
                        int num = Character.getNumericValue(ch);
                        String firstPart = rows[i].substring(0, j);
                        String lastPart = rows[i].substring(j + 1);
                        String spaces = String.format("%" + num + "s", "");
                        rows[i] = firstPart + spaces + lastPart;
                        squares[j][i].clear(); break;
                }//end switch

            } // end for j
        } // end for i
        showPieces();
    }

    /**
     * Sets a given Piece, and the square
     *
     * @param p
     * @param square
     */
    public void setPiece(Piece p, String square) {
        getSquare(square).setPiece(p);
        p.setSquare(getSquare(square));
    }

    /**
     * Sets a given Piece on a square
     *
     * @param p
     * @param sq
     */
    public void setPiece(Piece p, Square sq) {
        p.setSquare(sq);
        sq.setPiece(p);
    }

    public void takePassedPawn(String from, String to) {
        Square sq1 = getSquare(from);
        Square sq2 = getSquare(to);
        Piece p1 = sq1.getPiece();
        PieceColor pc = p1.getColor();

        //if white is taking a passed pawn, piece being captured is south of sq2
        int rank = (pc == PieceColor.WHITE) ? sq2.getRank() + 1 : sq2.getRank() - 1;
        Square sq3 = getSquare(sq2.getFile(), rank);

        setPiece(p1, sq2);
        p1.incrementMoveCount();
        sq3.getPiece().capture();
        sq3.clear();
        sq1.clear();

    }

    public String getPosition() {
        String position = "";
        Square square;
        Piece piece;
        PieceColor pc;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                square = squares[j][i];
                if (square.isEmpty()) position += "-";
                else {
                    piece = square.getPiece();
                    pc = piece.getColor();
                    if (pc == PieceColor.WHITE) {
                        if (piece instanceof King)   position += "K";
                        else if (piece instanceof Queen) position += "Q";
                        else if (piece instanceof Rook) position += "R";
                        else if (piece instanceof Bishop) position += "B";
                        else if (piece instanceof Knight) position += "N";
                        else position += "P";
                    } else if (piece instanceof King)  position += "k";
                    else if (piece instanceof Queen)  position += "q";
                    else if (piece instanceof Rook) position += "r";
                    else if (piece instanceof Bishop)  position += "b";
                    else if (piece instanceof Knight) position += "n";
                    else  position += "p";
                }
            } 
            if (i < 7)position += "/";
        }
        return position;
    }

    public Square[][] getCheckSquares(boolean white) {
        if (white) 
            return checkSquares_w;
        return checkSquares_b;
    }

    public void promote(Square to, String pType) {
        switch (pType) {
            case "Q":to.setPiece(new Queen(PieceColor.WHITE)); break;
            case "q":to.setPiece(new Queen(PieceColor.BLACK)); break;
            case "R":to.setPiece(new Rook(PieceColor.WHITE)); break;
            case "r":to.setPiece(new Rook(PieceColor.BLACK)); break;
            case "B":to.setPiece(new Bishop(PieceColor.WHITE)); break;
            case "b":to.setPiece(new Bishop(PieceColor.BLACK)); break;
            case "N":to.setPiece(new Knight(PieceColor.WHITE)); break;
            case "n":to.setPiece(new Knight(PieceColor.BLACK)); break;
            default: System.out.println("Bad piece type passed " + "to Board.promote(String sqName, String pType)");
        }

    }

    /**
     * Takes two squares as strings: from and to. Moves whatever piece is on
     * that square to the other square. Replaces any piece on the destination
     * square
     *
     * If no piece is on the original square, the destination square is emptied
     *
     * @param from
     * @param to
     * @param capture
     */
    public void move(String from, String to, boolean capture) {
        Square sq1 = getSquare(from);
        Square sq2 = getSquare(to);
        capture = !(sq2.isEmpty());
        Piece p1 = sq1.getPiece();

        if (capture) 
            sq2.getPiece().capture();

        //Move the piece and increment its move count
        setPiece(p1, sq2);
        p1.incrementMoveCount();

        //empty the old square
        sq1.clear();
        if (p1 instanceof King) {
            if (p1.getColor() == PieceColor.WHITE) {
                //update the checkSquares for enemy king
                w_King = p1;
                checkSquares_w = getCheckSquares(PieceColor.WHITE);
            } else {
                b_King = p1;
                checkSquares_b = getCheckSquares(PieceColor.BLACK);
            }
        }
    }

    public void emptySquare(String sqName) {
        Square sq = getSquare(sqName);

        if (!sq.isEmpty()) {
            Piece p = sq.getPiece();
            p.capture();
            sq.clear();
        }
    }

    /**
     * white king-side castle
     */
    public void castleOOWhite() {
        //Castle
        move("h1", "f1", false);
        move("e1", "g1", false);
    }

    /**
     * black king-side castle
     */
    public void castleOOBlack() {
        //Castle
        move("h8", "f8", false);
        move("e8", "g8", false);
    }

    /**
     * white queen-side castle
     */
    public void castleOOOWhite() {
        //Castle
        move("a1", "d1", false);
        move("e1", "c1", false);

    }

    /**
     * black queen-side castle
     */
    public void castleOOOBlack() {
        //Castle
        move("a8", "d8", false);
        move("e8", "c8", false);
    }

    //BOOLEANS
    public static boolean isOnTheBoard(String sq) {
        try {
            int file = Square.getFile(sq.charAt(0));
            int rank = Square.getRank(sq.charAt(1));

            return isOnTheBoard(file, rank);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Takes a combined index number. Returns true if the index is part of the
     * board valid index values:
     *
     * 00 10 20 30 40 50 60 70 01 11 21 31 41 51 61 71 02 12 22 32 42 52 62 72
     * 03 13 23 33 43 53 63 73 04 14 24 34 44 54 64 74 05 15 25 35 45 55 65 75
     * 06 16 26 36 46 56 66 76 07 17 27 37 47 57 67 77
     *
     * @param index
     * @return
     */
    public static boolean isOnTheBoard(int index) {
        //if less than zero or greater than 77 return false
        if (index < 0 || index > 77) {
            return false;
        }

        //if contains an 8 or a 9
        String str = Integer.toString(index);
        return !(str.contains("8") || str.contains("9"));
    }

    /**
     * takes a file and rank. Returns true if the rank and file are on the board
     *
     * @param file
     * @param rank
     * @return
     */
    public static boolean isOnTheBoard(int file, int rank) {
        return !(file < 0 || rank < 0 || file >= 8 || rank >= 8);
    }

    //LISTS
    /**
     * Returns all the pawns of the given color
     *
     * @param pieceType
     * @param pc
     * @return
     */
    public ArrayList<Piece> getPiecesOnTheBoard(Class<?> pieceType, PieceColor pc) {
        ArrayList<Piece> pieces = new ArrayList<>();
        Iterator<Square> iter = boardIterator();
        Square currSquare;
        Piece p;
        while (iter.hasNext()) {
            currSquare = iter.next();
            if (!currSquare.isEmpty()) {
                p = currSquare.getPiece();
                if (p.getClass() == pieceType && p.getColor() == pc) {
                    pieces.add(p);
                }
            }
        }
        return pieces;
    }

    /**
     * Takes a Shade, returns all the pieces matching that color
     *
     * @param pc
     * @return
     */
    public ArrayList<Piece> getPiecesOnTheBoard(PieceColor pc) {
        ArrayList<Piece> pieces = new ArrayList<>();
        Iterator<Square> iter = boardIterator();
        Square currSquare;

        //look at each square
        while (iter.hasNext()) {
            currSquare = iter.next();

            //if the square has a piece, and if the piece is the given color,
            if (!currSquare.isEmpty() && currSquare.getPiece().getColor() == pc) {
                // add it to the list
                pieces.add(currSquare.getPiece());
            }
        }

        return pieces;
    }

    /**
     * takes a Shade, returns every square name occupied by a piece of that
     * color
     *
     * @param pc
     * @return
     */
    public ArrayList<String> getSquares(PieceColor pc) {
        ArrayList<String> list = new ArrayList<>();
        Square sq;
        Iterator<Square> iter = boardIterator();

        while (iter.hasNext()) {
            sq = iter.next();
            if (!sq.isEmpty() && sq.getPiece().getColor() == pc) {
                list.add(sq.toString());
            }
        }
        return list;
    }

    /**
     * returns all the squares on the board
     *
     * @return
     */
    public Square[][] getSquares() {
        return this.squares;
    }

    /**
     * takes a square name as a string, returns the square from the board
     *
     * @param str
     * @return
     */
    public Square getSquare(String str) {
        int f = Square.getFile(str.charAt(0));
        int r = Square.getRank(str.charAt(1));
        return squares[f][r];
    }

    /**
     * Returns a square given the array index values
     *
     * @param file
     * @param rank
     * @return
     */
    public Square getSquare(int file, int rank) {
        return squares[file][rank];
    }

    /**
     *
     * @param file
     * @param rank
     * @param dir
     * @return
     */
    public int[] getNextSquare(int file, int rank, int dir) {
        int[] index = new int[2];
        index[0] = file;
        index[1] = rank;

        switch (dir) {
            case Dir.N:
                index[1]--;
                break;
            case Dir.S:
                index[1]++;
                break;
            case Dir.E:
                index[0]++;
                break;
            case Dir.W:
                index[0]--;
                break;
            case Dir.NE:
                index[0]++;
                index[1]--;
                break;
            case Dir.NW:
                index[1]--;
                index[0]--;
                break;
            case Dir.SE:
                index[0]++;
                index[1]++;
                break;
            case Dir.SW:
                index[0]--;
                index[1]++;
                break;
            case Dir.NEE:
                index[0]++;
                index[0]++;
                index[1]--;
                break;
            case Dir.NWW:
                index[0]--;
                index[0]--;
                index[1]--;
                break;
            case Dir.NNE:
                index[0]++;
                index[1]--;
                index[1]--;
                break;
            case Dir.NNW:
                index[0]--;
                index[1]--;
                index[1]--;
                break;
            case Dir.SEE:
                index[0]++;
                index[0]++;
                index[1]++;
                break;
            case Dir.SWW:
                index[0]--;
                index[0]--;
                index[1]++;
                break;
            case Dir.SSE:
                index[0]++;
                index[1]++;
                index[1]++;
                break;
            case Dir.SSW:
                index[0]--;
                index[1]++;
                index[1]++;
                break;
            default:
        }

        return index;
    }

    /**
     * takes a square and direction. Returns the next square in that direction
     * with a piece on it. If a no piece in that direction, the square passed is
     * returned
     *
     * @param sq
     * @param dir
     * @return
     */
    public Square getNextSquareWithPiece(Square sq, int dir) {
        int ourFile = sq.getFile();
        int ourRank = sq.getRank();
        int[] arr = getNextSquare(ourFile, ourRank, dir);

        while (Square.isValidIndex(arr[0]) && Square.isValidIndex(arr[1])) {
            Square currSq = squares[arr[0]][arr[1]];
            if (!currSq.isEmpty()) {
                return currSq;
            }

        }
        return sq;
    }

    /**
     * Returns the white King's Square
     *
     * @return
     */
    public Square getKingSquare_w() {
        if (w_King.getSquare() != null) {
            return w_King.getSquare();
        }

        PieceColor white = PieceColor.WHITE;
        Iterator<Square> iter = boardIterator();
        Square sq;
        Piece piece;
        PieceColor pc;

        while (iter.hasNext()) {
            sq = iter.next();
            if (!sq.isEmpty()) {
                piece = sq.getPiece();
                pc = piece.getColor();

                if (pc == white && piece instanceof King) {
                    return sq;
                }
            }
        }
        return w_King.getSquare();
    }

    /**
     * Returns the blackKings Square
     *
     * @return
     */
    public Square getKingSquare_b() {
        if (b_King.getSquare() != null) {
            return b_King.getSquare();
        }

        PieceColor black = PieceColor.BLACK;
        Iterator<Square> iter = boardIterator();
        Square sq;
        Piece piece;
        PieceColor pc;

        while (iter.hasNext()) {
            sq = iter.next();
            if (!sq.isEmpty()) {
                piece = sq.getPiece();
                pc = piece.getColor();
                if (pc == black && piece instanceof King) {
                    return sq;
                }
            }
        }
        return b_King.getSquare();
    }

    public Square getKingSquare(PieceColor pc) {
        return (pc == PieceColor.WHITE) ? w_King.getSquare() : b_King.getSquare();
    }

    public ArrayList<Square> getCheckSquaresWithPieces(PieceColor pc, Square sq) {
        ArrayList<Square> list = new ArrayList<>();
        list.addAll(getFileSqsWithQueensAndRooks(sq, pc));
        list.addAll(getRankSqsWithQueensAndRooks(sq, pc));
        list.addAll(getNEastDiagWithQueensAndBishops(sq, pc));
        list.addAll(getNWestDiagonalWithQueensAndBishops(sq, pc));
        return list;
    }

    public Square[][] getCheckSquares(PieceColor pc) {
        Square sq = pc == PieceColor.WHITE ? w_King.getSquare() : b_King.getSquare();
        return getCheckSquares(sq);
    }

    /**
     * Takes a square assumed to be the King's current position, returns a 2
     * dimensional array of squares in the following order: f, rank, diagonal /,
     * diagonal \, and knight squares ie the squares where any piece that can
     * possibly point at the King.
     *
     * @param sq
     * @return
     */
    private Square[][] getCheckSquares(Square sq) {
        Square[][] checkSquares = new Square[5][];
        checkSquares[0] = getFile(sq); // f 
        checkSquares[1] = getRank(sq); // rank
        checkSquares[2] = getForwardDiagonal(sq); // / diagonal
        checkSquares[3] = getBackwardDiagonal(sq); // \ diagonal
        checkSquares[4] = getKnightCheckSquares(sq);
        return checkSquares;
    }

    private ArrayList<Square> getFileSqsWithQueensAndRooks(Square sq, PieceColor pc) {
        ArrayList<Square> list = new ArrayList<>();
        for (Square square : getFile(sq)) {
            if (!square.isEmpty()) {
                Piece piece = square.getPiece();
                PieceColor color = piece.getColor();

                if (color == pc && (piece instanceof Queen || piece instanceof Rook)) {
                    list.add(square);
                }
            }

        }
        return list;
    }

    /**
     * takes a square, returns the squares on the same f excluding the square
     * being passed
     *
     * @param sq
     * @return
     */
    private Square[] getFile(Square sq) {
        int r = sq.getRank();
        int f = sq.getFile();
        Square[] arr = new Square[7];

        for (int i = 0, j = 0; i < 8; i++) {
            if (i != r) {
                arr[j] = squares[f][i];
                j++;
            }
        }

        return arr;
    }

    private ArrayList<Square> getRankSqsWithQueensAndRooks(Square sq, PieceColor pc) {
        ArrayList<Square> list = new ArrayList<>();
        for (Square square : getRank(sq)) {
            if (!square.isEmpty()) {
                Piece piece = square.getPiece();
                PieceColor color = piece.getColor();

                if (color == pc && (piece instanceof Queen || piece instanceof Rook)) {
                    list.add(square);
                }
            }
        }
        return list;
    }

    /**
     * takes a rank as a char, returns the squares on that rank excluding the
     * square being passed in
     *
     * @param sq
     * @return
     */
    private Square[] getRank(Square sq) {
        int r = sq.getRank();
        int f = sq.getFile();
        Square[] arr = new Square[7];
        for (int i = 0, j = 0; i < 8; i++) {
            if (i != f) {
                arr[j] = squares[i][r];
                j++;
            }
        }
        return arr;
    }

    private ArrayList<Square> getNEastDiagWithQueensAndBishops(Square sq, PieceColor pc) {
        ArrayList<Square> list = new ArrayList<>();
        for (Square square : getForwardDiagonal(sq)) {
            if (!square.isEmpty()) {
                Piece piece = square.getPiece();
                PieceColor color = piece.getColor();

                if (color == pc && (piece instanceof Queen || piece instanceof Bishop)) {
                    list.add(square);
                }
            }
        }
        return list;
    }

    /**
     * takes a square and returns all the squares on the forward tilted diagonal
     * Square[]= / excluding the square being passed
     *
     * @param sq
     * @return
     */
    private Square[] getForwardDiagonal(Square sq) {
        //get the bottom square first
        int f = sq.getFile();
        int r = sq.getRank();
        Square[] diagonal = new Square[1];
        try {
            if (f == 7 && r == 7) {
                diagonal = new Square[0];
                return diagonal;
            }
            while (f > 0 && r < 7) {
                f--;
                r++;
            }
            //get the size of the diagonal
            //if f is zero, size = rank + 1  //if rank is 7, size = 8 - f

            int size = (f == 0) ? r + 1 : 8 - f;

            //place squares in the diagonal array and return
            diagonal = new Square[size - 1]; //exclude the original square
            for (int i = 0, j = 0; i < size; i++) {
                if (f != sq.getFile() && r != sq.getRank()) {
                    diagonal[j] = squares[f][r];
                    j++;
                }
                f++;
                r--;
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in Board.getForwardDiagonal(");
            System.out.println("Diagonal Size = " + diagonal.length);
            throw ex;
        }
        return diagonal;
    }

    private ArrayList<Square> getNWestDiagonalWithQueensAndBishops(Square sq, PieceColor pc) {
        ArrayList<Square> list = new ArrayList<>();
        for (Square square : getBackwardDiagonal(sq)) {
            if (!square.isEmpty()) {
                Piece piece = square.getPiece();
                PieceColor color = piece.getColor();

                if (color == pc && (piece instanceof Queen || piece instanceof Bishop)) {
                    list.add(square);
                }
            }
        }
        return list;
    }

    /**
     * takes a square and returns all the squares in the same backward diagonal
     * \
     *
     * @param sq
     * @return
     */

    private Square[] getBackwardDiagonal(Square sq) {
        //get the top square first
        int f = sq.getFile();
        int r = sq.getRank();
        Square[] diagonal;

        while (f > 0 && r > 0) {
            f--;
            r--;
        }
        //get the size of the diagonal
        int size = (f == 0) ? 8 - r : 8 - f;

        //place squares in the diagonal array and return
        diagonal = new Square[size - 1];
        for (int i = 0, j = 0; i < size; i++) {
            if (f != sq.getFile() && r != sq.getRank()) {
                diagonal[j] = squares[f][r];
                j++;
            }
            f++;
            r++;
        }
        return diagonal;
    }


    /**
     * takes a square returns all squares that are one knight move away.
     *
     * @param sq
     * @return
     */
    private Square[] getKnightCheckSquares(Square sq) {
        Square[] arr;

        //get a knight's movement type
        Piece p = new Knight(PieceColor.BLACK);
        int[] movements = p.getMovementType();

        //combine the square's index digits to move with arithmetic
        int[] index = new int[]{sq.getFile(), sq.getRank()};
        int combine = ChessEngine.combineDigits(index);

        //get the amount of squares 
        int count = 0;

        for (int i = 0; i < movements.length; i++) {
            combine = ChessEngine.combineDigits(index);
            combine += movements[i];
            if (isOnTheBoard(combine)) 
                count++;
        }

        //initialize the array
        arr = new Square[count];

        //fill the array
        for (int i = 0, j = 0; i < movements.length; i++) {
            combine = ChessEngine.combineDigits(index);
            combine += movements[i];
            if (isOnTheBoard(combine)) {
                //split the index to get the square
                int[] temp = ChessEngine.splitDigits(combine);
                arr[j] = (temp.length < 2) ? squares[0][temp[0]] : squares[temp[0]][temp[1]];
                j++;
            }
        }
        return arr;
    }

    //ITERATOR SUBCLASS
    /**
     * Returns an Iterator to Iterate through each square on the board
     *
     * @return
     */
    public Iterator<Square> boardIterator() {
        return new BoardIterator();
    }

    /**
     * Iterator for each square on the board
     */
    private class BoardIterator implements Iterator<Square> {
        private int file;
        private int rank;

        BoardIterator() {
            rank = -1;
            file = 0;
        }

        @Override
        public boolean hasNext() {
            if (file == 7 && rank == 7) {
                return false;
            }
            return true;
        }

        @Override
        public Square next() {
            if (rank == 7) {
                file++;
                rank = 0;
            } else {
                rank++;
            }
            return squares[file][rank];
        }

    }

}
