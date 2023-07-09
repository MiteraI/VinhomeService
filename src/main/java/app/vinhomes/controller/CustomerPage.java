package app.vinhomes.controller;

import app.vinhomes.entity.Transaction;
import app.vinhomes.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;

@Controller
public class CustomerPage {
    @Autowired
    private OrderService orderService;

    @GetMapping(value = "/order-history")
    public String customerOrder(HttpServletRequest request, Model model) {
        model.addAttribute("transactionList", orderService.getCustomerTransactions(request));
        return "order-history";
    }

    @GetMapping(value = "/order-history/{orderId}")
    public String customerOrderDetails(@PathVariable Long orderId, HttpServletRequest request, Model model) {
        model.addAttribute("order", orderService.getCustomerOrderDetails(request, orderId));
        return "order-details";
    }
}
