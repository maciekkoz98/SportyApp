package pl.edu.pw.sportyapp.facility.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.facility.dao.Facility;
import pl.edu.pw.sportyapp.facility.repository.FacilityRepository;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.game.service.GameService;
import pl.edu.pw.sportyapp.shared.exception.DataDuplicationException;
import pl.edu.pw.sportyapp.shared.exception.EntityNotFoundException;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.user.dao.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class FacilityService {

    private SequenceGeneratorService sequenceGenerator;
    private FacilityRepository facilityRepository;
    private GameService gameService;

    @Autowired
    public FacilityService(FacilityRepository fr, SequenceGeneratorService sgs) {
        this.facilityRepository = fr;
        this.sequenceGenerator = sgs;
    }

    public List<Facility> getAll() {
        return facilityRepository.findAll();
    }

    public Facility getOne(long id) {
        Facility facility = facilityRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        boolean facilityChanged = false;
        List<Long> gamesToRemoveFromFacility = new ArrayList<>();
        for (Long gameId : facility.getEvents()) {
            Game game = gameService.findById(gameId);
            if (game.getDate() + game.getDuration() < System.currentTimeMillis()) {
                facilityChanged = true;
                gamesToRemoveFromFacility.add(game.getId());
            }
        }
        if (facilityChanged) {
            facility.getEvents().removeAll(gamesToRemoveFromFacility);
            facilityRepository.save(facility);
        }
        return facility;
    }

    public Long addFacility(Facility newFacility) {
        if (facilityRepository.existsByLatitudeAndLongitude(newFacility.latitude, newFacility.longitude)) {
            throw new DataDuplicationException();
        }
        newFacility.setId(sequenceGenerator.generateSequence(Facility.DBSEQUENCE_NAME));
        if (newFacility.getDisciplines() == null) {
            newFacility.setDisciplines(Lists.newArrayList());
        }
        if (newFacility.getEvents() == null) {
            newFacility.setEvents(Lists.newArrayList());
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

    public boolean addGameToFacility(Long facilityId, Long gameId) {
        Facility facility = facilityRepository.findById(facilityId).orElseThrow(EntityNotFoundException::new);
        facility.getEvents().add(gameId);
        facilityRepository.save(facility);
        return true;
    }

    public boolean deleteGameFromFacility(Long facilityId, Long gameId) {
        Facility facility = facilityRepository.findById(facilityId).orElseThrow(EntityNotFoundException::new);
        facility.getEvents().remove(gameId);
        facilityRepository.save(facility);
        return true;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }
}
