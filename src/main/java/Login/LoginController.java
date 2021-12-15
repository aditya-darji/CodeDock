package Login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginController {

    @FXML
    private Button button;

    @FXML
    void handleClick(ActionEvent event) throws IOException {
//        System.out.println("Button Clicked!");
        Parent root = FXMLLoader.load(getClass().getResource("../fxmlFiles/Editor.fxml"));
        Stage dashboardStage = new Stage();
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setTitle("codeDoc");
        dashboardStage.setScene(new Scene(root, 900, 700));
        dashboardStage.show();

        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}