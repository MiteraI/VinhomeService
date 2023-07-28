package app.vinhomes.controller;

import app.vinhomes.common.CheckSpecialChar;
import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import app.vinhomes.service.AzureBlobAdapter;
import app.vinhomes.service.ServiceCategoryService;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping (value = "/api")
public class ServiceCategoryController {
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;
    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    BlobContainerClient blobContainerClient;
    @Autowired
    ServiceCategoryService serviceCategoryService;
    @GetMapping(value = "/categories")
    public ResponseEntity<List<ServiceCategory>> getAllCategories () {
        return ResponseEntity.ok(serviceCategoryRepository.findAll());
    }


    @PostMapping (value = "/create-service-category")
    public ResponseEntity createServiceCategory (@RequestParam("categoryName") String categoryName, @RequestParam("categoryDescription") String description, @RequestParam(name = "file", required = false)MultipartFile file) throws IOException {
        if (categoryName.isEmpty() || categoryName == null || file.isEmpty() || file == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not create a new category");
        }

        else {
            ServiceCategory serviceCategory = serviceCategoryService.createService(categoryName, description);
            Long categoryId = serviceCategory.getServiceCategoryId();
            String originalFilename = file.getOriginalFilename();
            String [] splitOriginalName = originalFilename.split("\\.");
            for (String check : splitOriginalName) {
                boolean checkSpecialCharacter = CheckSpecialChar.isValidFileName(check);
                if (!checkSpecialCharacter) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not give a special character in file name");
                }
            }
            String newFilename = "";  // Specify the new file name here
            String extension = StringUtils.getFilenameExtension(originalFilename);
            newFilename = categoryId + "." + extension;
            String Url = "https://imagescleaningservice.blob.core.windows.net/images/category/" + newFilename;
            blobContainerClient = blobServiceClient.getBlobContainerClient("images/category");
            BlobClient blob = blobContainerClient
                    .getBlobClient(newFilename);
            //get file from images folder then upload to container images//
            blob.deleteIfExists();
            blob.upload(file.getInputStream(),
                    file.getSize());
            serviceCategory.setUrlImage(Url);
            serviceCategoryRepository.save(serviceCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body("Has created a new category");
        }
    }

    @PostMapping(value = "/update-category/{id}")
    public ResponseEntity updateCategory (@PathVariable("id") Long categoryId, @RequestParam("categoryName") String categoryName, @RequestParam("categoryDescription") String description, @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
//        String updateCategory = data.get("serviceCategory").asText();
//        Long categoryId = Long.parseLong(data.get("serviceCategoryId").asText());
        String categoryNameStr = "";
        String categoryDescriptionStr = description.equals("") ? "" : description;
        if (categoryName.isEmpty()) {
            categoryNameStr = serviceCategoryRepository.findByServiceCategoryId(categoryId).getServiceCategoryName();
        }
        if (categoryName.isEmpty() && file == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not update a new category");
        }
        if (file == null && !categoryName.isEmpty()) {
            ServiceCategory updateCate = serviceCategoryRepository.findByServiceCategoryId(categoryId);
            updateCate.setServiceCategoryName(categoryName);
            updateCate.setDescription(categoryDescriptionStr);
            serviceCategoryRepository.save(updateCate);
            return ResponseEntity.status(HttpStatus.CREATED).body("Update a new category");
        }
        if (file != null && categoryName.isEmpty()) {
            ServiceCategory updateCate = serviceCategoryRepository.findByServiceCategoryId(categoryId);
            String originalFilename = file.getOriginalFilename();
            String newFilename = "";  // Specify the new file name here
            String extension = StringUtils.getFilenameExtension(originalFilename);
            newFilename = categoryId + "." + extension;
            String Url = "https://imagescleaningservice.blob.core.windows.net/images/category/" + newFilename;
            blobContainerClient = blobServiceClient.getBlobContainerClient("images/category");
            BlobClient blob = blobContainerClient
                    .getBlobClient(newFilename);
            //get file from images folder then upload to container images//
            blob.deleteIfExists();
            blob.upload(file.getInputStream(),
                    file.getSize());
            updateCate.setUrlImage(Url);

            serviceCategoryRepository.save(updateCate);
            return ResponseEntity.status(HttpStatus.CREATED).body("Has update a new category");
        }
        else {
            ServiceCategory updateCate = serviceCategoryRepository.findByServiceCategoryId(categoryId);
            updateCate.setServiceCategoryName(categoryName);
            updateCate.setDescription(categoryDescriptionStr);
            String originalFilename = file.getOriginalFilename();
            String [] splitOriginalName = originalFilename.split("\\.");
            for (String check : splitOriginalName) {
                boolean checkSpecialCharacter = CheckSpecialChar.isValidFileName(check);
                if (!checkSpecialCharacter) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not give a special character in file name");
                }
            }
            String newFilename = "";  // Specify the new file name here
            String extension = StringUtils.getFilenameExtension(originalFilename);
            newFilename = categoryId + "." + extension;
            String Url = "https://imagescleaningservice.blob.core.windows.net/images/category/" + newFilename;
            blobContainerClient = blobServiceClient.getBlobContainerClient("images/category");
            BlobClient blob = blobContainerClient
                    .getBlobClient(newFilename);
            //get file from images folder then upload to container images//
            blob.deleteIfExists();
            blob.upload(file.getInputStream(),
                    file.getSize());
            updateCate.setUrlImage(Url);

            serviceCategoryRepository.save(updateCate);
            return ResponseEntity.status(HttpStatus.CREATED).body("Has update a new category");
        }
    }
}
