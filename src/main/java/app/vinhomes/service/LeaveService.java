package app.vinhomes.service;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.worker.LeaveReportRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class LeaveService {
    @Autowired
    private LeaveReportRepository leaveReportRepository;

    @Autowired
    private WorkerStatusRepository workerStatusRepository;
    @Autowired
    private AccountRepository accountRepository;
    public LeaveReport createNewLeaveReport (Long workerId, LocalDate startDate, LocalDate endDate, String reason, String FileURl) {
        LeaveReport leaveReport = LeaveReport.builder()
                .workerStatusId(workerId)
                .startTime(startDate)
                .endTime(endDate)
                .reason(reason)
                .fileURL(FileURl)
                .build();
        return leaveReportRepository.save(leaveReport);
    }

    public Map<String, Object> leaveReportDetail (LeaveReport leaveReport) {
        Long workerId = leaveReport.getWorkerStatusId();
        WorkerStatus workerStatus = workerStatusRepository.findById(workerId).get();
        Account account = accountRepository.findById(workerStatus.getAccount().getAccountId()).get();
        Map<String, Object> leaveMap = new HashMap<>();
        leaveMap.put("leaveReport", leaveReport);
        leaveMap.put("account", account);
        return leaveMap;
    }
}
