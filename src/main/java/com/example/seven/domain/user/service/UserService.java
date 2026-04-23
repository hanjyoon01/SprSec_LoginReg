package com.example.seven.domain.user.service;

import com.example.seven.domain.user.dto.PasswordChangeDTO;
import com.example.seven.domain.user.dto.UserRequestDTO;
import com.example.seven.domain.user.entity.RoleEntity;
import com.example.seven.domain.user.entity.UserEntity;
import com.example.seven.domain.user.repository.RoleRepository;
import com.example.seven.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
// UserDetailsService 인터페이스를 서비스단에서 구현해야 함 => 규약 준수
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 메소드
    public void join(UserRequestDTO dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();
        String email = dto.getEmail();

        //**** 디테일 추가 => 이미 동일한 username이 있는지 확인, 성공 여부에 따른 return값 추가 ****

        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setPassword(passwordEncoder.encode(password));
        entity.setEmail(email);
//        RoleEntity userRole = roleRepository.findByName("USER")
//                .orElseThrow(() -> new RuntimeException("DB에 ROLE_USER 권한 셋팅이 되어있지 않습니다."));
        String roleName = dto.isAdmin() ? "ADMIN" : "USER";
        RoleEntity userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("해당 권한을 찾을 수 없습니다."));
        entity.setRole(userRole);

        userRepository.save(entity);
    }

    // 비밀번호 변경
    public void changePassword(String username, PasswordChangeDTO dto) {
        UserEntity entity = userRepository.findByUsername(username).orElseThrow();

        // 현재 비밀번호 불일치
        if (!passwordEncoder.matches(dto.getCurrentPassword(), entity.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        // 새 비밀번호로 변경
        entity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(entity);
    }

    // 회원탈퇴 메소드
    public void leave(String username) {
        UserEntity entity = userRepository.findByUsername(username).orElseThrow();
        userRepository.delete(entity);
    }

    // AuthenticationProvider가 불러서 사용할 메서드
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // DB로부터 특정 유저를 찾아서 응답
        UserEntity entity = userRepository.findByUsername(username).orElseThrow();

        return new CustomUserDetails(entity);
    }

    public List<UserEntity> findAllUsersByRole(String roleName) {
        return userRepository.findAllByRoleName(roleName);
    }
}
