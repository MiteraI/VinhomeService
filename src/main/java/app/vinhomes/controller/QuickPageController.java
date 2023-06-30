package app.vinhomes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QuickPageController {
    @GetMapping("orderform")
    public String form() {
        return "order-form";
    }
}
