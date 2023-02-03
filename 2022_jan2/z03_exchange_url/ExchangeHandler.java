package z03_exchange_url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ExchangeHandler extends URLStreamHandler {

    public static final int DEFAULT_PORT = 12345;

    @Override
    protected int getDefaultPort() {
        return ExchangeHandler.DEFAULT_PORT;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new ExchangeURLConnection(u);
    }
}
