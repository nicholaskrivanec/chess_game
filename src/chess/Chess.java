package chess;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Chess extends Application {
    
    @Override public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ChessGUI/FXMLChessDocument.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @Override public void init() throws Exception {
        for(double num = 0.0; num < 1; num+= 0.002){
            LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(num));
            Rest();
            try {
                Thread.sleep(5L);
            } catch(InterruptedException ex){ }
        }
    }
    
    private void Rest()
    {
         try {
                Thread.sleep(5L);
            } catch(InterruptedException ex){ }
     
    }
    
    public static void main(String[] args) {
        LauncherImpl.launchApplication(Chess.class, chess.preloader.SplashScreen.class, args);
    }
}
