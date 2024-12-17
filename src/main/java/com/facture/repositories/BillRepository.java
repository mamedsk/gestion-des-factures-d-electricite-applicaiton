package com.facture.repositories;

import com.facture.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {


    // Find unpaid bills by user id
    List<Bill> findByUserIdAndPaidFalse(Long userId);

    // Find paid bills by user id
    List<Bill> findByUserIdAndPaidTrue(Long userId);

    @Query("select count(b) from Bill b where b.paid is true ")
    int getTotalBillsPaid();



    @Query("SELECT SUM(b.amount) FROM Bill b WHERE b.paid = true")
    Double getTotalAmountPaid();
}
