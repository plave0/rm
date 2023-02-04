package z01_c_files;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class CFileCounter {

    public static final String TEST_PATH = "2022_jun1/test";
    public static void main(String[] args) {

        CFileCounter cFileCounter = new CFileCounter();
        cFileCounter.findCFiles(Paths.get(CFileCounter.TEST_PATH));

        List<URL> cFiles = cFileCounter.getcFileUrls();
        int regularFileCount = cFileCounter.getRegularFileCount();

        System.out.println("File count:" + regularFileCount);
        System.out.println("C files:");
        for (URL u : cFiles) {
            System.out.println("\t" + u.toString());
        }

        int totalLineCount = 0;
        try {
            totalLineCount = cFileCounter.coutnAllLines();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Total line count in c files: " + totalLineCount);
    }

    private int coutnAllLines() throws InterruptedException {

        AtomicInteger globalCounter = new AtomicInteger(0);

        List<Thread> threads = new Vector<>();
        for(URL u : this.cFileUrls) {
            Thread t = new Thread(new LineCounterRunnable(u, globalCounter));
            threads.add(t);
            t.start();
        }
        for(Thread t : threads) {
            t.join();
        }

        return globalCounter.get();
    }

    private int regularFileCount = 0;
    private List<URL> cFileUrls = new Vector<>();

    public CFileCounter() {}

    public int getRegularFileCount() {
        return regularFileCount;
    }

    public List<URL> getcFileUrls() {
        return cFileUrls;
    }

    public void findCFiles(Path startPath) {

        try (DirectoryStream<Path> dStream = Files.newDirectoryStream(startPath)) {

            for(Path path : dStream) {

                if(Files.isRegularFile(path)) {
                    this.regularFileCount += 1;
                    if(path.toString().endsWith(".c")) {
                        this.cFileUrls.add(
                                new URL("FILE://" + path.toAbsolutePath())
                        );
                    }
                }
                else if(Files.isDirectory(path)) {
                    findCFiles(path);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
