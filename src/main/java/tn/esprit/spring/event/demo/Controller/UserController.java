package tn.esprit.spring.event.demo.Controller;

import java.io.IOException;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import tn.esprit.spring.event.demo.Model.User;
import tn.esprit.spring.event.demo.Repository.UserRepository;
import tn.esprit.spring.event.demo.Service.UserService;
import tn.esprit.spring.event.demo.config.JwtService;


@CrossOrigin(origins = "*", allowedHeaders ="*")
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user")
    public ResponseEntity<User> getUser() {
        return ResponseEntity.ok(new User()/*userService.getUser()*/);
    }

    @GetMapping("/getuser/{id}")
    public ResponseEntity<?> getUser(HttpServletRequest request, HttpServletResponse response,@PathVariable Long id) throws IOException {
        return userService.getUser(request, response, id);
    }
    @GetMapping("/getConnecteduser")
    public ResponseEntity<?> getConnectedUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return userService.getConnectedUser(request, response);
    }











}



