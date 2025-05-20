package com.example.userservice.service;

import com.example.userservice.model.Employee;
import com.example.userservice.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    RegistrationRepository registrationRepository;

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
}
