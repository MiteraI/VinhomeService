package app.vinhomes.service;

import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.repository.worker.LeaveReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LeaveService {
    @Autowired
    private LeaveReportRepository leaveReportRepository;
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
}
