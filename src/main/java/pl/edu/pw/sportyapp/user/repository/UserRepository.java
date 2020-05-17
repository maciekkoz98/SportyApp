package pl.edu.pw.sportyapp.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.pw.sportyapp.user.dao.User;

public interface UserRepository extends MongoRepository<User, Long> {
    public User findByUsername(String username);
}
