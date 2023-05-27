package app.vinhomes.controller;

import app.vinhomes.entity.ServiceCategory;
import app.vinhomes.entity.order.Service;
import app.vinhomes.repository.ServiceCategoryRepository;
import app.vinhomes.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //Use for API
@RequestMapping(value = "/category")
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
