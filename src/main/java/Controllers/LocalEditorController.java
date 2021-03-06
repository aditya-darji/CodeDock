package Controllers;

import UtilClasses.CreateTree;
import UtilClasses.TaskReadThread;
import UtilClasses.UseridInfo;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.*;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;


public class LocalEditorController implements Initializable {
    @FXML
    public TextArea textArea1, textArea2;

    @FXML
    public VBox editorVBox;
    public TreeView<String> directoryTreeView;
    public TabPane tabPane;
    public Label label1;
    public TextArea inputTextArea;
    public TextArea outputTextArea;
    public TextArea chatTextArea;
    public TextField messageTF;
    public TextField sendToTF;
    private String newFilePath;
    public Vector<TextArea> textAreas;
    private HashMap<String,String> taToPathMap=new HashMap<String,String>();
    private int currIdx = 0;
    private Socket socket;
    public UseridInfo useridInfo;
    Thread thread;

    Image cImage = new Image(getClass().getResourceAsStream("../images/cFileIcon.png"));
    Image cppImage = new Image((getClass().getResourceAsStream("../images/cppFileIcon.png")));
    Image javaImage = new Image((getClass().getResourceAsStream("../images/javaFileIcon.png")));
    Image pythonImage = new Image((getClass().getResourceAsStream("../images/pythonFileIcon.png")));
    Image folderImage = new Image((getClass().getResourceAsStream("../images/folderIcon.png")));
    Image fileImage = new Image((getClass().getResourceAsStream("../images/fileIcon.png")));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatTextArea.setEditable(false);
    }

    public void newMenuClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("Editor Controller: " + getSocket());

        try{
            ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
            oo.writeInt(1);
            oo.writeUTF("Temp message from client to server on perfect connection.");
            oo.flush();
        } catch(Exception e){
            e.printStackTrace();
        }

        DirectoryChooser dc = new DirectoryChooser();
        Stage stage = (Stage) editorVBox.getScene().getWindow();
        File file = dc.showDialog(stage);



        if(file != null){
            System.out.println(file.getAbsolutePath());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxmlFiles/NewFileDialogBox.fxml"));
            Parent root = loader.load();
            NewFileController nfc = loader.getController();
            nfc.setDirectoryPath(file.getAbsolutePath());

            Stage dashboardStage = new Stage();
            dashboardStage.initModality(Modality.APPLICATION_MODAL);
            dashboardStage.initStyle(StageStyle.DECORATED);
            dashboardStage.setTitle("Create New File");
            dashboardStage.setScene(new Scene(root, 602, 275));
            dashboardStage.showAndWait();
        }
    }

    public void openMenuClicked(ActionEvent actionEvent) throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TEXT Files", "*.txt"),
                new FileChooser.ExtensionFilter("CPP Files", "*.cpp"),
                new FileChooser.ExtensionFilter("C Files", "*.c"),
                new FileChooser.ExtensionFilter("JAVA Files", "*.java"),
                new FileChooser.ExtensionFilter("PYTHON Files", "*.py"));

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

            TextArea ta = new TextArea();

            ta.setId("textArea" + String.valueOf(currIdx++));
            ta.setLayoutX(38.0);
            ta.setLayoutY(114.0);
            ta.setPrefWidth(376.0);
            ta.setPrefHeight(505.0);

            ta.setText(result);
            taToPathMap.put(ta.getId(), selectedFile.getAbsolutePath());
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setMinHeight(0.0);
            anchorPane.setMinWidth(0.0);
            anchorPane.setPrefWidth(200.0);
            anchorPane.setPrefHeight(180.0);
            anchorPane.getChildren().add(ta);
            AnchorPane.setTopAnchor(ta, 0.0);
            AnchorPane.setBottomAnchor(ta, 0.0);
            AnchorPane.setLeftAnchor(ta, 0.0);
            AnchorPane.setRightAnchor(ta, 0.0);

            Tab newTab = new Tab(selectedFile.getName());
            newTab.setContent(anchorPane);
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
            System.out.println(tabPane.getTabs().size());


//            textArea1.setText(result);
//            taToPathMap.put("textArea1", selectedFile.getAbsolutePath());
//            <Tab text="Untitled Tab 1">
//              <content>
//                  <AnchorPane minHeight="0.0" minWidth="0.0" prefHeight="180.0" prefWidth="200.0">
//                      <children>
//                          <TextArea fx:id="textArea1" layoutX="38.0" layoutY="114.0" prefHeight="505.0" prefWidth="376.0" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0" />
//                      </children>
//                  </AnchorPane>
//              </content>
//            </Tab>
//            TextArea ta = new TextArea();
//



        }
        else{
            System.out.println("File is Not Valid!");
        }
    }

    public void openDirectoryMenuClicked(ActionEvent actionEvent) {
        DirectoryChooser dc = new DirectoryChooser();
        Stage stage = (Stage) editorVBox.getScene().getWindow();
        File file = dc.showDialog(stage);

        if(file != null){
            System.out.println("Path of Directory Chosen is: " + file.getName());

            CreateTree createTree = new CreateTree(this, file, folderImage, fileImage, cImage, cppImage, javaImage, pythonImage);
            Thread thread = new Thread(createTree);
            thread.start();
        }
    }



    private static String getFileExtension(String fileName) {
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    public void closeMenuClicked(ActionEvent actionEvent) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        tabPane.getTabs().remove(selectedTab);
        System.out.println("Close Menu Clicked!");
//        textArea1.setText("Close Menu Clicked!");
    }

    public void saveMenuClicked(ActionEvent actionEvent) throws IOException {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        AnchorPane anchorPane = (AnchorPane) selectedTab.getContent();
        TextArea ta = (TextArea) (anchorPane.getChildren()).get(0);
        String pathName = taToPathMap.get(ta.getId());

        ObservableList<CharSequence> paragraph = ta.getParagraphs();
        Iterator<CharSequence> iter = paragraph.iterator();
        BufferedWriter bf = new BufferedWriter(new FileWriter(new File(pathName)));
        while(iter.hasNext())
        {
            CharSequence seq = iter.next();
            bf.append(seq);
            bf.newLine();
        }
        bf.flush();
        bf.close();

        System.out.println("Save Menu Clicked!");
//        textArea1.setText("Save Menu Clicked!");
    }

    public void saveAsMenuClicked(ActionEvent actionEvent) {
    }

    public void preferencesMenuClicked(ActionEvent actionEvent) {
    }

    public void quitMenuClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) editorVBox.getScene().getWindow();
        stage.close();
    }

    public void setNewFilePath(String s) {
        this.newFilePath = s;
        System.out.println("NEW FILE PATH: " + this.newFilePath);
    }

    public void compileAndRunClicked(ActionEvent actionEvent) throws IOException, InterruptedException {
        System.out.println("ENTERED\n");
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        AnchorPane anchorPane = (AnchorPane) selectedTab.getContent();
        TextArea ta = (TextArea) (anchorPane.getChildren()).get(0);
        String pathName = taToPathMap.get(ta.getId());

        String fileExtension = getFileExtension(pathName);
        String filename = pathName.substring(pathName.lastIndexOf("\\") + 1);
        String dirPath = pathName.substring(0, pathName.lastIndexOf("\\"));
        System.out.println(filename + "\n" + fileExtension + "\n" + dirPath);

        String exeName = getFileName(filename, fileExtension);
        File dir = new File(dirPath);
        ProcessBuilder builder = null;
        try{
            switch (fileExtension) {
                case "c": {
                    Process p = Runtime.getRuntime().exec("cmd /C gcc " + filename + " -o " + exeName, null, dir);
                    if(p.getErrorStream().read() != -1){
                        printToOutputTextArea(p.getErrorStream());
                        return;
                    }
                    if(p.exitValue() != 0) return;
                    builder = new ProcessBuilder(dirPath + "\\" + exeName + ".exe");
                    break;
                }
                case "cpp": {
                    Process p = Runtime.getRuntime().exec("cmd /C g++ " + filename + " -o " + exeName, null, dir);
                    if(p.getErrorStream().read() != -1){
                        printToOutputTextArea(p.getErrorStream());
                        return;
                    }
                    if(p.exitValue() != 0) return;
                    builder = new ProcessBuilder(dirPath + "\\" + exeName + ".exe");
                    break;
                }
                case "java":
                    String[] command = {"javac", pathName};
                    ProcessBuilder processBuilder = new ProcessBuilder(command);
                    Process process = processBuilder.start();
                    if( process.getErrorStream().read() != -1 ){
                        printToOutputTextArea(process.getErrorStream());
                        return;
                    }
                    if(process.exitValue() != 0) return;
                    builder = new ProcessBuilder("java","-cp",dirPath + "\\",exeName);
                    break;
                default:
                    builder = new ProcessBuilder("python", dir + "\\" + filename);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String inputPathName = dirPath + "\\" + exeName + "_INPUT.txt";
        System.out.println(inputPathName);
        ObservableList<CharSequence> paragraph = inputTextArea.getParagraphs();
        Iterator<CharSequence> iter = paragraph.iterator();
        BufferedWriter bf = new BufferedWriter(new FileWriter(new File(inputPathName)));
        while(iter.hasNext())
        {
            CharSequence seq = iter.next();
            bf.append(seq);
            bf.newLine();
        }
        bf.flush();
        bf.close();

        String outputPathName = dirPath + "\\" + exeName + "_OUTPUT.txt";
        System.out.println(outputPathName);
        ObservableList<CharSequence> paragraph1 = outputTextArea.getParagraphs();
        Iterator<CharSequence> iter1 = paragraph1.iterator();
        BufferedWriter bf1 = new BufferedWriter(new FileWriter(new File(outputPathName)));
        while(iter1.hasNext())
        {
            CharSequence seq = iter1.next();
            bf1.append(seq);
            bf1.newLine();
        }
        bf1.flush();
        bf1.close();

        builder.redirectInput(new File(inputPathName)).redirectOutput(new File(outputPathName));
        Process process = builder.start();

        if( process.getErrorStream().read() != -1 ){
            printToOutputTextArea(process.getErrorStream());
        }

        int exitCode = process.waitFor();
        System.out.println("EXIT CODE: " + exitCode);

        FileReader reader = new FileReader(outputPathName);
        BufferedReader br = new BufferedReader(reader);

        String line;
        StringBuilder totalFile = new StringBuilder();
        long linesLoaded = 0;
        while ((line = br.readLine()) != null) {
            totalFile.append(line);
            totalFile.append("\n");
        }
        String result = totalFile.toString();
        outputTextArea.setText(result);
    }

    private void printToOutputTextArea(InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        System.out.println("************* "+ "Compilation Errors" +"***********************");
        String line = null;
        while((line = in.readLine()) != null ){
            outputTextArea.appendText(line + "\n");
        }
        in.close();
    }

    private static void printLines(String name, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(name + " " + line);
        }
    }

    private static void runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        printLines(command + " stdout:", pro.getInputStream());
        printLines(command + " stderr:", pro.getErrorStream());
        pro.waitFor();
        System.out.println(command + " exitValue() " + pro.exitValue());
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

    public void exportButtonClicked(ActionEvent actionEvent) throws IOException {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        AnchorPane anchorPane = (AnchorPane) selectedTab.getContent();
        TextArea ta = (TextArea) (anchorPane.getChildren()).get(0);
        String pathName = taToPathMap.get(ta.getId());
        String dirPath = pathName.substring(0, pathName.lastIndexOf("\\"));
        String filename = pathName.substring(pathName.lastIndexOf("\\") + 1);

        String fileExtension = getFileExtension(pathName);
        String exeName = getFileName(filename, fileExtension);

        String outputPathName = dirPath + "\\" + exeName + "_OUTPUT.txt";
        System.out.println(outputPathName);
        ObservableList<CharSequence> paragraph = outputTextArea.getParagraphs();
        Iterator<CharSequence> iter = paragraph.iterator();
        BufferedWriter bf = new BufferedWriter(new FileWriter(new File(outputPathName)));
        while(iter.hasNext())
        {
            CharSequence seq = iter.next();
            bf.append(seq);
            bf.newLine();
        }
        bf.flush();
        bf.close();
    }

    private String getFileName(String filename, String extension){
        switch (extension) {
            case "c":
                return filename.substring(0, filename.length() - 2);
            case "cpp":
                return filename.substring(0, filename.length() - 4);
            case "java":
                return filename.substring(0, filename.length() - 5);
            default:
                return filename.substring(0, filename.length() - 3);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {

        this.socket = socket;

        TaskReadThread task = new TaskReadThread(socket, this);
        thread = new Thread(task);
        thread.start();
    }

    public void sendMessageClicked(ActionEvent actionEvent) throws IOException {
        String sendTo = sendToTF.getText().trim();
        String message = messageTF.getText().trim();

        if(sendTo.isEmpty() || message.isEmpty()){
            JOptionPane.showMessageDialog(null, "Send to and Message should not be empty.");
        }
        else{
            chatTextArea.appendText("[" + useridInfo.getUsername() + "]: " + message + "\n");
            ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
            oo.writeInt(6);
            oo.writeUTF(sendTo);
            oo.writeUTF(message);
            oo.flush();
            sendToTF.clear();
            messageTF.clear();
        }
    }

    public void goToDashboardMenuClicked(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        thread.interrupt();
        stopThread();
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

    private void stopThread() throws IOException {
        ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
        oo.writeInt(6);
        oo.writeUTF("STOP-THREAD");
        oo.writeUTF("STOP-THREAD");
        oo.flush();
    }

    public void setUseridInfo(UseridInfo useridInfo) {
        this.useridInfo = useridInfo;
    }
}
