package pl.edu.pw.sportyapp.facility.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.edu.pw.sportyapp.facility.dao.Facility;

public interface FacilityRepository extends MongoRepository<Facility, Long> {
    boolean existsByLatitudeAndLongitude(double latitude, double longitude);
}
