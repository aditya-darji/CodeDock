package Controllers;

import UtilClasses.DocumentDetails;
import UtilClasses.GlobalEditorThread;
import UtilClasses.TaskReadThread;
import UtilClasses.UseridInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
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
    private Socket socket;
    private UseridInfo useridInfo;
    private DocumentDetails documentDetails;

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

    public void setSocket(Socket socket) {
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
    }
}
