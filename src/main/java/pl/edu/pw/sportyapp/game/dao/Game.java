package pl.edu.pw.sportyapp.game.dao;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    public long id;

    @Transient
    public static final String DBSEQUENCE_NAME = "gameSequenceID";

    public long date;
    public long owner;
    public long[] players;

    public boolean isPublic;

    @NonNull
    @NotNull
    @Positive
    public long facility;
}
