package com.facture.services;

import com.facture.entities.User;
import com.facture.repositories.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private userRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }


    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);

    }

    public Page<User> findAll(Pageable pageable){
        return (Page<User>) userRepository.findAll(pageable);

    }

    public List<User> findAll(){
        return userRepository.findAll();
    }
    public User findById(Long id) {
        return userRepository.findUserById(id);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);

    }

    public User findByCin(String cin) {
        return userRepository.findByCin(cin);
    }


    public int getTotalUsers() {
        return userRepository.getTotalUsers();
    }


    public Optional<User> findByEmail(String email) {
        return  userRepository.findByEmail(email);
    }

    public User findUserByEmail(String email) {
        return  userRepository.findUserByEmail(email);
    }
}
