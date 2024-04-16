package com.mawuli.dinning.services;

import com.mawuli.dinning.model.DiningReview;
import com.mawuli.dinning.model.User;
import com.mawuli.dinning.repositories.DiningReviewRepository;
import com.mawuli.dinning.repositories.UserRepository;

import java.util.Optional;

public class DiningService {
    private final DiningReviewRepository diningReviewRepository;
    private final UserRepository userRepository;

    public DiningService(DiningReviewRepository diningReviewRepository, UserRepository userRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.userRepository = userRepository;
    }

    public DiningReview createDiningReview(DiningReview diningReview) {
        Optional<User> userExist = userRepository.findUserByName(diningReview.getSubmittedBy());
        if(userExist.isEmpty()){
            throw new IllegalArgumentException("User does not exist");
        }
        return diningReviewRepository.save(diningReview);
    }
}
