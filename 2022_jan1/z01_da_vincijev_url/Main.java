package z01_da_vincijev_url;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    private static final int NUM_THREADS = 5;
    private static final int QUEUE_SIZE = 20;
    private static final String START_DIR = "2022_sep1/test";
    public static final Path END_WORK = Path.of("");

    public static void main(String[] args) {

        BlockingQueue<Path> queue = new ArrayBlockingQueue<Path>(QUEUE_SIZE);

        Thread threads[] = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i += 1) {
            threads[i] = new Thread(new FileReaderWorker(queue));
            threads[i].start();
        }

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Path.of(START_DIR))) {
            for (Path path : ds) {
                queue.put(path);
            }
            queue.put(Main.END_WORK);
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
