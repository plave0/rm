package z01_magicna_matrica;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

public class FileTreeWalkRunnable implements Runnable {

    private BlockingQueue<Path> workQueue;
    private Path startPath;

    public FileTreeWalkRunnable(Path startPath, BlockingQueue<Path> workQueue) {
        this.startPath = startPath;
        this.workQueue = workQueue;
    }

    @Override
    public void run() {

        try {
            walk(this.startPath);
            this.workQueue.put(Main.END_WORK);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void walk(Path startPath) throws InterruptedException, IOException {

        DirectoryStream<Path> dStream = Files.newDirectoryStream(startPath);
        for (Path path : dStream) {
            if (Files.isDirectory(path)) {
                walk(path);
            }
            else {
                this.workQueue.put(path);
            }
        }
        dStream.close();
    }
}
