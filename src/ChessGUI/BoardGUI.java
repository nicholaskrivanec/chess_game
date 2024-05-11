package ChessGUI;

import chess.Board;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Used to display the Chess Board Interface
 * @author nkriv_000
 */
public class BoardGUI extends Pane
{
    private final SquareGUI[] board;
    private String selectedSquare;
    
    /**
     * BoardGUI constructor; Takes a double represented as a width/height of the board;
     * resizes and defines the properties of the squares accordingly
     * @param length 
     */
    public BoardGUI(double length){
        //size the pane with height bound to width
        super();
        super.setWidth(length);
        
        //bind the height with the width bidirectionally
        this.minWidthProperty().bindBidirectional(this.minHeightProperty());
        this.maxWidthProperty().bindBidirectional(this.maxHeightProperty());
        this.prefHeightProperty().bindBidirectional(this.prefWidthProperty());
        super.setWidth(length);
        super.setHeight(length);
        board = new SquareGUI[64];
        int count = 0;
        boolean light = true;
        
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(count <= 7){
                    PromotionSquareGUI sq = new PromotionSquareGUI(light, true);
                    ImageView[] arr = sq.getImageViews();
                    board[count] = sq;
                    super.getChildren().addAll( 
                            board[count], board[count].getImageView(), 
                            arr[0], arr[1], arr[2], arr[3]);
                } else if(count > 55){
                    PromotionSquareGUI sq = new PromotionSquareGUI(light, false);
                    ImageView[] arr = sq.getImageViews();
                    board[count] = sq;
                    super.getChildren().addAll( 
                            board[count], board[count].getImageView(), 
                            arr[0], arr[1], arr[2], arr[3]);
                } else{
                    board[count] = new SquareGUI(light);
                    super.getChildren().addAll(board[count], board[count].getImageView());
                    super.getChildren().addAll(board[count].getDividingLine(), board[count].ptrsB, board[count].ptrsW);
                    
                }
                
                board[count].widthProperty().bind(this.widthProperty().divide(8));
                board[count].layoutXProperty().bind(this.widthProperty().divide(8).multiply(j));
                board[count].layoutYProperty().bind(this.widthProperty().divide(8).multiply(i));
                board[count].bindRotateProperty(this.rotateProperty());
                board[count].setId(Board.SQRS[count]);
                light = !light;
                count++;
            }
            light = !light;
        }
    }
    
    /**
     * returns a Squares GUI from the specified position in the SquareGUI array.
     * @param i
     * @return 
     */
    public SquareGUI getSquare(int i){  
        return board[i];
    }
    
    public String getSelectedSquare(){
        return this.selectedSquare;
    }

    /**
     * Disables all the event handlers for the squares except the PromotionSquareGUI square being passed.
     * This is for Pawn promotion purposes with PromotionSquareGUIs
     * @param sq 
     */
    public void disableAllBut(PromotionSquareGUI sq){
        for(SquareGUI currSq : board){
            if(sq != currSq){
                currSq.setDisable(true);
            }
        }
    }
    
    /**
     * Disables all square handlers, for the event of a player resigning, time...etc.
     */
    public void disableAll(){
        for(SquareGUI currSq : board){
            currSq.disablePiece();
        }
    }
    
    /**
     * Reverses any disable methods
     */
    public void enableAll(){
        for(SquareGUI currSq : board){
            currSq.setDisable(false);
        }
    }
}
