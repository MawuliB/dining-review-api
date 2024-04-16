package com.mawuli.dinning.controller;

import com.mawuli.dinning.model.Restaurant;
import com.mawuli.dinning.model.DiningReview;
import com.mawuli.dinning.model.ReviewStatus;
import com.mawuli.dinning.model.User;
import com.mawuli.dinning.repositories.RestaurantRepository;
import com.mawuli.dinning.repositories.DiningReviewRepository;
import com.mawuli.dinning.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequestMapping("/reviews")
@RestController
public class ReviewController {
    private final DiningReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public ReviewController(DiningReviewRepository reviewRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addUserReview(@RequestBody DiningReview review) {
        validateUserReview(review);

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(review.getRestaurantId());
        if (optionalRestaurant.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        review.setStatus(ReviewStatus.PENDING);
        reviewRepository.save(review);
    }

    private void validateUserReview(DiningReview review) {
        if (ObjectUtils.isEmpty(review.getSubmittedBy())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (ObjectUtils.isEmpty(review.getRestaurantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (ObjectUtils.isEmpty(review.getPeanutScore()) &&
                ObjectUtils.isEmpty(review.getDairyScore()) &&
                ObjectUtils.isEmpty(review.getEggScore())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<User> optionalUser = userRepository.findUserByName(review.getSubmittedBy());
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
