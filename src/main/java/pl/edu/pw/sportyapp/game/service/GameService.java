package pl.edu.pw.sportyapp.game.service;

import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.dao.QGame;
import pl.edu.pw.sportyapp.game.repository.GameRepository;
import pl.edu.pw.sportyapp.shared.exception.DataDuplicationException;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.exception.NotAllowedActionException;
import pl.edu.pw.sportyapp.shared.exception.UserNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.sport.service.SportService;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.service.UserService;

import java.security.acl.NotOwnerException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

@Service
public class GameService {

    private SequenceGeneratorService sequenceGenerator;
    private GameRepository gameRepository;
    private UserService userService;
    private SportService sportService;

    @Autowired
    public GameService(GameRepository gr, SequenceGeneratorService sgs, UserService us, SportService ss) {
        this.gameRepository = gr;
        this.sequenceGenerator = sgs;
        this.userService = us;
        this.sportService = ss;
    }

    public Long addGame(Game newGame) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        newGame.setId(sequenceGenerator.generateSequence(Game.DBSEQUENCE_NAME));
        newGame.setOwner(currentUser.getId());
        newGame.setPlayers(new ArrayList<>(asList(currentUser.getId())));
        if (!sportService.checkIfSportExists(newGame.id)) {
            newGame.setSport(0);
        }
        if (newGame.getPlayers().size() > newGame.getMaxPlayers()) {
            throw new IllegalArgumentException();
        }
        User user = userService.getUserById(currentUser.getId());
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
        if (!sportService.checkIfSportExists(game.id)) {
            game.setSport(0);
        }

        if (game.getPlayers().size() > game.getMaxPlayers()) {
            throw new IllegalArgumentException();
        }

        gameRepository.save(game);
    }

    public void addUserToGame(Long gameId, Long userId) throws DataDuplicationException, UserNotFoundException {
        Game game = gameRepository.findById(gameId).orElseThrow(EntityNotFoundException::new);
        User user = userService.getUserById(userId);
        if (game.getPlayers().contains(userId)) {
            throw new DataDuplicationException();
        }
        if (game.getPlayers().size() + 1 > game.getMaxPlayers()) {
            throw new IllegalArgumentException();
        }
        game.getPlayers().add(userId);
        gameRepository.save(game);
        user.getGamesParticipatedIds().add(gameId);
        userService.updateUser(userId, user);
    }

    public void removeUserFromGame(Long gameId, Long userId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Game game = gameRepository.findById(gameId).orElseThrow(EntityNotFoundException::new);
        User user = userService.getUserById(userId);
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

    public List<Game> findAllByFacility(long facilityId) {
        List<Game> games = gameRepository.findByIsPublicAndFacility(true, facilityId);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        games.removeIf(game -> (game.getDate() + game.getDuration()) < time.getTime());
        return games;
    }

    public List<Game> findByPredicate(BooleanExpression ... predicates) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Game> games;

        if (currentUser.getRole().name().equals("ADMIN")) {
            games = Lists.newArrayList(gameRepository.findAll(Expressions.allOf(predicates)));
        } else {
            QGame game = new QGame("game");
            List predicatesWithOwner = Arrays.asList(predicates);
            predicatesWithOwner.add(game.owner.eq(currentUser.getId()).or(game.players.contains(currentUser.getId())));
            games = Lists.newArrayList(gameRepository.findAll(Expressions.allOf((BooleanExpression[]) predicatesWithOwner.toArray())));
        }

        return games;
    }

    public List<Game> findByPredicate(boolean active, BooleanExpression ... predicates) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        List<Game> games;

        if (currentUser.getRole().name().equals("ADMIN")) {
            games = Lists.newArrayList(gameRepository.findAll(Expressions.allOf(predicates)));
        } else {
            QGame game = new QGame("game");
            List predicatesWithOwner = Arrays.asList(predicates);
            predicatesWithOwner.add(game.owner.eq(currentUser.getId()).or(game.players.contains(currentUser.getId())));
            games = Lists.newArrayList(gameRepository.findAll(Expressions.allOf((BooleanExpression[]) predicatesWithOwner.toArray())));
        }

        if(active) {
            games.removeIf(game -> (game.getDate() + game.getDuration()) < time.getTime());
        } else {
            games.removeIf(game -> (game.getDate() + game.getDuration()) > time.getTime());
        }

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
