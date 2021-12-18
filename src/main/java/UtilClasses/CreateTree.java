package UtilClasses;

import Controllers.LocalEditorController;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class CreateTree implements Runnable{
    LocalEditorController localEditorController;
    private File file;
    private Image folderImage, fileImage, cImage, cppImage, javaImage, pythonImage;

    public CreateTree(LocalEditorController ec, File file, Image folderImage, Image fileImage, Image cImage, Image cppImage, Image javaImage, Image pythonImage){
        this.localEditorController = ec;
        this.file = file;
        this.folderImage = folderImage;
        this.fileImage = fileImage;
        this.cImage = cImage;
        this.cppImage = cppImage;
        this.javaImage = javaImage;
        this.pythonImage = pythonImage;
    }

    @Override
    public void run() {
        TreeItem<String> rootItem = new TreeItem<>(file.getName(), new ImageView(folderImage));
        localEditorController.directoryTreeView.setShowRoot(true);

        File fileList[] = file.listFiles();

        for(File f: fileList){
            createTree(f, rootItem);
        }
        localEditorController.directoryTreeView.setRoot(rootItem);
    }

    private static String getFileExtension(String fileName) {
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    private void createTree(File file, TreeItem<String> parent) {
        if (file.isDirectory()) {
            TreeItem<String> treeItem = new TreeItem<>(file.getName(), new ImageView(folderImage));
            parent.getChildren().add(treeItem);
            for (File f : (file.listFiles())) {
                createTree(f, treeItem);
            }
        } else{
            String fileExtension = getFileExtension(file.getAbsolutePath());
            switch (fileExtension) {
                case "c":
                    parent.getChildren().add(new TreeItem<>(file.getName(), new ImageView(cImage)));
                    break;
                case "cpp":
                    parent.getChildren().add(new TreeItem<>(file.getName(), new ImageView(cppImage)));
                    break;
                case "java":
                    parent.getChildren().add(new TreeItem<>(file.getName(), new ImageView(javaImage)));
                    break;
                case "py":
                    parent.getChildren().add(new TreeItem<>(file.getName(), new ImageView(pythonImage)));
                    break;
                default:
                    parent.getChildren().add(new TreeItem<>(file.getName(), new ImageView(fileImage)));
                    break;
            }
        }
    }
}
