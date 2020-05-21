package pl.edu.pw.sportyapp.user.dao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.pw.sportyapp.user.security.AppUserRole;;

import java.util.Collection;

@Builder
@Getter
@Setter
@Document
public class User implements UserDetails {

    @Id
    private long id;

    @Transient
    public static final String DBSEQUENCE_NAME = "userSequenceID";

    private String username;
    private String passwordHash;
    private AppUserRole role;

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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
