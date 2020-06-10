package pl.edu.pw.sportyapp.facility.dao;

import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@QueryEntity
@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class Facility {

    @Id
    public long id;

    @Transient
    public static final String DBSEQUENCE_NAME = "facilitySequenceID";

    @Size(min = 3, max = 200)
    public String address;

    @NotNull
    public double latitude;
    @NotNull
    public double longitude;

    @NotNull
    public boolean isSportsHall;

    public List<Long> disciplines;
}
