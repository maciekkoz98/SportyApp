//package pl.edu.pw.sportyapp.game.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import pl.edu.pw.sportyapp.game.dao.Game;
//import pl.edu.pw.sportyapp.game.repository.GameRepository;
//import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
//import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
//import static org.mockito.Mockito.*;
//import static org.mockito.MockitoAnnotations.initMocks;
//
//class GameServiceTest {
//
//    GameService gameService;
//
//    @Mock
//    GameRepository gameRepository;
//
//    @Mock
//    SequenceGeneratorService sequenceGeneratorService;
//
//    @BeforeEach
//    void setUp() {
//        initMocks(this);
//        gameService = new GameService(gameRepository, sequenceGeneratorService);
//    }
//
//    @Test
//    void addGameValidInput() {
//        Game game = Game.builder().facility(2L).build();
//        when(sequenceGeneratorService.generateSequence(anyString())).thenReturn(3L);
//        when(gameRepository.insert(any(Game.class))).thenReturn(game);
//
//        assertThat(gameService.addGame(game)).isEqualTo(3L);
//    }
//
//    @Test
//    void deleteGameExistingId() {
//        when(gameRepository.existsById(anyLong())).thenReturn(true);
//
//        gameService.deleteGame(3L);
//    }
//
//    @Test
//    void deleteGameNotExistingId() {
//        when(gameRepository.existsById(anyLong())).thenReturn(false);
//
//        assertThatExceptionOfType(EntityNotFoundException.class)
//                .isThrownBy(() -> gameService.deleteGame(3L));
//    }
//
//    @Test
//    void updateGameValidInput() {
//        Game game = Game.builder().id(3L).facility(2L).build();
//        when(gameRepository.existsById(3L)).thenReturn(true);
//
//        gameService.updateGame(3L, game);
//    }
//
//    @Test
//    void updateGameNotExistingId() {
//        Game game = Game.builder().id(3L).facility(2L).build();
//        when(gameRepository.existsById(anyLong())).thenReturn(false);
//
//        assertThatExceptionOfType(EntityNotFoundException.class)
//                .isThrownBy(() -> gameService.updateGame(3L, game));
//    }
//
//    @Test
//    void updateGameInvalidInput() {
//        Game game = Game.builder().id(2L).facility(2L).build();
//        when(gameRepository.existsById(anyLong())).thenReturn(true);
//
//        assertThatExceptionOfType(IllegalArgumentException.class)
//                .isThrownBy(() -> gameService.updateGame(3L, game));
//    }
//}