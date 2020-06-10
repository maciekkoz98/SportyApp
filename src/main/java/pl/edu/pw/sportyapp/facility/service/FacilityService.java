package pl.edu.pw.sportyapp.facility.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.facility.dao.Facility;
import pl.edu.pw.sportyapp.facility.repository.FacilityRepository;
import pl.edu.pw.sportyapp.shared.exception.DataDuplicationException;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.user.dao.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacilityService {

    private SequenceGeneratorService sequenceGenerator;
    private FacilityRepository facilityRepository;

    @Autowired
    public FacilityService(FacilityRepository fr, SequenceGeneratorService sgs) {
        this.facilityRepository = fr;
        this.sequenceGenerator = sgs;
    }

    public List<Facility> getAll() {
        return facilityRepository.findAll();
    }

    public Facility getOne(long id) {
        return facilityRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Long addFacility(Facility newFacility) {
        if (facilityRepository.existsByLatitudeAndLongitude(newFacility.latitude, newFacility.longitude)) {
            throw new DataDuplicationException();
        }
        newFacility.setId(sequenceGenerator.generateSequence(Facility.DBSEQUENCE_NAME));
        if (newFacility.getDisciplines() == null) {
            newFacility.setDisciplines(Lists.newArrayList());
        }
        return facilityRepository.insert(newFacility).getId();
    }

    public void updateFacility(long id, Facility facility) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new PermissionDeniedDataAccessException("User not permitted to access this area", null);
        }
        if (!facilityRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }
        if (facility.getId() != id) {
            throw new IllegalArgumentException();
        }
        facilityRepository.save(facility);
    }

    public void deleteFacility(long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new PermissionDeniedDataAccessException("User not permitted to access this area", null);
        }
        if (!facilityRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }
        facilityRepository.deleteById(id);
    }

    public List<Facility> findByProximity(double lat, double lon, long distance) {
        return facilityRepository.findAll()
                .stream()
                .filter(facility -> {
                    long facilityDistance = (long) (108*(Math.pow((facility.latitude - lat), 2) + Math.pow((facility.longitude - lon), 2)));
                    return distance < facilityDistance;
                })
                .sorted((f1, f2) -> {
                    double f1Dist = Math.pow((f1.latitude - lat), 2) + Math.pow((f1.longitude - lon), 2);
                    double f2Dist = Math.pow((f2.latitude - lat), 2) + Math.pow((f2.longitude - lon), 2);
                    return Double.compare(f1Dist, f2Dist);
                })
                .collect(Collectors.toList());
    }

    public List<Facility> findByProximity(double lat, double lon) {
        return facilityRepository.findAll()
                .stream()
                .sorted((f1, f2) -> {
                    double f1Dist = Math.pow((f1.latitude - lat), 2) + Math.pow((f1.longitude - lon), 2);
                    double f2Dist = Math.pow((f2.latitude - lat), 2) + Math.pow((f2.longitude - lon), 2);
                    return Double.compare(f1Dist, f2Dist);
                })
                .collect(Collectors.toList());
    }
}
