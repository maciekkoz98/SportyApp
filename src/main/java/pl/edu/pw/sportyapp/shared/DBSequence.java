package pl.edu.pw.sportyapp.shared;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "db_sequences")

public class DBSequence {

    @Id
    private String name;

    @Getter
    private Long value;
}
