package app.vinhomes.controller;

import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping (value = "/api")
public class ServiceCategoryController {
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @GetMapping(value = "/categories")
    public ResponseEntity<List<ServiceCategory>> getAllCategories () {
        return ResponseEntity.ok(serviceCategoryRepository.findAll());
    }


    @PostMapping (value = "/create-service-category")
    public ResponseEntity createServiceCategory (@RequestParam("categoryName") String categoryName) {
        if (categoryName.isEmpty() || categoryName == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not create a new category");
        }

        else {
            ServiceCategory serviceCategory = ServiceCategory.builder()
                    .serviceCategoryName(categoryName)
                    .build();
            serviceCategoryRepository.save(serviceCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body("Has created a new category");
        }
    }

    @PostMapping(value = "/update-category/{id}")
    public ResponseEntity updateCategory (@PathVariable("id") Long categoryId, @RequestParam("categoryName") String categoryName) {
//        String updateCategory = data.get("serviceCategory").asText();
//        Long categoryId = Long.parseLong(data.get("serviceCategoryId").asText());
        if (categoryName.isEmpty() || categoryName == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not update a new category");
        }

        else {
            ServiceCategory updateCate = serviceCategoryRepository.findByServiceCategoryId(categoryId);
            updateCate.setServiceCategoryName(categoryName);
            serviceCategoryRepository.save(updateCate);
            return ResponseEntity.status(HttpStatus.CREATED).body("Has update a new category");
        }
    }
}
