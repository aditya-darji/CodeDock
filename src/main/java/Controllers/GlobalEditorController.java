package Controllers;

import UtilClasses.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
    public WebView webView;
    public AnchorPane rightAnchorPane;
    public ComboBox<Object> recommendationCB;
    private Socket socket;
    private UseridInfo useridInfo;
    public DocumentDetails documentDetails;
    public ArrayList<UserAccessInfo> userAccessInfoArrayList;
    public ArrayList<String> userList;
    public TextArea lines;
    public HashMap<Integer, Integer> usersCaretPosition = new HashMap<Integer, Integer>();
    public TextArea caretTextArea;
    private int keyPressCount = 0;
    public Trie prefixTree;

    private static final String[] CPP_KEYWORDS = new String[] {
            "Auto", "double", "int", "struct", "Break",
            "else", "long", "switch", "Case", "enum", "register",
            "typedef", "Char", "extern", "return", "union", "Const",
            "float", "short", "unsigned", "Continue", "for", "signed",
            "void", "Default", "goto", "sizeof", "volatile", "Do", "if",
            "static", "while", "Asm", "dynamic_cast", "namespace",
            "reinterpret_cast", "Bool", "explicit", "new", "static_cast",
            "Catch", "false", "operator", "template", "Class", "friend",
            "private", "this", "Const_cast", "inline", "public", "throw",
            "Delete", "mutable", "protected", "true", "Try", "typeid",
            "typename", "using", "virtual", "wchar_t"
    };

    private static final String[] JAVA_KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String[] C_KEYWORDS = new String[] {
            "auto","double","int","struct","break","else","long",
            "switch","case","enum","register","typedef","char",
            "extern","return","union","const","float","short",
            "unsigned","continue","for","signed","void","default",
            "goto","sizeof","volatile","do","if","static","while"
    };

    private static final String[] PYTHON_KEYWORDS = new String[] {
            "False", "None", "True", "and", "as", "assert", "break", "class",
            "continue", "def", "del", "elif", "else", "except", "finally",
            "for", "from", "global", "if", "import", "in", "is", "lambda",
            "nonlocal", "not", "or", "pass", "raise", "return",
            "try", "while", "with", "yield"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        personalChatTextArea.setEditable(false);
        roomChatTextArea.setEditable(false);
    }

    public void goToDashboardMenuClicked(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
        oo.writeInt(9);
        oo.writeInt(documentDetails.getRoomId());
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

    public void compileAndRunClicked(ActionEvent actionEvent) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeInt(18);
        objectOutputStream.writeInt(documentDetails.getRoomId());
        objectOutputStream.writeUTF(documentDetails.getDocumentName());
        objectOutputStream.writeUTF(documentDetails.getDocumentContent());
        objectOutputStream.writeUTF(documentDetails.getDocumentExtension());
        objectOutputStream.writeUTF(inputTextArea.getText());
        objectOutputStream.flush();
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

    public void importButtonClicked(ActionEvent actionEvent) throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fc.showOpenDialog(null);

        if(selectedFile != null){
            String filename = selectedFile.getAbsolutePath();
            FileReader reader = new FileReader(filename);
            BufferedReader br = new BufferedReader(reader);

            String line;
            StringBuilder totalFile = new StringBuilder();
            long linesLoaded = 0;
            while ((line = br.readLine()) != null) {
                totalFile.append(line);
                totalFile.append("\n");
            }
            String result = totalFile.toString();
            inputTextArea.setText(result);
        }
        else{
            System.out.println("File is Not Valid!");
        }
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

        caretTextArea = new TextArea();
        caretTextArea.setPrefWidth(400);
        caretTextArea.setPrefHeight(200);
        caretTextArea.setLayoutX(10);
        caretTextArea.setLayoutY(1000);
        caretTextArea.setEditable(false);
        rightAnchorPane.getChildren().add(caretTextArea);

        lines.setEditable(false);
        lines.setPrefWidth(70);
        lines.setMaxWidth(70);
        lines.setMinWidth(70);
        lines.setDisable(true);
        setLinesTextArea();
        documentContentTextArea.scrollTopProperty().bindBidirectional(lines.scrollTopProperty());
        lines.scrollTopProperty().bindBidirectional(documentContentTextArea.scrollTopProperty());

        prefixTree = new Trie();
        String documentExtension = documentDetails.getDocumentExtension();
        switch (documentExtension){
            case "cpp":
                for (String cppKeyword : CPP_KEYWORDS) {
                    prefixTree.insert(cppKeyword);
                }
                break;
            case "java":
                for (String javaKeyword : JAVA_KEYWORDS) {
                    prefixTree.insert(javaKeyword);
                }
                break;
            case "c":
                for (String cKeyword : C_KEYWORDS) {
                    prefixTree.insert(cKeyword);
                }
                break;
            case "python":
                for (String pythonKeyword : PYTHON_KEYWORDS) {
                    prefixTree.insert(pythonKeyword);
                }
                break;
            default:
                break;
        }

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

        WebEngine webEngine = webView.getEngine();
        webEngine.load("https://www.google.com/");
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

    public void onKeyPress(KeyEvent keyEvent) throws IOException {
        keyPressCount++;
        int caretPosition = documentContentTextArea.getCaretPosition();

        usersCaretPosition.put(useridInfo.getUserid(), caretPosition);

        documentDetails.setDocumentContent(documentContentTextArea.getText());

        SetCaretTextArea setCaretTextArea = new SetCaretTextArea(this);
        Thread thread = new Thread(setCaretTextArea);
        thread.start();

        if(keyPressCount == 10){
            ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream1.writeInt(16);
            objectOutputStream1.writeInt(documentDetails.getRoomId());
            objectOutputStream1.writeUTF(documentContentTextArea.getText());
            objectOutputStream1.flush();
            keyPressCount = 0;
        }
        else{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeInt(14);
            objectOutputStream.writeInt(documentDetails.getRoomId());
            objectOutputStream.writeInt(caretPosition);
            objectOutputStream.flush();
        }

        System.out.println("-----------");
        System.out.println(keyEvent.getCode());
        System.out.println(caretPosition);
        String str = documentContentTextArea.getText();

        System.out.println(str.charAt(caretPosition));
//        if(keyEvent.getCode().toString().equals("ENTER") || keyEvent.getCode().toString().equals("BACK_SPACE")){
            System.out.println("KEY PRESSED");
            setLinesTextArea();
//        }
        System.out.println("-----------\n");
    }

    public void setLinesTextArea(){
        String toCount = documentContentTextArea.getText();
        String[] lineArray = toCount.split("\n");
        StringBuilder text = new StringBuilder("1" + System.getProperty("line.separator"));
        for(int i = 2; i <= lineArray.length; i++) {
            text.append(i).append(System.getProperty("line.separator"));
        }
        lines.setText(text.toString());
    }

    public void saveClicked(ActionEvent actionEvent) throws IOException {
        documentDetails.setDocumentContent(documentContentTextArea.getText());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeInt(17);
        objectOutputStream.writeInt(documentDetails.getRoomId());
        objectOutputStream.writeUTF(documentDetails.getDocumentContent());
        objectOutputStream.flush();
    }

    public void onKeyRelease(KeyEvent keyEvent) {
        int caretPosition = documentContentTextArea.getCaretPosition();

        String s1 = documentContentTextArea.getText().substring(0, caretPosition);
        int lastIndex = Math.max(Math.max(s1.lastIndexOf(' '), s1.lastIndexOf('\n')), s1.lastIndexOf('#'));
        String userWord = s1.substring(lastIndex+1, caretPosition);
        TrieNode tn = prefixTree.searchNode(userWord);
        if(tn != null) {
            ArrayList<String> trieResult = prefixTree.wordsFinderTraversal(tn,0);
            recommendationCB.setItems(FXCollections.observableArrayList(trieResult));
            recommendationCB.getSelectionModel().selectFirst();
        }
    }
}