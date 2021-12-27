package Controllers;

import UtilClasses.DocumentDetails;
import UtilClasses.SerializableImage;
import UtilClasses.SetDocuments;
import UtilClasses.UseridInfo;
import javafx.application.Platform;
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
import sun.rmi.runtime.Log;

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
    public DashboardController dc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setSocket(Socket socket) throws IOException, ClassNotFoundException {
        this.socket = socket;
        dc = this;

//        DashboardControllerThread dashboardControllerThread = new DashboardControllerThread();
//        Thread thread1 = new Thread(dashboardControllerThread);
//        thread1.start();

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeInt(7);
        objectOutputStream.flush();

        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
        int choice = (int) oi.readInt();
        documentsList = (ArrayList<DocumentDetails>) oi.readObject();
        SetDocuments setDocuments = new SetDocuments(dc);
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
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//        objectOutputStream.writeInt(21);
//        objectOutputStream.flush();
//
//        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//        int choice = objectInputStream.readInt();


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

    public void startVideoCommunicationClicked(ActionEvent actionEvent) throws IOException {
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//        objectOutputStream.writeInt(21);
//        objectOutputStream.flush();



        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/VideoCommIntermediate.fxml"));
        Parent root = loader.load();
        VideoCommIntermediateController videoCommIntermediateController = loader.getController();
        videoCommIntermediateController.setDashboardController(this);

        Stage dashboardStage = new Stage();
        dashboardStage.initModality(Modality.APPLICATION_MODAL);
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setTitle("Video Communication Intermediate");
        dashboardStage.setScene(new Scene(root, 600, 400));
        dashboardStage.showAndWait();
    }

    public void logoutButtonClicked(ActionEvent actionEvent) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeInt(23);
        objectOutputStream.flush();

        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
        int choice = (int) oi.readInt();
        String acknowledgement = oi.readUTF();
        System.out.println(acknowledgement);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/Login.fxml"));
        Parent root = loader.load();
        LoginController ec = loader.getController();
        ec.setSocket(this.socket);

        Stage dashboardStage = new Stage();
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setScene(new Scene(root, 600, 400));
        dashboardStage.setTitle("Login");
        /* primaryStage.getIcons().add(new Image("/images/img.png"));*/
        dashboardStage.show();

        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.close();
    }

    class DashboardControllerThread implements Runnable {

        @Override
        public void run() {
            while(true){
                try{
                    ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                    int choice = (int) oi.readInt();
                    if(choice==1000) break;
                    if(choice==10){
                        String senderUsername = oi.readUTF();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/AudioVideoCommunication.fxml"));
                        Parent root = loader.load();
                        AudioVideoCommunicationController audioVideoCommunicationController = loader.getController();
                        audioVideoCommunicationController.setSocket(getSocket());
                        audioVideoCommunicationController.setUseridInfo(useridInfo);
//        audioVideoCommunicationController.setReceiverId(2);
                        audioVideoCommunicationController.setReceiverUsername(senderUsername);

                        Platform.runLater(() -> {
                            Stage dashboardStage = new Stage();
                            dashboardStage.initStyle(StageStyle.DECORATED);
                            dashboardStage.setTitle("Audio Video Communication");
                            dashboardStage.setMaximized(true);
//            dashboardStage.setScene(new Scene(loader.load()));
                            dashboardStage.setScene(new Scene(root, 1530, 780));
                            dashboardStage.show();

                            Stage stage1 = (Stage) welcomeLabel.getScene().getWindow();
                            stage1.close();
                        });

                        break;
                    }
                    else if(choice == 2){
                        String acknowledgement = oi.readUTF();
                        System.out.println(acknowledgement);
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/Login.fxml"));
                        Parent root = loader.load();
                        LoginController ec = loader.getController();
                        ec.setSocket(socket);

                        Platform.runLater(() -> {
                            Stage dashboardStage = new Stage();
                            dashboardStage.initStyle(StageStyle.DECORATED);
                            dashboardStage.setScene(new Scene(root, 600, 400));
                            dashboardStage.setTitle("Login");
                            /* primaryStage.getIcons().add(new Image("/images/img.png"));*/
                            dashboardStage.show();

                            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                            stage.close();
                        });
                        break;
                    }
                    switch (choice){
                        case 1:
                            documentsList = (ArrayList<DocumentDetails>) oi.readObject();
                            SetDocuments setDocuments = new SetDocuments(dc);
                            Thread thread = new Thread(setDocuments);
                            thread.start();
                            break;
                        default:
                            break;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
