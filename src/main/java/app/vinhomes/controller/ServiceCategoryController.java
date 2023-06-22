package app.vinhomes.controller;

import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping (value = "/api")
public class ServiceCategoryController {
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @PostMapping (value = "create-service-category")
    public ResponseEntity createServiceCategory (@RequestBody JsonNode data) {
        String newCategory = data.get("serviceCategory").asText();
        if (newCategory.isEmpty() || newCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not create a new category");
        }

        else {
            ServiceCategory serviceCategory = ServiceCategory.builder()
                    .serviceCategoryName(newCategory)
                    .build();
            serviceCategoryRepository.save(serviceCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body("Has created a new category");
        }
    }

    @PostMapping(value = "update-category")
    public ResponseEntity updateCategory (@RequestBody JsonNode data) {
        String updateCategory = data.get("serviceCategory").asText();
        Long categoryId = Long.parseLong(data.get("serviceCategoryId").asText());
        if (updateCategory.isEmpty() || updateCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not update a new category");
        }

        else {
            ServiceCategory updateCate = serviceCategoryRepository.findByServiceCategoryId(categoryId);
            updateCate.setServiceCategoryName(updateCategory);
            serviceCategoryRepository.save(updateCate);
            return ResponseEntity.status(HttpStatus.CREATED).body("Has update a new category");
        }
    }
}
