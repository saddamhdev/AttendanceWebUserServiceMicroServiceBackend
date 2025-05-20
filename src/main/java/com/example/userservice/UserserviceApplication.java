package com.example.userservice;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;
import com.example.userservice.model.*;

import java.util.TimeZone;

@EnableMongoAuditing

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.example.userservice.repository")
public class UserserviceApplication implements CommandLineRunner {

	@Autowired
	private RestTemplate restTemplate;

	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Dhaka"));
	}

	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Prepare the data to be sent
		Employee employeeData = new Employee(
				"12345",               // idNumber
				"John Doe",            // name
				"2025-02-18",          // joinDate
				"Developer" ,
				"nnn",
				"kkk",
				"llll",
				null,
				"kkk",
				"llll",
				" ",
				" "



		);

		// Define the URL for the POST request to the insert endpoint
		String url = "http://localhost:8080/login/insert";

		// Send the POST request with the employee data
		//String response = restTemplate.postForObject(url, employeeData, String.class);

		// Print the response from the API (success message, etc.)
		//System.out.println(response);
	}
}
