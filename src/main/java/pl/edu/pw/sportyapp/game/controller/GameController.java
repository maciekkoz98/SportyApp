package pl.edu.pw.sportyapp.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.repository.GameRepository;
import pl.edu.pw.sportyapp.game.service.GameService;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.user.dao.User;

import javax.validation.Valid;
import java.security.acl.NotOwnerException;
import java.util.List;

@RestController
public class GameController {

    private GameRepository gameRepository;
    private GameService gameService;

    @Autowired
    public GameController(GameRepository gr, GameService gs) {
        this.gameRepository = gr;
        this.gameService = gs;
    }

    @GetMapping("/game")
    public ResponseEntity<List<Game>> getAll() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(currentUser.getUsername());
        if(currentUser.getRole().name().equals("ADMIN")) {
            return new ResponseEntity<>(gameRepository.findAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(gameService.findAll(currentUser.getUsername()), HttpStatus.OK);
        }
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<Game> getOne(@PathVariable("id") Long id) {
        return new ResponseEntity<>(gameService.findById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/game", consumes = "application/json")
    public ResponseEntity<Long> create(@Valid @RequestBody Game newGame) {
        return new ResponseEntity<>(gameService.addGame(newGame), HttpStatus.CREATED);
    }

    @PutMapping(value = "/game/{id}", consumes = "application/json")
    public ResponseEntity update(@PathVariable("id") Long id, @Valid @RequestBody Game game) throws NotOwnerException {
        gameService.updateGame(id, game);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/game/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        gameService.deleteGame(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundExceptionHandler() {
        return new ResponseEntity<>("Game with this ID is not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentExceptionHandler() {
        return new ResponseEntity<>("General illegal argument error", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> validationExceptionHandler() {
        return new ResponseEntity<>("Validation exception", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotOwnerException.class)
    public ResponseEntity<String> notOwnerExceptionHandler() {
        return new ResponseEntity<>("Only owner can edit this game", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PermissionDeniedDataAccessException.class)
    public ResponseEntity<String> permissionDeniedExceptionHandler() {
        return new ResponseEntity<>("User not permitted to access this game", HttpStatus.BAD_REQUEST);
    }
}
