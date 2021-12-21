package Controllers;

import UtilClasses.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GlobalEditorController implements Initializable {
    public VBox editorVBox;
    public TextField messageTF;
    public TextField sendToTF;
    public TabPane tabPane;
    public TextArea inputTextArea;
    public TextArea outputTextArea;
    public TextArea personalChatTextArea;
    public TextArea roomChatTextArea;
    public TextField roomMessageTF;
    public TextArea documentContentTextArea;
    public MenuItem manageAccessMenuItem;
    public ScrollPane scrollPane;
    private Socket socket;
    private UseridInfo useridInfo;
    private DocumentDetails documentDetails;
    public ArrayList<UserAccessInfo> userAccessInfoArrayList;
    public ArrayList<String> userList;
    public TextArea lines;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        personalChatTextArea.setEditable(false);
        roomChatTextArea.setEditable(false);
    }

    public void goToDashboardMenuClicked(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
        oo.writeInt(9);
        oo.flush();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/Dashboard.fxml"));
        Parent root = loader.load();
        DashboardController dc = loader.getController();
        dc.setSocket(this.socket);
        dc.setUseridInfo(useridInfo);

        Stage dashboardStage = new Stage();
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setTitle("Dashboard");
        dashboardStage.setMaximized(true);
//            dashboardStage.setScene(new Scene(loader.load()));
        dashboardStage.setScene(new Scene(root, 1530, 780));
        dashboardStage.show();

        Stage stage = (Stage) editorVBox.getScene().getWindow();
        stage.close();
    }

    public void compileAndRunClicked(ActionEvent actionEvent) {
    }

    public void sendMessageClicked(ActionEvent actionEvent) throws IOException {
        String sendTo = sendToTF.getText().trim();
        String message = messageTF.getText().trim();

        if(sendTo.isEmpty() || message.isEmpty()){
            JOptionPane.showMessageDialog(null, "Send to and Message should not be empty.");
        }
        else{
            personalChatTextArea.appendText("[" + useridInfo.getUsername() + "]: " + message + "\n");
            System.out.println("Send Message clicked.");
            ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
            oo.writeInt(8);
            oo.writeUTF(sendTo);
            oo.writeUTF(message);
            oo.flush();
            sendToTF.clear();
            messageTF.clear();
        }
    }

    public void importButtonClicked(ActionEvent actionEvent) {
    }

    public void exportButtonClicked(ActionEvent actionEvent) {
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;

        GlobalEditorThread globalEditorThread = new GlobalEditorThread(socket, this);
        Thread thread = new Thread(globalEditorThread);
        thread.start();
    }

    public UseridInfo getUseridInfo() {
        return useridInfo;
    }

    public void setUseridInfo(UseridInfo useridInfo) {
        this.useridInfo = useridInfo;
    }

    public void roomSendMessageClicked(ActionEvent actionEvent) throws IOException {
        String roomMessage = roomMessageTF.getText().trim();

        if(roomMessage.isEmpty()){
            JOptionPane.showMessageDialog(null, "Message should not be empty.");
        }
        else{
            roomChatTextArea.appendText("[" + useridInfo.getUsername() + "]: " + roomMessage + "\n");
            System.out.println("Send Message clicked.");
            ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
            oo.writeInt(10);
            oo.writeInt(documentDetails.getRoomId());
            oo.writeUTF(roomMessage);
            oo.flush();
            roomMessageTF.clear();
        }
    }

    public DocumentDetails getDocumentDetails() {
        return documentDetails;
    }

    public void setDocumentDetails(DocumentDetails documentDetails) {
        this.documentDetails = documentDetails;
        documentContentTextArea.setText(this.documentDetails.getDocumentContent());

        if(this.documentDetails.getAccess() == 1){
            documentContentTextArea.setEditable(false);
        }
        if(this.documentDetails.getCreatorId() != useridInfo.getUserid()){
            manageAccessMenuItem.setDisable(true);
        }

        try{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            objectOutputStream.writeInt(11);
            objectOutputStream.writeInt(documentDetails.getRoomId());
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void manageAccessClicked(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/ManageAccess.fxml"));
        Parent root = loader.load();
        ManageAccessController manageAccessController = loader.getController();
        manageAccessController.setSocket(this.socket);
        manageAccessController.setUserAccessInfoArrayList(userAccessInfoArrayList);
        manageAccessController.setRoomId(documentDetails.getRoomId());
        manageAccessController.setUserList(userList);

        Stage dashboardStage = new Stage();
        dashboardStage.initModality(Modality.APPLICATION_MODAL);
        dashboardStage.initStyle(StageStyle.DECORATED);
        dashboardStage.setTitle("Manage Access");
//            dashboardStage.setScene(new Scene(loader.load()));
        dashboardStage.setScene(new Scene(root, 900, 700));
        dashboardStage.showAndWait();
    }
}
