package pl.edu.pw.sportyapp.facility.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import pl.edu.pw.sportyapp.facility.dao.Facility;

public interface FacilityRepository extends MongoRepository<Facility, Long>, QuerydslPredicateExecutor<Facility> {
    boolean existsByLatitudeAndLongitude(double latitude, double longitude);
}
