package app.vinhomes.service;

import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceCategoryService {
    @Autowired
    ServiceCategoryRepository serviceCategoryRepository;
    public ServiceCategory createService (String name) {
        ServiceCategory serviceCategory = ServiceCategory.builder()
                .serviceCategoryName(name)
                .build();
        serviceCategoryRepository.save(serviceCategory);
        return serviceCategory;
    }
}
