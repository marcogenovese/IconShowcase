/*
 *
 */

package jahirfiquitiva.iconshowcase.models;

public class FAQsItem {

    private String question;
    private String answer;

    public FAQsItem(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return question + "\n" + answer;
    }

}
