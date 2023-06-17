package app.vinhomes.repository;

import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WorkerStatusRepositoryTest {
    @Autowired
    private WorkerStatusRepository workerStatusRepository;
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Test
    public void printAllWorkerStatus() {
        System.out.println("All worker status's info = "+workerStatusRepository.findAll());
    }
    @Test
    public void printAllStatusesByJob() {
        System.out.println("All worker status's info ="+
                workerStatusRepository.findByServiceCategoryAndStatusOrderByWorkCountAsc(
                        serviceCategoryRepository.findById(1L).get(), 0
                ));
    }

    @Test
    public void createWorker () {
        WorkerStatus workerStatus = WorkerStatus.builder()
                .account(accountRepository.findByAccountId(3L))
                .status(0)
                .allowedDayOff(10)
                .workCount(0)
                .serviceCategory(serviceCategoryRepository.findByServiceCategoryId(1L))
                .build();
        workerStatusRepository.save(workerStatus);
    }
}