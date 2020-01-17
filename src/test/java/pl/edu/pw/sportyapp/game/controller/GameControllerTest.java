package pl.edu.pw.sportyapp.game.controller;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.repository.GameRepository;
import pl.edu.pw.sportyapp.game.service.GameService;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class GameControllerTest {

    private GameController gameController;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameService gameService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        gameController = new GameController(gameRepository, gameService);
    }

    @AfterEach
    void destroy() {
        gameController = null;
    }

    @Test
    void getAll() {
        Game g1 = Game.builder().id(2L).facility(3L).build();
        Game g2 = Game.builder().id(3L).facility(3L).build();
        when(gameRepository.findAll()).thenReturn(Lists.newArrayList(g1, g2));

        assertThat(gameController.getAll().getBody()).hasSize(2);
    }

    @Test
    void getOneEntityExists() {
        Game game = Game.builder().id(1L).name("supergra").facility(3L).build();
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(game));

        assertThat(gameController.getOne(1L).getBody().getId()).isEqualTo(1L);
    }

    @Test
    void getOneEntityNotFound() {
        when(gameRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> gameController.getOne(15L));
    }

    @Test
    void createWithValidInput() {
        Game game = Game.builder().name("supergra").facility(3L).build();
        when(gameService.addGame(Mockito.any(Game.class))).thenReturn(3L);

        assertThat(gameController.create(game).getBody()).isEqualTo(3L);
    }

    @Test
    void updateWithValidInput() {
        Game game = Game.builder().id(3L).facility(5L).build();
        doNothing().when(gameService).updateGame(3L, game);

        assertThat(gameController.update(3L, game).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void updateNotExistingEntity() {
        Game game = Game.builder().id(3L).facility(5L).build();
        doThrow(EntityNotFoundException.class).when(gameService).updateGame(3L, game);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> gameController.update(3L, game));
    }

    @Test
    void updateWithInvalidInput() {
        Game game = Game.builder().id(3L).facility(5L).build();
        doThrow(IllegalArgumentException.class).when(gameService).updateGame(5L, game);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> gameController.update(5L, game));
    }

    @Test
    void deleteExistingEntity() {
        doNothing().when(gameService).deleteGame(3L);

        assertThat(gameController.delete(3L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}