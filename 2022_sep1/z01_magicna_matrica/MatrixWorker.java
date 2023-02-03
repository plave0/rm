package z01_magicna_matrica;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class MatrixWorker implements Runnable{

    private BlockingQueue<Path> workQueue;

    public MatrixWorker(BlockingQueue<Path> workQueue) {
        this.workQueue = workQueue;
    }

    @Override
    public void run() {

        boolean workFinished = false;

        while(!workFinished) {

            try {

                Path nextTask = this.workQueue.take();
                if(nextTask.equals(Main.END_WORK)) {
                    this.workQueue.put(Main.END_WORK);
                    workFinished = true;
                    continue;
                }

                this.processTask(nextTask);

            } catch (InterruptedException e) {
                workFinished = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processTask(Path task) throws IOException {

        Scanner matrixScanner = new Scanner(task);

        int matrixSize = matrixScanner.nextInt();
        int[][] matrix = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i += 1) {
            for (int j = 0; j < matrixSize; j += 1) {
                matrix[i][j] = matrixScanner.nextInt();
            }
        }

        System.out.println("Matrix from file " + task.getFileName() + " is magical");
    }
}
