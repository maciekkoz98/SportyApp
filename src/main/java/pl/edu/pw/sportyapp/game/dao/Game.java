package pl.edu.pw.sportyapp.game.dao;

import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@QueryEntity
@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class Game {

    @Id
    public long id;

    @Transient
    public static final String DBSEQUENCE_NAME = "gameSequenceID";

    @NotBlank
    public String name;
    @Positive
    public long date;
    @Positive
    public long duration;

    public long owner;
    public List<Long> players;

    public boolean isPublic;

    @Positive
    public long facility;

    @Positive
    public long sport;
}
