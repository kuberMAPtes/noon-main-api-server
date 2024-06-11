package com.kube.noon.common.security.userdetails;

import com.kube.noon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 유저 인증 정보를 가져오는 UserDetailsService 구현체
 *
 * @author PGD
 * @see UserDetailsService
 * @see com.kube.noon.member.domain.Member
 */
@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository
                .findMemberById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Member ID not found: " + username));
    }
}
