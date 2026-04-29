package com.example.seven.domain.user.service;

import com.example.seven.domain.user.dto.PasswordChangeDTO;
import com.example.seven.domain.user.dto.UserRequestDTO;
import com.example.seven.domain.user.dto.UserUpdateDTO;
import com.example.seven.domain.user.entity.Role;
import com.example.seven.domain.user.entity.MemberEntity;
import com.example.seven.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

// UserDetailsService 인터페이스를 서비스단에서 구현해야 함 => 규약 준수
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 메소드
    public void join(UserRequestDTO dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();
        String email = dto.getEmail();
        String phoneNumber = dto.getPhoneNumber();
        String address = dto.getAddress();

        //**** 디테일 추가 => 이미 동일한 username이 있는지 확인, 성공 여부에 따른 return값 추가 ****

        MemberEntity entity = new MemberEntity();
        entity.setUsername(username);
        entity.setPassword(passwordEncoder.encode(password));
        entity.setEmail(email);
        entity.setPhoneNumber(phoneNumber);
        entity.setAddress(address);

//        RoleEntity userRole = roleRepository.findByName("USER")
//                .orElseThrow(() -> new RuntimeException("DB에 ROLE_USER 권한 셋팅이 되어있지 않습니다."));
        if(dto.isAdmin())
            entity.setRole(Role.ROLE_GENERAL_MANAGER);
        else
            entity.setRole(Role.ROLE_USER);

        memberRepository.save(entity);
    }

    // 회원탈퇴 메소드
    public void leave(String username) {
        MemberEntity entity = memberRepository.findByUsername(username).orElseThrow();
        memberRepository.delete(entity);
    }

    // 비밀번호 변경
    public void changePassword(String username, PasswordChangeDTO dto) {
        MemberEntity entity = memberRepository.findByUsername(username).orElseThrow();

        // 현재 비밀번호 불일치
        if (!passwordEncoder.matches(dto.getCurrentPassword(), entity.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        // 새 비밀번호로 변경
        entity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        memberRepository.save(entity);
    }

    // 사용자 정보 변경
    public void updateUserInfo(String username, UserUpdateDTO dto) {
        MemberEntity entity = memberRepository.findByUsername(username).orElseThrow();

        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAddress(dto.getAddress());

        memberRepository.save(entity);
        refreshAuthentication(entity);
    }

    private void refreshAuthentication(MemberEntity updatedEntity) {
        // 새로운 UserDetails 객체 생성
        MemberDetails newUserDetails = new MemberDetails(updatedEntity);

        // 새로운 인증 객체 생성 (기존의 권한 정보 유지)
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                null, // 비밀번호는 보안상 null 처리하거나 기존 것 사용
                newUserDetails.getAuthorities()
        );

        // 스프링 시큐리티 컨텍스트에 덮어쓰기
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    // AuthenticationProvider가 불러서 사용할 메서드
    @Override
    public MemberDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // DB로부터 특정 유저를 찾아서 응답
        MemberEntity entity = memberRepository.findByUsername(username).orElseThrow();

        return new MemberDetails(entity);
    }

    public List<MemberEntity> findAllUsersByRole(Role role) {
        return memberRepository.findByRole(role);
    }
}
