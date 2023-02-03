package z03_vote_url;

import java.io.IOException;
import java.net.*;

public class VoteHandler extends URLStreamHandler {

    public static final int DEFAULT_PORT = 12345;

    @Override
    protected int getDefaultPort() {
        return VoteHandler.DEFAULT_PORT;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {

        if (
                u.getQuery() == null
                || !u.getQuery().split("=")[0].equalsIgnoreCase("kandidat")
        ) {
            throw new MalformedURLException("Upit nije kompletan");
        }
        return new VoteURLConnection(u);
    }
}
