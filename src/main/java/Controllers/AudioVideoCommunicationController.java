package Controllers;

import UtilClasses.GlobalEditorThread;
import UtilClasses.SerializableImage;
import UtilClasses.UseridInfo;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
//import com.xuggle.mediatool.IMediaWriter;
//import com.xuggle.mediatool.ToolFactory;
//import com.xuggle.xuggler.ICodec;
//import com.xuggle.xuggler.IPixelFormat;
//import com.xuggle.xuggler.IVideoPicture;
//import com.xuggle.xuggler.video.ConverterFactory;
//import com.xuggle.xuggler.video.IConverter;
import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class AudioVideoCommunicationController implements Initializable {
    public ImageView senderImageView;
    public ImageView receiverImageView;
    Webcam webcam;
    private Socket socket;
    private UseridInfo useridInfo;
    private int receiverId;
    private String receiverUsername;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(320, 240));
        webcam.open();
//        new Thread(new VideoFeedTaker()).start();
    }

    class VideoFeedTaker implements Runnable {

        @Override
        public void run() {
            while (true){
                BufferedImage buffImage = webcam.getImage();
                Image img = SwingFXUtils.toFXImage(buffImage, null);
                senderImageView.setImage(img);
                SerializableImage serializableImage = new SerializableImage();
                serializableImage.setImage(img);
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeInt(19);
                    objectOutputStream.writeUTF(receiverUsername);
                    objectOutputStream.writeObject(serializableImage);
                    objectOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class AudioVideoCommunicationThread implements Runnable {

        @Override
        public void run() {
            while(true){
                try{
                    ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                    int choice = (int) oi.readInt();
                    if(choice==1000) break;

                    switch (choice){
                        case 1:
                            //To get image of client from server
                            SerializableImage serializableImage = (SerializableImage) oi.readObject();
                            System.out.println("HELLO");
                            receiverImageView.setImage(serializableImage.getImage());
                            break;
                        case 2:
                            break;
                        case 3:
                            String acknowledgementFromServer = oi.readUTF();
                            break;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        AudioVideoCommunicationThread audioVideoCommunicationThread = new AudioVideoCommunicationThread();
        Thread thread = new Thread(audioVideoCommunicationThread);
        thread.start();
        new Thread(new VideoFeedTaker()).start();
    }

    public void setUseridInfo(UseridInfo useridInfo) {
        this.useridInfo = useridInfo;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public Socket getSocket() {
        return socket;
    }

    public UseridInfo getUseridInfo() {
        return useridInfo;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }
}
