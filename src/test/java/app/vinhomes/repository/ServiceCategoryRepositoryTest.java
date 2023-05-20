package app.vinhomes.repository;

import app.vinhomes.entity.ServiceCategory;
import app.vinhomes.entity.order.Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServiceCategoryRepositoryTest {
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;
   // @Test
    public void addServiceCateAlongServices() {
        ServiceCategory serviceCategory = ServiceCategory.builder()
                .paymentCategoryName("Electrictian")
                .build();
        Service service1 = Service.builder()
                .serviceName("Fridge")
                .numOfPeople(1)
                .price(600000)
                .serviceCategory(serviceCategory)
                .build();
        Service service2 = Service.builder()
                .serviceName("Air Conditioner")
                .numOfPeople(1)
                .price(800000)
                .serviceCategory(serviceCategory)
                .build();
        serviceCategory.addService(service1);
        serviceCategory.addService(service2);
        serviceCategoryRepository.save(serviceCategory);
    }

    @Test
    public void printServiceBasedOnCategory() {
        System.out.println("Service info = "+ serviceCategoryRepository.findById(1L).get().getServices().get(1));
    }
}