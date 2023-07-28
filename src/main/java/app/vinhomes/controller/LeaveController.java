package app.vinhomes.controller;


import app.vinhomes.common.CheckSpecialChar;
import app.vinhomes.common.SessionUserCaller;
import app.vinhomes.entity.Account;

import app.vinhomes.entity.worker.Leave;

import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.worker.LeaveReportRepository;
import app.vinhomes.repository.worker.LeaveRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import app.vinhomes.service.AzureBlobAdapter;
import app.vinhomes.service.LeaveService;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class LeaveController {

    @Autowired
    private WorkerStatusRepository workerStatusRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private LeaveReportRepository leaveReportRepository;
    @Autowired
    private AzureBlobAdapter azureBlobAdapter;
    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    BlobContainerClient blobContainerClient;
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private SseController sseController;
    @PostMapping(value = "/leave-report/create")
    public ResponseEntity<?> LeaveReport (@RequestParam("startDate") String startDateStr,
                                          @RequestParam("endDate") String endDateStr,
                                          @RequestParam("reason") String reason, HttpServletRequest request,
                                          @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        Account account = SessionUserCaller.getSessionUser(request);
        Long accountId = account.getAccountId();
        WorkerStatus workerStatus = workerStatusRepository.findByAccount(account);
        int leaveDaysLimit = workerStatus.getAllowedDayOff();
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        // Lay ra cac ngay da dc accept nghi cua 1 worker de ko xin phep nghi trung
        List<Leave> listLeave = leaveRepository.findByAccount_AccountId(workerStatus.getWorkerStatusId());
        for (Leave leave : listLeave) {
            if (startDate.isEqual(leave.getLeaveDay()) || endDate.isEqual(leave.getLeaveDay())) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("The date you want to off has been accepted");
            }
        }

        // Ghi nhan ngay va lis do khong duoc trong
        if (startDateStr.isEmpty() || endDateStr.isEmpty() || reason.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Don't leave the fields empty except the file");
        }

        LocalDate dateNow = LocalDate.now();
        Long daysOff = ChronoUnit.DAYS.between(startDate, endDate);
        Long days = ChronoUnit.DAYS.between(dateNow, startDate);

        // Check ngay nghi co dung ko
        if (daysOff < 0) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Can not choose the startDate after endDate");
        }
        Integer daysOffInt = daysOff.intValue();
        // Rule cho viec xin gnhi truoc 10 ngay
        if (days < 10) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("You have to leave report for days off before 10 days");
        }

        //Check con du ngay nghi phep k
        if (leaveDaysLimit < daysOffInt) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("your limit leave-off do not cover all days for your requiring for off");
        }

        // TH nghi phep khong can nop file lien quan
        if (file == null || file.getOriginalFilename().isEmpty()) {
            if (workerStatus != null && days >= 10) {
                if (leaveDaysLimit > daysOffInt) {
                    LeaveReport leaveReport = leaveService.createNewLeaveReport(workerStatus.getWorkerStatusId(), startDate, endDate, reason, null, request);
                    Map<String, Object> leaveReportDetail = leaveService.leaveReportDetail(leaveReport);
                    sseController.sendSSEEvent("leaveReport", leaveReportDetail);
                    sseController.sendSSEEvent2("leaveReportCount");
                    return ResponseEntity.status(HttpStatus.CREATED).body("Has created a leave report ");
                }
            }
        }

        //Co nop file
        if (file != null || !file.getOriginalFilename().isEmpty()) {

            if (days >= 10 && leaveDaysLimit > daysOffInt) {
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
                newFilename = startDateStr + "_" + workerStatus.getWorkerStatusId() + "." + extension;
                String Url = "https://imagescleaningservice.blob.core.windows.net/images/leave/" + newFilename;
                LeaveReport leaveReport = leaveService.createNewLeaveReport(workerStatus.getWorkerStatusId(), startDate, endDate, reason,Url, request );

                blobContainerClient = blobServiceClient.getBlobContainerClient("images/leave");
                BlobClient blob = blobContainerClient
                        .getBlobClient(newFilename);
                //get file from images folder then upload to container images//
                blob.deleteIfExists();
                blob.upload(file.getInputStream(),
                        file.getSize());
                Map<String, Object> leaveReportDetail = leaveService.leaveReportDetail(leaveReport);
                sseController.sendSSEEvent("leaveReport", leaveReportDetail);
                sseController.sendSSEEvent2("leaveReportCount");
                return ResponseEntity.status(HttpStatus.CREATED).body("Has created a leave report with file");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Some fault");
    }

    // Lay ra cac leave report chua dc duyet
    @GetMapping (value = "/leave-report")
    public ResponseEntity<List<Map<String, Object>>> leaveReportsDidNotRead() {
        List<Map<String, Object>> listLeave = new ArrayList<>();
        List<LeaveReport> reportList = leaveReportRepository.findByStatus(0);
        for (LeaveReport leaveRe : reportList) {
            Account account = leaveRe.getWorker();
            Map<String, Object> leaveMap = new HashMap<>();
            leaveMap.put("leaveReport", leaveRe);
            leaveMap.put("account", account);
            listLeave.add(leaveMap);
        }
        return ResponseEntity.ok(listLeave);
    }


    @PostMapping (value = "/leave-report/update")
    public ResponseEntity updateLeaveReport (@RequestBody JsonNode data) {
        Integer status = Integer.parseInt(data.get("status").asText());
        Long leaveReportId = Long.parseLong(data.get("leaveReportId").asText());
        LeaveReport leaveReport = leaveReportRepository.findByLeaveReportId(leaveReportId);
        Account account = leaveReport.getWorker();
        if (status == 1) {
            LocalDate startDate = leaveReport.getStartTime();
            LocalDate endDate = leaveReport.getEndTime();
            Long daysLeave = ChronoUnit.DAYS.between(startDate, endDate) + 1L; // cong them ngay cuoi
            leaveReport.setStatus(1);
            leaveReportRepository.save(leaveReport);
            System.out.println(daysLeave);
            for (int i = 0; i < daysLeave; i++) {
                LocalDate addDate = startDate.plusDays(i);
                Leave leave = Leave.builder()
                        .leaveDay(addDate)
                        .account(account)
                        .build();
                leaveRepository.save(leave);
            }
            WorkerStatus workerStatus = workerStatusRepository.findByAccount(account);
            int allowDaysOff = (int) (workerStatus.getAllowedDayOff() - daysLeave);
            workerStatus.setAllowedDayOff(allowDaysOff);
            workerStatusRepository.save(workerStatus);
            return ResponseEntity.status(HttpStatus.OK).body("Approving the leave report");
        }
        else {
            leaveReport.setStatus(2);
            leaveReportRepository.save(leaveReport);
            return ResponseEntity.status(HttpStatus.OK).body("rejecting the leave report");
        }
    }

    @PostMapping(value = "/leave-report/cancel")
    public ResponseEntity cancelReport (@RequestParam("leaveReportId") Long leaveReportId) {
        LeaveReport leaveReport = leaveReportRepository.findByLeaveReportId(leaveReportId);
        leaveReport.setStatus(4);
        leaveReportRepository.save(leaveReport);
        return ResponseEntity.ok("Cancel report");
    }
//    @GetMapping (value = "/leave-report/{id}")
//    public List<LeaveReport> seeAllLeaveReports (@PathVariable("id") Long workerId, HttpServletRequest request, @RequestParam String status ) {
//        // cai RequestParam nay t dinh de xai combobox thi xem dc cai report process accept hay la reject
//        HttpSession session = request.getSession();
//        Account account = (Account) session.getAttribute("loginedUser");
//        if (status.isEmpty() || status == null || status.equals("ALL")) {
//            List<LeaveReport> leaveReports = leaveReportRepository.findByWorkerStatusId(account.getAccountId());
//            return leaveReports;
//        }
//        if (status.equals("Reject")) {
//            return leaveReportRepository.findByWorkerStatusIdAndStatus(account.getAccountId(), 2);
//        }
//        if (status.equals("Approve")) {
//            return leaveReportRepository.findByWorkerStatusIdAndStatus(account.getAccountId(), 1);
//        }
//        return null;
//    }


}
