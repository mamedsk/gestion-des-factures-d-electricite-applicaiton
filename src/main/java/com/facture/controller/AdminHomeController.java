package com.facture.controller;

import com.facture.entities.Admin;
import com.facture.services.BillService;
import com.facture.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminHomeController {
    @Autowired
    private UserService userService;

    @Autowired
    private BillService billService;

    @GetMapping("/admin/home")
    public String home(HttpSession session,Model model , RedirectAttributes redirectAttributes) {


        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        // Fetch statistics from the service layer

        int totalUsers = userService.getTotalUsers();
        int billsPaid = billService.getTotalBillsPaid();
        double totalAmountPaid = billService.getTotalAmountPaid();

        // Add these stats to the model to be accessed in Thymeleaf
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("billsPaid", billsPaid);
        model.addAttribute("totalAmountPaid", totalAmountPaid);


        return "admin-home"; // Return the name of the home view (home.html)
    }
}
