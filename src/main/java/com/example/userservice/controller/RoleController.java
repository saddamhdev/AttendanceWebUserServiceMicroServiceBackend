package com.example.userservice.controller;

import com.example.userservice.model.Developer;
import com.example.userservice.model.Employee;
import com.example.userservice.model.Role;
import com.example.userservice.repository.DeveloperRepository;
import com.example.userservice.repository.RegistrationRepository;
import com.example.userservice.repository.RoleRepository;
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


import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.out;

@RestController
@RequestMapping("/api/role")

public class RoleController {
    /* @Autowired
    private  AuthenticationManager authenticationManager;*/
 

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private RoleRepository roleRepository;

    private  UserDetailsService userDetailsService;

    private JwtGenerator jwtGenerator;
    private JwtFilter jwtFilter;
    public RoleController(JwtGenerator jwtGenerator, JwtFilter jwtFilter) {
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
       // System.out.println("Received Data: " + requestData); // Debugging

        // Check if roleName already exists
        Optional<Role> existingRole = roleRepository.findByRoleName(requestData.get("roleName"));
        if (existingRole.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Role already exists!");
        }

        // Restrict specific roleName
        if ("SNVN".equals(requestData.get("roleName"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("SNVN is not permitted to add. Reserved Role");
        }

        // Save new role
        Role newRole = new Role();
        newRole.setRoleName(requestData.get("roleName"));
        roleRepository.save(newRole);

        return ResponseEntity.ok("Successfully Inserted");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteRole(@RequestBody Map<String, String> requestData) {
        String roleName = requestData.get("id");


        // Restrict specific roleName
        if ("SNVN".equals(requestData.get("id"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("SNVN is not permitted to Delete. Reserved Role");
        }

        // Check if the role is assigned to any user
        List<Employee> employees = registrationRepository.findByStatus("1");

        for (Employee employee : employees) {
            if(employee.getType() != null){
                if (employee.getType().contains(roleName)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("❌ Sorry, this role is assigned to a user. Remove it before deleting.");
                }
            }

        }

        // Role is not assigned, proceed with deletion
        Optional<Role> data = roleRepository.findByRoleName(roleName);
        if (data.isPresent()) {
            roleRepository.delete(data.get());
            return ResponseEntity.ok("✅ Role deleted successfully.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("⚠️ Role not found.");
    }
    @PostMapping("/update")
    public ResponseEntity<String> updateRole(@RequestBody Map<String, String> requestData) {
        String oldRoleName = requestData.get("oldRoleName").trim();  // Trim any extra spaces
        String newRoleName = requestData.get("newRoleName").trim();

     //   System.out.println("Old Role: [" + oldRoleName + "], New Role: [" + newRoleName + "]"); // Debugging

        // Check if the role to be updated is "SNVN" and prevent modification
        if ("SNVN".equalsIgnoreCase(oldRoleName)) {
         //   System.out.println("Attempt to update reserved role SNVN");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("SNVN is not permitted to update. Reserved Role");
        }

        // Proceed with the update if the role is not "SNVN"
        Optional<Role> existingRole = roleRepository.findByRoleName(oldRoleName);
        if (existingRole.isPresent()) {
            Role role = existingRole.get();
            role.setRoleName(newRoleName);
            roleRepository.save(role);

            // Update role in employee list
            List<Employee> employees = registrationRepository.findByStatus("1");

            for (Employee employee : employees) {
                if(employee.getType() !=null){
                    List<String> updatedRoles = new ArrayList<>();
                    employee.getType().forEach(f -> {
                        updatedRoles.add(f.equalsIgnoreCase(oldRoleName) ? newRoleName : f);
                    });
                    employee.setType(updatedRoles);
                    registrationRepository.save(employee);
                }

            }

            return ResponseEntity.ok("Role successfully updated");
        }

        // If role does not exist, return a not found response
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found.");
    }


    @PostMapping("/deletePermission")
    public ResponseEntity<String> deletePermission(@RequestBody Map<String, String> requestData) {
        if ("SNVN".equals(requestData.get("roleName"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("SNVN is not permitted to Delete  . Reserved Role");
        }

        String id = requestData.get("id");
        String roleName = requestData.get("roleName");
       // out.println(id+" "+roleName);
        //  also employee list
        // Check if the role is assigned to any user
        List<Employee> employees = registrationRepository.findByStatus("1");
        Optional<Employee> data=registrationRepository.findById(id);
        if(data.isPresent()){
            Employee  mm=data.get();
            List<String> list=new ArrayList<>();
            mm.getType().forEach(e->{
                if(! e.equals(roleName)){
                    list.add(e);
                }
            });
            mm.setType(list);
            registrationRepository.save(mm);
            return ResponseEntity.ok("Role successfully updated.");
        }



        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found.");
    }

    @PostMapping("/insertPage")
    public ResponseEntity<String> insertEmployeeMenus(@RequestBody Map<String, String> requestData) {
       // System.out.println(requestData);

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
           // System.out.println(developer);

            return ResponseEntity.ok("Successfully Inserted");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Developer not found");
    }

    // Endpoint to retrieve all employees based on status
    @GetMapping("/getAll")
    public ResponseEntity<List<Developer>> getAll(@RequestParam String status,
                                             @RequestHeader(value = "Authorization", required = true) String token, Authentication authentication) {
         // System.out.println("Authenticated User: " + authentication);
        return ResponseEntity.ok((developerRepository.findAll()));
    }

    // Endpoint to retrieve all employees based on status
    @GetMapping("/getAllRole")
    public ResponseEntity<List<Role>> getAllRole(@RequestParam String status,
                                                  @RequestHeader(value = "Authorization", required = true) String token, Authentication authentication) {
     //   System.out.println("Authenticated User: " + authentication);
        return ResponseEntity.ok((roleRepository.findAll()));
    }

    @PostMapping("/permission1")
    public ResponseEntity<String> permission(@RequestBody Map<String, String> requestData) {
       // out.println(requestData);

        //roleRepository.save(new Role(requestData.get("roleName")));

        return ResponseEntity.ok("Successfully Inserted");
    }
    @PostMapping("/permission")
    public ResponseEntity<String> savePermissions(@RequestBody Map<String, String> permissions) {
        // Print permissions to the console for debugging

        // Restrict specific roleName
        if ("SNVN".equals(permissions.get("roleName"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("SNVN is not permitted to change Assign . Reserved Role");
        }
      //  System.out.println(permissions);
        String roleName = permissions.get("roleName");
        permissions.remove("roleName");

        Optional<Role> data = roleRepository.findByRoleName(roleName);
        if (data.isPresent()) {
            Role mm = data.get();
            roleRepository.delete(mm);
        }

        // Create the Role object
        Role role = new Role(roleName);
        role.setRoleStatus("ACTIVE");

        // Create Menu list
        List<Role.Menu> menuList = new ArrayList<>();
        Map<String, Role.Menu> menuMap = new HashMap<>();
        Map<String, Role.Menu.Page> pageMap = new HashMap<>();
        Map<String, Role.Menu.Page.Component> componentMap = new HashMap<>();

        for (Map.Entry<String, String> entry : permissions.entrySet()) {
            String permission = entry.getKey();
            boolean isSelected = Boolean.parseBoolean(entry.getValue());
            String[] parts = permission.split("\\.");

            if (parts.length == 1) { // Menu
                String menuName = parts[0];
                Role.Menu menu = menuMap.computeIfAbsent(menuName, k -> new Role.Menu(menuName));
                if (isSelected) {
                    menu.setMenuStatus("ACTIVE");
                }
                menuList.add(menu);
            } else if (parts.length == 2) { // Page
                String menuName = parts[0];
                String pageName = parts[1];
                Role.Menu menu = menuMap.computeIfAbsent(menuName, k -> new Role.Menu(menuName));
                Role.Menu.Page page = pageMap.computeIfAbsent(menuName + "." + pageName, k -> new Role.Menu.Page(pageName));
                if (isSelected) {
                    page.setPageStatus("ACTIVE");
                }
                menu.getPages().add(page);
                menuMap.put(menuName, menu);
            } else if (parts.length == 3) { // Component
                String menuName = parts[0];
                String pageName = parts[1];
                String componentName = parts[2];
                Role.Menu menu = menuMap.computeIfAbsent(menuName, k -> new Role.Menu(menuName));
                Role.Menu.Page page = pageMap.computeIfAbsent(menuName + "." + pageName, k -> new Role.Menu.Page(pageName));
                Role.Menu.Page.Component component = componentMap.computeIfAbsent(pageName + "." + componentName, k -> new Role.Menu.Page.Component(componentName));
                if (isSelected) {
                    component.setComponentStatus("ACTIVE");
                }
                page.getComponents().add(component);
                pageMap.put(menuName + "." + pageName, page);
                menuMap.put(menuName, menu);
            }
        }

        role.setMenus(menuList);
        roleRepository.save(role);
        return ResponseEntity.ok("Successfully assigned role.");
    }

    @PostMapping("/assignPermission")
    public ResponseEntity<String> assignPermission(@RequestBody Map<String, String> requestData) {
        out.println(requestData);
// Restrict specific roleName
        if ("SNVN".equals(requestData.get("roleName"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("SNVN is not permitted to Assign other  . Reserved Role");
        }

        Optional<Employee> data = registrationRepository.findByNameAndStatus(requestData.get("userName"), "1");

        if (data.isPresent()) {
            Employee mm = data.get();
            String newRole = requestData.get("roleName");
            // Ensure type is initialized
            if (mm.getType() == null) {
                mm.setType(new ArrayList<>());  // Initialize it to an empty list
            }

            // Check if the role already exists before adding it
            if (!mm.getType().contains(newRole)) {
                mm.getType().add(newRole);
                registrationRepository.save(mm);
                return ResponseEntity.ok("Successfully Inserted");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Role already exists");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @GetMapping("/getSingleRoleData")
    public ResponseEntity<List<Developer>> getSingleRoleData(@RequestParam String status,
                                                             @RequestHeader(value = "Authorization", required = true) String token,
                                                             Authentication authentication) {

       // out.println("Status: " + status);
        List<Developer> resultData = new ArrayList<>();

        // Fetch Role
        Role role = roleRepository.findByRoleName(status).orElse(null);
        if (role == null) {
            throw new RuntimeException("Role not found");
        }

        // Fetch All Developer Data
        List<Developer> developers = developerRepository.findAll();

        // Iterate Over Developer Data
        for (Developer developer : developers) {

            Role.Menu existingMenu = role.getMenus()
                    .stream()
                    .filter(m -> m.getMenuName().equals(developer.getMenuName()))
                    .findFirst()
                    .orElse(null);

            // If Menu Doesn't Exist in Role, Add It with "INACTIVE" Status
            if (existingMenu == null) {
                existingMenu = new Role.Menu();
                existingMenu.setMenuName(developer.getMenuName());
                existingMenu.setMenuStatus("INACTIVE");
                role.getMenus().add(existingMenu);
            }
            // ** Ensure developer.getPages() is never null **
            List<Developer.Page> developerPages = developer.getPages() != null ? developer.getPages() : new ArrayList<>();

            // Iterate Over Developer Pages
            for (Developer.Page devPage : developerPages) {

                Role.Menu.Page existingPage = existingMenu.getPages()
                        .stream()
                        .filter(p -> p.getPageName().equals(devPage.getPageName()))
                        .findFirst()
                        .orElse(null);

                // If Page Doesn't Exist in Role, Add It with "INACTIVE" Status
                if (existingPage == null) {
                    existingPage = new Role.Menu.Page();
                    existingPage.setPageName(devPage.getPageName());
                    existingPage.setPageStatus("INACTIVE");
                    existingMenu.getPages().add(existingPage);
                }

                // ** Fix: Ensure `components` is not null **
                List<Developer.Page.Component> devComponents = devPage.getComponents() != null ? devPage.getComponents() : new ArrayList<>();

                // Iterate Over Developer Components
                for (Developer.Page.Component devComponent : devComponents) {

                    Role.Menu.Page.Component existingComponent = existingPage.getComponents()
                            .stream()
                            .filter(c -> c.getComponentName().equals(devComponent.getComponentName()))
                            .findFirst()
                            .orElse(null);

                    // If Component Doesn't Exist in Role, Add It with "INACTIVE" Status
                    if (existingComponent == null) {
                        existingComponent = new Role.Menu.Page.Component();
                        existingComponent.setComponentName(devComponent.getComponentName());
                        existingComponent.setComponentStatus("INACTIVE");
                        existingPage.getComponents().add(existingComponent);
                    }
                }
            }
        }

        // Convert Updated Role to Developer List for Response
        for (Role.Menu menu : role.getMenus()) {
            Developer developer = new Developer();
            developer.setMenuName(menu.getMenuName());
            developer.setMenuStatus(menu.getMenuStatus());

            List<Developer.Page> developerPages = menu.getPages().stream().map(rolePage -> {
                Developer.Page devPage = new Developer.Page();
                devPage.setPageName(rolePage.getPageName());
                devPage.setPageStatus(rolePage.getPageStatus());

                List<Developer.Page.Component> devComponents = rolePage.getComponents() != null ?
                        rolePage.getComponents().stream().map(roleComponent -> {
                            Developer.Page.Component devComponent = new Developer.Page.Component();
                            devComponent.setComponentName(roleComponent.getComponentName());
                            devComponent.setComponentStatus(roleComponent.getComponentStatus());
                            return devComponent;
                        }).collect(Collectors.toList()) : new ArrayList<>();

                devPage.setComponents(devComponents);
                return devPage;
            }).collect(Collectors.toList());

            developer.setPages(developerPages);
            resultData.add(developer);
        }

      //  out.println("Updated Data: " + resultData);
        return ResponseEntity.ok(resultData);
    }



}
