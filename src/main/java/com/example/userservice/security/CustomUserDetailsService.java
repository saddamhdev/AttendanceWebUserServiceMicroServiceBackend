package com.example.userservice.security;

import com.example.userservice.model.Employee;
import com.example.userservice.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private RegistrationRepository registrationRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> data = registrationRepository.findByEmailAndStatus(username, "1");

        if (data.isPresent()) {
            Employee gg = data.get();

            // Ensure you retrieve roles dynamically
            // For example, if roles are stored as a list in Employee:
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if(gg.getType()!=null){
                gg.getType().forEach(e->{
                    authorities.add(new SimpleGrantedAuthority(e));
                });
            }

            authorities.add(new SimpleGrantedAuthority("SNVN"));

            return new User(username, gg.getPassword(), authorities);  // Returning user with roles
        }
        throw new UsernameNotFoundException("User not found");
    }

}

