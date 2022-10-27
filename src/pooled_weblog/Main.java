package pooled_weblog;

import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) throws Exception {

        PooledWeblog pw = new PooledWeblog(
                new FileInputStream("src/pooled_weblog/apache.logfile"),
                System.out,
                6
        );

        pw.processFiles();
        pw.close();
    }
}
