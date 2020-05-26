package pl.edu.pw.sportyapp.user.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.shared.exception.DataDuplicationException;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.exception.NotAllowedActionException;
import pl.edu.pw.sportyapp.shared.exception.UserNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.user.dao.Grade;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.repository.UserRepository;
import pl.edu.pw.sportyapp.user.security.AppUserRole;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository ur, SequenceGeneratorService sgs) {
        this.userRepository = ur;
        this.sequenceGeneratorService = sgs;
    }

    public List<User> findAll() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new PermissionDeniedDataAccessException("User not permitted to access this data", null);
        }
        return userRepository.findAll();
    }

    public Long addUser(User submittedUser) {
        Long newId = sequenceGeneratorService.generateSequence(User.DBSEQUENCE_NAME);
        if (userRepository.existsByUsernameOrEmail(submittedUser.getUsername(), submittedUser.getEmail())) {
            throw new DataDuplicationException();
        }
        User newUser = new User(newId, submittedUser.getUsername(),
                Optional.ofNullable(submittedUser.getFullname()).orElse(null),
                passwordEncoder.encode(submittedUser.getPasswordHash()), AppUserRole.USER, true,
                true, true, true, submittedUser.getEmail(),
                Lists.newArrayList(5.0f, 5.0f, 5.0f), Lists.newArrayList(0, 0, 0), Lists.newArrayList(), Lists.newArrayList());
        return userRepository.insert(newUser).getId();
    }

    public User getUserById(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (currentUser.getRole().name().equals("ADMIN")) {
            return user;
        } else if (currentUser.getId() == user.getId()) {
            return new User(user.getId(), user.getUsername(), user.getFullname(), null,
                    AppUserRole.EMPTY, true, true, true, true,
                    user.getEmail(), user.getRatings(), user.getRatingsNumber(), user.getGamesParticipatedIds(), user.getFriendsIds());
        } else {
            return new User(user.getId(), user.getUsername(), user.getFullname(), null,
                    AppUserRole.EMPTY, true, true, true, true,
                    null, user.getRatings(), user.getRatingsNumber(), user.getGamesParticipatedIds(), null);
        }
    }

    public void updateUser(Long id, User user) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getId() != id && !currentUser.getRole().name().equals("ADMIN")) {
            throw new NotAllowedActionException();
        }
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }
        if (user.getId() != id) {
            throw new IllegalArgumentException();
        }
        userRepository.save(user);
    }

    public void rateUser(Long id, Grade grade) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        float newSkillGrade = ((user.getRatings().get(0) * user.getRatingsNumber().get(0)) + grade.kindness) / (user.getRatingsNumber().get(0) + 1);
        float newPunctGrade = ((user.getRatings().get(1) * user.getRatingsNumber().get(1)) + grade.kindness) / (user.getRatingsNumber().get(1) + 1);
        float newKindGrade = ((user.getRatings().get(2) * user.getRatingsNumber().get(2)) + grade.kindness) / (user.getRatingsNumber().get(2) + 1);
        user.getRatings().set(0, newSkillGrade);
        user.getRatings().set(1, newPunctGrade);
        user.getRatings().set(2, newKindGrade);
        user.getRatingsNumber().set(0, user.getRatingsNumber().get(0) + 1);
        user.getRatingsNumber().set(1, user.getRatingsNumber().get(1) + 1);
        user.getRatingsNumber().set(2, user.getRatingsNumber().get(2) + 1);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new NotAllowedActionException();
        }
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }
        userRepository.deleteById(id);
    }
}
