package app.vinhomes.controller;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Service;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.order.ServiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.event.ListDataEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/services")
public class ServiceController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Service getServiceById(@PathVariable("id") Long serviceId) {
        return serviceRepository.getServicesByServiceId(serviceId);
    }

    @GetMapping(value = "/avg-rating/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object[] calculateAvgStarForEachService(@PathVariable("id") Long categoryId) {
        List<Service> serviceWithSameCategory = new ArrayList<>();
        List<Integer> serviceId = new ArrayList<>();
        Object[] avgStartEachService = new Object[2];
        serviceWithSameCategory = serviceRepository.findByServiceCategory_serviceCategoryId(categoryId);
        serviceId = serviceWithSameCategory.stream().map(s -> s.getServiceId().intValue()).collect(Collectors.toList());
        System.out.println("pc's serviceId.size is: " + serviceId.size());
        System.out.println("avg rating of each service: " + avgRatingForEachService(serviceId));
        avgStartEachService[0] = avgRatingForEachService(serviceId);
        avgStartEachService[1] = avgForEachRating(ratingMap(serviceId), serviceId);
        System.out.println(avgForEachRating(ratingMap(serviceId), serviceId));
        return ResponseEntity.status(HttpStatus.OK).body(avgStartEachService).getBody();
    }

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
