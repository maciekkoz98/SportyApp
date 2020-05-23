package pl.edu.pw.sportyapp.user.controller;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.repository.UserRepository;
import pl.edu.pw.sportyapp.user.service.UserService;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserControllerTest {

    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        userController = new UserController(userRepository, userService);
    }

    @AfterEach
    void destroy() {
        userController = null;
    }

    @Test
    void getAll() {
        User user1 = User.builder().username("testowy1").email("jkowal1@mail.com")
                .gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();
        User user2 = User.builder().username("testowy2").email("jkowal2@mail.com")
                .gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        when(userRepository.findAll()).thenReturn(Lists.newArrayList(user1, user2));

        assertThat(userController.getAll().getBody()).hasSize(2);
    }

    @Test
    void getOneEntityExists() {
        User user = User.builder().id(1L).username("testowy1").email("jkowal1@mail.com")
                .gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        assertThat(userController.getOne(1L).getBody().getId()).isEqualTo(1L);
    }

    @Test
    void getOneEntityNotFound() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> userController.getOne(10L));
    }

    @Test
    void createWithValidInput() {
        User user = User.builder().username("testowy1").email("jkowal1@mail.com")
                .gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        when(userService.addUser(Mockito.any(User.class))).thenReturn(3L);

        assertThat(userController.create(user).getBody()).isEqualTo(3L);
    }

    @Test
    void updateWithValidInput() {
        User user = User.builder().id(1L).username("testowyNowy").email("jkowal1@mail.com")
                .gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        doNothing().when(userService).updateUser(1L, user);

        assertThat(userController.update(1L, user).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void updateWithInvalidInput() {
        User user = User.builder().id(1L).username("testowyNowy").email("jkowal1@mail.com")
                .gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        doThrow(IllegalArgumentException.class).when(userService).updateUser(2L, user);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> userController.update(2L, user));
    }

    @Test
    void updateNotExistingEntity() {
        User user = User.builder().id(1L).username("testowyNowy").email("jkowal1@mail.com")
                .gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        doThrow(EntityNotFoundException.class).when(userService).updateUser(1L, user);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> userController.update(1L, user));
    }

    @Test
    void deleteExistingEntity() {
        doNothing().when(userService).deleteUser(1L);

        assertThat(userController.delete(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }


}
