package app.vinhomes.service;

import app.vinhomes.common.SessionUserCaller;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.type_enum.TransactionStatus;
import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.repository.order.ScheduleRepository;
import app.vinhomes.repository.order.TimeSlotRepository;
import app.vinhomes.repository.worker.LeaveReportRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkerService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LeaveReportRepository leaveReportRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    @Autowired
    private WorkerStatusRepository workerStatusRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AzureBlobAdapter azureBlobAdapter;
    @Autowired
    BlobServiceClient blobServiceClient;
    @Autowired
    BlobContainerClient blobContainerClient;
    public List<Schedule> getSchedulesForSelf(LocalDate startDate, LocalDate endDate, HttpServletRequest request) {
        Account account = SessionUserCaller.getSessionUser(request);
        if (account == null) return new ArrayList<Schedule>();
        List<Schedule> fullSchedules = scheduleRepository.findAllByWorkDayBetweenAndWorkers_AccountId(startDate
                , endDate
                , account.getAccountId());
        List<Schedule> cancelledSchedules = scheduleRepository.findAllByWorkDayBetweenAndWorkers_AccountIdAndOrder_Status(
                startDate
                , endDate
                , account.getAccountId()
                , OrderStatus.CANCEL
        );
        List<Schedule> pendingAndCompletedSchedules = new ArrayList<>();
        for (Schedule schedule : fullSchedules) {
            if (!cancelledSchedules.contains(schedule)) {
                pendingAndCompletedSchedules.add(schedule);
            }
        }
        return pendingAndCompletedSchedules;
    }

    //Ham nay de clay worker nao la cho order nao de confirm
    public Account getWorkerOfOneOrderForConfirmation (Long orderId) {
        Account account = null;
        Order order = orderRepository.findByOrderId(orderId);
        Schedule schedule = scheduleRepository.findByOrder(order);
        List<Account> workers = schedule.getWorkers();
        if (workers != null) {
            for (Account worker: workers) {
                account = worker;
                return account;
            }
        }
        return null;
    }

    public boolean confirmOrder (Long workerId, Long orderId, MultipartFile image) throws IOException {
        Account worker = accountRepository.findByAccountId(workerId);
        Order order = orderRepository.findByOrderId(orderId);
        Schedule schedule = scheduleRepository.findByOrder(order);
        LocalDate workDate = schedule.getWorkDay();
        TimeSlot timeSlot = schedule.getTimeSlot();
        List<Account> accounts = schedule.getWorkers();
        if (workDate.isEqual(LocalDate.now()) && timeSlot.getStartTime().isBefore(LocalTime.now())) {
            order.setStatus(OrderStatus.SUCCESS);
            Transaction getTransaction= transactionRepository.findById(orderId).get();
            getTransaction.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(getTransaction);
            String originalFilename = image.getOriginalFilename();
            String newFilename = "";  // Specify the new file name here
            String extension = StringUtils.getFilenameExtension(originalFilename);
            newFilename = orderId + "_confirm." + extension;

            String Url = "https://imagescleaningservice.blob.core.windows.net/images/order/confirm/" + newFilename;

            blobContainerClient = blobServiceClient.getBlobContainerClient("images/order/confirm");
            BlobClient blob = blobContainerClient
                    .getBlobClient(newFilename);
            //get file from images folder then upload to container images//
            blob.deleteIfExists();
            blob.upload(image.getInputStream(),
                    image.getSize());
            order.setUrlImageConfirm(Url);
            for (Account acc : accounts) {
                WorkerStatus workerStatus = acc.getWorkerStatus();
                int workCount = workerStatus.getWorkCount() + 1;
                workerStatus.setWorkCount(workCount);
                workerStatusRepository.save(workerStatus);
            }
            orderRepository.save(order);
            return true;
        }
        else {
            return false;
        }
    }
    public List<LeaveReport> getAllLeaveReportForSelf(HttpServletRequest request) {
        Account account = SessionUserCaller.getSessionUser(request);
        return leaveReportRepository.findByWorker_AccountId(account.getAccountId());
    }
    public Account getCustomerOfOrder(Long orderId) {
        return orderRepository.findByOrderId(orderId).getAccount();
    }
    public Transaction getTransactionOfOrder(Long orderId) {
        return transactionRepository.findById(orderId).get();
    }
    public List<Account> getAllWorker(){
        return accountRepository.findByRole(1);
    }
}
