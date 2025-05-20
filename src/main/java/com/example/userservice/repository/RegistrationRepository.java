package com.example.userservice.repository;


import com.example.userservice.model.Employee;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends MongoRepository<Employee, String> {

    // Custom query to find employees by status
    List<Employee> findByStatus(String status);
    List<Employee> findByIdNumber(String idNumber);
    Optional<Employee> findByEmailAndStatus(String idNumber, String status);
    Optional<Employee> findByNameAndStatus(String name,String status);
    Optional<Employee> findByIdNumberAndStatus(String idNumber, String status);
    Optional<?> findByEmailAndPasswordAndStatus(String email ,String password, String status);
   // Optional<Employee> findByStatus(String status);
}
