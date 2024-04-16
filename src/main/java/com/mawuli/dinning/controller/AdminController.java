package com.mawuli.dinning.controller;

import com.mawuli.dinning.model.AdminReviewAction;
import com.mawuli.dinning.model.DiningReview;
import com.mawuli.dinning.model.Restaurant;
import com.mawuli.dinning.model.ReviewStatus;
import com.mawuli.dinning.repositories.RestaurantRepository;
import com.mawuli.dinning.repositories.DiningReviewRepository;
import com.mawuli.dinning.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@RequestMapping("/admin")
@RestController
public class AdminController {
    private final DiningReviewRepository diningReviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public AdminController(UserRepository userRepository, RestaurantRepository restaurantRepository, DiningReviewRepository diningReviewRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/reviews")
    public List<DiningReview> getReviewsByStatus(@RequestParam String review_status) {
        ReviewStatus reviewStatus = ReviewStatus.PENDING;
        try {
            reviewStatus = ReviewStatus.valueOf(review_status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return diningReviewRepository.findDiningReviewsByStatus(reviewStatus);
    }

    @PutMapping("/reviews/{reviewId}")
    public void performReviewAction(@PathVariable Long reviewId, @RequestBody AdminReviewAction adminReviewAction) {
        Optional<DiningReview> optionalReview = diningReviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        DiningReview review = optionalReview.get();

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(review.getRestaurantId());
        if (optionalRestaurant.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (adminReviewAction.getAccept()) {
            review.setStatus(ReviewStatus.ACCEPTED);
        } else {
            review.setStatus(ReviewStatus.REJECTED);
        }

        diningReviewRepository.save(review);
        updateRestaurantReviewScores(optionalRestaurant.get());
    }

    private void updateRestaurantReviewScores(Restaurant restaurant) {
        List<DiningReview> reviews = diningReviewRepository.findDiningReviewsByRestaurantIdAndStatus(restaurant.getId(), ReviewStatus.ACCEPTED);
        if (reviews.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        int peanutSum = 0;
        int peanutCount = 0;
        int dairySum = 0;
        int dairyCount = 0;
        int eggSum = 0;
        int eggCount = 0;
        for (DiningReview r : reviews) {
            if (!ObjectUtils.isEmpty(r.getPeanutScore())) {
                peanutSum += r.getPeanutScore();
                peanutCount++;
            }
            if (!ObjectUtils.isEmpty(r.getDairyScore())) {
                dairySum += r.getDairyScore();
                dairyCount++;
            }
            if (!ObjectUtils.isEmpty(r.getEggScore())) {
                eggSum += r.getEggScore();
                eggCount++;
            }
        }

        int totalCount = peanutCount + dairyCount + eggCount ;
        int totalSum = peanutSum + dairySum + eggSum;

        float overallScore = (float) totalSum / totalCount;
        restaurant.setOverallScore(Float.valueOf(decimalFormat.format(overallScore)));

        if (peanutCount > 0) {
            float peanutScore = (float) peanutSum / peanutCount;
            restaurant.setPeanutAverageScore(Float.valueOf(decimalFormat.format(peanutScore)));
        }

        if (dairyCount > 0) {
            float dairyScore = (float) dairySum / dairyCount;
            restaurant.setDairyAverageScore(Float.valueOf(decimalFormat.format(dairyScore)));
        }

        if (eggCount > 0) {
            float eggScore = (float) eggSum / eggCount;
            restaurant.setEggAverageScore(Float.valueOf(decimalFormat.format(eggScore)));
        }

        restaurantRepository.save(restaurant);
    }
}
