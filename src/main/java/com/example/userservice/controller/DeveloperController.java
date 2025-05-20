package com.example.userservice.controller;

import com.example.userservice.model.Developer;
import com.example.userservice.model.Employee;
import com.example.userservice.repository.DeveloperRepository;
import com.example.userservice.repository.RegistrationRepository;
import com.example.userservice.security.JwtFilter;
import com.example.userservice.security.JwtGenerator;
import com.example.userservice.service.EmployeeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.example.userservice.security.JwtGenerator.secretKey;
import static java.lang.System.out;

@RestController
@RequestMapping("/api/developer")

public class DeveloperController {
    /* @Autowired
    private  AuthenticationManager authenticationManager;*/
    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private DeveloperRepository developerRepository;

    private  UserDetailsService userDetailsService;

    private JwtGenerator jwtGenerator;
    private JwtFilter jwtFilter;
    public DeveloperController(JwtGenerator jwtGenerator, JwtFilter jwtFilter) {
        this.jwtGenerator = jwtGenerator;
        this.jwtFilter=jwtFilter;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/insert")
    public ResponseEntity<String> insertEmployee(@RequestBody Map<String, String> requestData) {
        // out.println(requestData);

        developerRepository.save(new Developer(requestData.get("menuName")));

        return ResponseEntity.ok("Successfully Inserted");
    }
    @PostMapping("/insertPage")
    public ResponseEntity<String> insertEmployeeMenus(@RequestBody Map<String, String> requestData) {


        Optional<Developer> optionalDeveloper = developerRepository.findByMenuName(requestData.get("menuName"));

        if (optionalDeveloper.isPresent()) {
            Developer developer = optionalDeveloper.get();


            // Initialize dashboard if null
            if (developer.getPages() == null) {
                developer.setPages(new ArrayList<>());
            }

            // Add new Navigation menu
            developer.getPages().add(new Developer.Page(requestData.get("pageName")));



            // Save updated developer object
            developerRepository.save(developer);
            //  System.out.println(developer);

            return ResponseEntity.ok("Successfully Inserted");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Developer not found");
    }

    @PostMapping("/insertComponent")

    public ResponseEntity<String> insertEmployeeComponent(@RequestBody Map<String, String> requestData) {
        //  System.out.println(requestData);

        Optional<Developer> optionalDeveloper = developerRepository.findByMenuName(requestData.get("menuName"));

        if (optionalDeveloper.isPresent()) {
            Developer developer = optionalDeveloper.get();
            List<Developer.Page> pages = developer.getPages();

            boolean pageExists = false;

            for (Developer.Page page : pages) {
                if (page.getPageName().equals(requestData.get("pageName"))) {
                    pageExists = true;
                    // Add component if provided
                    if (requestData.containsKey("componentName")) {
                        if (page.getComponents() == null) {
                            page.setComponents(new ArrayList<>());
                        }
                        page.getComponents().add(new Developer.Page.Component(requestData.get("componentName")));
                    }
                    break;
                }
            }


            // Save updated developer object
            developerRepository.save(developer);
            //   System.out.println(developer);

            return ResponseEntity.ok("Successfully Inserted");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Developer not found");
    }


    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }

    // Endpoint to retrieve all employees based on status
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllEmployees(@RequestParam String status,
                                             @RequestHeader(value = "Authorization", required = true) String token, Authentication authentication) {
        //  System.out.println("Authenticated User: " + authentication);
        return ResponseEntity.ok((developerRepository.findAll()));
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteEmployee(@RequestBody Map<String, String> requestData) {
        try {
            String  id = requestData.get("id");
            String endDate = requestData.get("endDate");

            boolean isDeleted = employeeService.deleteEmployee(id, endDate);
            if (isDeleted) {
                return ResponseEntity.ok("Employee deleted successfully.");
            } else {
                return ResponseEntity.badRequest().body("Employee not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting employee: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public  Map<String, String> login(HttpServletRequest request, HttpServletResponse response,
                                      FilterChain chain,@RequestBody Map<String, String> requestData) {

        // out.println(response);
        String username = requestData.get("email");
        String password = requestData.get("password");
        Optional<Employee> data = registrationRepository.findByEmailAndStatus(username, "1");

        if (data.isPresent()) {
            Employee employee = data.get();

            // Compare raw password with the stored encoded password
            if (passwordEncoder.matches(password, employee.getPassword())) {

                String token = jwtGenerator.generateToken(username,employee.getType());
                // System.out.println("Generated Token: " + token);

                String refreshToken = jwtGenerator.generateRefreshToken(username,employee.getType());
                // out.println("refreshToken "+refreshToken);
                // store token in database

                // Create response map
                Map<String, String> dataT = new HashMap<>();
                dataT.put("token", token);
                dataT.put("refreshToken",refreshToken);
                dataT.put("result", "Authenticated");



                return dataT;
            }
        }

        // Create response map
        Map<String, String> dataT = new HashMap<>();
        dataT.put("token", null);
        dataT.put("result", "Not Authenticated");

        return dataT;


       /* String email = requestData.get("email").trim();
        String password = requestData.get("password").trim(); // Raw password

        Optional<Employee> data = registrationRepository.findByEmailAndStatus(email, "1");

        if (data.isPresent()) {
            Employee employee = data.get();

            // Compare raw password with the stored encoded password
            if (passwordEncoder.matches(password, employee.getPassword())) {
                return ResponseEntity.ok("Authenticated");
            }
        }

        return ResponseEntity.ok("Not Authenticated");*/
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        // check with time
        if( refreshToken==null || JwtGenerator.isTokenValid(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.getDecoder().decode(secretKey ))
                    .parseClaimsJws(refreshToken)
                    .getBody();

            // Ensure it's a refresh token
            if ("refresh".equals(claims.get("type"))) {
                String username = claims.getSubject();  // Extract username
                List<String> role = (List<String>) claims.get("role");  // Fix role extraction

                // Generate a new access token using the same role
                String newAccessToken = jwtGenerator.generateToken(username, role);

                return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }


    @GetMapping("/token")
    public ResponseEntity<String> getToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Get existing session
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No session found");
        }

        String token = (String) session.getAttribute("authToken");
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token not found in session");
        }

        return ResponseEntity.ok(token);
    }


    @PutMapping("/updatePosition")
    public ResponseEntity<?> updatePosition(
            @RequestParam String idNumber,
            @RequestParam String name,
            @RequestParam int status,
            @RequestParam int newPosition) {
        // Decoding URL-encoded parameters
        try {
            idNumber = URLDecoder.decode(idNumber, StandardCharsets.UTF_8.name());
            name = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error decoding parameters");
        }

        // Debugging logs
      /*  System.out.println("Received: idNumber=" + idNumber + ", name=" + name +
                ", status=" + status + ", newPosition=" + newPosition);*/

        boolean updated=false;
        Optional<Employee> data=registrationRepository.findByIdNumberAndStatus(idNumber,"1");

        if(data.isPresent()){
            Employee gog=data.get();
            gog.setPosition(Integer.toString(newPosition));
            registrationRepository.save(gog);
            updated=true;
        }
        else{
            // out.println("Not Found");
        }

        if (updated) {
            return ResponseEntity.ok("Employee position updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update position");
        }
    }

    public static List<Employee> sorting(List<Employee> data)
    {
        List<Integer> Prepositions=new ArrayList();
        data.forEach(e->{
            Prepositions.add(Integer.parseInt(e.getPosition()));
        });

        // sorting position
        Collections.sort(Prepositions);
        List<String> positions=new ArrayList();
        Prepositions.forEach(e->{
            positions.add(Integer.toString(e));
            // out.println(e);
        });

        List<Employee> returndata= new ArrayList<>();

        positions.forEach(e->{
            data.forEach(f->{
                if(e.equals(f.getPosition()))
                {
                    returndata.add(f);
                }
            });
        });
        returndata.forEach(e->{
            //out.println(e.getName()+"  "+e.getPosition());
        });
        // out.println(Arrays.toString(returndata.toArray()));
        return returndata;
    }

}
