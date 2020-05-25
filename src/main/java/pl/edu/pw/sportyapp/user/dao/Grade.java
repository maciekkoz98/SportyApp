package pl.edu.pw.sportyapp.user.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Valid
public class Grade {
    @Positive
    public int skill;
    @Positive
    public int punctuality;
    @Positive
    public int kindness;
}
