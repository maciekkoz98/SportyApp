package pl.edu.pw.sportyapp.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.repository.GameRepository;
import pl.edu.pw.sportyapp.shared.SequenceGeneratorService;

@Service
public class GameService {

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @Autowired
    GameRepository gameRepository;

    public Long addGame(Game newGame) {
        newGame.setId(sequenceGenerator.generateSequence(Game.DBSEQUENCE_NAME));
        return gameRepository.insert(newGame).getId();
    }
}
