package pl.edu.pw.sportyapp.facility.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.sportyapp.facility.dao.Facility;
import pl.edu.pw.sportyapp.facility.service.FacilityService;
import pl.edu.pw.sportyapp.shared.exception.DataDuplicationException;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController
public class FacilityController {
    private FacilityService facilityService;

    @Autowired
    public FacilityController(FacilityService fs) {
        this.facilityService = fs;
    }

    @GetMapping("/facility")
    public ResponseEntity<List<Facility>> getAll() {
        return new ResponseEntity<>(facilityService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/facility/{id}")
    public ResponseEntity<Facility> getOne(@PathVariable("id") Long id) {
        return new ResponseEntity<>(facilityService.getOne(id), HttpStatus.OK);
    }

    @PostMapping(value = "/facility", consumes = "application/json")
    public ResponseEntity<Long> create(@Valid @RequestBody Facility newFacility) {
        return new ResponseEntity<>(facilityService.addFacility(newFacility), HttpStatus.CREATED);
    }

    @PutMapping(value = "/facility/{id}", consumes = "application/json")
    public ResponseEntity update(@PathVariable("id") Long id, @Valid @RequestBody Facility facility) {
        facilityService.updateFacility(id, facility);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(value = "/facility/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        facilityService.deleteFacility(id);
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
        return new ResponseEntity<>("User not permitted to access this area", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataDuplicationException.class)
    public ResponseEntity<String> dataDuplicationExceptionHandler() {
        return new ResponseEntity<>("Facility at this position is already in the database", HttpStatus.valueOf(409));
    }
}
