package pl.edu.pw.sportyapp.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.repository.GameRepository;
import pl.edu.pw.sportyapp.shared.exception.DataDuplicationException;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.exception.NotAllowedActionException;
import pl.edu.pw.sportyapp.shared.exception.UserNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.service.UserService;

import java.security.acl.NotOwnerException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GameService {

    private SequenceGeneratorService sequenceGenerator;
    private GameRepository gameRepository;
    private UserService userService;

    @Autowired
    public GameService(GameRepository gr, SequenceGeneratorService sgs, UserService us) {
        this.gameRepository = gr;
        this.sequenceGenerator = sgs;
        this.userService = us;
    }

    public Long addGame(Game newGame) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        newGame.setId(sequenceGenerator.generateSequence(Game.DBSEQUENCE_NAME));
        newGame.setOwner(currentUser.getId());
        newGame.setPlayers(new ArrayList<>(Arrays.asList(currentUser.getId())));
        User user = userService.getUserById(currentUser.getId()).orElseThrow(UserNotFoundException::new);
        user.getGamesParticipatedIds().add(newGame.getId());
        userService.updateUser(user.getId(), user);
        return gameRepository.insert(newGame).getId();
    }

    public void deleteGame(long id) throws NotOwnerException, EntityNotFoundException {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Game game = gameRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!gameRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }
        if (!currentUser.getRole().name().equals("ADMIN") && currentUser.getId() != game.getOwner()) {
            throw new NotOwnerException();
        }
        gameRepository.deleteById(id);
    }

    public void updateGame(long id, Game game) throws NotOwnerException {
        if (!gameRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }

        if (game.getId() != id) {
            throw new IllegalArgumentException();
        }

        if (((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId() != game.getOwner()) {
            throw new NotOwnerException();
        }

        gameRepository.save(game);
    }

    public void addUserToGame(Long gameId, Long userId) throws DataDuplicationException, UserNotFoundException {
        Game game = gameRepository.findById(gameId).orElseThrow(EntityNotFoundException::new);
        User user = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);
        if (game.getPlayers().contains(userId)) {
            throw new DataDuplicationException();
        }
        game.getPlayers().add(userId);
        gameRepository.save(game);
        user.getGamesParticipatedIds().add(gameId);
        userService.updateUser(userId, user);
    }

    public void removeUserFromGame(Long gameId, Long userId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Game game = gameRepository.findById(gameId).orElseThrow(EntityNotFoundException::new);
        User user = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);
        if (currentUser.getId() != userId && currentUser.getId() != game.getOwner() && !currentUser.getRole().name().equals("ADMIN")) {
            throw new NotAllowedActionException();
        }
        if (!game.getPlayers().contains(userId)) {
            throw new UserNotFoundException();
        }
        if (game.getOwner() == userId) {
            if (game.getPlayers().size() > 1) {
                game.setOwner(game.getPlayers().get(1));
            }
        }
        game.getPlayers().remove(userId);
        user.getGamesParticipatedIds().remove(gameId);
        if (game.getPlayers().size() >= 1) {
            gameRepository.save(game);
        } else {
            gameRepository.deleteById(gameId);
        }
        userService.updateUser(userId, user);
    }

    public List<Game> findAllAvailableForUser(Long userId) {
        List<Game> games = gameRepository.findByIsPublicOrOwnerOrPlayersContains(true, userId, userId);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        games.removeIf(game -> (game.getDate() + game.getDuration()) < time.getTime());
        return games;
    }

    public List<Game> findAllOldGames() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Game> games;
        if (currentUser.getRole().name().equals("ADMIN")) {
            games = gameRepository.findAll();
        } else {
            games = gameRepository.findByOwnerOrPlayersContains(currentUser.getId(), currentUser.getId());
        }
        Timestamp time = new Timestamp(System.currentTimeMillis());
        games.removeIf(game -> (game.getDate() + game.getDuration()) > time.getTime());
        return games;
    }

    public Game findById(Long id) {
        Game game = gameRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!game.isPublic && !currentUser.getRole().name().equals("ADMIN")) {
            if ((game.owner != currentUser.getId()) && !game.getPlayers().contains(currentUser.getId())) {
                throw new PermissionDeniedDataAccessException("User not permitted to access this game", null);
            }
        }
        return game;
    }
}
