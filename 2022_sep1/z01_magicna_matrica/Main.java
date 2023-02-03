package z01_magicna_matrica;

import z01_MnozenjeMatrica.MatrixMultipier;

import java.awt.image.AreaAveragingScaleFilter;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    private static final int NUM_THREAD = 5;
    private static final int QUEUE_SIZE = 10;
    private static final String TEST_PATH = "2022_sep1/tests/z01";
    public static final Path END_WORK = Path.of("");

    public static void main(String[] args) {

        BlockingQueue<Path> workQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);

        Thread fileTreeWalkThead = new Thread(
                new FileTreeWalkRunnable(Path.of(TEST_PATH), workQueue)
        );
        fileTreeWalkThead.start();

        Thread[] threadPool = new Thread[NUM_THREAD];
        for (int i = 0; i < NUM_THREAD; i += 1) {
            threadPool[i] = new Thread(new MatrixWorker(workQueue));
            threadPool[i].start();
        }
    }
}
