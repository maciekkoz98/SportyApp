package pl.edu.pw.sportyapp.game.dao;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    @NonNull
    public long id;

    @Transient
    public static final String DBSEQUENCE_NAME = "gameSequenceID";

    public String name;
    public Date date;
    public long[] players;

    @NonNull
    public long facility;
}
