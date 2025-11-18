package com.example.userservice.service;

import com.example.userservice.model.Employee;
import com.example.userservice.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    RegistrationRepository registrationRepository;
    @Value("${user.service.url}")
    private String userServiceUrl;
    public boolean deleteEmployee(String id, String endDate) {

        Optional<Employee> data=registrationRepository.findById(id);
        if(data.isPresent()){
            Employee em=data.get();
            em.setStatus("2");
            em.setEndDate(endDate);
            registrationRepository.save(em);
            return true;
        }
        else{
         //   System.out.println("Not found");
            return false;
        }

    }
    public void updateAttendance(String header, String oldEmployeeId, String newEmployeeId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", header);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String urlWithParams = userServiceUrl + "?oldEmployeeId=" + oldEmployeeId + "&newEmployeeId=" + newEmployeeId;

        // ✔ Expect plain string instead of JSON
        ResponseEntity<String> response =
                restTemplate.exchange(urlWithParams, HttpMethod.POST, entity, String.class);

        System.out.println("UpdateAttendance API Response = " + response.getBody());
    }

}
