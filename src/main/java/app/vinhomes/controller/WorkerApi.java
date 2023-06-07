package app.vinhomes.controller;

import app.vinhomes.entity.order.Schedule;
import app.vinhomes.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/worker")
public class WorkerApi {
    @Autowired
    WorkerService workerService;
    @GetMapping("/schedules")
    public ResponseEntity<List<Schedule>> getSchedules() {
        return workerService.getSchedulesForSelf()
    }
}
