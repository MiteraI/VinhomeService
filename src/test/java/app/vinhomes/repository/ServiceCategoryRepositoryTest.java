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
                .paymentCategoryName("Cleaning")
                .build();
        Service service1 = Service.builder()
                .serviceName("40M2 Room")
                .numOfPeople(2)
                .price(600000)
                .serviceCategory(serviceCategory)
                .build();
        Service service2 = Service.builder()
                .serviceName("60M2 Room")
                .numOfPeople(3)
                .price(800000)
                .serviceCategory(serviceCategory)
                .build();
        serviceCategory.addService(service1);
        serviceCategory.addService(service2);
        serviceCategoryRepository.save(serviceCategory);
    }
}