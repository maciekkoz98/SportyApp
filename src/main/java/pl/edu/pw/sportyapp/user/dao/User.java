package pl.edu.pw.sportyapp.user.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.pw.sportyapp.user.security.AppUserRole;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@Valid
public class User implements UserDetails {

    @Id
    private long id;

    @Transient
    public static final String DBSEQUENCE_NAME = "userSequenceID";

    @Size(min = 3, max = 200)
    private String username;

    @Size(min = 3, max = 200)
    private String fullname;

    private String passwordHash;

    private AppUserRole role;
    private final boolean isAccountNonExpired;
    private final boolean isAccountNonLocked;
    private final boolean isCredentialsNonExpired;
    private final boolean isEnabled;

    @Email
    private String email;

    private List<Float> ratings;
    private List<Integer> ratingsNumber;

    private List<Long> gamesParticipatedIds;

    private List<Long> friendsIds;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getGrantedAuthorities();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
