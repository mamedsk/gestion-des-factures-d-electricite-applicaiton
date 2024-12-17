package com.facture.controller;

import com.facture.entities.User;
import com.facture.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;


    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        // check the user infromation
        User user = authService.authenticate(email, password);

        if (user != null) {
            // Login is successful, store user information in the session
            session.setAttribute("loggedInUser", user);


            return "redirect:/home";
        } else {
            // Login failed, add error message and redirect to login page
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "index";  // Redirect to the login page
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Invalidate the session to clear all attributes
        return "redirect:/login";  // Redirect to login page after logout
    }
}
