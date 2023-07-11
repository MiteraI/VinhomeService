package app.vinhomes.controller;

import app.vinhomes.common.SessionUserCaller;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.event.event_storage.SendEmailOnRefund_OnFinishOrder;
import app.vinhomes.service.LeaveService;
import app.vinhomes.service.OrderService;
import app.vinhomes.service.ScheduleService;
import app.vinhomes.service.WorkerService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/worker")
public class WorkerApi {
    @Autowired
    WorkerService workerService;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private LeaveService leaveService;

    @PostMapping("/schedules")
    public ResponseEntity<List<Schedule>> postSchedules(@RequestBody JsonNode jsonNode, HttpServletRequest request) {
        return ResponseEntity.ok(workerService.getSchedulesForSelf(
                LocalDate.parse(jsonNode.get("startDate").asText())
                , LocalDate.parse(jsonNode.get("endDate").asText())
                , request)
        );
    }

    @PostMapping (value = "/orders/{orderId}/confirm-order")
    public ResponseEntity getListWorker (@PathVariable Long orderId, @RequestParam(name = "image", required = false) MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("have to give image to prove the confirming");
        }
        Account worker = workerService.getWorkerOfOneOrderForConfirmation(orderId);
        boolean confirm = workerService.confirmOrder(worker.getAccountId(), orderId, image);
        if (confirm) {
            applicationEventPublisher.publishEvent(new SendEmailOnRefund_OnFinishOrder(
                    workerService.getCustomerOfOrder(orderId)
                    ,workerService.getTransactionOfOrder(orderId)
                    ,true));
            return ResponseEntity.status(HttpStatus.OK).body("Confirm order successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Confirm order not successfully");
        }
    }

    @GetMapping(value = "/your-account")
    public Account getWorkerAccount(HttpServletRequest request) {
        return SessionUserCaller.getSessionUser(request);
    }

    @GetMapping(value = "/getFreeWorkerAtTimeSLot/{orderId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Account> getFreeWorkerForServiceCategory(@PathVariable Long orderId){
        Order getOrder  = orderService.getOrderById(orderId);
        List<Account> getAllFreeWorkerWithinTimeslot_WorkDay = this.getFreeWorkerAtTimeSLot(orderId);
        getAllFreeWorkerWithinTimeslot_WorkDay.forEach(worker ->{
            if(worker.getWorkerStatus().getServiceCategory().getServiceCategoryId() != getOrder.getService().getServiceCategory().getServiceCategoryId()){
                getAllFreeWorkerWithinTimeslot_WorkDay.remove(worker);
            }
        });
        return getAllFreeWorkerWithinTimeslot_WorkDay;
    }
    public List<Account> getFreeWorkerAtTimeSLot(Long orderId){
        try{
            Order getOrder = orderService.getOrderById(orderId);
            LocalDate getWorkDay = getOrder.getSchedule().getWorkDay();
            Long timeSlotID = getOrder.getSchedule().getTimeSlot().getTimeSlotId();
            List<Account> getBusyWorker = getBusyWorkerAtTimeSlot_WorkDay(timeSlotID,getWorkDay);
            List<Account> getWorkerOffThatWorkDay =leaveService.getAccountContainWorkDay(getWorkDay);
            Set<Account> getUniqueAccountWorkerThatUNAVAILABLE =  new LinkedHashSet<>(getBusyWorker);
            getUniqueAccountWorkerThatUNAVAILABLE.addAll(getWorkerOffThatWorkDay);
            List<Account> finalListOfUNAVAILABLEWorker = getUniqueAccountWorkerThatUNAVAILABLE.stream().toList();
            List<Account> getAllFreeWorker = workerService.getAllWorker();
            getAllFreeWorker.removeAll(finalListOfUNAVAILABLEWorker);
            getAllFreeWorker.forEach(worker ->{
                if(worker.getWorkerStatus().getStatus() == 1){
                    getAllFreeWorker.remove(worker);
                }
            });
            System.out.println(getAllFreeWorker);
            return getAllFreeWorker;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    private List<Account> getBusyWorkerAtTimeSlot_WorkDay( Long timeSlotId, LocalDate workDay){
        try{
            ///// sort 1, select worker that is not absent on that workDay
             List<Account> getWorkersThatBusyInThatDayAndSlot = scheduleService.getWorkersAccountMatchWorkday_TimeSlot(workDay,timeSlotId).stream().toList() ;
            System.out.println(getWorkersThatBusyInThatDayAndSlot);
             return getWorkersThatBusyInThatDayAndSlot;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

}
