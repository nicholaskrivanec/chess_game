package ChessGUI;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * A child class of the SquareGUI class that enables the ability to promote a pawn
 * @author nkriv_000
 */
public class PromotionSquareGUI extends SquareGUI
{
    private final double GROW_AMNT = 1.50;
    private final double SHRINK_AMNT = 1.00;
    private final ImageView knight;
    private final ImageView bishop;
    private final ImageView rook;
    private final ImageView queen;
    private final boolean white;
    
    /**
     * PromotionSquareGUI Constructor: takes two boolean values; The first determines 
     * if the square is to be dark or light, and the second determines what type of pawn promotion square it is
     * If true, the promotion pieces are white pieces; otherwise, black.
     * @param light
     * @param white 
     */
    PromotionSquareGUI(boolean light, boolean white){
        super(light);
        this.white = white;
        knight = new ImageView();
        bishop = new ImageView();
        rook = new ImageView();
        queen = new ImageView();

        if(white){
            queen.setImage(new Image(whiteQueenURL));
            queen.setId("Q");
            rook.setImage(new Image(whiteRookURL));
            rook.setId("R");
            knight.setImage(new Image(whiteKnightURL));
            knight.setId("N");
            bishop.setImage(new Image(whiteBishopURL));
            bishop.setId("B");
        }
        else{
            queen.setImage(new Image(blackQueenURL));
            queen.setId("q");
            rook.setImage(new Image(blackRookURL));
            rook.setId("r");
            knight.setImage(new Image(blackKnightURL));
            knight.setId("n");
            bishop.setImage(new Image(blackBishopURL));
            bishop.setId("b");
        }
        
        queen.setOnMouseEntered(new MouseEnterHandler());
        queen.setOnMouseExited(new MouseExitHandler());
        rook.setOnMouseEntered(new MouseEnterHandler());
        rook.setOnMouseExited(new MouseExitHandler());
        bishop.setOnMouseEntered(new MouseEnterHandler());
        bishop.setOnMouseExited(new MouseExitHandler());
        knight.setOnMouseEntered(new MouseEnterHandler());
        knight.setOnMouseExited(new MouseExitHandler());
        
        
        queen.fitHeightProperty().bind(this.heightProperty().divide(2));
        queen.fitWidthProperty().bind(this.widthProperty().divide(2));
        queen.layoutXProperty().bind(this.layoutXProperty());
        queen.layoutYProperty().bind(this.layoutYProperty());
        queen.rotateProperty().bind(this.pieceImage.rotateProperty());
        
        rook.fitHeightProperty().bind(this.heightProperty().divide(2));
        rook.fitWidthProperty().bind(this.widthProperty().divide(2));
        rook.layoutXProperty().bind(this.layoutXProperty().add(this.widthProperty().divide(2)));
        rook.layoutYProperty().bind(this.layoutYProperty());
        rook.rotateProperty().bind(this.pieceImage.rotateProperty());
        
        bishop.fitHeightProperty().bind(this.heightProperty().divide(2));
        bishop.fitWidthProperty().bind(this.widthProperty().divide(2));
        bishop.layoutXProperty().bind(this.layoutXProperty());
        bishop.layoutYProperty().bind(this.layoutYProperty().add(this.heightProperty().divide(2)));
        bishop.rotateProperty().bind(this.pieceImage.rotateProperty());
        
        knight.fitHeightProperty().bind(this.heightProperty().divide(2));
        knight.fitWidthProperty().bind(this.widthProperty().divide(2));
        knight.layoutXProperty().bind(this.layoutXProperty().add(this.widthProperty().divide(2)));
        knight.layoutYProperty().bind(this.layoutYProperty().add(this.heightProperty().divide(2)));
        knight.rotateProperty().bind(this.pieceImage.rotateProperty());
        hidePromotions();
        
    }
    
    /**
     * Overrides the setImage method; This is done for the case that a pawn enters the square.
     * For any promotion square, a pawn entering means a promotions.
     * @param image 
     */
    @Override
    public void setImage(char image){
        hidePromotions();
        if(( white && image == 'P' ) || ( !white && image == 'p' )){
            setImage(null);
            showPromotions();
        } else {
            super.setImage(image);
        }
    }
    
    /**
     * Shows and enables the promotion choices: Queen, Knight, Rook Bishop
     */
    private void showPromotions(){
        this.pieceImage.setImage(null);
        knight.setDisable(false);
        knight.setVisible(true);
        queen.setDisable(false);
        queen.setVisible(true);
        rook.setDisable(false);
        rook.setVisible(true);
        bishop.setDisable(false);
        bishop.setVisible(true);
        knight.toFront();
        bishop.toFront();
        rook.toFront();
        queen.toFront();
    }
    
    @Override
    public void showLabels(){
        super.showLabels();
        this.line.toFront();
        this.ptrsB.toFront();
        this.ptrsW.toFront();
        this.queen.setMouseTransparent(true);
        this.rook.setMouseTransparent(true);
        this.bishop.setMouseTransparent(true);
        this.knight.setMouseTransparent(true);

        
    }
    
    @Override
    public void hideLabels(){
        super.hideLabels();
        this.queen.setMouseTransparent(false);
        this.rook.setMouseTransparent(false);
        this.bishop.setMouseTransparent(false);
        this.knight.setMouseTransparent(false);
    }
    
    /**
     * Hides and disables the promotion choice images.
     */
    private void hidePromotions(){
        knight.setVisible(false);
        knight.setDisable(true);
        queen.setVisible(false);
        queen.setDisable(true);
        rook.setVisible(false);
        rook.setDisable(true);
        bishop.setVisible(false);
        bishop.setDisable(true);
    }
    
    /** 
     * Promotes the main PieceImage to a Queen image
     */
    private void promoteToQueen(){
        hidePromotions();
        this.setImage(queen.getImage());
    }
    
    /** 
     * Promotes the main PieceImage to a Rooks image
     */
    private void promoteToRook(){
        hidePromotions();
        this.setImage(rook.getImage());      
    }
    
    /**
     * Promotes the PieceImage to a Bishop
     */
    private void promoteToBishop(){
        hidePromotions();
        this.setImage(bishop.getImage()); 
    }
    
    /**
     * Promotes the PieceImage to a Knight
     */
    private void promoteToKnight(){
        hidePromotions();
        this.setImage(knight.getImage());
    }
    
    /**
     * Takes a String, or rather a single character as a String, and promotes accordingly
     * @param id 
     */
    public void promote(String id){
        switch(id.toLowerCase()){
            case "q": promoteToQueen(); break;
            case "r": promoteToRook(); break;
            case "n": promoteToKnight(); break;
            case "b": promoteToBishop(); break;
        }
    }
    
    /**
     * Returns all the imageViews of the Promotion Images
     * @return 
     */
    public ImageView[] getImageViews(){
        ImageView[] arr = new ImageView[4];
        arr[0] = queen;
        arr[1] = rook;
        arr[2] = bishop;
        arr[3] = knight;
        
        return arr;
    }
    
    /**
     * Handler for promotion images when a mouse enters: Causes the image to Scale up
     */
    private class MouseEnterHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent e){
            ImageView iv = (ImageView)e.getSource();
            iv.setScaleX(GROW_AMNT);
            iv.setScaleY(GROW_AMNT);
        }
    }
    
    /**
     * Handler for promotion images when a mouse exits: Causes the image to scale to normal size
     */
    private class MouseExitHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent e){
            ImageView iv = (ImageView)e.getSource();
            iv.setScaleX(SHRINK_AMNT);
            iv.setScaleY(SHRINK_AMNT);
        }
    }
    
}
