package app.vinhomes.service;

import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import app.vinhomes.repository.order.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import app.vinhomes.entity.order.Service;

@org.springframework.stereotype.Service
public class ServiceTypeService {
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    public Service getServiceType(Long serviceId) {
        return serviceRepository.findById(serviceId).get();
    }
    public ServiceCategory getServiceCateByServiceId(Long serviceId) {
        return serviceRepository.findById(serviceId).get().getServiceCategory();
    }
    public Service getServiceByServiceId(Long serviceId){
        return serviceRepository.findById(serviceId).get();
    }
}
