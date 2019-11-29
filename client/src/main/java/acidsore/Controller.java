package acidsore;
import io.netty.channel.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;



public class Controller implements Initializable {
    @FXML
    HBox bottomPanel;
    @FXML
    Button Auth;
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    Text errorMessage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            try {
                NettyClient.getInstance().openConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



     public void auth() throws Exception, InterruptedException  {
        String authInfo ="auth"+" "+loginField.getText()+" "+passwordField.getText();
         NettyClient.getInstance().sendMsg(authInfo);
         NettyClient.getInstance().getNettyClientHandler().setAuthAction(()-> {
             Platform.runLater(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         createClientWindow();
                     } catch (IOException e) {
                         e.printStackTrace();
                     } catch (SQLException e) {
                         e.printStackTrace();
                     }
                 }
             });
           });
    }
   // opens new window
    public void createClientWindow() throws IOException, SQLException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/clientWindow.fxml"));
        stage.setTitle("CloudStorage");
        stage.setScene(new Scene(root, 450, 450));
        Stage oldStage = (Stage) Auth.getScene().getWindow();
        oldStage.close();
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
            stage.close();
                Platform.exit();
                System.exit(0);
            }
        });

    }
}