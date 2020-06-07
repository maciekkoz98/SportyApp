package pl.edu.pw.sportyapp.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.dao.QGame;
import pl.edu.pw.sportyapp.game.repository.GameRepository;
import pl.edu.pw.sportyapp.game.service.GameService;

import java.util.List;

@RestController
public class SearchController {

    private GameRepository gameRepository;
    private GameService gameService;

    @Autowired
    public SearchController(GameService service, GameRepository repository) {
        this.gameService = service;
        this.gameRepository = repository;
    }

    @GetMapping("/search/game")
    public ResponseEntity<List<Game>> findGames(
            @RequestParam(name = "facility", required = false) Long facility,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "laterThan", required = false) Long laterThan,
            @RequestParam(name = "earlierThan", required = false) Long earlierThan,
            @RequestParam(name = "player", required = false) Long player,
            @RequestParam(name = "startsWith", required = false) String startsWith
            ) {
        QGame game = new QGame("game");

        return new ResponseEntity<>(gameService.findByPredicate(
                facility != null ? game.facility.eq(facility) : null,
                name != null ? game.name.eq(name) : null,
                laterThan != null ? game.date.gt(laterThan) : null,
                earlierThan != null ? game.date.lt(earlierThan) : null,
                player != null ? game.players.contains(player) : null,
                startsWith != null ? game.name.startsWith(startsWith) : null), HttpStatus.OK);

    }

}
