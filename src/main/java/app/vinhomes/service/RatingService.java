package app.vinhomes.service;

import app.vinhomes.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class RatingService {

    @Autowired
    private OrderRepository orderRepository;

    public Map<Integer, Map<Integer, Integer>> ratingMap(List<Integer> serviceId) {
        Map<Integer, Map<Integer, Integer>> serviceRatingMap = new HashMap<Integer, Map<Integer, Integer>>();
        int maxRating = 5, sameRating = 0;
        for (int i = 0; i < serviceId.size(); i++) {
            Map<Integer, Integer> ratingMap = new HashMap<Integer, Integer>();
            for (int j = 1; j <= maxRating; j++) {
                sameRating = orderRepository.COUNT_RATING_FOR_SERVICE(serviceId.get(i), j);
                ratingMap.put(j, sameRating);
            }
            serviceRatingMap.put(serviceId.get(i), ratingMap);
        }
        return serviceRatingMap;
    }

    public List<Double> avgRatingForEachService(List<Integer> serviceId) {
        List<Double> avgRatingEachService = new ArrayList<>();
        Map<Integer, Map<Integer, Integer>> allRatingForEachService = new HashMap<>();
        allRatingForEachService = ratingMap(serviceId);
        int maxRating = 5, totalRating = 0, sumOfAllRating = 0;
        double avgRatingService = 0;
        for (Map.Entry<Integer, Map<Integer, Integer>> rating : allRatingForEachService.entrySet()) {
            totalRating = 0;
            sumOfAllRating = 0;
            for (int i = 1; i <= maxRating; i++) {
                totalRating += rating.getValue().get(i);
                sumOfAllRating += rating.getValue().get(i) * i;
            }
            avgRatingService = (double) sumOfAllRating / totalRating > 0 ? (((double) sumOfAllRating / totalRating) * 100d) / 100d : 0;
            avgRatingEachService.add(avgRatingService);
        }
        return avgRatingEachService;
    }

    public Map<Integer, List<Double>> avgForEachRating(Map<Integer, Map<Integer, Integer>> ratingMap, List<Integer> serviceId) {
        Map<Integer, List<Double>> avgEachRating = new HashMap<>();
        int totalRatingForEachService = 0;
        int maxRating = 5;
        double avg = 0;
        for (Map.Entry<Integer, Map<Integer, Integer>> rating : ratingMap.entrySet()) {
            List<Double> avgRating = new ArrayList<>();
            totalRatingForEachService = totalRatingForEachService(rating.getKey(), serviceId);
            for (int i = 1; i <= maxRating; i++) {
                System.out.println("avg: " + rating.getValue().get(i) + " / " + totalRatingForEachService);
                avg = (double) rating.getValue().get(i) / totalRatingForEachService > 0 ? (double) rating.getValue().get(i) / totalRatingForEachService : 0;
                avgRating.add(avg);
            }
            avgEachRating.put(rating.getKey(), avgRating);
        }
        return avgEachRating;
    }

    public int totalRatingForEachService(int key, List<Integer> serviceId) {
        int maxRating = 5, totalRatingEachService = 0;
        Map<Integer, Map<Integer, Integer>> ratingMap = ratingMap(serviceId);
        for (int i = 1; i <= maxRating; i++) {
            totalRatingEachService += ratingMap.get(key).get(i);
            System.out.println(ratingMap.get(key).get(i));
        }
        return totalRatingEachService;
    }

}
