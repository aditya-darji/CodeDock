package UtilClasses;

import Controllers.DashboardController;
import Controllers.GlobalEditorController;
import Controllers.LocalEditorController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class SetDocuments implements Runnable{
    DashboardController dashboardController;

    public SetDocuments(DashboardController dashboardController){
        this.dashboardController = dashboardController;
    }


    @Override
    public void run() {
        for(int i = 0; i < dashboardController.getDocumentsList().size(); i++){
            Label roomIDLabel = new Label("Room ID:");
            Label creatorIDLabel = new Label("Creator ID:");
            Label documentNameLabel = new Label("Document Name:");
            Label documentExtensionLabel = new Label("Document Extension:");
            Label accessLabel = new Label("Access");
            Label createdAtLabel = new Label("Created At:");

            TextField roomIDTF = new TextField();
            TextField creatorIDTF = new TextField();
            TextField documentNameTF = new TextField();
            TextField documentExtensionTF = new TextField();
            TextField accessTF = new TextField();
            TextField createdAtTF = new TextField();

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setMinWidth(Region.USE_COMPUTED_SIZE);
            anchorPane.setMinHeight(Region.USE_COMPUTED_SIZE);
            anchorPane.setPrefWidth(800);
            anchorPane.setPrefHeight(120);
            anchorPane.setMaxWidth(Region.USE_COMPUTED_SIZE);
            anchorPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
            anchorPane.setPadding(new Insets(10, 10, 10, 10));
            anchorPane.setStyle("-fx-border-color: black;");
            anchorPane.getChildren().addAll(roomIDLabel, creatorIDLabel, documentNameLabel, documentExtensionLabel, accessLabel, createdAtLabel);
            anchorPane.getChildren().addAll(roomIDTF, creatorIDTF, documentNameTF, documentExtensionTF, accessTF, createdAtTF);
            anchorPane.setId("AnchorPane-" + i);
            int finalI = i;
            anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            try{
                                ObjectOutputStream os = new ObjectOutputStream(dashboardController.getSocket().getOutputStream());
                                os.writeInt(15);
                                os.writeInt(dashboardController.getDocumentsList().get(finalI).getRoomId());
                                os.flush();

                                System.out.println(anchorPane.getId() + " is Clicked.");
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/GlobalEditor.fxml"));
                                Parent root = loader.load();
                                GlobalEditorController gec = loader.getController();
                                gec.setSocket(dashboardController.getSocket());
                                gec.setUseridInfo(dashboardController.useridInfo);
                                System.out.println();
                                gec.setDocumentDetails(dashboardController.getDocumentsList().get(finalI));

                                Stage dashboardStage = new Stage();
                                dashboardStage.initStyle(StageStyle.DECORATED);
                                dashboardStage.setTitle("CodeDock Document Name: " + dashboardController.documentsList.get(finalI).getDocumentName() + "." + dashboardController.documentsList.get(finalI).getDocumentExtension());
                                dashboardStage.setMaximized(true);
//            dashboardStage.setScene(new Scene(loader.load()));
                                dashboardStage.setScene(new Scene(root, 1530, 780));
                                dashboardStage.show();

                                Stage stage = (Stage) dashboardController.welcomeLabel.getScene().getWindow();
                                stage.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });

            anchorPane.addEventHandler(MouseEvent.MOUSE_ENTERED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            anchorPane.getScene().setCursor(Cursor.HAND);
                            anchorPane.setMinWidth(810);
                            anchorPane.setMinHeight(130);
                            anchorPane.setPrefWidth(810);
                            anchorPane.setPrefHeight(130);
                            anchorPane.setMaxWidth(810);
                            anchorPane.setMaxHeight(130);
                        }
                    });

            anchorPane.addEventHandler(MouseEvent.MOUSE_EXITED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            anchorPane.getScene().setCursor(Cursor.DEFAULT);
                            anchorPane.setMinWidth(800);
                            anchorPane.setMinHeight(120);
                            anchorPane.setPrefWidth(800);
                            anchorPane.setPrefHeight(120);
                            anchorPane.setMaxWidth(800);
                            anchorPane.setMaxHeight(120);
                        }
                    });

            roomIDLabel.setLayoutX(14.0);
            roomIDLabel.setLayoutY(14.0);

            creatorIDLabel.setLayoutX(424.0);
            creatorIDLabel.setLayoutY(14.0);

            documentNameLabel.setLayoutX(14.0);
            documentNameLabel.setLayoutY(47.0);

            documentExtensionLabel.setLayoutX(424.0);
            documentExtensionLabel.setLayoutY(47.0);

            accessLabel.setLayoutX(14.0);
            accessLabel.setLayoutY(80.0);

            createdAtLabel.setLayoutX(424.0);
            createdAtLabel.setLayoutY(80.0);

            roomIDTF.setMaxWidth(Region.USE_COMPUTED_SIZE);
            roomIDTF.setMinWidth(Region.USE_COMPUTED_SIZE);
            roomIDTF.setPrefWidth(187);
            roomIDTF.setMaxHeight(Region.USE_COMPUTED_SIZE);
            roomIDTF.setMinHeight(Region.USE_COMPUTED_SIZE);
            roomIDTF.setPrefHeight(31);
            roomIDTF.setLayoutX(149);
            roomIDTF.setLayoutY(9);
            roomIDTF.setEditable(false);
            roomIDTF.setText(Integer.toString(dashboardController.getDocumentsList().get(i).getRoomId()));

            creatorIDTF.setMaxWidth(Region.USE_COMPUTED_SIZE);
            creatorIDTF.setMinWidth(Region.USE_COMPUTED_SIZE);
            creatorIDTF.setPrefWidth(187);
            creatorIDTF.setMaxHeight(Region.USE_COMPUTED_SIZE);
            creatorIDTF.setMinHeight(Region.USE_COMPUTED_SIZE);
            creatorIDTF.setPrefHeight(31);
            creatorIDTF.setLayoutX(587);
            creatorIDTF.setLayoutY(9);
            creatorIDTF.setEditable(false);
            creatorIDTF.setText(Integer.toString(dashboardController.getDocumentsList().get(i).getCreatorId()));


            documentNameTF.setMaxWidth(Region.USE_COMPUTED_SIZE);
            documentNameTF.setMinWidth(Region.USE_COMPUTED_SIZE);
            documentNameTF.setPrefWidth(187);
            documentNameTF.setMaxHeight(Region.USE_COMPUTED_SIZE);
            documentNameTF.setMinHeight(Region.USE_COMPUTED_SIZE);
            documentNameTF.setPrefHeight(31);
            documentNameTF.setLayoutX(149);
            documentNameTF.setLayoutY(42);
            documentNameTF.setEditable(false);
            documentNameTF.setText(dashboardController.getDocumentsList().get(i).getDocumentName());


            documentExtensionTF.setMaxWidth(Region.USE_COMPUTED_SIZE);
            documentExtensionTF.setMinWidth(Region.USE_COMPUTED_SIZE);
            documentExtensionTF.setPrefWidth(187);
            documentExtensionTF.setMaxHeight(Region.USE_COMPUTED_SIZE);
            documentExtensionTF.setMinHeight(Region.USE_COMPUTED_SIZE);
            documentExtensionTF.setPrefHeight(31);
            documentExtensionTF.setLayoutX(587);
            documentExtensionTF.setLayoutY(42);
            documentExtensionTF.setEditable(false);
            documentExtensionTF.setText(dashboardController.getDocumentsList().get(i).getDocumentExtension());


            accessTF.setMaxWidth(Region.USE_COMPUTED_SIZE);
            accessTF.setMinWidth(Region.USE_COMPUTED_SIZE);
            accessTF.setPrefWidth(187);
            accessTF.setMaxHeight(Region.USE_COMPUTED_SIZE);
            accessTF.setMinHeight(Region.USE_COMPUTED_SIZE);
            accessTF.setPrefHeight(31);
            accessTF.setLayoutX(149);
            accessTF.setLayoutY(74);
            accessTF.setEditable(false);
            accessTF.setText((dashboardController.getDocumentsList().get(i).getAccess() == 0? "Read and Write" : "Read Only"));


            createdAtTF.setMaxWidth(Region.USE_COMPUTED_SIZE);
            createdAtTF.setMinWidth(Region.USE_COMPUTED_SIZE);
            createdAtTF.setPrefWidth(187);
            createdAtTF.setMaxHeight(Region.USE_COMPUTED_SIZE);
            createdAtTF.setMinHeight(Region.USE_COMPUTED_SIZE);
            createdAtTF.setPrefHeight(31);
            createdAtTF.setLayoutX(587);
            createdAtTF.setLayoutY(74);
            createdAtTF.setEditable(false);
            createdAtTF.setText(dashboardController.getDocumentsList().get(i).getCreatedAt().toString());

            Platform.runLater(() -> dashboardController.allDocumentsVBox.getChildren().add(anchorPane));

//            anchorPane.getChildren().addAll((Collection<? extends Node>) roomIDLabel);
//            anchorPane.getChildren().addAll((Collection<? extends Node>) creatorIDLabel);
//            anchorPane.getChildren().addAll((Collection<? extends Node>) documentNameLabel);
//            anchorPane.getChildren().addAll((Collection<? extends Node>) documentExtensionLabel);
//            anchorPane.getChildren().addAll((Collection<? extends Node>) accessLabel);
//            anchorPane.getChildren().addAll((Collection<? extends Node>) createdAtLabel);
//
//            dashboardController.allDocumentsVBox.getChildren().add(anchorPane);

//            System.out.println("=========");
//            System.out.println(documentsList.get(i).getRoomId());
//            System.out.println(documentsList.get(i).getDocumentName());
//            System.out.println(documentsList.get(i).getDocumentExtension());
//            System.out.println(documentsList.get(i).getDocumentContent());
//            System.out.println(documentsList.get(i).getCreatedAt());
//            System.out.println(documentsList.get(i).getCreatorId());
//            System.out.println(documentsList.get(i).getAccess());
        }


    }
}
