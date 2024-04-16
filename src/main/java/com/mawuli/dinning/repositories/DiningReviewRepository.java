package com.mawuli.dinning.repositories;

import com.mawuli.dinning.model.DiningReview;
import com.mawuli.dinning.model.ReviewStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DiningReviewRepository extends CrudRepository<DiningReview, Long> {
    List<DiningReview> findDiningReviewsByRestaurantIdAndStatus(Long restaurantId, ReviewStatus reviewStatus);
    List<DiningReview> findDiningReviewsByStatus(ReviewStatus reviewStatus);
}
