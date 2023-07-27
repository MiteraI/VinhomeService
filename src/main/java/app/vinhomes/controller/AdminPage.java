package app.vinhomes.controller;

import app.vinhomes.repository.order.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminPage {
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;
    @RequestMapping(value = "/adminDisplayWorker_page", method = RequestMethod.GET)
    public String adminDisplayWorker(Model model) {
        model.addAttribute("category", serviceCategoryRepository.findAll());
        return "adminDisplayWorker";
    }

    @RequestMapping(value = "/adminDisplayCustomer_page", method = RequestMethod.GET)
    public String adminDisplayCustomer() {
        return "adminDisplayCustomer";
    }

    @RequestMapping(value = "/admin_UpdateWorker/{workerAccountId}", method = RequestMethod.GET)
    public String adminUpdateWorker(@PathVariable String workerAccountId) {
        return "adminUpdateWorker";//?accountId="+workerAccountId//redirect:/
    }

    @RequestMapping(value = "/admin_UpdateCustomer/{customerId}", method = RequestMethod.GET)
    public String adminUpdateWorker() {
        return "adminUpdateCustomer";
    }

    @RequestMapping(value = "/admin_OrderTransaction/{vnpTxnRef}", method = RequestMethod.GET)
    public String adminOrderTransaction() {
        System.out.println("inside orderTransactionAdmin");
        return "adminOrderTransaction";
    }
    @RequestMapping(value = "/admin_OrderTransaction", method = RequestMethod.GET)
    public String adminOrder() {
        return adminOrderTransaction();
    }
    @RequestMapping(value = "/see-leave-report", method = RequestMethod.GET)
    public String seeAllLeaveReport() {
        return "adminSeeLeaveReport";
    }

    @RequestMapping(value = "/see-all-order-by-admin", method = RequestMethod.GET)
    public String seeAllLeaveOrderByAdmin() {
        return "adminSeeAllOrder";
    }

}
