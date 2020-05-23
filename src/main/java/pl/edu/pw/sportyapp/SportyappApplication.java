package pl.edu.pw.sportyapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.sportyapp.shared.sequence.SequenceGeneratorService;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.repository.UserRepository;
import pl.edu.pw.sportyapp.user.security.AppUserRole;

@SpringBootApplication
public class SportyappApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SportyappApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsById(1L)) {
            User admin = User.builder()
                    .id(sequenceGeneratorService.generateSequence(User.DBSEQUENCE_NAME))
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("admin"))
                    .role(AppUserRole.ADMIN)
                    .isAccountNonExpired(true).isCredentialsNonExpired(true).isAccountNonLocked(true).isEnabled(true)
                    .build();
            userRepository.insert(admin);
        }

    }
}
