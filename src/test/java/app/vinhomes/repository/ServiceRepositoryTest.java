package app.vinhomes.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServiceRepositoryTest {
    @Autowired
    private ServiceRepository serviceRepository;
    @Test
    public void getAllServices() {
        System.out.println("Services info = " + serviceRepository.findAll());
    }
}