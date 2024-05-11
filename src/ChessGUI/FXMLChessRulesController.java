package ChessGUI;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class FXMLChessRulesController implements Initializable{
    private final WebView browser = new WebView();
    private final WebEngine engine = browser.getEngine();
    
    /**
     * @param location
     * @param resources 
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    /**
     * Handler for the Rules HyperLink
     * @param e 
     */
    @FXML
    private void onRulesHyperLinkClicked(ActionEvent e){
        if(Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI("https:////en.wikipedia.org//wiki//Rules_of_chess"));
            } catch (IOException | URISyntaxException e1) {}
        }
    }
}
