package ru.mirea.project.Domain.Models;
import ru.mirea.project.Domain.Enums.SubscriptionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;
    private String name;
    private int age;
    private String phone;
    private String email;
    private SubscriptionType subscriptionType;
    private String passwordHash;

    public User(String name, int age, String phone, String email, SubscriptionType subscriptionType, String passwordHash){
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.subscriptionType = subscriptionType;
        this.passwordHash = passwordHash;
    }

    public User(Long id, String name, int age, String phone, String email, SubscriptionType subscriptionType, String passwordHash){
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.subscriptionType = subscriptionType;
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString(){
        return "User{id=" + id + ", name='" + name + "'}";
    }

}
