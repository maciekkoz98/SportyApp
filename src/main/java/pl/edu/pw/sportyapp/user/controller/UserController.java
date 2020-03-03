package pl.edu.pw.sportyapp.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.repository.UserRepository;
import pl.edu.pw.sportyapp.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    public UserController(UserRepository ur, UserService us) {
        this.userRepository = ur;
        this.userService = us;
    }

    @GetMapping("/user")
    public ResponseEntity<List<User>> getAll() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getOne(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userRepository.findById(id).orElseThrow(EntityNotFoundException::new), HttpStatus.OK);
    }

    @PostMapping(value = "/user", consumes = "application/json")
    public ResponseEntity<Long> create(@Valid @RequestBody User newUser) {
        return new ResponseEntity<>(userService.addUser(newUser), HttpStatus.CREATED);
    }

    @PutMapping(value = "/user/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @Valid @RequestBody User user) {
        userService.updateUser(id, user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(value = "/user/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundExceptionHandler() {
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentExceptionHandler() {
        return new ResponseEntity<>("Illegal argument error", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> validationExceptionHandler() {
        return new ResponseEntity<>("Validation exception", HttpStatus.BAD_REQUEST);
    }

}
