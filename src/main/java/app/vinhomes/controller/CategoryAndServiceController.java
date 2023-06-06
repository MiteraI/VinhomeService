package app.vinhomes.controller;

import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import app.vinhomes.repository.order.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController //Use for API
@RequestMapping(value = "/api/category")
public class CategoryAndServiceController {

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private ServiceRepository serviceRepository;
    @GetMapping()
    public List<ServiceCategory> getCategory(){
        System.out.println(serviceCategoryRepository.findAll());
        return serviceCategoryRepository.findAll();
    }

}
