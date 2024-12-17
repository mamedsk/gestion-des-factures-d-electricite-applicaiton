package com.facture.controller;

import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.ui.Model;
import com.facture.entities.Admin;
import com.facture.entities.Bill;
import com.facture.entities.User;
import com.facture.services.AdminService;
import com.facture.services.BillService;
import com.facture.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private BillService billService;

    @GetMapping("/admin/login")
    public String showAdminLoginPage() {
        return "admin-login";  // This will resolve to src/main/resources/templates/admin/login.html
    }

    @PostMapping("admin/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {

        Admin admin = adminService.authenticate(email, password);
        if (admin != null) {
            // Store user/admin information in the session
            session.setAttribute("loggedInAdmin", admin);


                return "redirect:/admin/home";  // Redirect to admin dashboard
        } else {
            // Authentication failed so the inforamtion are not valid, redirect to login page with an error
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/admin/login";  // Redirect to the admin login page
        }

    }
    @GetMapping("admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Invalidate the session to clear all attributes
        return "redirect:/admin/login";  // Redirect to login page after logout
    }

//###########################################################################################################
    //USER ENDPOINTS
//###########################################################################################################

    @GetMapping("/admin/createUser")
    public String showCreateUserForm(HttpSession session,RedirectAttributes redirectAttributes) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        return "create-user";
    }

    @PostMapping("/admin/createUser")
    public String createUser(@ModelAttribute User user , RedirectAttributes redirectAttributes) {
        // Create new user

        Optional<User> useremail=userService.findByEmail(user.getEmail());
        User usercin=userService.findByCin(user.getCin());
        Admin adminemail = adminService.findByEmail(user.getEmail());


        if(useremail.isPresent() && usercin != null || adminemail !=null ){
            //error email already existed
            redirectAttributes.addFlashAttribute("error", "Email or user already existed .");
            return "redirect:/admin/createUser";
        }
        else {

            userService.saveUser(user);

            // Add success message
            redirectAttributes.addFlashAttribute("success", "User created successfully");
            // Redirect to the home page after creating the user
            return "redirect:/admin/createUser";
        }


    }

    @GetMapping("/admin/users")
    public String displayUsers(@RequestParam(name = "page", required = false, defaultValue = "0") int p,
                               @RequestParam(name = "size", required = false, defaultValue = "10") int s,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        // Fetch list of users and add it to the model
        List<User> pageuser = userService.findAll(PageRequest.of(p, s)).getContent();
        model.addAttribute("listuser", pageuser);
        return "userslist"; // template name
    }

    @GetMapping("/admin/edituser/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model,RedirectAttributes redirectAttributes,HttpSession session) {

        User user = userService.findById(id); // Fetch user by ID
        //the admin should be logged in to do this edit
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        if (user == null) {
            // user not found
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "edituser"; // the edit user html page
    }

    @PostMapping("/admin/editUser")
    public String updateUser(@ModelAttribute User user,@RequestParam Long id,@RequestParam String name, @RequestParam String email, RedirectAttributes redirectAttributes) {
        User useremail = userService.findUserByEmail(email);

        System.out.println("#########################"+useremail);

        Admin adminemail = adminService.findByEmail(email);
        System.out.println("#########################"+adminemail);

        //check if the new email is already exist

        if(useremail!= null || adminemail !=null ){
            //error email already existed
            redirectAttributes.addFlashAttribute("error", "Email or user already existed .");
            return "redirect:/admin/editUser";
        }
        User user1 = userService.findUserById(id);
        user1.setFullname(name);
        user1.setEmail(email);

        userService.saveUser(user1); // Save the updated user
        redirectAttributes.addFlashAttribute("success", "User updated successfully");
        return "redirect:/admin/users"; // Redirect to the user list
    }

    @GetMapping("/admin/deleteuser/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,HttpSession session) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        userService.deleteUserById(id); // Delete the user
        redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        return "redirect:/admin/users"; // Redirect to the user list
    }




//###########################################################################################################
    //ADMIN ENDPOINTS
//###########################################################################################################



    @GetMapping("/admin/createAdmin")
    public String showCreateAdminForm(RedirectAttributes redirectAttributes,HttpSession session) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        return "create-admin";
    }

    @PostMapping("/admin/createAdmin")
    public String createAdmin(@RequestParam String name, @RequestParam String email, @RequestParam String password,HttpSession session, RedirectAttributes redirectAttributes,Model model) {

        Admin admin = new Admin(name, email, password);

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no admin is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        Admin adminemail = adminService.findByEmail(email);
        Optional<User> useremail = userService.findByEmail(email);

        System.out.println("##########################"+adminemail);
        System.out.println("##########################"+useremail);

        if(adminemail != null || useremail.isPresent()){
            //error email already existed
            redirectAttributes.addFlashAttribute("error", "Email or user already existed .");
            return "redirect:/admin/createAdmin";
        }
        try {
            adminService.saveAdmin(admin);
            redirectAttributes.addFlashAttribute("success", "Admin created successfully");
            return "redirect:/admin/createAdmin";
        }catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());
            return "create-admin";  // Stay on the same page to display the error
        }


    }

    @GetMapping("/admin/admins")
    public String displayAdmins(@RequestParam(name = "page", required = false, defaultValue = "0") int p,
                               @RequestParam(name = "size", required = false, defaultValue = "10") int s,
                               Model model,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        // Fetch list of users and add it to the model
        List<Admin> pageadmin = adminService.findAll(PageRequest.of(p, s)).getContent();
        model.addAttribute("listadmin", pageadmin);
        return "adminslist"; // template name
    }

    @GetMapping("/admin/editadmin/{id}")
    public String showEditAdminForm(@PathVariable("id") Long id, Model model,RedirectAttributes redirectAttributes,HttpSession session) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        Optional<Admin> admin = adminService.findById(id); // Fetch user by ID
        if (admin.isEmpty()) {
            // user not found
            return "redirect:/admin/admins";
        }
        model.addAttribute("admin", admin);
        return "edit-admin"; // the edit user html page
    }


    @PostMapping("/admin/editAdmin")
    public String updateAdmin(@ModelAttribute Admin admin, RedirectAttributes redirectAttributes,HttpSession session) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }

        adminService.saveUser(admin); // Save the updated admin
        redirectAttributes.addFlashAttribute("success", "User updated successfully");
        return "redirect:/admin/admins"; // Redirect to the admin list
    }

    @GetMapping("/admin/deleteadmin/{id}")
    public String deleteAdmin(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,HttpSession session) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }

        adminService.deleteUserById(id); // Delete the admin
        redirectAttributes.addFlashAttribute("success", "admin deleted successfully");
        return "redirect:/admin/admins"; // Redirect to the user list
    }





    //########################################################################################
                                        //BILLS API
    //########################################################################################

    @GetMapping("/admin/createBill")
    public String showCreateBillForm(RedirectAttributes redirectAttributes ,Model model,HttpSession session) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }

        //model.addAttribute("userlist",users);
        return "create-bill";
    }

    @PostMapping("/admin/createBill")
    public String createBill(@RequestParam String dueDate,@RequestParam String cin,Model model, RedirectAttributes redirectAttributes,HttpSession session) {


        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }

        // Fetch the user from the database

        User user1 = userService.findByCin(cin);
        //Double Amount =amount;

        if (user1 == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/admin/createBill";
        }


        double UserConsumption =  billService.simulateRealTimeConsumption(user1.getId());

        double amount = billService.calculateBill(UserConsumption);



        model.addAttribute("user",user1);
        model.addAttribute("amount",amount);
        model.addAttribute("dueDate",dueDate);
        model.addAttribute("UserConsumption",UserConsumption);

//        // Create the bill
//        Bill bill = new Bill(amount, LocalDate.parse(dueDate), user1);

        // Save the bill
//        billService.saveBill(bill);
        //redirectAttributes.addFlashAttribute("success", "Bill created successfully");
        return "generatedbill";
    }



    @PostMapping("/admin/generatebill")
    public String submitBill(@RequestParam String amount,@RequestParam Double UserConsumption,@RequestParam LocalDate dueDate,@RequestParam String cin,Model model, RedirectAttributes redirectAttributes,HttpSession session){



        System.out.println("Amount: " + amount);
        System.out.println("UserConsumption: " + UserConsumption);
        System.out.println("DueDate: " + dueDate);
        System.out.println("CIN: " + cin);

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        User user = userService.findByCin(cin);
        if (user ==null){
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/admin/createBill";
        }

        double amount1  = Double.parseDouble(amount);
        Bill bill = new Bill(amount1,dueDate,user);
        bill.setUserConsumption(UserConsumption);

        billService.saveBill(bill);
        redirectAttributes.addFlashAttribute("success", "Bill created successfully ");

        return "redirect:/admin/createBill";
    }




    @GetMapping("/admin/bills")
    public String displayBills(@RequestParam(name = "page", required = false, defaultValue = "0") int p,
                                @RequestParam(name = "size", required = false, defaultValue = "10") int s,
                                Model model,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        // Fetch list of users and add it to the model
        try {
            List<Bill> pagebill = billService.findAll(PageRequest.of(p, s)).getContent();
            for (Bill bill : pagebill){
                Long Id =bill.getUser().getId();
                User userbill = userService.findUserById(Id);
                model.addAttribute("user",userbill);
                model.addAttribute("listbills", pagebill);
            }

            return "billslist"; // template name
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/admin/editbill/{id}")
    public String showEditBillForm(@PathVariable("id") Long id, Model model,RedirectAttributes redirectAttributes,HttpSession session) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }

        try {
            Bill bill = billService.findById(id); // Fetch user by ID
            if (bill==null) {
                // user not found
                return "redirect:/admin/bills";
            }
            model.addAttribute("bill", bill);
            return "edit-bill"; // the edit user html page
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/admin/editBill")
    public String updateBill(@ModelAttribute Bill bill, @RequestParam double amount, @RequestParam String dueDate, RedirectAttributes redirectAttributes,HttpSession session) {


        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }
        //System.out.println(dueDate);

        Bill bill1 = billService.findById(bill.getId());
        if (bill1==null) {
            redirectAttributes.addFlashAttribute("error", "Due date cannot be null.");
            return "redirect:/admin/editBill/{id}{id=bill.id}"; // Redirect to the same page with an error message
        }
        else {
            bill1.setAmount(amount);
            bill1.setDueDate(LocalDate.parse(dueDate));
            // Save the updated Bill
            billService.saveBill(bill1);
            //
            redirectAttributes.addFlashAttribute("success", "Bill updated successfully");
            return "redirect:/admin/bills"; // Redirect to the bills list

        }



    }
    @GetMapping("/admin/deletebill/{id}")
    public String deleteBill(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,HttpSession session) {

        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addAttribute("error", "You must be logged in to access this page.");
            return "redirect:/admin/login";
        }

        Bill bill = billService.findById(id);
        if (bill==null){
            redirectAttributes.addAttribute("error", "Bill is not found.");
            return "redicret:/admin/bills";
        }
        billService.deleteById(id);// Delete the bill with ID
        redirectAttributes.addAttribute("success", "bill deleted successfully");
        return "redirect:/admin/bills"; // Redirect to the user list
    }






}
