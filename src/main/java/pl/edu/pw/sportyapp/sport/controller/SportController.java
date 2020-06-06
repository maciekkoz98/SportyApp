package pl.edu.pw.sportyapp.sport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.sport.dao.Sport;
import pl.edu.pw.sportyapp.sport.service.SportService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class SportController {
    private SportService sportService;

    @Autowired
    public SportController(SportService ss) {
        this.sportService = ss;
    }

    @GetMapping("/sport")
    public ResponseEntity<List<Sport>> getAll() {
        return new ResponseEntity<>(sportService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/sport/{id}")
    public ResponseEntity<Sport> getOne(@PathVariable("id") Long id) {
        return new ResponseEntity<>(sportService.getOne(id), HttpStatus.OK);
    }

    @PostMapping(value = "/sport", consumes = "application/json")
    public ResponseEntity<Long> create(@Valid @RequestBody Sport newSport) {
        return new ResponseEntity<>(sportService.addSport(newSport), HttpStatus.CREATED);
    }

    @PutMapping(value = "/sport/{id}", consumes = "application/json")
    public ResponseEntity update(@PathVariable("id") Long id, @Valid @RequestBody Sport sport) {
        sportService.updateSport(id, sport);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(value = "/sport/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        sportService.deleteSport(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundExceptionHandler() {
        return new ResponseEntity<>("Sport with this ID is not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentExceptionHandler() {
        return new ResponseEntity<>("General illegal argument error", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> validationExceptionHandler() {
        return new ResponseEntity<>("Validation exception", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PermissionDeniedDataAccessException.class)
    public ResponseEntity<String> permissionDeniedExceptionHandler() {
        return new ResponseEntity<>("User not permitted to access this area.", HttpStatus.FORBIDDEN);
    }
}
