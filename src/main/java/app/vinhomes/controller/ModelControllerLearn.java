package app.vinhomes.controller;

import app.vinhomes.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/template")
public class ModelControllerLearn {
    @Autowired private AccountRepository accountRepository;

    @GetMapping
    public String viewHomePage(Model model) {
        model.addAttribute("accountList", accountRepository.findAll());
        return "homepage";
    }
}
