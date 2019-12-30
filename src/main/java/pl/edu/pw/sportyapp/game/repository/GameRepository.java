package pl.edu.pw.sportyapp.game.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.pw.sportyapp.game.dao.Game;

import java.util.List;

public interface GameRepository extends MongoRepository<Game, Long> {

    Game findByName(Long id);
    List<Game> findByFacility(Long id);
}
