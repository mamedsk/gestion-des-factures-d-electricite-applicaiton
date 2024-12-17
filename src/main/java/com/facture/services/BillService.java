package com.facture.services;

import com.facture.entities.Bill;
import com.facture.entities.User;
import com.facture.repositories.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BillService {

    @Autowired
    private  BillRepository billRepository;

    @Autowired
    private UserService userService;

    public void saveBill(Bill bill) {
        billRepository.save(bill);
    }

    public Page<Bill> findAll(Pageable pageable){

        return (Page<Bill>) billRepository.findAll(pageable);

    }



    public Bill findById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + id));
    }




    public List<Bill> getUnpaidBills(Long userId) {
        return billRepository.findByUserIdAndPaidFalse(userId);
    }


    public  List<Bill> getPaidBills(Long userId) {
        return billRepository.findByUserIdAndPaidTrue(userId);
    }

    public int getTotalBillsPaid() {
        return billRepository.getTotalBillsPaid();
    }

    public double getTotalAmountPaid() {
        Double totalAmountPaid = billRepository.getTotalAmountPaid();
        return (totalAmountPaid != null) ? totalAmountPaid : 0.0;
    }


    public void deleteById(Long id) {
        billRepository.deleteById(id);
    }

    private final Random random = new Random();

    // Simulate hourly consumption
    public double simulateRealTimeConsumption(Long userId) {
        User user = userService.findUserById(userId);
        double consumption = 0.5 + (100.0 - 10.5) * random.nextDouble();  // Random between 10 and 100
        user.setUserConsumption(consumption);
        return consumption;
    }


    public double getConsumption(Long userId) {
        User user = userService.findUserById(userId);
        return user.getUserConsumption();
    }

    private static final double RATE_PER_KWH = 1.5;  // Electricity rate per kWh
    public double calculateBill(double consumption) {
        return consumption * RATE_PER_KWH;
    }

}
