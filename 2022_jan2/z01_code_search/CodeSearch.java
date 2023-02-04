package z01_code_search;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class CodeSearch {

    public static final String SEARCH_PATH = "2022_jan2/test/01";

    public static void main(String[] args) {

        CodeSearch codeSearch = new CodeSearch();

        try (Scanner stdInScanner = new Scanner(System.in)) {
            String stringtoSearch = stdInScanner.nextLine();
            codeSearch.find(stringtoSearch);
        }
    }

    private List<Thread> searchThreads;

    public CodeSearch() {
        this.searchThreads = new Vector<>();
    }

    public void find(String stringToSearch) {

        printAllUsages(Paths.get(CodeSearch.SEARCH_PATH), stringToSearch);

        try {
            for (Thread t : this.searchThreads) {
                t.join();
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public void printAllUsages(Path searchPath, String stringToSearch) {

        List<Thread> searchThreads = new Vector<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(searchPath)) {

            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {

                    String fileName = path.getFileName().toString();
                    if(
                            !fileName.endsWith(".png")
                            && !fileName.endsWith(".ico")
                            && !fileName.endsWith("svg")
                    ) {

                        Thread searchThread = new Thread(new CodeSearchRunnable(path, stringToSearch));
                        searchThreads.add(searchThread);
                        searchThread.start();
                    }
                }
                else if (Files.isDirectory(path)) {
                    printAllUsages(path, stringToSearch);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
