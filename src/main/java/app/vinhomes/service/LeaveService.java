package app.vinhomes.service;

import app.vinhomes.common.SessionUserCaller;
import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.worker.LeaveReportRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
}
