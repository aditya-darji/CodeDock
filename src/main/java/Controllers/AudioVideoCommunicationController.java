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
import javax.sound.sampled.*;
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
    String errStr;
    double duration, seconds;
    AudioInputStream audioInputStream,audioInputStreamincoming;
    final int bufSize = 16384;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(320, 240));
        webcam.open();
        new Thread(new VideoFeedTaker()).start();
//        new Thread(new AudioFeedTaker()).start();
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
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public class Playback implements Runnable {

        SourceDataLine line;

        Thread thread;

        public void start() {
            errStr = null;
            thread = new Thread(this);
            thread.setName("Playback");
            thread.start();
        }

        public void stop() {
            thread = null;
        }

        private void shutDown(String message) {
            if ((errStr = message) != null) {
                System.err.println(errStr);
            }
            if (thread != null) {
                thread = null;

            }
        }

        public void run() {

            // make sure we have something to play
            if (audioInputStreamincoming == null) {
                shutDown("No loaded audio to play back");
                return;
            }
            // reset to the beginnning of the stream
            try {
                audioInputStreamincoming.reset();
            } catch (Exception e) {
                shutDown("Unable to reset the stream\n" + e);
                return;
            }

            // get an AudioInputStream of the desired format for playback

            AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
            float rate = 44100.0f;
            int channels = 2;
            int frameSize = 4;
            int sampleSize = 16;
            boolean bigEndian = true;

            AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
                    * channels, rate, bigEndian);

            AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format,
                    audioInputStreamincoming);

            if (playbackInputStream == null) {
                shutDown("Unable to convert stream of format " + audioInputStreamincoming + " to format " + format);
                return;
            }

            // define the required attributes for our line,
            // and make sure a compatible line is supported.

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }

            // get and open the source data line for playback.

            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format, bufSize);
            } catch (LineUnavailableException ex) {
                shutDown("Unable to open the line: " + ex);
                return;
            }

            // play back the captured audio data

            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead = 0;

            // start the source data line
            line.start();

            while (thread != null) {
                try {
                    if ((numBytesRead = playbackInputStream.read(data)) == -1) {
                        break;
                    }
                    int numBytesRemaining = numBytesRead;
                    while (numBytesRemaining > 0) {
                        numBytesRemaining -= line.write(data, 0, numBytesRemaining);
                    }

                } catch (Exception e) {
                    shutDown("Error during playback: " + e);
                    break;
                }
            }
            // we reached the end of the stream.
            // let the data play out, then
            // stop and close the line.
            if (thread != null) {
                line.drain();
            }
            line.stop();
            line.close();
            line = null;
            shutDown(null);
        }
    } // End class Playback
    class Capture implements Runnable {

        TargetDataLine line;

        Thread thread;

        public void start() {
            errStr = null;
            thread = new Thread(this);
            thread.setName("Capture");
            thread.start();
        }

        public void stop() {
            thread = null;
        }

        private void shutDown(String message) {
            if ((errStr = message) != null && thread != null) {
                thread = null;

                System.err.println(errStr);
            }
        }

        public void run() {

            duration = 0;
            audioInputStream = null;

            // define the required attributes for our line,
            // and make sure a compatible line is supported.

            AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
            float rate = 44100.0f;
            int channels = 2;
            int frameSize = 4;
            int sampleSize = 16;
            boolean bigEndian = true;

            AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
                    * channels, rate, bigEndian);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }

            // get and open the target data line for capture.

            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format, line.getBufferSize());
            } catch (LineUnavailableException ex) {
                shutDown("Unable to open the line: " + ex);
                return;
            } catch (SecurityException ex) {
                shutDown(ex.toString());
                //JavaSound.showInfoDialog();
                return;
            } catch (Exception ex) {
                shutDown(ex.toString());
                return;
            }

            // play back the captured audio data
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead;

            line.start();

            while (thread != null) {
                if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                    break;
                }
                out.write(data, 0, numBytesRead);
            }

            // we reached the end of the stream.
            // stop and close the line.
            line.stop();
            line.close();
            line = null;

            // stop and close the output stream
            try {
                out.flush();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // load bytes into the audio input stream for playback

            byte audioBytes[] = out.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

            long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format
                    .getFrameRate());
            duration = milliseconds / 1000.0;

            try {
                audioInputStream.reset();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

        }
    } // End class Capture
    class AudioFeedTaker implements Runnable{

        @Override
        public void run() {
            while(true){
                Capture capture=new Capture();
                Thread thread=new Thread(capture);
                thread.start();
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                thread.stop();
                try {
                    ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeInt(22);
                    objectOutputStream.writeUTF(receiverUsername);
                    objectOutputStream.writeObject(audioInputStream);
                    objectOutputStream.flush();
                } catch (IOException e) {
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
                            receiverImageView.setImage(serializableImage.getImage());
                            break;
                        case 2:
                            audioInputStreamincoming=(AudioInputStream) oi.readObject();
                            Playback playback=new Playback();
                            Thread thread=new Thread(playback);
                            thread.start();

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
