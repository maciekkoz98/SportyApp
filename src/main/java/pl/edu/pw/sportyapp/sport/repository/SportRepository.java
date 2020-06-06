package pl.edu.pw.sportyapp.sport.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.pw.sportyapp.sport.dao.Sport;

public interface SportRepository extends MongoRepository<Sport, Long> {

}
