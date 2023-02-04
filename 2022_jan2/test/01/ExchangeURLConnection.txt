package z03_exchange_url;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class ExchangeURLConnection extends URLConnection {

    private Socket connection;
    public ExchangeURLConnection(URL u) {
        super(u);
        this.connection = null;
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if(!this.connected) {
            this.connect();
        }
        InputStream serverInt = this.connection.getInputStream();
        return serverInt;
    }

    @Override
    public synchronized void connect() throws IOException {

        if (this.connected) { return; }

        int connectionPort =
                this.url.getPort() != -1 ?
                        this.url.getPort() :
                        ExchangeHandler.DEFAULT_PORT;

        this.connection = new Socket(this.url.getHost(), connectionPort);

        String urlQuery = this.url.getQuery();
        String queryToSend = this.buildQuery(urlQuery);

        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(this.connection.getOutputStream()));

        serverWriter.write(queryToSend);
        serverWriter.flush();

        this.connected = true;
    }

    private String buildQuery(String urlQuery) {
        String[] variables = urlQuery.split("&");

        String curreny = variables[0].split("=")[1];
        String amount = variables[1].split("=")[1];

        return curreny + " " + amount + "\n";
    }
}
