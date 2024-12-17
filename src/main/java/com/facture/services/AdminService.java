package com.facture.services;

import com.facture.entities.Admin;
import com.facture.entities.User;
import com.facture.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.ContentHandler;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public void saveAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    public Admin authenticate(String email, String password) {
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }

    public Page<Admin> findAll(Pageable pageable){

        return (Page<Admin>) adminRepository.findAll(pageable);

    }

    public Optional<Admin> findById(Long id) {

        return adminRepository.findById(id);
    }

    public void saveUser(Admin admin) {

        try {
            adminRepository.save(admin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUserById(Long id) {

        try {
            adminRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
}
