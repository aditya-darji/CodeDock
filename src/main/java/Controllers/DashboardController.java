package Controllers;

import UtilClasses.DocumentDetails;
import UtilClasses.SetDocuments;
import UtilClasses.UseridInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public VBox allDocumentsVBox;
    public VBox settingsVBox;
    public Label welcomeLabel;
    private Socket socket;
    public UseridInfo useridInfo;
    public ArrayList<DocumentDetails> documentsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setSocket(Socket socket) throws IOException, ClassNotFoundException {
        this.socket = socket;
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeInt(7);
        objectOutputStream.flush();

        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        documentsList = (ArrayList<DocumentDetails>) objectInputStream.readObject();

        SetDocuments setDocuments = new SetDocuments(this);
        Thread thread = new Thread(setDocuments);
        thread.start();
    }

    public ArrayList<DocumentDetails> getDocumentsList() {
        return documentsList;
    }

    public void setDocumentsList(ArrayList<DocumentDetails> documentsList) {
        this.documentsList = documentsList;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setUseridInfo(UseridInfo useridInfo) {
        this.useridInfo = useridInfo;
        welcomeLabel.setText("Welcome " + useridInfo.getUsername());
    }

    public void openLocalEditorButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/LocalEditor.fxml"));
        Parent root = loader.load();
        LocalEditorController ec = loader.getController();
        ec.setSocket(this.socket);
        ec.setUseridInfo(useridInfo);

        Stage dashboardStage = new Stage();
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setTitle("CodeDock");
        dashboardStage.setMaximized(true);
//            dashboardStage.setScene(new Scene(loader.load()));
        dashboardStage.setScene(new Scene(root, 1530, 780));
        dashboardStage.show();

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.close();
    }

    public void createNewDocumentClicked(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        System.out.println("New Document Button Clicked");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/NewDocumentCreator.fxml"));
        Parent root = loader.load();
        NewDocumentCreatorController newDocumentCreatorController = loader.getController();
        newDocumentCreatorController.setSocket(this.socket);

        Stage dashboardStage = new Stage();
        dashboardStage.initModality(Modality.APPLICATION_MODAL);
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setTitle("New Document Creator");
//            dashboardStage.setScene(new Scene(loader.load()));
        dashboardStage.setScene(new Scene(root, 900, 700));
        dashboardStage.showAndWait();
    }
}
