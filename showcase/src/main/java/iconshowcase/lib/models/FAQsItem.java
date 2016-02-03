package iconshowcase.lib.models;

public class FAQsItem {

    private String question;
    private String answer;

    public FAQsItem(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getanswer() {
        return answer;
    }

    public void setanswer(String answer) {
        this.answer = answer;
    }

    public String getquestion() {
        return question;
    }

    public void setquestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return question + "\n" + answer;
    }

}
