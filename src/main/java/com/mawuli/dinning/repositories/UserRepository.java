package com.mawuli.dinning.repositories;

import com.mawuli.dinning.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByName(String name);
}
