package com.example.userservice.controller;

import com.example.userservice.repository.RegistrationRepository;
import com.example.userservice.model.*;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.security.JwtFilter;
import com.example.userservice.security.JwtGenerator;
import com.example.userservice.service.EmployeeService;
import com.google.gson.Gson;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.userservice.security.JwtGenerator.secretKey;
import static java.lang.System.out;

@RestController
@RequestMapping("/api/user")

public class Registration {
    /* @Autowired
    private  AuthenticationManager authenticationManager;*/
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private RoleRepository roleRepository;

    private  UserDetailsService userDetailsService;
    boolean result=false;
    int count =0;
    private JwtGenerator jwtGenerator;
    private JwtFilter jwtFilter;
    public Registration(JwtGenerator jwtGenerator,JwtFilter jwtFilter) {
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
        Optional<Employee> data1 = registrationRepository.findByIdNumberAndStatus(requestData.get("idNumber"), "1");
        if(data1.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to Insert: use unique id Number.");
        }
        Optional<Employee> data = registrationRepository.findByEmailAndStatus(requestData.get("email"), "1");
        if(data.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to Insert: use unique email.");
        }

        if (data.isEmpty()) {
            Employee em=new Employee();
            em.setPassword(passwordEncoder.encode(requestData.get("password"))); // Hash password
            em.setType(null);
            em.setEndDate(requestData.get("endDate"));
            em.setStatus(requestData.get("status"));
            em.setPosition(requestData.get("position"));
            em.setDesignation(requestData.get("designation"));
            em.setEmail(requestData.get("email"));
            em.setJoinDate(requestData.get("joinDate"));
            em.setIdNumber(requestData.get("idNumber"));
            em.setCurrentTimee(requestData.get("currentTimee"));
            em.setName(requestData.get("name"));
            // Save the employee data to the database
            registrationRepository.save(em);
            return ResponseEntity.ok("Successfully Inserted");
        }

        // Return error response if the employee already exists
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to Insert: Employee already exists.");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateEmployee(@RequestBody Map<String, String> requestData) {

        // out.println(requestData);
        //  Optional<Employee> data = registrationRepository.findByIdNumberAndStatus(requestData.get("idNumber"), "1");
        Optional<Employee> data = registrationRepository.findById(requestData.get("rowId"));
        Employee data1=data.get();
        List<String> typeList = data1.getType();

        if (typeList != null && !typeList.isEmpty()) {
            if (typeList.contains("SNVN")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("This is not permitted to Update. Reserved account");
            }
        }


        if (data.isPresent()) {
            Employee mm=data.get();
            mm.setStatus("0");
            registrationRepository.save(mm);

            Employee em=new Employee();
            em.setPassword(passwordEncoder.encode(requestData.get("password"))); // Hash password
            em.setType(mm.getType());
            em.setEndDate(requestData.get("endDate"));
            em.setStatus(requestData.get("status"));
            em.setPosition(mm.getPosition());
            em.setDesignation(requestData.get("designation"));
            em.setEmail(requestData.get("email"));
            em.setJoinDate(requestData.get("joinDate"));
            em.setIdNumber(requestData.get("idNumber"));
            em.setCurrentTimee(requestData.get("currentTimee"));
            em.setName(requestData.get("name"));
            // Save the employee data to the database
            registrationRepository.save(em);
            return ResponseEntity.ok("Successfully Updated");
        }

        // Return error response if the employee already exists
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to Insert: Employee already exists.");
    }


    @GetMapping("/hello")
    public String hello() {
        // out.println("Hello");

        return "Hello guy from Spring Boot Good !";
    }

    // Endpoint to retrieve all employees based on status
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllEmployees(@RequestParam String status,
                                             @RequestHeader(value = "Authorization", required = true) String token, Authentication authentication) {
        //  System.out.println("Authenticated User: " + authentication);
        List<Employee> data=registrationRepository.findByStatus("1");
        data.removeIf(e-> e.getIdNumber().equals("SNVN_Developer"));// hide developer info
        //
        // readCSV("C:\\Users\\Saddam\\Downloads/userData.csv");
        return ResponseEntity.ok(sorting(data));
    }



    public  void readCSVForInsertUser(String filePath) {
        String line;
        String regex = "\"([^\"]*)\"|([^,]+)"; // Regex to capture quoted and unquoted values
        Pattern pattern = Pattern.compile(regex);
        int k=1;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // ✅ Skip the first line (header)
            while ((line = br.readLine()) != null) {
                List<String> values = new ArrayList<>();
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    if (matcher.group(1) != null) {
                        values.add(matcher.group(1)); // Quoted value
                    } else {
                        values.add(matcher.group(2)); // Unquoted value
                    }
                }

                //     System.out.println(values.size()+"  "+values); // Print as a list
                Employee ee=new Employee();
                ee.setCurrentTimee(convertUtcToDhaka(values.get(1)));
                ee.setDesignation(values.get(2));
                ee.setEndDate(values.get(3));
                ee.setIdNumber(values.get(4));
                ee.setJoinDate(values.get(5));
                ee.setName(values.get(6));
                ee.setPosition(values.get(7));
                ee.setStatus(values.get(8));
                ee.setEmail("example"+k+"@gmail.com");
                ee.setPassword(passwordEncoder.encode(k+""));

                registrationRepository.save(ee);
                k++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/delete")
    public ResponseEntity<String> deleteEmployee(@RequestBody Map<String, String> requestData) {


        try {
            String id = requestData.get("id");
            String endDate = requestData.get("endDate");

            // Check if the employee exists
            Optional<Employee> optionalEmployee = registrationRepository.findById(id);
            if (optionalEmployee.isEmpty()) {
                return ResponseEntity.badRequest().body("Employee not found.");
            }

            Employee data=optionalEmployee.get();
            if (data.getType().contains("SNVN")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("This is not permitted to Delete  . Reserved account");
            }


            Employee employee = optionalEmployee.get();

            // Check if the user is an owner
            if (mergeRolesStatus(employee.getType())) {
                if (ownerShipCount() == 1) { // Only one owner exists, cannot delete
                    return ResponseEntity.badRequest().body("Cannot delete this account. System requires at least one owner.");
                }
            }

            // Attempt to delete the employee
            boolean isDeleted = employeeService.deleteEmployee(id, endDate);
            if (isDeleted) {
                return ResponseEntity.ok("Employee deleted successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete employee.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting employee: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(HttpServletRequest request, HttpServletResponse response,
                                                     FilterChain chain, @RequestBody Map<String, String> requestData) {

        // readCSVForInsertUser("C:\\Users\\01957\\Downloads/userData.csv");

        Map<String, String> responseData = new HashMap<>();

        if (ownerShipCount() == 0) {
            responseData.put("error", "No ownership found. Default user creation required.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
        }

        String username = requestData.get("email");
        String password = requestData.get("password");

        Optional<Employee> data = registrationRepository.findByEmailAndStatus(username, "1");

        // Check if user exists
        if (data.isEmpty()) {
            responseData.put("error", "User not found or inactive.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }

        Employee employee = data.get();
        // out.println(employee.getName()+"  "+employee.getPosition()+"  "+employee.getType());
        boolean result=false;
        // Restrict SNVN developers

        List<String> typeList = employee.getType();

        if (typeList != null && !typeList.isEmpty()) {
            if (typeList.contains("SNVN")) {
                result=true;
            }
        }

        // Validate password
        if (!passwordEncoder.matches(password, employee.getPassword())) {
            responseData.put("error", "Invalid credentials.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }

        // Generate Tokens
        String token = jwtGenerator.generateToken(username, employee.getType());
        String refreshToken = jwtGenerator.generateRefreshToken(username, employee.getType());

        // Successful authentication response
        responseData.put("token", token);
        responseData.put("refreshToken", refreshToken);
        responseData.put("result", "Authenticated");

        if(typeList!=null && !typeList.isEmpty() ){
            responseData.put("Role", mergeActiveRoles(employee.getType(), result));
        }else{
            responseData.put("Role","NoRole");
            responseData.put("Name",employee.getName( ));
        }


        return ResponseEntity.ok(responseData);
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

                Optional<Employee> data = registrationRepository.findByEmailAndStatus(username, "1");
                boolean result=false;
                Employee data1=data.get();
                if (data1.getType().contains("SNVN")) {
                    result=true;
                }
                // Create response map
                Map<String, String> dataT = new HashMap<>();
                dataT.put("accessToken", newAccessToken);

                if(data.isPresent()){
                    Employee mm=data.get();
                    dataT.put("Role",mergeActiveRoles(mm.getType(),result));
                }
                else{
                    dataT.put("Role",null);
                }

                return ResponseEntity.ok(dataT);
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

    public String mergeActiveRoles(List<String> rolesName,boolean result) {
        Role combinedRole = new Role("CombinedSNVNRole");
        List<Role> roles = new ArrayList<>();

        rolesName.forEach(roleName -> {
            Optional<Role> roleData = roleRepository.findByRoleName(roleName);
            roleData.ifPresent(roles::add);
        });

        // Filter only active roles
        List<Role> activeRoles = roles.stream()
                .filter(role -> "ACTIVE".equals(role.getRoleStatus()))
                .collect(Collectors.toList());

        Map<String, Role.Menu> menuMap = new HashMap<>();

        for (Role role : activeRoles) {
            for (Role.Menu menu : role.getMenus()) {
                if (!"ACTIVE".equals(menu.getMenuStatus())) continue;

                menuMap.putIfAbsent(menu.getMenuName(), new Role.Menu(menu.getMenuName()));
                Role.Menu combinedMenu = menuMap.get(menu.getMenuName());

                // Use a map to ensure unique pages within the menu
                Map<String, Role.Menu.Page> pageMap = new HashMap<>();

                for (Role.Menu.Page page : menu.getPages()) {
                    if (!"ACTIVE".equals(page.getPageStatus())) continue;

                    pageMap.putIfAbsent(page.getPageName(), new Role.Menu.Page(page.getPageName()));
                    Role.Menu.Page combinedPage = pageMap.get(page.getPageName());

                    // Use a Set to store unique components by name
                    Map<String, Role.Menu.Page.Component> componentMap = new HashMap<>();

                    for (Role.Menu.Page.Component component : page.getComponents()) {
                        if ("ACTIVE".equals(component.getComponentStatus())) {
                            componentMap.putIfAbsent(component.getComponentName(), new Role.Menu.Page.Component(component.getComponentName()));
                        }
                    }

                    // Only add pages that have components
                    if (!componentMap.isEmpty()) {
                        combinedPage.setComponents(new ArrayList<>(componentMap.values()));
                        pageMap.put(page.getPageName(), combinedPage);
                    }
                }

                // Only add menus that have pages
                if (!pageMap.isEmpty()) {
                    combinedMenu.setPages(new ArrayList<>(pageMap.values()));
                    menuMap.put(menu.getMenuName(), combinedMenu);
                }
            }
        }

        if(!result){
            menuMap.remove("Developer");
        }
        // Convert combined menus to a list
        combinedRole.setMenus(new ArrayList<>(menuMap.values()));

        // Return JSON string
        String resultJson = new Gson().toJson(combinedRole);
        //   System.out.println(resultJson);
        return resultJson;
    }

    public boolean mergeRolesStatus(List<String> rolesName) {
        Role combinedRole = new Role("CombinedSNVNRole");
        List<Role> roles = new ArrayList<>();

        if(rolesName==null){
            return false;
        }

        rolesName.forEach(roleName -> {
            Optional<Role> roleData = roleRepository.findByRoleName(roleName);
            roleData.ifPresent(roles::add);
        });

        // Filter only active roles
        List<Role> activeRoles = roles.stream()
                .filter(role -> "ACTIVE".equals(role.getRoleStatus()))
                .collect(Collectors.toList());

        Map<String, Role.Menu> menuMap = new HashMap<>();

        for (Role role : activeRoles) {
            for (Role.Menu menu : role.getMenus()) {
                if (!"ACTIVE".equals(menu.getMenuStatus())) continue;

                menuMap.putIfAbsent(menu.getMenuName(), new Role.Menu(menu.getMenuName()));
                Role.Menu combinedMenu = menuMap.get(menu.getMenuName());

                // Use a map to ensure unique pages within the menu
                Map<String, Role.Menu.Page> pageMap = new HashMap<>();

                for (Role.Menu.Page page : menu.getPages()) {
                    if (!"ACTIVE".equals(page.getPageStatus())) continue;

                    pageMap.putIfAbsent(page.getPageName(), new Role.Menu.Page(page.getPageName()));
                    Role.Menu.Page combinedPage = pageMap.get(page.getPageName());

                    // Use a Set to store unique components by name
                    Map<String, Role.Menu.Page.Component> componentMap = new HashMap<>();

                    for (Role.Menu.Page.Component component : page.getComponents()) {
                        if ("ACTIVE".equals(component.getComponentStatus())) {
                            componentMap.putIfAbsent(component.getComponentName(), new Role.Menu.Page.Component(component.getComponentName()));
                        }
                    }

                    // Only add pages that have components
                    if (!componentMap.isEmpty()) {
                        combinedPage.setComponents(new ArrayList<>(componentMap.values()));
                        pageMap.put(page.getPageName(), combinedPage);
                    }
                }

                // Only add menus that have pages
                if (!pageMap.isEmpty()) {
                    combinedMenu.setPages(new ArrayList<>(pageMap.values()));
                    menuMap.put(menu.getMenuName(), combinedMenu);
                }
            }
        }

        // Convert combined menus to a list
        combinedRole.setMenus(new ArrayList<>(menuMap.values()));
        result=false;
        // out.println(combinedRole.getMenus());
        combinedRole.getMenus().forEach(e->{
            if(e.getMenuName().equals("Owner")){
                e.getPages().forEach(f->{
                    if(f.getPageName().equals("Permission")){
                        f.getComponents().forEach(g->{
                            // out.println(g.getComponentName());
                            if(g.getComponentName().equals("Save or Update")){

                                result=true;
                            }
                        });
                    }
                });
            }
        });


        return result;
    }

    public  int ownerShipCount(){
        count =0;
        List<Employee> data=registrationRepository.findByStatus("1");
        // out.println(data.size());
        data.forEach(e->{
            if(mergeRolesStatus(e.getType())){
                count++;
            }

        });

        return count;
    }
    public static String convertUtcToDhaka(String inputTime) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime utcDateTime = LocalDateTime.parse(inputTime, inputFormatter);

        ZonedDateTime utcZoned = utcDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime dhakaZoned = utcZoned.withZoneSameInstant(ZoneId.of("Asia/Dhaka"));

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dhakaZoned.format(outputFormatter);
    }

}
