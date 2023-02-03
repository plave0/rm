package z03_exchange_url;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {

        try {

            URL url = new URL(null, "exchange://localhost/?valuta=EUR&iznos=5", new ExchangeHandler());

            URLConnection connection = url.openConnection();

            BufferedReader serverReader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()
                    , StandardCharsets.US_ASCII)
            );

            String line = serverReader.readLine();

            System.out.println(line);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
