package app.vinhomes.controller;

import app.vinhomes.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerPage {
    @Autowired
    private OrderService orderService;

    @GetMapping(value = "/order-history")
    public String customerOrder(HttpServletRequest request, Model model) {
        model.addAttribute("orderList", orderService.getCustomerOrder(request));
        return "orderhistory";
    }
}
