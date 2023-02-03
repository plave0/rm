package z01_niti;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class LineWorker implements Runnable{

    private BlockingQueue<String> workQueue;
    public LineWorker(BlockingQueue<String> workQueue) {
        this.workQueue = workQueue;
    }

    @Override
    public void run() {

        boolean workFinished = false;

        while(!workFinished) {

            String nextTask;

            try {
                nextTask = this.workQueue.take();
                if (nextTask.equals(Main.END_WORK)) {
                    workFinished = true;
                    this.workQueue.put(Main.END_WORK);
                    continue;
                }
            }
            catch (InterruptedException e) {
                workFinished = true;
                continue;
            }

            this.processTask(nextTask);
        }
    }

    private void processTask(String task) {

        URL url;
        try {
            url = new URL(task);
        } catch (MalformedURLException e) {
            url = null;
        }

        if (url == null) {
            System.out.println("BADURL " + task);
        }
        else if (url.getProtocol().equalsIgnoreCase("file")) {
            String output = this.createOutput(url);
            System.out.println(output);
        }
        else {
            System.out.println("FORWARD " + task + " " + new Date());
        }
    }

    private String createOutput(URL url) {

        String output;

        try {
            URLConnection connection = url.openConnection();

            BufferedReader connectionReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            String filePath = url.getFile();
            long lineCount = connectionReader.lines().count();

            output = "OK " + filePath + " " + lineCount;

            connectionReader.close();

        } catch (IOException e) {
            output = "NOTFOUND " + url.getFile();
        }

        return output;
    }
}
