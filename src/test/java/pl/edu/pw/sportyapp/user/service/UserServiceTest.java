package pl.edu.pw.sportyapp.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.repository.UserRepository;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserServiceTest {

    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    SequenceGeneratorService sequenceGeneratorService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        userService = new UserService(userRepository, sequenceGeneratorService);
    }

    @Test
    void addUserValidInput() {
        User user = User.builder().nickname("testowy").name("Jan").surname("Kowalski").email("jkowal@mail.com")
                .password("testowe").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();
        when(sequenceGeneratorService.generateSequence(anyString())).thenReturn(10L);
        when(userRepository.insert(any(User.class))).thenReturn(user);

        assertThat(userService.addUser(user)).isEqualTo(10L);
    }

    @Test
    void deleteUserExistingId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.deleteUser(10L);
    }

    @Test
    void deleteUserNotExistingId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> userService.deleteUser(10L));
    }

    @Test
    void updateUserValidInput() {
        User user = User.builder().id(10L).nickname("testowyNowy").name("Jan").surname("Kowalski").email("jkowal@mail.com")
                .password("testowe").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();
        when(userRepository.existsById(10L)).thenReturn(true);

        userService.updateUser(10L, user);
    }

    @Test
    void updateUserInvalidInput() {
        User user = User.builder().id(11L).nickname("testowyNowy").name("Jan").surname("Kowalski").email("jkowal@mail.com")
                .password("testowe").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userService.updateUser(10L, user));
    }

    @Test
    void updateUserNotExistingId() {
        User user = User.builder().id(10L).nickname("testowyNowy").name("Jan").surname("Kowalski").email("jkowal@mail.com")
                .password("testowe").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> userService.updateUser(10L, user));
    }

}
