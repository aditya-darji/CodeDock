package Controllers;

import UtilClasses.LoadFile;
import UtilClasses.NewDocumentInfo;
import UtilClasses.UserArrayWrapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class NewDocumentCreatorController implements Initializable {
    public ComboBox selectUsernameCB;
    public ComboBox<Object> accessCB;
    public TextArea viewCollaboratorsTA;
    public TextField documentNameTF;
    public ComboBox<String> documentExtensionCB;
    public Label filenameLabel;
    private Socket socket;
    public ObservableList<String> usersList;
    private String fileContent = "";
    public HashMap<Integer, Integer> collaboratorMap=new HashMap<Integer,Integer>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] accessArray = { "Read and Write", "Read only"};
        accessCB.setItems(FXCollections.observableArrayList(accessArray));
        accessCB.getSelectionModel().selectFirst();

        String[] extensionArray = {"txt", "c", "cpp", "java", "py"};
        documentExtensionCB .setItems(FXCollections.observableArrayList(extensionArray));
        documentExtensionCB.getSelectionModel().selectFirst();

        viewCollaboratorsTA.setEditable(false);
    }

    public void setSocket(Socket socket) throws IOException, ClassNotFoundException {
        this.socket = socket;
        System.out.println("Entered");
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());

        os.writeInt(4);
        os.flush();

        //get input from the server
        ArrayList<String> tempUserList = (ArrayList<String>) oi.readObject();
        setUsersList(FXCollections.observableList(tempUserList));

        selectUsernameCB.setItems(this.usersList);
        selectUsernameCB.getSelectionModel().selectFirst();
//        GetUser getUser = new GetUser(this.socket, this);
//        Thread thread = new Thread(getUser);
//        thread.start();
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public void setUsersList(ObservableList<String> usersList) {
        this.usersList = usersList;
    }

    public ObservableList<String> getUsersList() {
        return usersList;
    }

    public void openFileButton(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TEXT Files", "*.txt"),
                new FileChooser.ExtensionFilter("CPP Files", "*.cpp"),
                new FileChooser.ExtensionFilter("C Files", "*.c"),
                new FileChooser.ExtensionFilter("JAVA Files", "*.java"),
                new FileChooser.ExtensionFilter("PYTHON Files", "*.py"));

        File selectedFile = fc.showOpenDialog(null);
        if(selectedFile != null){
            filenameLabel.setText(selectedFile.getName());
            LoadFile loadFile = new LoadFile(this, selectedFile);
            Thread thread = new Thread(loadFile);
            thread.start();
        }
        else{
            System.out.println("File is Not Valid!");
        }
    }

    public void addCollaboratorButtonClicked(ActionEvent actionEvent) {
        String collaborator = (String) selectUsernameCB.getSelectionModel().getSelectedItem();
        String access = (String) accessCB.getSelectionModel().getSelectedItem();
        Integer selectedUserId = Integer.parseInt(collaborator.substring(0, collaborator.indexOf("-")));
        Integer accessIndex = accessCB.getSelectionModel().getSelectedIndex();

        if(collaboratorMap.containsKey(selectedUserId)){
            JOptionPane.showMessageDialog(null, "Selected User is already added.");
        }
        else{
            collaboratorMap.put(selectedUserId, accessIndex);
            viewCollaboratorsTA.appendText("=>" + collaborator + " <-> ACCESS: " + access + "\n");
        }
    }

    public void createButtonClicked(ActionEvent actionEvent) throws IOException {
        if(documentNameTF.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null, "Document Name should not be empty.");
        }
        else{
            String documentName = documentNameTF.getText().trim();
            String documentExtension = documentExtensionCB.getSelectionModel().getSelectedItem();

            NewDocumentInfo newDocumentInfo = new NewDocumentInfo(documentName, documentExtension, fileContent, collaboratorMap);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeInt(5);
            objectOutputStream.writeObject(newDocumentInfo);
            objectOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            String serverReply = objectInputStream.readUTF();
            JOptionPane.showMessageDialog(null, serverReply);

            Stage stage = (Stage) accessCB.getScene().getWindow();
            stage.close();
        }
    }
}
