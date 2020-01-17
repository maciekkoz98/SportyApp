package pl.edu.pw.sportyapp.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.repository.GameRepository;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;

@Service
public class GameService {

    private SequenceGeneratorService sequenceGenerator;
    private GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gr, SequenceGeneratorService sgs) {
        this.gameRepository = gr;
        this.sequenceGenerator = sgs;
    }

    public Long addGame(Game newGame) {
        newGame.setId(sequenceGenerator.generateSequence(Game.DBSEQUENCE_NAME));
        return gameRepository.insert(newGame).getId();
    }

    public void deleteGame(long id) {
        if(!gameRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }

        gameRepository.deleteById(id);
    }

    public void updateGame(long id, Game game) {
        if(!gameRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }

        if(game.getId() != id) {
            throw new IllegalArgumentException();
        }

        gameRepository.save(game);
    }
}
