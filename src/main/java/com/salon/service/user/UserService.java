package com.salon.service.user;

import com.salon.config.CustomUserDetails;
import com.salon.entity.Member;
import com.salon.repository.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final MemberRepo memberRepo;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member= memberRepo.findByLoginId(loginId);

        if(member == null) {
            throw  new UsernameNotFoundException(loginId);
        }

        return new CustomUserDetails(member);
    }
}
