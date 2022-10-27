package pooled_weblog;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PooledWeblog implements AutoCloseable{

    private final BufferedReader in;
    private final BufferedWriter out;
    private final int numOfThreads;
    private boolean finished;

    private final List<String> entries = Collections.synchronizedList(new LinkedList<>());

    public PooledWeblog(InputStream in, OutputStream out, int numOfThreads) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = new BufferedWriter(new OutputStreamWriter(out));
        this.numOfThreads = numOfThreads;
        this.finished = false;
    }

    void processFiles() {

        Thread[] threads = new Thread[this.numOfThreads];
        for (int i = 0; i < this.numOfThreads; i += 1) {
            Thread t = new Thread(new LookupRunnable(this));
            t.start();
            threads[i] = t;
        }

        try {
            for (String entry = in.readLine(); entry != null; entry = in.readLine()) {
                while (this.entries.size() > this.numOfThreads) {
                    try {
                        Thread.sleep(1000/this.numOfThreads);
                    } catch (InterruptedException e) {
                        this.finished = true;
                        synchronized (this.entries) {
                            this.entries.notifyAll();
                        }
                        return;
                    }
                }
                synchronized (this.entries) {
                    this.entries.add(0, entry);
                    this.entries.notifyAll();
                }
            }

            this.finished = true;
            synchronized (this.entries) {
                this.entries.notifyAll();
            }

            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getEntries() {
        return this.entries;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void log(String msg) throws IOException {
        this.out.write(msg);
        this.out.newLine();
        this.out.flush();
    }

    @Override
    public void close() throws Exception {
        this.in.close();
        this.out.close();
    }
}
