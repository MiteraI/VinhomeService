package app.vinhomes.controller;

import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.service.WorkerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class WorkerPage {
    @Autowired
    private WorkerService workerService;
    @GetMapping("/your-schedule")
    public String viewSchedule() {
        return "worker-homepage";
    }
    @GetMapping("/leave-register")
    public String viewOrderToConfirm (HttpServletRequest request, Model model) {
        List<LeaveReport> leaveReportList = workerService.getAllLeaveReportForSelf(request);
        model.addAttribute("leaveList", leaveReportList);
        return "worker-leave-page";
    }

}
