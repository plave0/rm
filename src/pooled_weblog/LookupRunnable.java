package pooled_weblog;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class LookupRunnable implements Runnable{

    private final PooledWeblog log;
    private final List<String> entries;

    public LookupRunnable(PooledWeblog log) {
        this.log = log;
        this.entries = log.getEntries();
    }

    @Override
    public void run() {

        for (String entry = this.getNextEntry(); entry != null; entry = this.getNextEntry()) {

            String result = this.analyzeEntryAndGetResult(entry);
            if (result == null) {
                continue;
            }

            try {
                this.log.log(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getNextEntry() {
        synchronized (this.entries) {
            while (this.entries.size() == 0) {
                if(this.log.isFinished()) {
                    System.err.println("Thread exiting: " + Thread.currentThread());
                    return null;
                }

                try {
                    this.entries.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return this.entries.remove(0);
        }
    }

    private String analyzeEntryAndGetResult(String entry) {
        int index = entry.indexOf(' ');
        if (index == -1)
            return null;

        String remoteHost = entry.substring(0, index);
        String theRest = entry.substring(index);

        try {
            remoteHost = InetAddress.getByName(remoteHost).getHostName();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return remoteHost + theRest;
    }
}
