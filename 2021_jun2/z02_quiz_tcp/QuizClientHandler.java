package z02_quiz_tcp;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuizClientHandler implements Runnable{

    private Socket clientSocket;
    private String clientName;
    private String clientSubject;
    private Map<String, List<Question>> questionsBySubject;


    @Override
    public void run() {

        try (
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(
                        this.clientSocket.getInputStream()
                ));
                BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(
                        this.clientSocket.getOutputStream()
                ))
        ) {

            this.clientName = clientReader.readLine();

            String subjectsString = String.join(", ", this.questionsBySubject.keySet());
            clientWriter.write("Choose your subject: " + subjectsString + "\n");
            clientWriter.flush();

            this.clientSubject = clientReader.readLine();

            if (this.questionsBySubject.keySet().contains(this.clientSubject)) {

                for (Question q : this.questionsBySubject.get(this.clientSubject)) {

                    clientWriter.write(q.getQuestion());
                    clientWriter.flush();

                    Instant startTime = Instant.now();
                    String answer = clientReader.readLine();
                    Instant endTime = Instant.now();

                    if(endTime.minusSeconds(5).isAfter(startTime)) {
                        clientWriter.write("Nista stigli da odgovorite na vreme.\n");
                        clientWriter.flush();
                    }
                    else if(answer.equalsIgnoreCase(q.getAnswer())) {
                        clientWriter.write("Tacan odgovor. Osvojili ste " + q.getPoints() + " poena.\n");
                        clientWriter.flush();
                    }
                    else if(answer.equalsIgnoreCase("ne znam")) {
                        clientWriter.write("Niste znali tacan odgovor.\n");
                        clientWriter.flush();
                    }
                    else {
                        clientWriter.write("Netacan odgovor. Izgubili ste 1 poen.\n");
                        clientWriter.flush();
                    }
                }

                clientWriter.write("\n");
                clientWriter.flush();
            }
            else {

                clientWriter.write("Oblast ne postoji\n");
                clientWriter.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public QuizClientHandler(Socket clientSocket, Map<String, List<Question>> questionsBySubject) {
        this.clientSocket = clientSocket;
        this.questionsBySubject = questionsBySubject;
    }
}
