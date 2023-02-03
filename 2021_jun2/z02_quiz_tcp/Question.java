package z02_quiz_tcp;

public class Question {

    private String question;
    private String answer;
    private int points;

    public Question(String question, String answer, int points) {
        this.question = question;
        this.answer = answer;
        this.points = points;
    }

    public Question(String questionLine) {

        this.question = questionLine.substring(0, questionLine.indexOf("?") + 1);
        this.answer = questionLine.split("\\?")[1].strip().split(" ")[0];
        this.points = Integer.parseInt(questionLine.split("\\?")[1].strip().split(" ")[1]);
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getPoints() {
        return points;
    }
}
