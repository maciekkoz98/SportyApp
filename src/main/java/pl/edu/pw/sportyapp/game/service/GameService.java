package pl.edu.pw.sportyapp.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.repository.GameRepository;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.user.dao.User;

import java.security.acl.NotOwnerException;
import java.util.Arrays;
import java.util.List;

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
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        newGame.setId(sequenceGenerator.generateSequence(Game.DBSEQUENCE_NAME));
        newGame.setOwner(currentUser.getId());
        newGame.setPlayers(new long[]{currentUser.getId()});
        return gameRepository.insert(newGame).getId();
    }

    public void deleteGame(long id) {
        if(!gameRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }

        gameRepository.deleteById(id);
    }

    public void updateGame(long id, Game game) throws NotOwnerException {
        if(!gameRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }

        if(game.getId() != id) {
            throw new IllegalArgumentException();
        }

        if(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId() != game.getOwner()) {
            throw new NotOwnerException();
        }

        gameRepository.save(game);
    }

    public List<Game> findAll(String username) {
        return gameRepository.findByIsPublic(true);
    }

    public Game findById(Long id) {
        Game game = gameRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!game.isPublic) {
            if((game.owner != currentUser.getId()) && (!Arrays.asList(game.getPlayers()).contains(currentUser.getId()))) {
                throw new PermissionDeniedDataAccessException("User not permitted to access this game", null);
            }
        }

        return game;
    }
}
