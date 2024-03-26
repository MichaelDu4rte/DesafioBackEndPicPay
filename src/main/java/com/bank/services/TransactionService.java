package com.bank.services;



import com.bank.domain.transaction.Transaction;
import com.bank.domain.user.User;
import com.bank.dtos.TransactionDTO;
import com.bank.repositories.ITransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private ITransactionRepository transactionRepository;

    private RestTemplate restTemplate;

    private NotificationService notificationService;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {

        // get transaction sender and receiver by TransactionDTO
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        // validate balance and type user
        userService.validateTransaction(sender, transaction.amount());

        // check if the user is authorized
        Boolean isAutherized = this.authenticateTransaction(sender, transaction.amount());
        if (!isAutherized) {
            throw new Exception(("transaction not autorized: "));
        }

        // send the transaction
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.amount());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        // updating transaction values
        sender.setBalance(sender.getBalance().subtract(transaction.amount()));
        receiver.setBalance(receiver.getBalance().add(transaction.amount()));

        // save and update the transaction
        this.transactionRepository.save(newTransaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);

        // notify the user that the transaction, received
        this.notificationService.sendNotification(sender,"Transaction successfully updated");
        this.notificationService.sendNotification(receiver,"Transaction received successfully");

        return newTransaction;
    }

    //authenticateTransaction validates the transaction and returns
    public Boolean authenticateTransaction(User sender, BigDecimal amount) {
        String authenticationUrl = "https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc";
        ResponseEntity<Map> authenticateResponse = restTemplate.getForEntity(authenticationUrl, Map.class);

       if (authenticateResponse.getStatusCode() == HttpStatus.OK) {

           String message = (String) authenticateResponse.getBody().get("message");
           return "Autorizado".equalsIgnoreCase(message);
       } else return false;
    }
}
