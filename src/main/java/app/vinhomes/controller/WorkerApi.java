package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.service.AzureBlobAdapter;
import app.vinhomes.service.WorkerService;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/worker")
public class WorkerApi {
    @Autowired
    WorkerService workerService;

    @Autowired
    BlobContainerClient blobContainerClient;
    @Autowired
    private AzureBlobAdapter azureBlobAdapter;
    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/schedules")
    public ResponseEntity<List<Schedule>> getSchedules(HttpServletRequest request) {
        return ResponseEntity.ok(workerService.getSchedulesForSelf(
                        LocalDate.of(1, 1, 1)
                        , LocalDate.of(1, 1, 1)
                        , request
                )
        );
    }

    @PostMapping("/schedules")
    public ResponseEntity<List<Schedule>> postSchedules(@RequestBody JsonNode jsonNode, HttpServletRequest request) {
        return ResponseEntity.ok(workerService.getSchedulesForSelf(
                LocalDate.parse(jsonNode.get("startDate").asText())
                , LocalDate.parse(jsonNode.get("endDate").asText())
                , request)
        );
    }

    @PostMapping (value = "/orders/{orderId}/confirm-order")
    public ResponseEntity getListWorker (@PathVariable Long orderId, @RequestParam(name = "image", required = false)MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("have to give image to prove the confirming");
        }
        Order order = orderRepository.findByOrderId(orderId);
        Account worker = workerService.getWorkerOfOneOrderForConfirmation(orderId);
        boolean confirm = workerService.confirmOrder(worker.getAccountId(), orderId);
        if (confirm) {

            String originalFilename = image.getOriginalFilename();
            String newFilename = "";  // Specify the new file name here
            String extension = StringUtils.getFilenameExtension(originalFilename);
            newFilename = orderId + "_confirm." + extension;

            String Url = "https://imagescleaningservice.blob.core.windows.net/images/order/confirm" + newFilename;

            blobContainerClient = blobServiceClient.getBlobContainerClient("images/order/confirm");
            BlobClient blob = blobContainerClient
                    .getBlobClient(newFilename);
            //get file from images folder then upload to container images//
            blob.deleteIfExists();
            blob.upload(image.getInputStream(),
                    image.getSize());
            order.setUrlImageConfirm(Url);
            return ResponseEntity.status(HttpStatus.OK).body("Confirm order successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Confirm order not successfully");
        }
    }
}
