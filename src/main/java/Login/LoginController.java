package Login;

import Controllers.SignupController;
import Editor.EditorController;
import UtilClasses.LoginInfo;
import UtilClasses.UseridInfo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginController {

    @FXML
    public TextField usernameTF;
    public TextField passwordTF;
    @FXML
    private Button button;
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void loginButtonClicked(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        //Action event for login purpose of user
        String username = usernameTF.getText();
        String password = passwordTF.getText();

        if(username.isEmpty() || password.isEmpty()){
            //If any field is empty
            JOptionPane.showMessageDialog(null, "Fill all fields.");
            return;
        }
        LoginInfo user = new LoginInfo(username, password);

        System.out.println(socket);
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());

        try{
            //Credentials are sent to the server in LoginInfo class
            os.writeInt(2);
            os.writeObject(user);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        UseridInfo useridInfo = (UseridInfo) oi.readObject();

        if(useridInfo.getUsername().equals("")){
            JOptionPane.showMessageDialog(null, "Username or Password entered is Incorrect");
        }
        else{
            //On successful login moved to next screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/Editor.fxml"));
            Parent root = loader.load();
            EditorController ec = loader.getController();
            ec.setSocket(this.socket);

            Stage dashboardStage = new Stage();
            dashboardStage.initStyle(StageStyle.DECORATED);
            dashboardStage.setTitle("CodeDock");
            dashboardStage.setMaximized(true);
//            dashboardStage.setScene(new Scene(loader.load()));
            dashboardStage.setScene(new Scene(root, 1530, 780));
            dashboardStage.show();

            Stage stage = (Stage) button.getScene().getWindow();
            stage.close();
        }
    }

    public void signupButtonClicked(ActionEvent actionEvent) throws IOException {
        //If user is new to the CodeDock signup button is clicked and user is moved to signup window
        FXMLLoader signuploader = new FXMLLoader(getClass().getResource("../fxmlFiles/Signup.fxml"));
        Parent root = signuploader.load();
        SignupController sc = signuploader.getController();
        sc.setSocket(this.socket);

        Stage dashboardStage = new Stage();
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setTitle("Signup");
        dashboardStage.setScene(new Scene(root, 800, 600));
        dashboardStage.show();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}