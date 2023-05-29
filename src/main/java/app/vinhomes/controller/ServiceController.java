package app.vinhomes.controller;

import app.vinhomes.entity.order.Service;
import app.vinhomes.repository.ServiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Service> getAllServices () {
        return serviceRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Service getServiceById (@PathVariable("id") Long serviceId, Model model) {
        return serviceRepository.getServicesByServiceId(serviceId);
    }
}
