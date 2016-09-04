package dataObjects;

/**
 * Created by Thagus on 03/09/16.
 */
public class Document {
    private int id;
    private String title;

    public Document(int id){
        this.id = id;
        title = "";
    }

    public void appendTitle(String string){
        title += string;
        title = title.trim();
    }

    public String getTitle() {
        return title;
    }
}
