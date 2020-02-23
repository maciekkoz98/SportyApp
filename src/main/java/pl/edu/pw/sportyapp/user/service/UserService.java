package pl.edu.pw.sportyapp.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    public UserService(UserRepository ur, SequenceGeneratorService sgs) {
        this.userRepository = ur;
        this.sequenceGeneratorService = sgs;
    }

    public Long addUser(User newUser) {
        newUser.setId(sequenceGeneratorService.generateSequence(User.DBSEQUENCE_NAME));
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
