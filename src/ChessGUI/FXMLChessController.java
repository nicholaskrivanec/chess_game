package ChessGUI;

import chess.Board;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import chess.ChessEngine;
import chess.Enums.PieceColor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;
import chess.Symbol;

public class FXMLChessController implements Initializable{
    private final Font courier = Font.font("Courier", FontPosture.REGULAR, 18);
    private final String MOVE_AUDIO_PTH = "src/Audio/untitled.wav";
    private final String CHECK_AUDIO_PTH = "src/Audio/check.mp3";
    private final String CHECKMATE_AUDIO_PTH = "src/Audio/checkmate.wav";
    private final URI URI_CHECK_AUDIO = new File(CHECK_AUDIO_PTH).toURI();
    private final URI URI_CHECKMATE_AUDIO = new File(CHECKMATE_AUDIO_PTH).toURI();
    private final URI URI_MOVE_AUDIO = new File(MOVE_AUDIO_PTH).toURI();
    private final int ROTATE_AMT = 5;
    private final double VOL = 1.0;
    
    private final HashMap<String, SquareGUI> squareMap;
    private ArrayList<String> moves;
    private int positionStackId;
    private String square;
    private String selectedSquare;
    private Timeline rotateAnimation;
    private BoardGUI board;
    private ChessEngine chess;
    private boolean gameover;
    private String cpuColor;
    private AudioClip moveAudioClip;
    private AudioClip checkAudioClip;
    private AudioClip checkmateAudioClip;
    
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private BorderPane boarderPane;
    @FXML
    private GridPane gameJournal;
    @FXML
    private GridPane fileCoordinates;
    @FXML
    private GridPane rankCoordinates;
    @FXML
    private Label notificationLabel;
    @FXML
    private RadioButton rBtn;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private TextField fenPositionTxt;

    public FXMLChessController() {
        this.chess = new ChessEngine();
        this.squareMap = new HashMap<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        moveAudioClip = new AudioClip(URI_MOVE_AUDIO.toString());
        moveAudioClip.setVolume(VOL);
        checkAudioClip = new AudioClip(URI_CHECK_AUDIO.toString());
        checkAudioClip.setVolume(VOL);
        checkmateAudioClip = new AudioClip(URI_CHECKMATE_AUDIO.toString());
        checkmateAudioClip.setVolume(VOL);
        gameover = false;
        rotateAnimation = new Timeline(new KeyFrame(Duration.millis(20), e->{rotateBoard();}));
        rotateAnimation.setCycleCount(Timeline.INDEFINITE);
        square = "";
        selectedSquare = "";
        notificationLabel.setVisible(false);
        choiceBox.getItems().addAll(Symbol.BLACK, Symbol.WHITE, Symbol.NO_CPU);
        choiceBox.setValue(choiceBox.getItems().get(0));
        chess = new ChessEngine();
        cpuColor = Symbol.BLACK;
        chess.setCPUColor(cpuColor);
        fenPositionTxt.setText(chess.getStartPosition(true));
        startGame();
    }
    
    /**
     *Initializes the clocks(currently disabled)
     * initializes the board, 
     */
    private void startGame(){
        initializeBoard();
        System.out.println("Game Started");
    }
    
    /**
     * Sets the square names given in the Board class, as their interface ID;
 adds a SquareClickHandler to the squares/pieceImages,
 Disables all pieces, to prevent movement before getting validated moves.
     * Sets each square in the HashMap<String, SquareGUI> squareMap.
     * gets the start position from ChessEngine and passes it to setPosition method.
     */
    private void initializeBoard(){
        board = new BoardGUI(640);
        boarderPane.setCenter(board);
        BorderPane.setAlignment(board, Pos.CENTER);
        boarderPane.setMaxSize(680, 680);
        board.setMaxHeight(640);
        
        positionStackId = 0;
        int count = 0;
        //map out all the squares in the window
        for (String str : Board.SQRS) {
            SquareGUI sq = board.getSquare(count);
            ImageView pieceImage = sq.getImageView();
            pieceImage.setMouseTransparent(true);
            squareMap.put(str, sq);
            count++;
            
            //SQUARE MOUSE PRESSED
            sq.setOnMousePressed(new SquareClickHandler());
            
            //SQUARE MOUSE DRAGGED HANDLER
            sq.setOnMouseDragged((MouseEvent e)->{
                SquareGUI sqGui = (SquareGUI)e.getSource();
                ImageView iv = sqGui.getImageView();
                board.setCursor(Cursor.CLOSED_HAND);
                iv.setX(e.getX()- sq.getWidth()/2);
                iv.setY(e.getY()- sq.getHeight()/2);
            });
            
            //SQUARE DRAG DETECTED HANDLER
            sq.setOnDragDetected(e->{
                SquareGUI sqGui = (SquareGUI)e.getSource();
                sqGui.startFullDrag();
            });            
            
            //SQUARE MOUSE DRAG ENTERED HANDLER
            sq.setOnMouseDragEntered(e->{
                SquareGUI sqEntered = (SquareGUI)e.getSource();
                ImageView iv = ((SquareGUI)e.getGestureSource()).getImageView();
                sqEntered.toFront();
                sqEntered.getImageView().toFront();
                iv.toFront();  
                if(moves.contains(sqEntered.getId())){
                    sqEntered.setScaleX(1.125);
                    sqEntered.setScaleY(1.125);
                }
            });
            
            //SQUARE MOUSE DRAG EXITED HANDLER
            sq.setOnMouseDragExited(e->{
                SquareGUI sqExited = (SquareGUI)e.getSource();
                sqExited.setScaleX(1);
                sqExited.setScaleY(1);
            });

            //SQUARE MOUSE DRAG RELEASED HANDLER
            sq.setOnMouseDragReleased(e->{
                SquareGUI fromSq = (SquareGUI)e.getGestureSource();
                SquareGUI toSq = (SquareGUI)e.getSource();
                if(moves.contains(toSq.getId())){
                    String from = fromSq.getId();
                    String to = toSq.getId();
                    tryMove(from, to, false);
                }
            });
            
            //SQUARE MOUSE RELEASE HANDLER
            sq.setOnMouseReleased(e->{
                SquareGUI sqGui = (SquareGUI)e.getSource();
                ImageView iv = sqGui.getImageView();
                if(sqGui.hasPiece()){
                    board.setCursor(Cursor.OPEN_HAND);
                } else {
                    board.setCursor(Cursor.DEFAULT);
                }
                iv.setX(0);
                iv.setY(0);
            });    
        }
        setPosition(chess.getStartPosition(true));

    }
    
    /**
     * Handles the Square mouse events
     */
    private class SquareClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {
            //get the square the mouse is in at time of click
            SquareGUI sq = (SquareGUI)e.getSource();
            sq.selectSquare();
            square = sq.getId();
            String to = square;
            String from = selectedSquare;
            ImageView pieceImage = sq.getImageView();
            sq.toFront();
            pieceImage.toFront();
            
            if(e.getButton() == MouseButton.PRIMARY && e.getButton() != MouseButton.SECONDARY){
                unfocusSquares();
                if(sq.hasPiece())
                    sq.setCursor(Cursor.OPEN_HAND);
                
                //if a piece is on the square, render the piece in front 
                if(sq instanceof PromotionSquareGUI)
                    for(ImageView iv : ((PromotionSquareGUI) sq).getImageViews())
                        iv.toFront();
                
                if (positionStackId != 0) {
                    setPosition(chess.getPosition());
                    positionStackId = 0;
                }
                
                squareMap.get(square).highlightSquare();
                
                //if no square is selected, try selecting the clicked square
                if (from.trim().isEmpty()) {
                    trySelectingSquare( to );
                    e.setDragDetect( true ); 
                } else {
                    tryMove( from, to );
                }
                
            } else if (e.getButton() == MouseButton.SECONDARY ){   
                if (sq.isHighlightedBold()){
                    sq.unFocusSquare();
                } else {
                    sq.hightlightBold();   
                    System.out.println(sq.getId() + "\n\tPointers WHITE = " + chess.getPointers(to, PieceColor.WHITE));
                    System.out.println("\tPointers BLACK = " + chess.getPointers(to, PieceColor.BLACK));
                }
                
            } else {
                e.setDragDetect(false);
            }
        }
    }
    

    private void unfocusSquares() {
        squareMap.values().forEach((sq) -> { 
            sq.unFocusSquare();
        });
    }

 
    private void flipAllCoordinates() {
        for (Label label : getCoordinateLabels()) 
            label.setText(flipCoordinate(label.getText()));
    }

    /**
     * Takes a coordinate as a String and returns its relative opposite.
     * (e.g. a1 => h8)
     * @param str
     * @return 
     */
    private String flipCoordinate(String str) {
        char ch = str.charAt(0);
        ch = (ch >= 'a' && ch <= 'h') ? (char)('a' + 'h' - ch) : (char)('1' + '8' - ch);
        return "" + ch;
    }
    
    /**
     * Returns a Label array of each of the FXML coordinate labels within the 
     * rankCoordinates/fileCoordinates GridPanes
     * @return 
     */
    private Label[] getCoordinateLabels() {
        Label[] labels = new Label[16];
        int labelCount = 0;
        for (Node node : rankCoordinates.getChildren()) {
            if (node instanceof Label) {
                labels[labelCount] = (Label) node;
                labelCount++;
            }
        }
        for (Node node : fileCoordinates.getChildren()) {
            if (node instanceof Label) {
                labels[labelCount] = (Label) node;
                labelCount++;
            }
        }
        return labels;
    }

    /**
     * Takes two squares as Strings and animates an image from the first to the second on the board.
     * @param from
     * @param to 
     */
    private void moveImage(String from, String to) {
        selectedSquare = "";
        board.disableAll();
        
        //get the image from the square
        SquareGUI sq1 = squareMap.get(from);
        SquareGUI sq2 = squareMap.get(to);
        
        ImageView iv = new ImageView();
        iv.setImage(sq1.getImageView().getImage());
        sq1.getImageView().setImage(null);

        //create a line start at from and ending at to
        double x1 = sq1.getLayoutX() + sq1.getWidth() / 2;
        double y1 = sq1.getLayoutY() + sq1.getHeight() / 2;
        double x2 = sq2.getLayoutX() + sq2.getWidth() / 2;
        double y2 = sq2.getLayoutY() + sq2.getHeight() / 2;
        Line line = new Line(x1, y1, x2, y2);
        board.getChildren().add(iv);

        //add the line to a path transition
        PathTransition pt = new PathTransition();
        pt.setDuration(Duration.millis(225));
        pt.setPath(line);
        iv.setRotate(sq1.getImageView().getRotate());

        //unbind the imagePane from the rectangle
        sq1.unbindCenter();
        pt.setNode(iv);
        
        pt.setOnFinished(e -> {
            moveAudioClip.play();
            board.enableAll();
            board.getChildren().remove(iv);
            updateGameJournal();
            if(rBtn.isSelected()){
                for(int i = 0; i < 180/ROTATE_AMT; i++){
                    rotateBoard();
                }
            }
            if (chess.isCPUTurn()){
                moveCPU();
            }
        });
        
        pt.play();
    }
    
    /**
     * Uses the BoardGUI class to disable every square on the board except the one passed.
     * This method may also be used to disable controls not directly on the BoardGUI.
     * @param sqGui 
     */
    private void disableControlsExcept(PromotionSquareGUI sqGui){
        board.disableAllBut(sqGui);
    }
    
    /**
     * Reverses actions done by the disableControlsExcept method
     */
    private void enableControls(){
        board.enableAll();
    }
    
    private void tryMove(String from, String to){
        tryMove(from, to, true);
    }
    
    /**
     * This method attempts to move a piece from the given String square name to the next.
     * Sends the move to the ChessEngine class to be analyzed.
     * Also, gets any needed Pawn promotion image info, GameJournal entries;
     * @param from
     * @param to 
     */
    private void tryMove(String from, String to, boolean animate) {
        if(gameover){ 
            return;
        }
        if (moves.contains(to)) {    
            unfocusSquares();
            
            // check if it is a pawn promotion
            if (chess.isPawnPromotion(from, to)) {
                PromotionSquareGUI sqGui = (PromotionSquareGUI) squareMap.get(to);
                disableControlsExcept(sqGui);
                for(ImageView iv : sqGui.getImageViews()){
                    iv.setOnMouseClicked(e->{
                        //get the image id that was clicked and pass to engine
                        String pieceName = iv.getId();
                        sqGui.promote(iv.getId());
                        chess.promote(to, pieceName);
                        enableControls();
                    });
                }
            }
            chess.move(from, to);
            if (animate == true){
                moveImage(from, to);
            } else {
                updateGameJournal();
                moveAudioClip.play();
                if(rBtn.isSelected()){
                    for(int i = 0; i < 180/ROTATE_AMT; i++){
                        rotateBoard();
                    }
                }
                if (chess.isCPUTurn()){
                    moveCPU();
                }
            }
        } else {
            trySelectingSquare(to);
        }
    }
    
    /**
     * Does nothing at the moment
     * @param white 
     */
    private void highlightKingSquares(boolean white){
        
    }
    
    private void moveCPU(){
        if(chess.isCPUTurn()){
            String cpuMove = chess.getCPUMove();
            String cpuFrom = ""+cpuMove.charAt(0) + cpuMove.charAt(1);
            String cpuTo = "" + cpuMove.charAt(2) + cpuMove.charAt(3);
            chess.move(cpuFrom, cpuTo);
            moveImage(cpuFrom, cpuTo);
        }
    }

    /**
     * Takes a String containing the chess notation from the game journal;
     * Writes a message using the notification Label, that elaborates on the game results;
     * Declares the winner.
     * @param winner 
     */
    private void displayWinner(String winner) {
        //unhighlight squares and freeze the board position.
        unfocusSquares();
        board.disableAll();
        
        
        //determine how the game ended
        String message = "";
        
        //by Draw
        if (winner.contains(Symbol.DRAW_GAME)){
            message = "DRAW GAME: ";
            if(winner.contains(Symbol.STALE_MATE)){
                message += " Stalemate";
            }else if(winner.contains(Symbol.FIFTY)){
                message += " 50 moves without a capture or a pawn move";
            }else if(winner.contains(Symbol.THREE)){
                message += " Three-fold repitition";
            }    
            
        //by Checkmate
        } else if (winner.contains(Symbol.CHECK_MATE)){
            //find the king square in checkmate and surround in hightlighted squares
            message = "CHECKMATE: ";
            if(winner.contains(Symbol.WHITE_WINS)){
                message += "White wins!";
                highlightKingSquares(true);
            } else {
                message += "Black wins!";              
            }
            checkmateAudioClip.play();
  
        //by Resignation
        } else if (winner.contains(Symbol.WHITE_WINS)){
                message = "White wins! Black resigns";
        } else if (winner.contains(Symbol.BLACK_WINS)){
                message = "Black wins! White resigns";
        }
       
        gameover = true;
        notificationLabel.setText(message);
        notificationLabel.setVisible(true);
    }

    /**
     * Records in the gameJournal GridPane the moves that have been played.
     * Makes a link to that specific position, and handles the core content of the 
     * undo, back, forward, rewind, and fast-forward button functions.
     */
    private void updateGameJournal() {
        //get the row and column being changed in gameJournal
        if (chess.getNumberOfMoves() == 0) {
            return;
        }
        int totalMoves = chess.getNumberOfMoves()-1;
        int rowIndex = totalMoves / 2;
        Text move = new Text(chess.getLastMove());
        move.setId("" + totalMoves);
        move.setFontSmoothingType(FontSmoothingType.LCD);
        move.setFont(courier);
        
        move.setOnMouseEntered(e->{
            move.setUnderline(true);
            move.setFill(Color.MEDIUMSLATEBLUE);
        });
        move.setOnMouseExited(e->{
            move.setUnderline(false);
            move.setFill(Color.BLACK);
        });
        
        if (!chess.isWhitesTurn()) {
            int moveNum = rowIndex + 1;
            String moveNumStr = "" + moveNum;
            Text num = new Text(moveNumStr);
            num.setFont(courier);
            num.setFontSmoothingType(FontSmoothingType.LCD);
            gameJournal.add(num, 0, rowIndex);
            gameJournal.add(move, 1, rowIndex);
            GridPane.setHalignment(num, HPos.CENTER);
            GridPane.setHalignment(move,HPos.CENTER);
        } else {
            gameJournal.add(move, 2, rowIndex - 1);
            GridPane.setHalignment(move, HPos.CENTER);
        }
        move.setOnMouseClicked(e->{
            Text txtMove = (Text)e.getSource();
            positionStackId = chess.getNumberOfMoves() - 1 - Integer.parseInt(txtMove.getId());
            selectJournalEntry();
            e.consume();
        });
        reviewGameJournal(move.getText());
        selectJournalEntry();
        scrollPane.setVvalue(1);
    }

    /** 
     *  Reviews the last entry in the GameJournal, to adjust the notification label accordingly.
     */
    private void reviewLastEntry(){
        //get the last entry
        String move;
        if(!gameJournal.getChildren().isEmpty()){
            int index = gameJournal.getChildren().size()-1;
            move = ((Text)(gameJournal.getChildren().get(index))).getText();
            reviewGameJournal(move);
        }
        
        //review it
    }
    
    /**
     * Checks the last journal entry to see if any significant game changes have been
     * declared by the chess engine. This function eliminates any unecessary links to the ChessEngine Class
     * @param move 
     */
    private void reviewGameJournal(String move){ //check for checkmate in the gameJournal
        if  ( 
                move.contains(Symbol.CHECK_MATE) ||
                move.contains(Symbol.WHITE_WINS) ||
                move.contains(Symbol.BLACK_WINS) ||
                move.contains(Symbol.DRAW_GAME) ||
                move.contains(Symbol.STALE_MATE)){
            displayWinner(move);
        } else if (move.contains(Symbol.CHECK)){
            notificationLabel.setText("CHECK");
            notificationLabel.setVisible(true);
            checkAudioClip.play();
        } else {
            notificationLabel.setVisible(false);
        }
    }
    
    /**
     * Removes the last move made in the GameJournal
     */
    private void removeLastJournalEntry() {
        int size = gameJournal.getChildren().size();
        if (size != 0) {
            if (( size - 2 ) % 3 == 0) {
                GridPane.clearConstraints(gameJournal.getChildren().get(size-1));
                GridPane.clearConstraints(gameJournal.getChildren().get(size-2));
                gameJournal.getChildren().remove(size-1);
                gameJournal.getChildren().remove(size-2);
                
            }else{
                GridPane.clearConstraints(gameJournal.getChildren().get(size-1));
                gameJournal.getChildren().remove(size-1);   
            }
            chess.undoMove();
        }
    }
    
    /**
     * removes all children within the gameJournal
     */
    private void clearGameJournal(){
        while(gameJournal.getChildren().size() > 0){
            removeLastJournalEntry();
        }
    }
    
    private Text getJournalEntry(int i){
        Text txt = new Text();
        int index = ( i + (i-1)/2 );
        try{
        txt = (Text)gameJournal.getChildren().get(index);
        } catch(Exception ex){
            System.out.println("Exception in getJournalEntry at i = " + i);
            System.out.println("index = " + index);
            System.out.println("positionId = " + positionStackId);
            System.out.println("gameJournal size = " + gameJournal.getChildren().size());
        }
        return txt;
        /*
            f(n) = n + (n-1)/2
        */ 
    }
    
    private void selectJournalEntry(){
        gameJournal.getChildren().stream().filter((node) -> (node instanceof Text)).map((node) -> (Text)node).map((txt) -> {
            txt.setFill(Color.BLACK);
            return txt;
        }).forEachOrdered((txt) -> {
            txt.setUnderline(false);
        });
        int index = chess.getNumberOfMoves() - positionStackId - 1;
        if(index > chess.getNumberOfMoves() || index < 0){
            return;
        }
        if(index == 0){
            setPosition(chess.getStartPosition());
            return;
        }
        Text text = getJournalEntry(index);
        text.setFill(Color.BLUE);
        text.setUnderline(true);
        unfocusSquares();
        String pos = chess.getPosition(text.getId());
        fenPositionTxt.setText(pos);
        setPosition(pos);
    }
    
    /**
     * Shows a view of the last position relative to the current one being viewed.
     */
    private void back() {
        if (positionStackId++ == chess.getNumberOfMoves()-1) {
            positionStackId--;
            setPosition(chess.getStartPosition());
            return;
        }
        selectJournalEntry();
    }

    /**
     * Shows a view of the next position relative to the current one being viewed,
     */
    private void forward() {
        if (positionStackId-- == 0) {
            positionStackId = 1;
            return;
        }
        selectJournalEntry();
    }
    
    /**
     * Rotates the board, along with the coordinate labels
     */
    private void rotateBoard(){
        if(board.getRotate() == 355){
            board.setRotate(0.0);
        } else {
            board.setRotate(board.getRotate() + ROTATE_AMT);
        }
        double rot = board.getRotate();
        int rotationAmnt;
        if(rot == 90 || rot == 270){
            rotationAmnt = -ROTATE_AMT;
            flipAllCoordinates();
        } else if((rot > 90 && rot < 180 ) || (rot > 270 && rot < 360)){
            rotationAmnt = -ROTATE_AMT;
        } else {
            rotationAmnt = ROTATE_AMT;
        }
        for(Label lb : getCoordinateLabels()){
            lb.setRotate(lb.getRotate()+rotationAmnt);
        }
    }
    
    private void rotateBoard(PieceColor pc){
        if(pc == PieceColor.WHITE){
            while(board.getRotate() != 0.0){
                rotateBoard();
            }
        } else {
            while(board.getRotate() != 270.0) {
                rotateBoard();
            }
        }
    
    }
   
    /**
     * Takes a String FEN position and sets the board images accordingly using 
     * the BoardGUI class;
     * Start Position String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"
     * @param position 
     */
    private void setPosition(String position) {
        String arr[] = position.trim().split("/");
        char rank = '8';
        final char file = 'a';
        int num = 0;
        char ch = file;
        try{
            for (int i = 0; i < arr.length; i++, rank--) {
                ch = file;
                
                for (int j = 0; j < arr[i].length(); j++, ch++) {
                    if (Character.isDigit(arr[i].charAt(j))){
                        num = Integer.parseInt(""+arr[i].charAt(j));
                        for (int k = 0; k < num; k++){
                            squareMap.get("" + ch + rank).setImage('-');
                            ch++;
                        }
                        ch--;
                    } else {
                        squareMap.get("" + ch + rank).setImage(arr[i].charAt(j));
                    }
                }
            }
            System.out.println("Position = " + position);
        }catch (NumberFormatException ex){
            System.out.println("Exception Caught in setPosition");
            System.out.println("Position = " + position);
            System.out.println("arr = " + Arrays.toString(arr));
            System.out.println("At file: " + ch + " rank: " + rank + "\tnum = " + num);
        }
    }

    /**
     * Selects a square, passes the name to the ChessEngine class, to get the 
     * valid moves for that square.
     * @param sq 
     */
    private void trySelectingSquare(String sq) {
        moves = chess.getMoves(sq);
        
        if (!moves.isEmpty()) {
            selectedSquare = sq;
            moves.forEach((move) -> {
                squareMap.get(move).highlightSquare();
            });
        } else {
            unfocusSquares();
            selectedSquare = "";
        }
    }

    /**
     * Handles the Resign Button, which ends the game
     * @param event
     * @throws IOException 
     */
    @FXML
    private void OnResignClicked(ActionEvent event) throws IOException {
        chess.resign();
        String results = (chess.isWhitesTurn()) ? 
            Symbol.BLACK_WINS : Symbol.WHITE_WINS;
        
        displayWinner(results);
    }

    /**
     * Handles the Flip Button that flips and reorients the board
     * @param event 
     */
    @FXML
    private void OnFlipClick(ActionEvent event) {
        double rot = board.getRotate();
        int rotateAmnt;
        //find the nearest angle to add to the rotation to 0 or 180
        if(rot >= 180){
            rotateAmnt = 72 -((int)rot/5);
        } else {
            rotateAmnt =  36 - ((int)rot/5);
        }
        for(int i = 0; i < rotateAmnt; i++){
            rotateBoard();
        }
    }

    /**
     * Handles the Back Button, which shows the previous position.
     * @param event 
     */
    @FXML
    private void onBackButtonClicked(ActionEvent event) {
        unfocusSquares();
        back();
    }

    /**
     * Handles the forward button, which shows the position after the one being viewed.
     * If there are no more positions to view, this button should do nothing.
     * @param event 
     */
    @FXML
    private void onForwardButton(ActionEvent event) {
        unfocusSquares();
        forward();
    }

    /**
     * Handles the fast forward button; Shows the most recent position
     * @param event 
     */
    @FXML
    private void onFastForwardButton(ActionEvent event) {
        unfocusSquares();
        positionStackId = 0;
        selectJournalEntry();
    }

    /**
     * Handles the rewind button; Shows the starting position;
     * @param event 
     */
    @FXML
    private void onRewindButton(ActionEvent event) {
        unfocusSquares();
        positionStackId = chess.getNumberOfMoves()-1;
        selectJournalEntry();
    }

    /**
     * Handles the undo button; Undoes the last move made as if it never was played.
     * @param event 
     */
    @FXML
    private void onUndoButton(ActionEvent event) {
        selectedSquare = "";
        enableControls();
        unfocusSquares();
        if (chess.isCPUTurn()) {
            removeLastJournalEntry();
            setPosition(chess.getPosition());
            reviewLastEntry();
        } else {
            removeLastJournalEntry();
            removeLastJournalEntry();
            setPosition(chess.getPosition());
        }
    }

    /**
     * Handles the help menu buttons; Loads the FXMLChessRules screen
     * @param event
     * @throws IOException 
     */
    @FXML
    private void onHelpMenuButton(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLChessRules.fxml"));
        Parent root2 = (Parent)loader.load();
        stage.setScene(new Scene(root2));
        stage.setTitle("Rules");
        stage.show();
    }

    /**
     * Restarts the game from the beginning position
     * @param event 
     */
    @FXML
    private void onNewGame(ActionEvent event) {
        gameover = false;
        notificationLabel.setVisible(false);
        notificationLabel.setText("CHECK");
        unfocusSquares();
        clearGameJournal();
        setPosition(chess.getPosition());
        chess.newGame(choiceBox.getValue());
        
        if (cpuColor.equals(Symbol.WHITE)) 
            moveCPU();
    }
    
    /**
     * Handles the rotate button; Plays the rotateAnimation;
     * @param event
     * @throws InterruptedException 
     */
    @FXML
    private void onRotateDown(MouseEvent event) throws InterruptedException {
        rotateAnimation.play();
    }

    /**
     * Handles the Rotate Button being released; Pauses the animation. 
     * This method may need to call the stop method. I'm not sure what the difference is
     * with garbage collection.
     * @param event 
     */
    @FXML
    private void onRotateReleased(MouseEvent event) {
        rotateAnimation.pause();
    }
    
    @FXML
    private void onShowBalance(ActionEvent event){
        
        
        for(String str : Board.SQRS){
            SquareGUI sqGui = squareMap.get(str);
            sqGui.setWhiteLabel("" + chess.getPointers(str, PieceColor.WHITE).size());
            sqGui.setBlackLbel("" + chess.getPointers(str, PieceColor.BLACK).size());
            
            if(sqGui.labelsShowing()){
                sqGui.hideLabels();
            }else{
                sqGui.showLabels();
            }
            
        }
    }
    
    /**
     * Handles the Return Button; Returns the user to the previous screen.
     * @param event
     * @throws IOException 
     */
    @FXML
    private void onReturnButtonClicked(ActionEvent event) throws IOException{
        chess.killFish();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/games/FXMLMain.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Menu");
        stage.show();
    }
    
    @FXML
    private void onCPUColorChanged(MouseEvent event){
        chess.setCPUColor((String)choiceBox.getValue());
    }

}
