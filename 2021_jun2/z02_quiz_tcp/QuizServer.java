package z02_quiz_tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class QuizServer {

    private Map<String, List<Question>> questionsBySubject;

    public static void main(String[] args) {

        try {

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
            String fileName = "2021_jun2/test/z01";

            QuizServer quizServer = new QuizServer(fileName);
            quizServer.startServer();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public QuizServer(String questionPath) throws IOException {

        this.questionsBySubject = new HashMap<>();
        this.loadQuestions(questionPath);
    }

    public void startServer() {

        try (ServerSocket serverSocket = new ServerSocket(12321)) {

            while(true) {

                Socket clientSocket = serverSocket.accept();

                new Thread(new QuizClientHandler(clientSocket, this.questionsBySubject)).start();
            }

            // Run thread
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadQuestions(String questionsPathString) throws IOException {

        Path questionsPath = Path.of(questionsPathString);
        if(!questionsPath.toFile().isDirectory()) {
            throw new IOException("Invalid path, must be a directory");
        }

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(questionsPath)) {

            for (Path p : directoryStream) {

                if(Files.isRegularFile(p)) {

                    String subjectName = p.getFileName().toString().split("\\.")[0];

                    try (BufferedReader questionsReader = new BufferedReader(new InputStreamReader(
                            new FileInputStream(p.toFile())
                    )) ) {

                        List<Question> questions = new Vector<>();
                        String line;
                        while((line = questionsReader.readLine()) != null) {

                            questions.add(new Question(line));
                        }

                        this.questionsBySubject.put(subjectName, questions);
                    }
                }
            }
        }
    }
}
