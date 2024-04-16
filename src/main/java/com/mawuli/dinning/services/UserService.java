package com.mawuli.dinning.services;

import com.mawuli.dinning.model.User;
import com.mawuli.dinning.repositories.UserRepository;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (user.getName() == null) {
            throw new IllegalArgumentException("Name is required");
        }
        if (userRepository.findUserByName(user.getName()).isPresent()) {
            return null;
        }
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        if (user.getName() == null) {
            throw new IllegalArgumentException("Name is required");
        }
        Optional<User> userEntity = userRepository.findUserByName(user.getName());
        if (userEntity.isEmpty()) {
            return null;
        }
        user.setUser_id(userEntity.get().getUser_id());
        return userRepository.save(user);
    }
}
