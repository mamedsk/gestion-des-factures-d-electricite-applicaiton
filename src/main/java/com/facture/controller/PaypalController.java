package com.facture.controller;

import com.facture.entities.Bill;
import com.facture.entities.User;
import com.facture.services.BillService;
import com.facture.services.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaypalController {


    private final PaypalService paypalService;

    @Autowired
    private BillService billService;

    @GetMapping("/paymentpage")
    public String paymentPage(@RequestParam("id") Long id,
                              @RequestParam("amount") Double amount,
                              @RequestParam("dueDate") LocalDate dueDate,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes){

        // Check if user is logged in (session contains 'loggedInUser')
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/login";
        }

        model.addAttribute("id", id);
        model.addAttribute("amount", amount);
        model.addAttribute("dueDate", dueDate);

        Bill bill = billService.findById(id);

        session.setAttribute("bill",bill);
        return "paymentpage";
    }

    @PostMapping("payment/create")
    public RedirectView createPayment(HttpSession session,RedirectAttributes redirectAttributes){


        // Check if user is logged in (session contains 'loggedInUser')
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");

            return new RedirectView("/login");
        }

        Bill bill = (Bill) session.getAttribute("bill");
        try{

            String cancelUrl  = "http://localhost:8080/payment/cancel";
            String successUrl = "http://localhost:8080/payment/success";
            Payment payment = paypalService.createPayment(
                    bill.getAmount(),"USD","paypal","sale","facture payment",cancelUrl,successUrl
            );
            for(Links links : payment.getLinks()){
                if (links.getRel().equals("approval_url")){
                    return new RedirectView(links.getHref());
                }
            }
        }catch (PayPalRESTException e){

            log.error("Error occurred :: ", e);
        }
        return new RedirectView("/payment/error");
    }

    @GetMapping("payment/success")
    public String paymentSuccess (@RequestParam ("paymentId")  String paymentId,@RequestParam("PayerID") String PayerID,HttpSession session,RedirectAttributes redirectAttributes){



        // Check if user is logged in (session contains 'loggedInUser')
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/login";
        }

        try{

            Payment payment = paypalService.executePayment(paymentId,PayerID);

            if(payment.getState().equals("approved")){

                Bill bill = (Bill) session.getAttribute("bill");
                //change the bill to paid bill
                bill.setPaid(true);
                bill.setPaymentdate(LocalDate.now());

                //save the new change
                billService.saveBill(bill);

                session.removeAttribute("bill");
                return "paymentsuccess";
            }
        }catch (PayPalRESTException e){

            log.error("Error occured :: ", e);
        }
        return "paymentsuccess";

    }

    @GetMapping("/payment/cancel")
    public String paymentCancel(RedirectAttributes redirectAttributes,HttpSession session){

        // Check if user is logged in (session contains 'loggedInUser')
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/login";
        }

        return "paymentcancel";
    }


    @GetMapping("/payment/error")
    public String paymentError(RedirectAttributes redirectAttributes,HttpSession session){
        // Check if user is logged in (session contains 'loggedInUser')
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            // If no user is logged in, redirect to the login page
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/login";
        }

        return "paymenterror";
    }

}
