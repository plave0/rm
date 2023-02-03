package z01_niti;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    private static final int NUM_THREADS = 5;
    private static final int QUEUE_SIZE = 10;
    private static final String INPUT_PATH = "2022_jun2/test/log.txt";
    public static final String END_WORK = "";

    public static void main(String[] args) {

        BlockingQueue<String> workQueue = new ArrayBlockingQueue<String>(QUEUE_SIZE);

        Thread threads[] = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i += 1) {
            threads[i] = new Thread(new LineWorker(workQueue));
            threads[i].start();
        }

        try (Scanner logScanner = new Scanner(new File(INPUT_PATH))) {

            while(logScanner.hasNextLine()) {
                workQueue.put(logScanner.nextLine());
            }

            workQueue.put(END_WORK);
        }
        catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }
}
