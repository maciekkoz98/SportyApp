package pl.edu.pw.sportyapp.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import pl.edu.pw.sportyapp.user.dao.User;

public interface UserRepository extends MongoRepository<User, Long>, QuerydslPredicateExecutor<User> {
    User findByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    User findByEmail(String email);
}
