package ru.vernalis.servicesspringsample.persistance.model;


import javax.persistence.*;

@Entity
@Table(name = "user_message")
public class UserMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne(cascade = CascadeType.ALL)
    private ServiceUser user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String password) {
        this.text = password;
    }

    public ServiceUser getUser() {
        return user;
    }

    public void setUser(ServiceUser user) {
        this.user = user;
    }
}
