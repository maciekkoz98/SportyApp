package pl.edu.pw.sportyapp.shared.utils;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.sport.dao.Sport;
import pl.edu.pw.sportyapp.sport.repository.SportRepository;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.repository.UserRepository;
import pl.edu.pw.sportyapp.user.security.AppUserRole;

@Component
public class DbFiller {
    private UserRepository userRepository;
    private SportRepository sportRepository;
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DbFiller(UserRepository ur, SportRepository sr, SequenceGeneratorService sgs) {
        this.userRepository = ur;
        this.sportRepository = sr;
        this.sequenceGeneratorService = sgs;
    }

    public void initDb() {
        addAdmin();
        addBasicSports();
    }

    private void addAdmin() {
        if (!userRepository.existsById(1L)) {
            User admin = User.builder()
                    .id(sequenceGeneratorService.generateSequence(User.DBSEQUENCE_NAME))
                    .username("admin")
                    .fullname("admin")
                    .email("admin@mail.com")
                    .passwordHash(passwordEncoder.encode("admin"))
                    .role(AppUserRole.ADMIN)
                    .isAccountNonExpired(true).isCredentialsNonExpired(true).isAccountNonLocked(true).isEnabled(true)
                    .ratings(Lists.newArrayList()).ratingsNumber(Lists.newArrayList())
                    .gamesParticipatedIds(Lists.newArrayList()).friendsIds(Lists.newArrayList())
                    .build();
            userRepository.insert(admin);
        }
    }

    private void addBasicSports() {
        if (!sportRepository.existsById(1L)) {
            Sport sport = Sport.builder()
                    .id(sequenceGeneratorService.generateSequence(Sport.DBSEQUENCE_NAME))
                    .namePL("Piłka nożna")
                    .nameEN("Football")
                    .synonyms(Lists.newArrayList())
                    .build();
            sportRepository.insert(sport);
        }
        if (!sportRepository.existsById(2L)) {
            Sport sport = Sport.builder()
                    .id(sequenceGeneratorService.generateSequence(Sport.DBSEQUENCE_NAME))
                    .namePL("Koszykówka")
                    .nameEN("Basketball")
                    .synonyms(Lists.newArrayList())
                    .build();
            sportRepository.insert(sport);
        }
        if (!sportRepository.existsById(3L)) {
            Sport sport = Sport.builder()
                    .id(sequenceGeneratorService.generateSequence(Sport.DBSEQUENCE_NAME))
                    .namePL("Siatkówka")
                    .nameEN("Volleyball")
                    .synonyms(Lists.newArrayList())
                    .build();
            sportRepository.insert(sport);
        }
        if (!sportRepository.existsById(4L)) {
            Sport sport = Sport.builder()
                    .id(sequenceGeneratorService.generateSequence(Sport.DBSEQUENCE_NAME))
                    .namePL("Piłka ręczna")
                    .nameEN("Handball")
                    .synonyms(Lists.newArrayList())
                    .build();
            sportRepository.insert(sport);
        }
    }
}
