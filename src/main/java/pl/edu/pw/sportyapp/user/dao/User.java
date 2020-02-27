package pl.edu.pw.sportyapp.user.dao;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    public long id;

    @Transient
    public static final String DBSEQUENCE_NAME = "userSequenceID";

    @NotNull
    public String username;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    private String email;

    @NotNull
    private Long averageGrade;

    @NotNull
    private List<Long> gamesParticipatedIds;

    @NotNull
    private List<Long> friendsIds;
}
