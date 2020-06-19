import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Main {
    public  static  String siteURL = "https://abit.itmo.ru/page/195/";
    public static  void  checkSite(String wordToFind) {
        try {
            URL google = new URL(siteURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(google.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) { // process each line
                if (inputLine.contains( wordToFind))
                {
                    System.out.println( "Yes" );
                    return;
                }
            }
            in.close();
        } catch (MalformedURLException me) {
            System.out.println(me);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

    }
    
    public static void main(String[] args) {
        System.out.println("Введите ФИО через пробел в одну строку с большой буквы:");
        Scanner scan = new Scanner(System.in);
        String str = scan.nextLine();
        System.out.println("Ждите до появления Yes");

        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //no need to implement
                    }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //no need to implement
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

        Integer initalDelay = 0;
        Integer period = 10; //number of seconds to repeat

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
