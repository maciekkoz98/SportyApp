package pl.edu.pw.sportyapp.shared.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.sportyapp.user.security.AppUserRole;
import pl.edu.pw.sportyapp.user.security.UserDetailsServiceImpl;

@Configuration
@EnableConfigurationProperties
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfiguration(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/game/**").hasAnyRole(AppUserRole.ADMIN.name(), AppUserRole.USER.name())
                .antMatchers("/search/**").hasAnyRole(AppUserRole.ADMIN.name(), AppUserRole.USER.name())
                .antMatchers("/sport/**").hasAnyRole(AppUserRole.ADMIN.name(), AppUserRole.USER.name())
                .antMatchers("/facility/**").hasAnyRole(AppUserRole.ADMIN.name(), AppUserRole.USER.name())
                .antMatchers(HttpMethod.POST, "/user").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and().sessionManagement().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
