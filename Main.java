import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sound.sampled.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Main {
    // size of the byte buffer used to read/write the audio stream
    private static final int BUFFER_SIZE = 4096;
    //audioFilePath Path of the audio file
    private static final String audioFilePath = "C:\\Users\\Plaza\\IdeaProjects\\HelloWorld\\src\\file_example_WAV_10MG.wav";
    //website for parsing the word
    private static final String siteURL = "https://abit.itmo.ru/page/195/";

    public static void play(String audioFilePath) {
        File audioFile = new File(audioFilePath);
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format);
            audioLine.start();
            System.out.println("Playback started.");
            byte[] bytesBuffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
                audioLine.write(bytesBuffer, 0, bytesRead);
            }
            audioLine.drain();
            audioLine.close();
            audioStream.close();
            System.out.println("Playback completed.");
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
            System.out.println("Audio line for playing back is unavailable.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error playing the audio file.");
            ex.printStackTrace();
        }
    }

    public static void checkSite(String wordToFind) {
        try {
            URL google = new URL(siteURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(google.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) { // process each line
                if (inputLine.contains(wordToFind)) {
                    System.out.println( "Yes" );
                    play(audioFilePath);
                }
            }
            in.close();
        } catch (MalformedURLException me) {
            System.out.println(me);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public static void SSL() {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }
                }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Введите ФИО через пробел в одну строку с большой буквы:");
        Scanner scan = new Scanner(System.in);
        String str = scan.nextLine();
        System.out.println("Ждите до появления Yes");
        System.out.println("----------------------------------------------------------");

        SSL();

        int initalDelay = 0;
        int period = 10; //number of seconds to repeat

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                checkSite(str);
                // do stuff
            }
        }, initalDelay, period, TimeUnit.SECONDS);

    }
}
