package ChessGUI;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * Square interface/Piece interface for the Chess Board
 * @author nkriv_000
 */
public class SquareGUI extends Rectangle
{
    protected final String whitePawnURL = "/image/WhitePawn.png";
    protected final String whiteKnightURL = "/image/WhiteKnight.png";
    protected final String whiteBishopURL = "/image/WhiteBishop.png";
    protected final String whiteRookURL = "/image/WhiteRook.png";
    protected final String whiteQueenURL = "/image/WhiteQueen.png";
    protected final String whiteKingURL = "/image/WhiteKing.png";
    protected final String blackPawnURL = "/image/BlackPawn.png";
    protected final String blackKnightURL = "/image/BlackKnight.png";
    protected final String blackBishopURL = "/image/BlackBishop.png";
    protected final String blackRookURL = "/image/BlackRook.png";
    protected final String blackQueenURL = "/image/BlackQueen.png";
    protected final String blackKingURL = "/image/BlackKing.png";
    protected final Image whitePawn = new Image(whitePawnURL);
    protected final Image whiteBishop = new Image(whiteBishopURL);
    protected final Image whiteKnight = new Image(whiteKnightURL);
    protected final Image whiteRook = new Image(whiteRookURL);
    protected final Image whiteQueen = new Image(whiteQueenURL);
    protected final Image whiteKing = new Image(whiteKingURL);
    protected final Image blackPawn = new Image(blackPawnURL);
    protected final Image blackBishop = new Image(blackBishopURL);
    protected final Image blackKnight = new Image(blackKnightURL);
    protected final Image blackRook = new Image(blackRookURL);
    protected final Image blackQueen = new Image(blackQueenURL);
    protected final Image blackKing = new Image(blackKingURL);
    
    
    private final double WIDTH_BOLD = 4.0;
    private final double WIDTH_NORMAL = 2.0;
    public static final Color LIGHT = Color.PAPAYAWHIP;
    public static final Color LIGHT_BOLD = Color.web("0xB99A69");
    public static final Color DARK = Color.SKYBLUE;
    public static final Color DARK_BOLD = Color.web("0x3C97BF");
    private final Color color;
    protected ImageView pieceImage;
    protected Label ptrsW, ptrsB;
    protected Line line;
    private boolean labelsShowing;
    /**
     * SquareGUI constructor; Takes a boolean. If true, the square is painted as
     * a light square; otherwise, it is painted as a darkSquare.
     * @param isLight 
     */
    public SquareGUI( boolean isLight){
        //create a square
        super();
        color = (isLight)? LIGHT : DARK;
        this.heightProperty().bindBidirectional(this.widthProperty());
        
        labelsShowing = false;
        line = new Line();
        line.startXProperty().bind(this.xProperty());
        line.startYProperty().bind(this.yProperty());
        line.endXProperty().bind(this.xProperty().add(this.widthProperty()));
        line.endYProperty().bind(this.yProperty().add(this.heightProperty()));
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(WIDTH_BOLD);
        line.layoutXProperty().bind(this.layoutXProperty());
        line.layoutYProperty().bind(this.layoutYProperty());
        ptrsW = new Label();
        ptrsB = new Label();
        ptrsW.setText("-");
        ptrsB.setText("-");
        ptrsW.layoutXProperty().bind(this.layoutXProperty().add(this.widthProperty().divide(4)));
        ptrsW.layoutYProperty().bind(this.layoutYProperty().add(this.heightProperty().divide(4)));
        ptrsB.layoutXProperty().bind(this.layoutXProperty().add(this.widthProperty().multiply(3).divide(4)));
        ptrsB.layoutYProperty().bind(this.layoutYProperty().add(this.heightProperty().multiply(3).divide(4)));
         

        this.setFill(isLight?LIGHT:DARK);


        //create an image view, bind it to the center of the rectangle
        pieceImage = new ImageView();
        pieceImage.layoutXProperty().bind(this.layoutXProperty());
        pieceImage.layoutYProperty().bind(this.layoutYProperty());
        pieceImage.scaleXProperty().bind(this.scaleXProperty());
        pieceImage.scaleYProperty().bind(this.scaleYProperty());
        
        this.setStrokeType(StrokeType.INSIDE);
        
        //set the piece cursor to OPEN_HAND
        pieceImage.setCursor(Cursor.OPEN_HAND);

        line.setVisible(false);
        ptrsB.setVisible(false);
        ptrsW.setVisible(false);
        labelsShowing = false;
    }
    
    /**
     * Returns true if the square has a pieceImage
     * @return 
     */
    public boolean hasPiece(){
        return (pieceImage.getImage() != null);
    }
    
    /**
     * Disables the ImageView of the pieceImage
     */
    public void disablePiece(){
        pieceImage.setDisable(true);
    }
    
    /**
     * Enables the ImageView of the pieceImage
     */
    public void enablePiece(){
        pieceImage.setDisable(false);
    }
    
    /**
     * Highlights the square with a border
     */
    public void highlightSquare(){
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(WIDTH_NORMAL);
    }
    
    /**
     * Highlights the square with a bold border, and red fill
     */
    public void hightlightBold(){
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(WIDTH_BOLD);
        this.setStyle("-fx-fill: red");
    }
    
    public void selectSquare() {
         this.setFill((color == LIGHT ) ? LIGHT_BOLD : DARK_BOLD);
    }
    
    public void unselectSquare(){
        if(color == LIGHT){
            this.setFill(LIGHT);
        }else{
            this.setFill(DARK);
        }
    }
    
    public void setWhiteLabel(String str){
        ptrsW.setText(str);
    }
    
    public void setBlackLbel(String str){
        ptrsB.setText(str);
    }
    
    public boolean labelsShowing(){
        return labelsShowing;
    }
    
    public void showLabels(){
        labelsShowing = true;
        pieceImage.setVisible(false);
        pieceImage.setDisable(true);
        ptrsB.toFront();
        ptrsW.toFront();
        line.setVisible(true);
        line.toFront();
        ptrsB.setVisible(true);
        ptrsW.setVisible(true);
    }
    
    public void hideLabels(){
        pieceImage.setDisable(false);
        pieceImage.setVisible(true);
        pieceImage.toFront();
        line.setVisible(false);
        ptrsB.setVisible(false);
        ptrsW.setVisible(false);
        labelsShowing = false;
    }
    
    
    /**
     * The piece represented as a single character and sets the square image 
     * accordingly. Default value of null
     * @param image 
     */
    public void setImage(char image){
        switch (image) {
            case 'p':  pieceImage.setImage(blackPawn);       break;
            case 'P':  pieceImage.setImage(whitePawn);       break;
            case 'n':  pieceImage.setImage(blackKnight);     break;
            case 'N':  pieceImage.setImage(whiteKnight);     break;
            case 'b':  pieceImage.setImage(blackBishop);     break;
            case 'B':  pieceImage.setImage(whiteBishop);     break;
            case 'r':  pieceImage.setImage(blackRook);       break;
            case 'R':  pieceImage.setImage(whiteRook);       break;
            case 'q':  pieceImage.setImage(blackQueen);      break;
            case 'Q':  pieceImage.setImage(whiteQueen);      break;
            case 'k':  pieceImage.setImage(blackKing);       break;
            case 'K':  pieceImage.setImage(whiteKing);       break;
            default:   pieceImage.setImage(null); break;
        }
        
        if(image == '-'){
            disablePiece();
            this.setCursor(Cursor.DEFAULT);
        } else {
            enablePiece();
            this.setCursor(Cursor.OPEN_HAND);
            this.pieceImage.toFront();
        }
    }
    
    /**
     * Takes an Image and sets it to the square
     * @param image 
     */
    public void setImage(Image image){
        this.pieceImage.setImage(image);
        this.pieceImage.toFront();
    }
    
    /**
     * Unhighlights the square, if highlighted
     */
    public void unFocusSquare(){
        this.setStrokeWidth(WIDTH_NORMAL);
        this.setStroke(null);
        this.setStyle(null);
        unselectSquare();
        
    }

    /**
     * Unbinds the ImageView from the Rectangle layout, to move
     */
    public void unbindCenter(){
        pieceImage.layoutXProperty().unbind();
        pieceImage.layoutYProperty().unbind();
    }
    
    /**
     * Binds the ImageView layout properties to teh rectangles width and height properties;
     * Used to center the image when resizing. This method may also find use to determine that a piece has been moved;
     * Depending on if the image is bound initially.
     */
    public void bindCenter(){
        pieceImage.setX(0);
        pieceImage.setY(0);
        pieceImage.setTranslateX(0);
        pieceImage.setTranslateY(0);
        pieceImage.layoutXProperty().bind(this.widthProperty().divide(2).add(this.xProperty()));
        pieceImage.layoutYProperty().bind(this.heightProperty().divide(2).add(this.yProperty()));
    }

    /**
     * Returns the imageView piece.
     * @return 
     */
    public ImageView getImageView(){
        return this.pieceImage;
    }
    
    /**
     * Binds the square rotation property relative to the piece
     * @param rotate 
     */
    public void bindRotateProperty( DoubleProperty rotate){
        pieceImage.rotateProperty().bind(rotate.multiply(-1));
    }
    
    /**
     * Returns true if the square is highlighted bold
     * @return 
     */
    public boolean isHighlightedBold(){
        return (this.getStrokeWidth() == WIDTH_BOLD);
    }
    
    public Line getDividingLine(){
        return this.line;
    }
 
}
