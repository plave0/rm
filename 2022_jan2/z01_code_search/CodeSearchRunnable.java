package z01_code_search;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class CodeSearchRunnable implements Runnable {

    private Path filePath;
    private String stringToSearch;

    public CodeSearchRunnable(Path filePath, String stringToSearch) {
        this.filePath = filePath;
        this.stringToSearch = stringToSearch;
    }

    @Override
    public void run() {

        try (Scanner fileScanner = new Scanner(this.filePath)) {

            int lineCount = 0;

            while(fileScanner.hasNextLine()) {

                lineCount += 1;

                String line = fileScanner.nextLine();
                if(line.contains(this.stringToSearch)) {

                    int position = line.indexOf(this.stringToSearch);
                    synchronized (this) {
                        System.out.println(this.filePath.toString()
                                + ":" + lineCount + ":" + position
                                + "\t"
                                + line);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
