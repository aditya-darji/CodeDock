package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class VideoCommIntermediateController {
    public TextField receiverNameTF;
    public DashboardController dashboardController;

    public void requestVideoCallClicked(ActionEvent actionEvent) throws IOException {
        String receiverName = receiverNameTF.getText().trim();

        System.out.println(dashboardController.useridInfo.getUsername());
        System.out.println(receiverName);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(dashboardController.getSocket().getOutputStream());
        objectOutputStream.writeInt(20);
        objectOutputStream.writeUTF(dashboardController.useridInfo.getUsername());
        objectOutputStream.writeUTF(receiverName);
        objectOutputStream.flush();

        ObjectInputStream objectInputStream = new ObjectInputStream(dashboardController.getSocket().getInputStream());
        boolean reply = objectInputStream.readBoolean();

        if(reply){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/AudioVideoCommunication.fxml"));
            Parent root = loader.load();
            AudioVideoCommunicationController audioVideoCommunicationController = loader.getController();
            audioVideoCommunicationController.setSocket(dashboardController.getSocket());
            audioVideoCommunicationController.setUseridInfo(dashboardController.useridInfo);
//        audioVideoCommunicationController.setReceiverId(2);
            audioVideoCommunicationController.setReceiverUsername(receiverName);

            Stage dashboardStage = new Stage();
            dashboardStage.initStyle(StageStyle.DECORATED);
            dashboardStage.setTitle("Audio Video Communication");
            dashboardStage.setMaximized(true);
//            dashboardStage.setScene(new Scene(loader.load()));
            dashboardStage.setScene(new Scene(root, 1530, 780));
            dashboardStage.show();

            Stage stage1 = (Stage) dashboardController.welcomeLabel.getScene().getWindow();
            stage1.close();
        }
        else{
            JOptionPane.showMessageDialog(null, "Call Declined");
        }

        Stage stage = (Stage) receiverNameTF.getScene().getWindow();
        stage.close();
    }

    public DashboardController getDashboardController() {
        return dashboardController;
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
}
