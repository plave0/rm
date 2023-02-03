package z03_vote_url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {

        try {
            URL url = new URL(null, "evote://localhost?test=5", new VoteHandler());
            URLConnection connection = url.openConnection();
            connection.connect();

            BufferedReader serverReader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()
                    , StandardCharsets.US_ASCII
            ));

            System.out.println(connection.getContent());

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
