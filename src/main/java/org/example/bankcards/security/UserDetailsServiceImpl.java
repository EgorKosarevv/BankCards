package org.example.bankcards.security;

import lombok.RequiredArgsConstructor;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("The user was not found"));

        if (!user.isEnabled()) {
            throw new DisabledException("The user is blocked");
        }

        return new UserDetailsImpl(user);
    }

}