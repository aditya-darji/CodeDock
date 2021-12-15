package NewFile;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class NewFile extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxmlFiles/NewFileDialogBox.fxml"));
        Stage dashboardStage = new Stage();
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setTitle("codeDoc");
        dashboardStage.setScene(new Scene(root, 602, 275));
        dashboardStage.show();
    }
}
