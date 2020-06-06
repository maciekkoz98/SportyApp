package pl.edu.pw.sportyapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.edu.pw.sportyapp.shared.utils.DbFiller;

@SpringBootApplication
public class SportyappApplication implements CommandLineRunner {
    @Autowired
    private DbFiller dbFiller;

    public static void main(String[] args) {
        SpringApplication.run(SportyappApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        dbFiller.initDb();
    }
}
