package app.vinhomes.controller;

import app.vinhomes.common.CheckSpecialChar;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.order.ServiceRepository;
import app.vinhomes.service.AzureBlobAdapter;
import app.vinhomes.service.RatingService;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.event.ListDataEvent;
import java.io.IOException;
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
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private RatingService ratingService;

    @Autowired
    BlobContainerClient blobContainerClient;
    @Autowired
    private AzureBlobAdapter azureBlobAdapter;
    @Autowired
    BlobServiceClient blobServiceClient;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getAllServices() {
        List<Service> listService = serviceRepository.findAll();
        List<Map<String,Object>> listMap = new ArrayList<>();
        for (Service ser : listService) {
            ServiceCategory serviceCategory = ser.getServiceCategory();
            Map<String, Object> map = new HashMap<>();
            map.put("category", serviceCategory);
            map.put("service", ser);
            listMap.add(map);
        }
        return ResponseEntity.ok(listMap);
    }

    @GetMapping(value = "/{id}")
    public Service getServiceById(@PathVariable("id") Long serviceId) {
        return serviceRepository.getServicesByServiceId(serviceId);
    }

    @PostMapping (value = "/create")
    public ResponseEntity<?> createService(@RequestParam("serviceName") String serviceName,
                                                @RequestParam("category") String categoryId,
                                                @RequestParam("price") String priceStr,
                                                @RequestParam("description") String description,
                                                @RequestParam("numOfPeople") String numOfPeopleStr,
                                                @RequestParam("image") MultipartFile image) throws IOException {
        if (serviceName.isEmpty() || categoryId.isEmpty() || priceStr.isEmpty() || description.isEmpty() || numOfPeopleStr.isEmpty() || image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
        double price = Double.parseDouble(priceStr);
        int numOfPeople;
        try {
            numOfPeople = Integer.parseInt(numOfPeopleStr);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("The number of the employee have to be an integer");
        }
        Long serviceCategoryId = Long.parseLong(categoryId);

        if (price < 0) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not input negative integer in price");
        }
        if (numOfPeople < 0) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not input negative integer in number of people");
        }

        // Handel Image
        String originalFilename = image.getOriginalFilename();
        String [] splitOriginalName = originalFilename.split("\\.");
        for (String check : splitOriginalName) {
            boolean checkSpecialCharacter = CheckSpecialChar.isValidFileName(check);
            if (!checkSpecialCharacter) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not give a special character in file name");
            }
        }
        String newFilename = "";  // Specify the new file name here
        String extension = StringUtils.getFilenameExtension(originalFilename);
        newFilename = serviceName + "_" + categoryId + "." + extension;

        String Url = "https://imagescleaningservice.blob.core.windows.net/images/service/" + newFilename;
//        if (!extension.equals("png") || !extension.equals("jpg")) {
//            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Not an image");
//        }
        ServiceCategory serviceCategory = serviceCategoryRepository.findByServiceCategoryId(serviceCategoryId);
        blobContainerClient = blobServiceClient.getBlobContainerClient("images/service");
        BlobClient blob = blobContainerClient
                .getBlobClient(newFilename);
        //get file from images folder then upload to container images//
        blob.deleteIfExists();
        blob.upload(image.getInputStream(),
                image.getSize());
        Service service = Service.builder()
                .serviceName(serviceName)
                .description(description)
                .price(price)
                .numOfPeople(numOfPeople)
                .serviceCategory(serviceCategory)
                .urlImage(Url)
                .build();
        serviceRepository.save(service);
        List<Service> listService = serviceRepository.findAll();
        List<Map<String,Object>> listMap = new ArrayList<>();
        for (Service ser : listService) {
            serviceCategory = ser.getServiceCategory();
            Map<String, Object> map = new HashMap<>();
            map.put("category", serviceCategory);
            map.put("service", ser);
            listMap.add(map);
        }
        return ResponseEntity.status(HttpStatus.OK).body(listMap);
    }
    @PostMapping(value = "/{id}/update")
    public ResponseEntity updateService (@RequestParam("serviceName") String serviceName,
                                         @RequestParam("category") String categoryId,
                                         @RequestParam("price") String priceStr,
                                         @RequestParam("description") String description,
                                         @RequestParam("numOfPeople") String numOfPeopleStr,
                                         @RequestParam(name = "image", required = false) MultipartFile image,
                                         @PathVariable("id")  Long serviceId) throws IOException {
        if (serviceName.isEmpty() && priceStr.isEmpty() && numOfPeopleStr.isEmpty() && description.isEmpty() && categoryId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Not leaving any field empty");
        }
//        String serviceName = data.get("serviceName").asText();
//        String priceStr = data.get("price").asText();
//        String numOfPeopleStr = data.get("numOfPeople").asText();
//        String description = data.get("description").asText();
//        String serviceCategoryIdStr = data.get("serviceCategoryId").asText();
        ServiceCategory serviceCategory = null;
        double price = 0;
        int numOfPeople = 0;
        Service service = serviceRepository.getServicesByServiceId(serviceId);
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
        else if (numOfPeopleStr.contains(".")) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not input a double in number of people");
        }
        else if (!numOfPeopleStr.isEmpty()) {
            numOfPeople = Integer.parseInt(numOfPeopleStr);
        }
        if (description.isEmpty()) {
            description = service.getDescription();
        }
        if (categoryId.isEmpty()) {
            serviceCategory = service.getServiceCategory();
        }
        else if (!categoryId.isEmpty()) {
            serviceCategory = serviceCategoryRepository.findByServiceCategoryId(Long.parseLong(categoryId));
        }
        if (price < 0) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not input negative integer in price");
        }
        if (numOfPeople < 0) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not input negative integer in number of people");
        }

        service.setServiceCategory(serviceCategory);
        service.setServiceName(serviceName);
        service.setPrice(price);
        service.setDescription(description);
        service.setNumOfPeople(numOfPeople);
        if (image != null) {
            String originalFilename = image.getOriginalFilename();
            String [] splitOriginalName = originalFilename.split("\\.");
            for (String check : splitOriginalName) {
                boolean checkSpecialCharacter = CheckSpecialChar.isValidFileName(check);
                if (!checkSpecialCharacter) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not give a special character in file name");
                }
            }
            String newFilename = "";  // Specify the new file name here
            String extension = StringUtils.getFilenameExtension(originalFilename);
            newFilename = serviceName + "_" + categoryId + "." + extension;

            String Url = "https://imagescleaningservice.blob.core.windows.net/images/service/" + newFilename;

            blobContainerClient = blobServiceClient.getBlobContainerClient("images/service");
            BlobClient blob = blobContainerClient
                    .getBlobClient(newFilename);
            //get file from images folder then upload to container images//
            blob.deleteIfExists();
            blob.upload(image.getInputStream(),
                    image.getSize());
            service.setUrlImage(Url);
        }
        serviceRepository.save(service);
        return ResponseEntity.status(HttpStatus.OK).body("Has updated the service");
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

    @PostMapping(value = "/{id}/updateStatus")
    public ResponseEntity<String> updateStatusService (@PathVariable("id") Long serviceId,@RequestBody JsonNode data)  {
        String statusStr = data.get("status").asText();
        int status = Integer.parseInt(statusStr);
        Service service = serviceRepository.getServicesByServiceId(serviceId);
        service.setStatus(status);
        serviceRepository.save(service);
        if (status == 1) {
            return ResponseEntity.ok("Has changed the status from inactive to active");
        }
        else {
            return ResponseEntity.ok("Has changed the from active to inactive");
        }
    }



}