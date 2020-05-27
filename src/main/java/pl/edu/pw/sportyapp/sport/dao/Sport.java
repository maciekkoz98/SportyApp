package pl.edu.pw.sportyapp.sport.dao;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class Sport {

    @Id
    public long id;

    @Transient
    public static final String DBSEQUENCE_NAME = "sportSequenceID";

    @NotBlank
    public String namePL;
    @NotBlank
    public String nameEN;

    public List<String> synonyms;
}
