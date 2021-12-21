package Controllers;

import UtilClasses.PlotUserAccessThread;
import UtilClasses.UserAccessInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ManageAccessController {
    public VBox manageAccessVBox;
    public ComboBox addUsernameCB;
    public ComboBox addAccessCB;
    private Socket socket;
    public ArrayList<UserAccessInfo> userAccessInfoArrayList;
    private int roomId;
    public ArrayList<String> userList;
    public HashMap<Integer, Integer> collaboratorMap=new HashMap<Integer,Integer>();

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;

        PlotUserAccessThread plotUserAccessThread = new PlotUserAccessThread(this);
        Thread thread = new Thread(plotUserAccessThread);
        thread.start();
    }

    public ArrayList<String> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<String> userList) {
        this.userList = userList;

        addUsernameCB.setItems(FXCollections.observableList(this.userList));
        addUsernameCB.getSelectionModel().selectFirst();

        String[] accessArray = { "Read and Write", "Read only"};
        addAccessCB.setItems(FXCollections.observableArrayList(accessArray));
        addAccessCB.getSelectionModel().selectFirst();
    }

    public Socket getSocket() {
        return socket;
    }

    public ArrayList<UserAccessInfo> getUserAccessInfoArrayList() {
        return userAccessInfoArrayList;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setUserAccessInfoArrayList(ArrayList<UserAccessInfo> userAccessInfoArrayList) {
        this.userAccessInfoArrayList = userAccessInfoArrayList;

        for (UserAccessInfo userAccessInfo : userAccessInfoArrayList) {
            collaboratorMap.put(userAccessInfo.getUserId(), userAccessInfo.getAccess());
        }
    }

    public void makeChangesButtonClicked(ActionEvent actionEvent) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeInt(12);
        objectOutputStream.writeInt(roomId);
        objectOutputStream.writeObject(userAccessInfoArrayList);
        objectOutputStream.flush();

        Stage stage = (Stage) manageAccessVBox.getScene().getWindow();
        stage.close();
    }

    public void addButtonClicked(ActionEvent actionEvent) {
        String collaborator = (String) addUsernameCB.getSelectionModel().getSelectedItem();
        String access = (String) addAccessCB.getSelectionModel().getSelectedItem();
        Integer selectedUserId = Integer.parseInt(collaborator.substring(0, collaborator.indexOf("-")));
        Integer accessIndex = addAccessCB.getSelectionModel().getSelectedIndex();

        if(collaboratorMap.containsKey(selectedUserId)){
            JOptionPane.showMessageDialog(null, "Selected User is already added.");
        }
        else{
            collaboratorMap.put(selectedUserId, accessIndex);
            userAccessInfoArrayList.add(new UserAccessInfo(selectedUserId, collaborator.substring(collaborator.lastIndexOf("-")+1), accessIndex));
        }
    }
}
