package pl.edu.pw.sportyapp.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.repository.GameRepository;
import pl.edu.pw.sportyapp.game.service.GameService;

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

    @PostMapping(value = "/game", consumes = "application/json")
    public ResponseEntity<Long> createGame(@Valid @RequestBody Game newGame) {
        return new ResponseEntity<>(gameService.addGame(newGame), HttpStatus.CREATED);
    }
}
