package pl.edu.pw.sportyapp.user.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.repository.UserRepository;
import pl.edu.pw.sportyapp.user.security.AppUserRole;

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

    public Long addUser(User submittedUser) {
        Long newId = sequenceGeneratorService.generateSequence(User.DBSEQUENCE_NAME);
        User newUser = new User(newId, submittedUser.getUsername(), Optional.ofNullable(submittedUser.getFullname()).orElse(null), passwordEncoder.encode(submittedUser.getPasswordHash()), AppUserRole.USER, true, true, true, true, submittedUser.getEmail(), Lists.newArrayList(), Lists.newArrayList());
        return userRepository.insert(newUser).getId();
    }

    public void updateUser(long id, User user) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }

        if (user.getId() != id) {
            throw new IllegalArgumentException();
        }

        userRepository.save(user);
    }

    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }

        userRepository.deleteById(id);
    }
}
