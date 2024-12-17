package com.facture.controller;

import com.facture.entities.Admin;
import com.facture.entities.User;
import com.facture.services.AdminService;
import com.facture.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    UserService userService;

    @Autowired
    AdminService adminService;


    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            // Redirect to login if no user is logged in
            return "redirect:/login";
        }

        // Add user info to the model
        model.addAttribute("user", loggedInUser);
        return "profile"; // Return the name of the profile view
    }

    @GetMapping("/profile/update")
    public String ShowUpdateProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            // Redirect to login if no user is logged in
            return "redirect:/login";
        }

        // Add user info to the model
        model.addAttribute("user", loggedInUser);
        return "editProfile"; // Return the name of the profile html page
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // Update user details
        loggedInUser.setFullname(updatedUser.getFullname());
        loggedInUser.setEmail(updatedUser.getEmail());
        loggedInUser.setNumber(updatedUser.getNumber());
        loggedInUser.setCin(updatedUser.getCin());
        loggedInUser.setAddress(updatedUser.getAddress());
        loggedInUser.setDateNaissance(updatedUser.getDateNaissance());
        // attributs for the model
        loggedInUser.setNumRooms(updatedUser.getNumRooms());
        loggedInUser.setNumPeople(updatedUser.getNumPeople());
        loggedInUser.setHouseArea(updatedUser.getHouseArea());
        loggedInUser.setHasAC(updatedUser.isHasAC());
        loggedInUser.setHasTV(updatedUser.isHasTV());
        loggedInUser.setAveMonthlyIncome(updatedUser.getAveMonthlyIncome());
        loggedInUser.setFlat(updatedUser.isFlat());
        loggedInUser.setNumChildren(updatedUser.getNumChildren());
        loggedInUser.setUrban(updatedUser.isUrban());

        userService.saveUser(loggedInUser);
        // Save updated user to the session or database (if applicable)
        session.setAttribute("loggedInUser", loggedInUser);



        return "redirect:/profile"; // Redirect to the profile page after update
    }

    //##########################################################################
    //ADMIN PROFILE APIQs
    //##########################################################################

    @GetMapping("/admin/profile")
    public String viewAdminProfile(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin == null) {
            // Redirect to login if no user is logged in
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");

            return "redirect:/admin/login";
        }

        // Add user info to the model
        model.addAttribute("admin", loggedInAdmin);
        return "adminprofile"; // Return the name of the profile view
    }

    @GetMapping("/admin/profile/update")
    public String ShowUpdateAdminProfile(HttpSession session, Model model,RedirectAttributes redirectAttributes) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin == null) {
            // Redirect to login if no user is logged in
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");

            return "redirect:/admin/login";
        }

        // Add user info to the model
        model.addAttribute("admin", loggedInAdmin);
        return "editadminprofile"; // Return the name of the profile html page
    }

    @PostMapping("/admin/profile/update")
    public String updateAdminProfile(@ModelAttribute User updatedUser, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin == null) {
            return "redirect:/admin/login";
        }

        // Update user details
        loggedInAdmin.setFullname(updatedUser.getFullname());
        loggedInAdmin.setEmail(updatedUser.getEmail());
        loggedInAdmin.setNumber(updatedUser.getNumber());
        loggedInAdmin.setAddress(updatedUser.getAddress());
        loggedInAdmin.setDateNaissance(updatedUser.getDateNaissance());


        adminService.saveAdmin(loggedInAdmin);
        // Save updated user to the session or database (if applicable)
        session.setAttribute("loggedInUser", loggedInAdmin);



        return "redirect:/admin/profile"; // Redirect to the profile page after update
    }

}
