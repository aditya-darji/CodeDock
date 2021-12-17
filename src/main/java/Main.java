import Login.LoginController;
import NewFile.NewFileController;
import UtilClasses.ConnectionUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main extends Application {

    public Socket socket;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        try{
            // Create a socket to connect to the server
            socket = new Socket(ConnectionUtil.host, ConnectionUtil.port);

            // Connection successful
            System.out.println("Connection Successful");
            System.out.println("Main: " + socket);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmlFiles/Login.fxml"));
            Parent root = loader.load();
            LoginController lc = loader.getController();
            lc.setSocket(this.socket);

            primaryStage.initStyle(StageStyle.DECORATED);
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.setTitle("Login");
            /* primaryStage.getIcons().add(new Image("/images/img.png"));*/
            primaryStage.show();

            ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
            oo.writeInt(1);
            oo.writeUTF("Temp message from client to server on perfect connection.");
            oo.flush();

            ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
            String s = oi.readUTF();
            System.out.println(s);

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}