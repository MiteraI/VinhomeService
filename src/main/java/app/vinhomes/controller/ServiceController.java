package app.vinhomes.controller;

import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import app.vinhomes.repository.order.ServiceRepository;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Service> getAllServices () {
        return serviceRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Service getServiceById (@PathVariable("id") Long serviceId, Model model) {
        return serviceRepository.getServicesByServiceId(serviceId);
    }

    @PostMapping (value = "/create")
    public ResponseEntity createService (@RequestBody JsonNode data) {
        String serviceName = data.get("serviceName").asText();
        String priceStr = data.get("price").asText();
        String numOfPeopleStr = data.get("numOfPeople").asText();
        String description = data.get("description").asText();
        String serviceCategoryIdStr = data.get("serviceCategoryId").asText();
        if (serviceName.isEmpty() || priceStr.isEmpty() || numOfPeopleStr.isEmpty() || description.isEmpty() || serviceCategoryIdStr.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Please fill all field to create new Service");
        }
        double price = Double.parseDouble(priceStr);
        int numOfPeople = Integer.parseInt(numOfPeopleStr);
        Long serviceCategoryId = Long.parseLong(serviceCategoryIdStr);
        ServiceCategory serviceCategory = serviceCategoryRepository.findByServiceCategoryId(serviceCategoryId);
        Service service = Service.builder()
                .serviceName(serviceName)
                .description(description)
                .price(price)
                .numOfPeople(numOfPeople)
                .serviceCategory(serviceCategory)
                .build();
        serviceRepository.save(service);
        return ResponseEntity.status(HttpStatus.CREATED).body("Has created a new service");
    }
    @PostMapping(value = "update/{id}")
    public ResponseEntity updateService (@RequestBody JsonNode data, @PathVariable("id") Long serviceId) {
        String serviceName = data.get("serviceName").asText();
        String priceStr = data.get("price").asText();
        String numOfPeopleStr = data.get("numOfPeople").asText();
        String description = data.get("description").asText();
        String serviceCategoryIdStr = data.get("serviceCategoryId").asText();
        double price = 0;
        int numOfPeople = 0;
        ServiceCategory serviceCategory = null;
        Service service = serviceRepository.getServicesByServiceId(serviceId);
        if (serviceName.isEmpty() && priceStr.isEmpty() && numOfPeopleStr.isEmpty() && description.isEmpty() && serviceCategoryIdStr.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Please update something new");
        }
        if (serviceName.isEmpty()) {
            serviceName = service.getServiceName();
        }
        if (priceStr.isEmpty()) {
            price = service.getPrice();
        }
        else if (!priceStr.isEmpty()) {
            price = Double.parseDouble(priceStr);
        }
        if (numOfPeopleStr.isEmpty()) {
            numOfPeople = service.getNumOfPeople();
        }
        else if (!numOfPeopleStr.isEmpty()) {
            numOfPeople = Integer.parseInt(numOfPeopleStr);
        }
        if (description.isEmpty()) {
            description = service.getDescription();
        }
        if (serviceCategoryIdStr.isEmpty()) {
            serviceCategory = service.getServiceCategory();
        }
        service.setServiceCategory(serviceCategory);
        service.setServiceName(serviceName);
        service.setPrice(price);
        service.setDescription(description);
        service.setNumOfPeople(numOfPeople);
        serviceRepository.save(service);
        return ResponseEntity.status(HttpStatus.OK).body("Has updated the service");
    }
}
