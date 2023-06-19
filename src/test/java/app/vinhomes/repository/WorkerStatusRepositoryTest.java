//package app.vinhomes.repository;
//
//import app.vinhomes.repository.order.ServiceCategoryRepository;
//import app.vinhomes.repository.worker.WorkerStatusRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class WorkerStatusRepositoryTest {
//    @Autowired
//    private WorkerStatusRepository workerStatusRepository;
//    @Autowired
//    private ServiceCategoryRepository serviceCategoryRepository;
//    @Test
//    public void printAllWorkerStatus() {
//        System.out.println("All worker status's info = "+workerStatusRepository.findAll());
//    }
//    @Test
//    public void printAllStatusesByJob() {
//        System.out.println("All worker status's info ="+
//                workerStatusRepository.findByServiceCategoryAndStatusOrderByWorkCountAsc(
//                        serviceCategoryRepository.findById(1L).get(), 0
//                ));
//    }
//}