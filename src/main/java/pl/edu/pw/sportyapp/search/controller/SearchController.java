package pl.edu.pw.sportyapp.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.sportyapp.facility.dao.Facility;
import pl.edu.pw.sportyapp.facility.dao.QFacility;
import pl.edu.pw.sportyapp.facility.service.FacilityService;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.dao.QGame;
import pl.edu.pw.sportyapp.game.service.GameService;
import pl.edu.pw.sportyapp.user.dao.QUser;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.service.UserService;

import java.util.List;

@RestController
public class SearchController {

    private GameService gameService;
    private UserService userService;
    private FacilityService facilityService;

    @Autowired
    public SearchController(GameService gameService, UserService userService, FacilityService facilityService) {
        this.gameService = gameService;
        this.userService = userService;
        this.facilityService = facilityService;
    }

    @GetMapping("/search/game")
    public ResponseEntity<List<Game>> findGames(
            @RequestParam(name = "facility", required = false) Long facility,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "laterThan", required = false) Long laterThan,
            @RequestParam(name = "earlierThan", required = false) Long earlierThan,
            @RequestParam(name = "player", required = false) Long player,
            @RequestParam(name = "nameStartsWith", required = false) String nameStartsWith,
            @RequestParam(name = "nameContains", required = false) String nameContains,
            @RequestParam(name = "sport", required = false) Long sport
            ) {
        QGame game = new QGame("game");

        return new ResponseEntity<>(gameService.findByPredicate(
                facility != null                        ? game.facility.eq(facility) : null,
                name != null                            ? game.name.eq(name) : null,
                laterThan != null && laterThan > 0      ? game.date.gt(laterThan) : null,
                earlierThan != null && earlierThan > 0  ? game.date.lt(earlierThan) : null,
                player != null                          ? game.players.contains(player) : null,
                nameStartsWith != null                  ? game.name.startsWith(nameStartsWith) : null,
                nameContains != null                    ? game.name.contains(nameContains) : null,
                sport != null  && sport > 0             ? game.sport.eq(sport) : null
        ), HttpStatus.OK);
    }

    @GetMapping("/search/user")
    public ResponseEntity<List<User>> findUsers(
            @RequestParam(name = "usernameStartsWith", required = false) String usernameStartsWith,
            @RequestParam(name = "usernameContains", required = false) String usernameContains
            ) {
        QUser user = new QUser("user");

        return new ResponseEntity<>(userService.findByPredicate(
                usernameStartsWith != null ? user.username.startsWith(usernameStartsWith) : null,
                usernameContains != null ? user.username.contains(usernameContains) : null
        ), HttpStatus.OK);
    }

    @GetMapping("/search/facility")
    public ResponseEntity<List<Facility>> findFacilities(
            @RequestParam(name = "proximityLat", required = true) Double proximityLat,
            @RequestParam(name = "proximityLon", required = true) Double proximityLon,
            @RequestParam(name = "maxDistance", required = false) Long maxDistance
    ) {
        QFacility facility = new QFacility("facility");

        return new ResponseEntity<>(
                maxDistance != null && maxDistance > 0
                        ? facilityService.findByProximity(proximityLat, proximityLon, maxDistance)
                        : facilityService.findByProximity(proximityLat, proximityLon)
                , HttpStatus.OK);
    }

}
