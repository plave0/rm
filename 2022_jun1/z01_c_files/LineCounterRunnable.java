package z01_c_files;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class LineCounterRunnable implements Runnable{

    private URL cFileUrl;
    private AtomicInteger globalCounter;

    public LineCounterRunnable(URL cFileUrls, AtomicInteger globalCounter) {
        this.cFileUrl = cFileUrls;
        this.globalCounter = globalCounter;
    }

    @Override
    public void run() {

        int lineCounter = 0;

        try (
                InputStream cFileStream = this.cFileUrl.openStream();
                Scanner cFileScanner = new Scanner(cFileStream)
        ) {
                while(cFileScanner.hasNextLine()) {
                    lineCounter += 1;
                    cFileScanner.nextLine();
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        globalCounter.getAndAdd(lineCounter);
    }
}
