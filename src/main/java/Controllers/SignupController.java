package Controllers;

import UtilClasses.SignupInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String name = nameTF.getText().trim();
        String username = usernameTF.getText().trim();
        String email = emailTF.getText().trim();
        String password = passwordTF.getText().trim();
        String confirmPassword = confirmPasswordTF.getText().trim();

        if(name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            JOptionPane.showMessageDialog(null, "Fill all fields.");
        }
        else if(!isValidUsername(username)){
            JOptionPane.showMessageDialog(null,
                    "Please check your username. Check that:\n" +
                    "=> The username consists of 6 to 30 characters inclusive. If the username\n" +
                    "=> consists of less than 6 or greater than 30 characters, then it is an invalid username.\n" +
                    "=> The username can only contain alphanumeric characters and underscores (_). Alphanumeric characters describe the character set consisting of lowercase characters [a – z], uppercase characters [A – Z], and digits [0 – 9].\n" +
                    "=> The first character of the username must be an alphabetic character, i.e., either lowercase character [a – z] or uppercase character [A – Z].");
        }
        else if(!isValidPassword(password)){
            JOptionPane.showMessageDialog(null,
                    "Please check your password. Check that:\n" +
                    "-> It contains at least 8 characters and at most 20 characters.\n" +
                    "-> It contains at least one digit.\n" +
                    "-> It contains at least one upper case alphabet.\n" +
                    "-> It contains at least one lower case alphabet.\n" +
                    "-> It contains at least one special character which includes !@#$%&*()-+=^.\n" +
                    "-> It doesn’t contain any white space.");
        }
        else if(!password.equals(confirmPassword)){
            JOptionPane.showMessageDialog(null, "Passwords don't match");
        }
        else if(!isValidEmail(email)){
            JOptionPane.showMessageDialog(null, "Please enter a valid Email ID.");
        }
        else{
            SignupInfo user = new SignupInfo(name, username, email, password);
            ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
            oo.writeInt(3);
            oo.writeObject(user);
            oo.flush();

            int b = oi.readInt();
            System.out.println("BOOLEAN: " + b);
            if (b == 1) {
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
            } else if(b == 0){
                JOptionPane.showMessageDialog(null, "Account not created");
            } else {
                JOptionPane.showMessageDialog(null, "Given Username or Email Id is taken! Please try again with a different one.");
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

    public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    // Function to validate the password.
    public static boolean
    isValidPassword(String password)
    {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the password is empty
        // return false
        if (password == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);

        // Return if the password
        // matched the ReGex
        return m.matches();
    }

    // Function to validate the username
    public static boolean isValidUsername(String name)
    {

        // Regex to check valid username.
        String regex = "^[A-Za-z]\\w{5,29}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the username is empty
        // return false
        if (name == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given username
        // and regular expression.
        Matcher m = p.matcher(name);

        // Return if the username
        // matched the ReGex
        return m.matches();
    }
}
