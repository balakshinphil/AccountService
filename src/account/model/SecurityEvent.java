package account.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SECURITY_EVENT")
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date date;

    @Enumerated(EnumType.STRING)
    private SecurityEventAction action;

    private String subject;

    private String object;

    private String path;

    public SecurityEvent() {
    }

    public SecurityEvent(SecurityEventAction action, String subject, String object, String path) {
        this.date = new Date();
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public SecurityEventAction getAction() {
        return action;
    }

    public void setAction(SecurityEventAction action) {
        this.action = action;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
