package com.bank.services;

import com.bank.domain.user.User;
import com.bank.dtos.NotificationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {

    private RestTemplate restTemplate;

    // send notification
    public void sendNotification(User user, String message) throws Exception {

        // email user
        String email = user.getEmail();

        // parameters for notification
        NotificationDTO notificationRequest = new NotificationDTO(email, message);

        // url for notification
        ResponseEntity<String> notificationResponse = restTemplate.postForEntity("https://run.mocky.io/v3/54dc2cf1-3add-45b5-b5a9-6bf7e7f1f4a6", notificationRequest, String.class);

        // if verification get status code is not ok
        if (!(notificationResponse.getStatusCode() == HttpStatus.OK)) {
            // throw exception notification
            throw  new Exception("Service not found notification " + notificationRequest);
        }

        System.out.println("Notification send");
    }

}
