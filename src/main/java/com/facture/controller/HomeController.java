package com.facture.controller;

import com.facture.entities.Bill;
import com.facture.entities.User;
import com.facture.services.BillService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {
    @Autowired
    private BillService billService;
    @GetMapping("/home")
    public String home(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if user is logged in (session contains 'loggedInUser')
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/login";
        }

        // If logged in, add necessary model attributes for Thymeleaf
        model.addAttribute("unpaidBills", billService.getUnpaidBills(loggedInUser.getId())); // Example method to fetch unpaid bills
        model.addAttribute("paidBills", billService.getPaidBills(loggedInUser.getId()));     // Example method to fetch paid bills
        return "home";  // Thymeleaf template name for the home page
    }
    @GetMapping("/contactus")
    public String contactUs(){
        return "contact";
    }

    @GetMapping("/about")
    public String aboutUs(){
        return "aboutus";
    }

    @GetMapping("/bill/details/{id}")
    public String viewBillDetails(@PathVariable("id") Long id, Model model,HttpSession session, RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/login";
        }
            // Fetch bill details by ID from the database
            Bill bill = billService.findById(id);

            // Add the bill object to the model to pass to the view
            model.addAttribute("bill", bill);

            // Return the Thymeleaf template name
            return "billdetails";
        }


}
