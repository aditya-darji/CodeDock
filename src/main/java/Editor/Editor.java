package Editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;


public class Editor extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxmlFiles/Editor.fxml"));
        Stage dashboardStage = new Stage();
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setTitle("CodeDock");
        dashboardStage.setScene(new Scene(root, 900, 700));
        dashboardStage.show();
    }
}
