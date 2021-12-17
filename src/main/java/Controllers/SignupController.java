package Controllers;

import Login.LoginController;
import UtilClasses.SignupInfo;
import UtilClasses.UseridInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SignupController {

    public TextField nameTF;
    public TextField usernameTF;
    public TextField emailTF;
    public PasswordField passwordTF;
    public PasswordField confirmPasswordTF;
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void signupButtonClicked(ActionEvent actionEvent) throws IOException {
        String name = nameTF.getText();
        String username = usernameTF.getText();
        String email = emailTF.getText();
        String password = passwordTF.getText();
        String confirmPassword = confirmPasswordTF.getText();

        if(name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            JOptionPane.showMessageDialog(null, "Fill all fields.");
        }
        else if(!password.equals(confirmPassword)){
            JOptionPane.showMessageDialog(null, "Passwords don't match");
        }
        else{
            SignupInfo user = new SignupInfo(name, username, email, password);
            ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
            oo.writeInt(3);
            oo.writeObject(user);
            oo.flush();

            Boolean b = (Boolean) oi.readBoolean();
            System.out.println("BOOLEAN: " + b);
            if (b) {
                JOptionPane.showMessageDialog(null, "Account is created successfully! Login to continue.");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/Login.fxml"));
                Parent root = loader.load();
                LoginController ec = loader.getController();
                ec.setSocket(this.socket);

                Stage dashboardStage = new Stage();
                dashboardStage.initStyle(StageStyle.DECORATED);
                dashboardStage.setTitle("Login");
                dashboardStage.setScene(new Scene(root, 600, 400));
                dashboardStage.show();
                Stage stage = (Stage) nameTF.getScene().getWindow();
                stage.close();
            } else {
                JOptionPane.showMessageDialog(null, "Account not created");
            }
        }
    }
    public void loginButtonClicked(ActionEvent actionEvent) throws IOException {
        //If user is new to the CodeDock signup button is clicked and user is moved to signup window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/Login.fxml"));
        Parent root = loader.load();
        LoginController ec = loader.getController();
        ec.setSocket(this.socket);

        Stage dashboardStage = new Stage();
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setTitle("Login");
        dashboardStage.setScene(new Scene(root, 600, 400));
        dashboardStage.show();
        Stage stage = (Stage) nameTF.getScene().getWindow();
        stage.close();
    }

}
