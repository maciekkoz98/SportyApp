package pl.edu.pw.sportyapp.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }

//        return new UserDetailsImpl(user);
//        return org.springframework.security.core.userdetails.User.withUsername(username)
//                .password(user.getPasswordHash())
//                .roles(user.getRole().toString())
//                .build();
        return user;
    }
}
