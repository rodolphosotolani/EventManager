package br.com.rts.eventmanager.bff;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginWebController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/aguarde-vinculo")
    public String aguardeVinculo(Model model) {
        model.addAttribute("pageTitle", "Aguardando Vínculo");
        return "seguranca/aguarde-vinculo";
    }
}
