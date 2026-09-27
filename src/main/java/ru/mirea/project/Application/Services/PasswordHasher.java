package ru.mirea.project.Application.Services;

public interface PasswordHasher {

    String hash(String password);
    boolean matches(String password, String hash);
}