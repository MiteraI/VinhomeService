package app.vinhomes.service;

import app.vinhomes.common.SessionUserCaller;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.worker.LeaveReportRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaveService {
    @Autowired
    private LeaveReportRepository leaveReportRepository;
    @Autowired
    private AccountRepository accountRepository;
    public LeaveReport createNewLeaveReport (Long workerId, LocalDate startDate, LocalDate endDate, String reason, String FileURl, HttpServletRequest request) {
        LeaveReport leaveReport = LeaveReport.builder()
                .worker(accountRepository.findByAccountId(SessionUserCaller.getSessionUser(request).getAccountId()))
                .startTime(startDate)
                .endTime(endDate)
                .reason(reason)
                .fileURL(FileURl)
                .build();
        return leaveReportRepository.save(leaveReport);
    }

    public Map<String, Object> leaveReportDetail (LeaveReport leaveReport) {
        Account account = leaveReport.getWorker();
        Map<String, Object> leaveMap = new HashMap<>();
        leaveMap.put("leaveReport", leaveReport);
        leaveMap.put("account", account);
        return leaveMap;
    }
    public List<Account> getAccountContainWorkDay(LocalDate workday){
        List<LeaveReport> getLeaveReport = leaveReportRepository.findAll();
        List<Account> getAccountContainWorkday = new ArrayList<>();
        getLeaveReport.forEach(leaveReport -> {
            if(workday.isAfter(leaveReport.getStartTime()) && workday.isBefore(leaveReport.getEndTime())){
                getAccountContainWorkday.add(leaveReport.getWorker());
            }
        });
        return getAccountContainWorkday;
    }
}
