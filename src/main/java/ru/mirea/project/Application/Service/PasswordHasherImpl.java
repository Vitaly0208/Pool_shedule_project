package ru.mirea.project.Application.Service;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasherImpl implements PasswordHasher {

    @Override
    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public boolean matches(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}