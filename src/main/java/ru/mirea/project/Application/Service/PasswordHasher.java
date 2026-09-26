package ru.mirea.project.Application.Service;

public interface PasswordHasher {

    String hash(String password);
    boolean matches(String password, String hash);
}