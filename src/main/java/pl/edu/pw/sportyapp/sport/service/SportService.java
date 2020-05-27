package pl.edu.pw.sportyapp.sport.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.sport.dao.Sport;
import pl.edu.pw.sportyapp.sport.repository.SportRepository;
import pl.edu.pw.sportyapp.user.dao.User;

import java.util.List;

@Service
public class SportService {

    private SequenceGeneratorService sequenceGenerator;
    private SportRepository sportRepository;

    @Autowired
    public SportService(SportRepository sr, SequenceGeneratorService sgs) {
        this.sportRepository = sr;
        this.sequenceGenerator = sgs;
    }

    public List<Sport> getAll() {
        return sportRepository.findAll();
    }

    public Sport getOne(long id) {
        return sportRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Long addSport(Sport newSport) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new PermissionDeniedDataAccessException("User not permitted to access this area", null);
        }
        newSport.setId(sequenceGenerator.generateSequence(Sport.DBSEQUENCE_NAME));
        if (newSport.getSynonyms() == null) {
            newSport.setSynonyms(Lists.newArrayList());
        }
        return sportRepository.insert(newSport).getId();
    }

    public void updateSport(long id, Sport sport) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new PermissionDeniedDataAccessException("User not permitted to access this area", null);
        }
        if (!sportRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }
        if (sport.getId() != id) {
            throw new IllegalArgumentException();
        }

        sportRepository.save(sport);
    }

    public void deleteSport(long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new PermissionDeniedDataAccessException("User not permitted to access this area", null);
        }
        if (!sportRepository.existsById(id)) {
            throw new EntityNotFoundException();
        }
        sportRepository.deleteById(id);
    }
}
