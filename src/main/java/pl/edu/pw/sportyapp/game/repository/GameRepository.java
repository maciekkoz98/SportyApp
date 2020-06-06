package pl.edu.pw.sportyapp.game.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.pw.sportyapp.game.dao.Game;

import java.util.List;

public interface GameRepository extends MongoRepository<Game, Long> {
    List<Game> findByIsPublicOrOwnerOrPlayersContains(boolean isPublic, long owner, long player);

    List<Game> findByOwnerOrPlayersContains(long owner, long player);

    List<Game> findByIsPublicAndFacility(boolean isPublic, long facility);
}
