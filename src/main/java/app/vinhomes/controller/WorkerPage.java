package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.worker.CancelRequest;
import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.repository.worker.CancelRequestRepository;
import app.vinhomes.service.CancelRequestService;
import app.vinhomes.service.WorkerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    @Autowired
    private CancelRequestService cancelRequestService;
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

    @GetMapping("/cancel-request-worker")
    public String viewCancelRequest (HttpServletRequest request, Model model) {
        HttpSession sesion = request.getSession();
        Account account =(Account) sesion.getAttribute("loginedUser");
        List<CancelRequest> cancelRequests = cancelRequestService.getAllRequestByWorkerId(account.getAccountId());
        model.addAttribute("cancelList", cancelRequests);
        return "worker-cancel-request";
    }
}
