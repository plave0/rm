package z01_da_vincijev_url;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class FileReaderWorker implements Runnable{

    private final BlockingQueue<Path> workQueue;

    public FileReaderWorker(BlockingQueue<Path> queue) {
        this.workQueue = queue;
    }

    @Override
    public void run() {

        boolean workFinished = false;

        while (!workFinished) {

            Path nextPath;

            try {

                nextPath = this.workQueue.take();

                if (nextPath.equals(Main.END_WORK)) {
                    workFinished = true;
                    this.workQueue.put(Main.END_WORK);
                    continue;
                }

            } catch (InterruptedException e) {
                workFinished = true;
                continue;
            }

            this.proccessFile(nextPath);
        }
    }

    private void proccessFile(Path path) {

        Scanner fileInput;

        try {
            fileInput = new Scanner(path.toFile());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        StringBuilder output = new StringBuilder();
        String line;
        int lineNumber = 0;

        while(fileInput.hasNextLine()) {

            lineNumber += 1;
            line = fileInput.nextLine();
            URL url = this.decode(line);

            if (url != null) {
                output.append(this.makeOutput(lineNumber, url));
                output.append("\n");
            }
        }

        this.printOutput(path, output.toString());

    }

    private URL decode(String line) {

        StringBuilder reverseLineBuilder = new StringBuilder();
        for (int i = line.length() - 1; i >= 0; i -= 1) {
            reverseLineBuilder.append(line.charAt(i));
        }

        String reverseLine = reverseLineBuilder.toString();
        URL retURL;

        try {
            retURL = new URL(reverseLine);
        } catch (MalformedURLException e) {
            retURL = null;
        }

        return retURL;
    }

    private String makeOutput(int lineNumber, URL url) {

        StringBuilder output = new StringBuilder();

        output.append(lineNumber);
        output.append(" : ");
        output.append(url.getProtocol());

        if (url.getProtocol().equalsIgnoreCase("https")) {
            output.append(" : ");
            output.append(url.getPort());
        }

        return output.toString();
    }

    synchronized void printOutput(Path currentPath, String output) {
        String fileName = currentPath.toFile().getName();
        System.out.println(fileName + ":");
        System.out.println(output);
    }
}
