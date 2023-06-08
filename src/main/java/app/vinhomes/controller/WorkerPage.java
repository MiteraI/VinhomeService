package app.vinhomes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/worker")
public class WorkerPage {
    @GetMapping("/schedule")
    public String viewSchedule() {
        return "scheduleTable";
    }
}
