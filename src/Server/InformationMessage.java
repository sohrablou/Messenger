package Server;

import enums.InformationType;
import objects.User;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Adam on 8/19/2016.
 */
public class InformationMessage implements Serializable {

    private String text;
    private String timestamp;
    private User user;
    private InformationType type;
    private int key;
    private SerializableRoom room;


    private DateFormat dateFormat = new SimpleDateFormat("[MM/dd/YYYY HH:mm:ss] ");
    private Calendar cal = Calendar.getInstance();

    public InformationMessage(String text, int key){
        this.text = text;
        this.key = key;
        this.type = InformationType.INFORMATION;
        this.timestamp = timeStamp();

    }

    public InformationMessage(String text, User user, int key, InformationType type){
        this.text = text;
        this.user = user;
        this.key = key;
        this.type = type;
        this.timestamp = timeStamp();
    }

    public InformationMessage(String text, User user, InformationType type, SerializableRoom room){
        this.text = text;
        this.user = user;
        this.type = type;
        this.timestamp = timeStamp();
        this.room = room;
    }


    public String getTimestamp(){
        return timestamp;
    }

    private String timeStamp(){
        return dateFormat.format(cal.getTime());
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }

    public InformationType getType() {
        return type;
    }

    public int getKey() {
        return key;
    }

    public SerializableRoom getRoom() {
        return room;
    }

    public void setRoom(SerializableRoom room) {
        this.room = room;
    }
}
