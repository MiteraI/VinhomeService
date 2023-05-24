package app.vinhomes.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WorkerStatusRepositoryTest {
    @Autowired
    private WorkerStatusRepository workerStatusRepository;
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;
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
}