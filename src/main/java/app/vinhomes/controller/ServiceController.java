package app.vinhomes.controller;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Service;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.order.ServiceRepository;
import app.vinhomes.service.RatingService;

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

    @Autowired
    private RatingService ratingService;

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
        avgStartEachService[0] = ratingService.avgRatingForEachService(serviceId);
        avgStartEachService[1] = ratingService.avgForEachRating(ratingService.ratingMap(serviceId), serviceId);
        return ResponseEntity.status(HttpStatus.OK).body(avgStartEachService).getBody();
    }



}
