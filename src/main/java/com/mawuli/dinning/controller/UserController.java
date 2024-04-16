package com.mawuli.dinning.controller;

import com.mawuli.dinning.model.User;
import com.mawuli.dinning.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(@RequestBody User user) {
        validateUser(user);

        userRepository.save(user);
    }

    @GetMapping("/{displayName}")
    public User getUser(@PathVariable String displayName) {
        validateDisplayName(displayName);

        Optional<User> optionalExistingUser = userRepository.findUserByName(displayName);
        if (optionalExistingUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User existingUser = optionalExistingUser.get();
        existingUser.setUser_id(null);

        return existingUser;
    }

    @PutMapping("/{displayName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserInfo(@PathVariable String displayName, @RequestBody User updatedUser) {
        validateDisplayName(displayName);

        Optional<User> optionalExistingUser = userRepository.findUserByName(displayName);
        if (optionalExistingUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User existingUser = optionalExistingUser.get();

        copyUserInfoFrom(updatedUser, existingUser);
        userRepository.save(existingUser);
    }

    private void copyUserInfoFrom(User updatedUser, User existingUser) {
        if (ObjectUtils.isEmpty(updatedUser.getName())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (!ObjectUtils.isEmpty(updatedUser.getCity())) {
            existingUser.setCity(updatedUser.getCity());
        }

        if (!ObjectUtils.isEmpty(updatedUser.getState())) {
            existingUser.setState(updatedUser.getState());
        }

        if (!ObjectUtils.isEmpty(updatedUser.getZipcode())) {
            existingUser.setZipcode(updatedUser.getZipcode());
        }

        if (!ObjectUtils.isEmpty(updatedUser.isPeanut())) {
            existingUser.setPeanut(updatedUser.isPeanut());
        }

        if (!ObjectUtils.isEmpty(updatedUser.isDairy())) {
            existingUser.setDairy(updatedUser.isDairy());
        }

        if (!ObjectUtils.isEmpty(updatedUser.isEgg())) {
            existingUser.setEgg(updatedUser.isEgg());
        }
    }

    private void validateUser(User user) {
        validateDisplayName(user.getName());

        Optional<User> existingUser = userRepository.findUserByName(user.getName());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private void validateDisplayName(String displayName) {
        if (ObjectUtils.isEmpty(displayName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
