package pl.edu.pw.sportyapp.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.sportyapp.shared.exception.DataDuplicationException;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.exception.NotAllowedActionException;
import pl.edu.pw.sportyapp.shared.exception.UserNotFoundException;
import pl.edu.pw.sportyapp.user.dao.Grade;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.repository.UserRepository;
import pl.edu.pw.sportyapp.user.security.AppUserRole;
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
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/user/me")
    public ResponseEntity<User> getMyself() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(currentUser.getId()).orElseThrow(EntityNotFoundException::new);
        User userToReturn = new User(user.getId(), user.getUsername(), user.getFullname(), null,
                AppUserRole.EMPTY, true, true, true, true,
                user.getEmail(), user.getRatings(), user.getRatingsNumber(), user.getGamesParticipatedIds(), user.getFriendsIds());
        return new ResponseEntity<>(userToReturn, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getOne(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/user", consumes = "application/json")
    public ResponseEntity<Long> create(@Valid @RequestBody User newUser) {
        return new ResponseEntity<>(userService.addUser(newUser), HttpStatus.CREATED);
    }

    @PostMapping(value = "/user/{id}/rate", consumes = "application/json")
    public ResponseEntity rateUser(@PathVariable("id") Long id, @Valid @RequestBody Grade grade) {
        userService.rateUser(id, grade);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping(value = "/user/{id}", consumes = "application/json")
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

    @ExceptionHandler(PermissionDeniedDataAccessException.class)
    public ResponseEntity<String> permissionDeniedExceptionHandler() {
        return new ResponseEntity<>("No permission to access.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> userNotFoundExceptionHandler() {
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAllowedActionException.class)
    public ResponseEntity<String> notAllowedActionExceptionHandler() {
        return new ResponseEntity<>("Not authorized to perform operation", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataDuplicationException.class)
    public ResponseEntity<String> dataDuplicationExceptionHandler() {
        return new ResponseEntity<>("User already exists", HttpStatus.valueOf(409));
    }

}
