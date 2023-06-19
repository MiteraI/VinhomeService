package app.vinhomes.controller;

import app.vinhomes.entity.order.Schedule;
import app.vinhomes.service.WorkerService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/worker")
public class WorkerApi {
    @Autowired
    WorkerService workerService;

    @GetMapping("/schedules")
    public ResponseEntity<List<Schedule>> getSchedules(HttpServletRequest request) {
        return ResponseEntity.ok(workerService.getSchedulesForSelf(
                        LocalDate.of(1, 1, 1)
                        , LocalDate.of(1, 1, 1)
                        , request
                )
        );
    }

    @PostMapping("/schedules")
    public ResponseEntity<List<Schedule>> postSchedules(@RequestBody JsonNode jsonNode, HttpServletRequest request) {
        return ResponseEntity.ok(workerService.getSchedulesForSelf(
                LocalDate.parse(jsonNode.get("startDate").asText())
                , LocalDate.parse(jsonNode.get("endDate").asText())
                , request)
        );
    }
}
