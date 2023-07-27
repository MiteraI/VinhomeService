package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.type_enum.CancelRequestStatus;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.worker.CancelRequest;
import app.vinhomes.event.event_storage.SendEmailCancelOrder;
import app.vinhomes.repository.worker.CancelRequestRepository;
import app.vinhomes.service.AccountService;
import app.vinhomes.service.CancelRequestService;
import app.vinhomes.service.OrderService;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/cancel-request")
public class CancelRequestAPI {
    @Autowired
    private CancelRequestService cancelRequestService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionAPI transactionAPI;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    BlobContainerClient blobContainerClient;


    @GetMapping(value = "/getAllPendingRequest", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CancelRequest> getAllPendingRequest(){
        return cancelRequestService.getAllRequest_Status(CancelRequestStatus.PENDING);
    }
    @GetMapping(value = "/getAllRequest", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CancelRequest> getAllRequest(){
        return cancelRequestService.getAllRequest();
    }
    @GetMapping(value = "/getAllRequest/{workerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CancelRequest> getAllRequestWorker(@PathVariable long workerId){
        return cancelRequestService.getAllRequestByWorkerId(workerId);
    }
    @PostMapping(value = "/create-request")
    public ResponseEntity<String> createCancelRequestInfo(@RequestParam("orderId") String orderId,
                                                       @RequestParam("workerId") String workerId,
                                                       @RequestParam("reason") String reason,
                                                       @RequestParam(name = "image", required = false) MultipartFile file )
    {
        System.out.println("inside create cancel request");
        if(orderId == null || workerId == null || reason == null || file == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All information must be filled");
        }
        if(orderId.isEmpty() || workerId.isEmpty()|| reason.isEmpty() || file.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All information must be filled");
        }
        try{
            long parsedOrderId = Long.parseLong(orderId);
            long parsedWorkerId = Long.parseLong(workerId);
            Order getOrder = orderService.getOrderById(parsedOrderId);
            Account getWorker = accountService.getAccountById(parsedWorkerId);
            if(getOrder == null || getWorker == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("input fields are invalid, cannot find related information to it");
            }
            if(cancelRequestService.checkIfValidForCreate(getWorker,getOrder) == false){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("a cancel request for this order has been made or has been confirmed by manager and only be canceled before 1 day");
            }
            //todo: create cancle requets, at filename and upload file
            String uploadFileName = returnFileName_SaveToAzure(file,parsedWorkerId,parsedOrderId);
            CancelRequest createNew = CancelRequest.builder()
                    .worker(getWorker)
                    .order(getOrder)
                    .timeCancel(LocalDateTime.now())
                    .reason(reason)
                    .fileURL(uploadFileName)
                    .status(CancelRequestStatus.PENDING)
                    .build();

            cancelRequestService.createCancelRequest(createNew);
            System.out.println("pass cancel request");
            return ResponseEntity.status(HttpStatus.OK).body("");
        }catch (NumberFormatException e){
            System.out.println("error inside cancelrequestAPI: "+ e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("number format exception");
        }
    }
    @PostMapping(value = "/worker-update")
    public ResponseEntity<String> workerUpdateCancelRequest(@RequestParam("cancelRequestId") long cancelRequestId){
        CancelRequest getCancelRequest = cancelRequestService.getCancelRequest(cancelRequestId);
        if(getCancelRequest == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not found request cancel");
        }
        cancelRequestService.updateCancelRequest(getCancelRequest,true,true);
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @PostMapping(value = "/admin-update")
    public ResponseEntity<String> adminUpdateCancelRequest(@RequestBody JsonNode data, HttpServletRequest request, HttpServletResponse response){
        String getCancelRequestId= data.get("cancelRequestId").asText().trim();
        String getStatus = data.get("status").asText().trim();
        try{
            long parsedCancelRequest = Long.parseLong(getCancelRequestId);
            CancelRequest getCancelRequest = cancelRequestService.getCancelRequest(parsedCancelRequest);
            if(getCancelRequest == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot find the related request, try again ");
            }
            CancelRequestStatus parsedCancelStatus = parseStatusString(getStatus);
            if(parsedCancelStatus == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("status does not match any of status enum");
            }
            if(parsedCancelStatus.equals(CancelRequestStatus.ACCEPT)){//accept
                //todo refund order cho khách nếu accept
                System.out.println("yes admin accept cancel request");
                updateCancelRequest_OrderStatus(getCancelRequest,true,request,response);
            }else if(parsedCancelStatus.equals(CancelRequestStatus.REJECT)){//reject
                System.out.println("yes admin reject cancel request");
                updateCancelRequest_OrderStatus(getCancelRequest,false,request,response);
            }else if(parsedCancelStatus.equals(CancelRequestStatus.CANCEL)){
                System.out.println("the request has been cancel, order is still continue by worker");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("the request has been cancelled by worker");
            }else{
                System.out.println("PENDING??????");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR ???");
            }
            eventPublisher.publishEvent(new SendEmailCancelOrder(getCancelRequest));
            return ResponseEntity.status(HttpStatus.OK).body("success");
        }catch (NumberFormatException e){
            System.out.println("erorr inside update cancel request : "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("cannot parsed number id ");
        }
    }
    private void updateCancelRequest_OrderStatus(CancelRequest cancelRequest,boolean isAcceptOrNot,HttpServletRequest request, HttpServletResponse response){
        cancelRequestService.updateCancelRequest(cancelRequest,isAcceptOrNot,false);
        if(isAcceptOrNot){
            transactionAPI.cancelOrderAdmin(cancelRequest.getOrder().getOrderId().toString(),request,response);
        }
        //if not then still pending
    }
    private CancelRequestStatus parseStatusString(String status){
        if(status == null||status.isEmpty()){
            return null;
        }
        if(status.trim().toUpperCase().equals(CancelRequestStatus.CANCEL.name().trim())){
            return CancelRequestStatus.CANCEL;
        }
        if(status.trim().toUpperCase().equals(CancelRequestStatus.ACCEPT.name().trim())){
            return CancelRequestStatus.ACCEPT;
        }
        if(status.trim().toUpperCase().equals(CancelRequestStatus.REJECT.name().trim())){
            return CancelRequestStatus.REJECT;
        }
        if(status.trim().toUpperCase().equals(CancelRequestStatus.PENDING.name().trim())){
            return CancelRequestStatus.PENDING;
        }
        return null;
    }
    private String returnFileName_SaveToAzure(MultipartFile file, long workerId,long orderId){
        String originalFilename = file.getOriginalFilename();
        String newFilename = "";  // Specify the new file name here
        String extension = StringUtils.getFilenameExtension(originalFilename);
        newFilename = "worker" + workerId + "_order" + orderId +"_time"+ LocalDateTime.now()+"end."+extension;
        String Url = "https://imagescleaningservice.blob.core.windows.net/images/cancelRequest/" + newFilename;
        blobContainerClient = blobServiceClient.getBlobContainerClient("images/cancelRequest");
        BlobClient blob = blobContainerClient.getBlobClient(newFilename);

        //get file from images folder then upload to container images//
        blob.deleteIfExists();
        try {
            blob.upload(file.getInputStream(),file.getSize());
        } catch (IOException e) {
            System.out.println("error inside uploading image to azure, cancel request: "+e.getMessage());
            throw new RuntimeException(e);
        }
        return Url;
    }
}
