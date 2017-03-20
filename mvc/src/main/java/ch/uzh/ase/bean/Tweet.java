package ch.uzh.ase.bean;


/**
 * Created by flaviokeller on 19.03.17.
 */
//@Entity
public class Tweet {

    private int id;
    private String content;
    private String author;

    public Tweet(String content, String author) {
        this.content = content;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
