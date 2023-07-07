package app.vinhomes.repository.worker;

import app.vinhomes.entity.worker.LeaveReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveReportRepository extends JpaRepository<LeaveReport, Long> {

    LeaveReport findByLeaveReportId (Long id);
//    List<LeaveReport> findByWorkerStatusId(Long id);
//    List<LeaveReport> findByWorkerStatusIdAndStatus (Long id, int status);
    List<LeaveReport> findByWorker_AccountId(Long accountId);
    List<LeaveReport> findByStatus(int status);


//    @Query(value = "select t1.leave_report_id, t1.worker_status_id, t2.first_name, t2.last_name, t1.start_time, t1.end_time, t1.reason\n" +
//            "from tbl_leave_report t1, tbl_account t2\n" +
//            "where t1.worker_status_id = t2.account_id and t1.status = ?1", nativeQuery = true)
//    HashMap<LeaveReport, Account> findLeaveReportDetail (int status);

}
