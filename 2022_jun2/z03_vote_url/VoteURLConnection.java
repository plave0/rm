package z03_vote_url;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

public class VoteURLConnection extends URLConnection {

    private Socket serverConnection;

    public VoteURLConnection(URL url) {
        super(url);
        this.serverConnection = null;
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        return this.serverConnection == null ?
                null :
                this.serverConnection.getInputStream();
    }

    public int getConnectionPort() {

        return this.url.getPort() != -1 ?
                this.url.getPort() :
                VoteHandler.DEFAULT_PORT;
    }

    @Override
    public synchronized void connect() throws IOException {

        if(this.connected) { throw new IOException("Already connected to server"); }

        this.serverConnection = new Socket(this.url.getHost(), this.getConnectionPort());

        // Sending reques
        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(
                this.serverConnection.getOutputStream()
                , StandardCharsets.US_ASCII
        ));
        String requestString = this.parseRequest(this.url);

        serverWriter.write(requestString);
        serverWriter.flush();

        this.connected = true;
    }

    @Override
    public String getContent() throws IOException {

        BufferedReader serverReader = new BufferedReader(new InputStreamReader(
                this.getInputStream()
                , StandardCharsets.US_ASCII
        ));

        return serverReader.readLine();
    }

    private String parseRequest(URL url) {

        String vote =
                URLDecoder.decode(this.url.getQuery())
                .split("=")[1];

        return vote + "\n";
    }
}
