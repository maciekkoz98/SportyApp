package pl.edu.pw.sportyapp.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.repository.GameRepository;
import pl.edu.pw.sportyapp.game.service.GameService;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @GetMapping("/game")
    public ResponseEntity<List<Game>> getAll() {
        return new ResponseEntity<>(gameRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<Game> getOne(@PathVariable("id") Long id) {
        return new ResponseEntity<>(gameRepository.findById(id).orElseThrow(EntityNotFoundException::new), HttpStatus.OK);
    }

    @PostMapping(value = "/game", consumes = "application/json")
    public ResponseEntity<Long> create(@Valid @RequestBody Game newGame) {
        return new ResponseEntity<>(gameService.addGame(newGame), HttpStatus.CREATED);
    }

    @PutMapping(value = "/game/{id}", consumes = "application/json")
    public ResponseEntity update(@PathVariable("id") Long id, @Valid @RequestBody Game game) {
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
}
