package com.bank.services;

import com.bank.domain.user.User;
import com.bank.domain.user.UserType;
import com.bank.dtos.UserDTO;
import com.bank.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {

        // validate type user
        if (!(sender.getUserType() == UserType.COMMON)) {
            throw new Exception("Usuario "+ sender.getUserType() + " nao esta autorizado a realizar transferencias");
        }

        // validade amount
        if(sender.getBalance().compareTo(amount) <= 0) {
            throw new Exception("Saldo insuficiente");
        }

    }

    // user find by id
    public User findUserById(Long id) throws Exception {
        return this.userRepository.findUserById(id).orElseThrow(() -> new Exception("User not found"));
    }

    // user save database
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    // create user
    public User createUser(UserDTO userDTO) {
        User newUser = new User(userDTO);
        this.saveUser(newUser);

        return newUser;
    }

    // retorn user all listeners

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }
}
