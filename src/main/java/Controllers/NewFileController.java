package Controllers;

import Controllers.EditorController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewFileController implements Initializable {
    public TextField fileNameTF;
    public ComboBox fileExtensionCB;
    public Button createFileButton;
    public Button closeButton;
    public Label pathLabel;
    private String directoryPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setDirectoryPath(String directoryPath){
        this.directoryPath = directoryPath;
        pathLabel.setText(this.directoryPath);
    }


    public void createFileClicked(ActionEvent actionEvent) throws IOException {
        String fileName = fileNameTF.getText();
        String extension = (String) fileExtensionCB.getValue();

        System.out.println(directoryPath + "\\" + fileName + extension);
        File myObj = new File(directoryPath + "\\" + fileName + extension);
        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
        } else {
            System.out.println("File already exists.");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/Editor.fxml"));
        Parent root = loader.load();
        EditorController ec = loader.getController();
        ec.setNewFilePath(directoryPath + "\\" + fileName + extension);

        Stage stage = (Stage) fileNameTF.getScene().getWindow();
        stage.close();
    }

    public void closeClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
