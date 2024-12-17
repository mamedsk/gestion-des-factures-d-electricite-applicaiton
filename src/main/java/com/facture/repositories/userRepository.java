package com.facture.repositories;

import com.facture.entities.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface userRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);


    User findByCin(String cin);

    User findUserById(Long Id);
    User findUserByEmail(String email);


    @Query("SELECT COUNT(u) FROM User u")
    int getTotalUsers();

}
