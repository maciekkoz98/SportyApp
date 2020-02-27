package pl.edu.pw.sportyapp.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.pw.sportyapp.user.dao.User;

public interface UserRepository extends MongoRepository<User, Long> {
    User findByUsername(String username);

    User findByNameAndSurname(String name, String surname);

    User findByEmail(String email);
}
