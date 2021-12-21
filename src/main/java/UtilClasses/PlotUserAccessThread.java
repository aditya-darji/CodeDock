package UtilClasses;

import Controllers.ManageAccessController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

public class PlotUserAccessThread implements Runnable{
    ManageAccessController manageAccessController;

    public PlotUserAccessThread(ManageAccessController manageAccessController){
        this.manageAccessController = manageAccessController;
    }

    @Override
    public void run() {

        for(int i = 0; i < manageAccessController.userAccessInfoArrayList.size(); i++){
            Label usernameLabel = new Label("-> " + manageAccessController.userAccessInfoArrayList.get(i).getUsername());
            ComboBox<String> accessCB = new ComboBox<String>();
            String[] accessArray = { "Read and Write", "Read only", "Remove Collaborator"};
            accessCB.setItems(FXCollections.observableArrayList(accessArray));
            accessCB.getSelectionModel().select(manageAccessController.userAccessInfoArrayList.get(i).getAccess());

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setMinWidth(Region.USE_COMPUTED_SIZE);
            anchorPane.setMinHeight(Region.USE_COMPUTED_SIZE);
            anchorPane.setPrefWidth(850);
            anchorPane.setPrefHeight(50);
            anchorPane.setMaxWidth(Region.USE_COMPUTED_SIZE);
            anchorPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
            anchorPane.setPadding(new Insets(5, 5, 5, 5));

            anchorPane.getChildren().addAll(usernameLabel, accessCB);

            usernameLabel.setLayoutX(125);
            usernameLabel.setLayoutY(15);

            accessCB.setLayoutX(465);
            accessCB.setLayoutY(10);
            accessCB.setPrefHeight(31);
            accessCB.setPrefWidth(260);

            int finalI = i;
            accessCB.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                int accessRequested = accessCB.getSelectionModel().getSelectedIndex();
                int userid = manageAccessController.userAccessInfoArrayList.get(finalI).getUserId();
                String username = manageAccessController.userAccessInfoArrayList.get(finalI).getUsername();

                if(accessRequested == 2){
                    manageAccessController.userAccessInfoArrayList.remove(finalI);
                    manageAccessController.collaboratorMap.remove(userid);
                }
                else{
                    manageAccessController.userAccessInfoArrayList.set(finalI, new UserAccessInfo(userid, username, accessRequested));
                }
            });

            Platform.runLater(() -> manageAccessController.manageAccessVBox.getChildren().add(anchorPane));
        }
    }
}
