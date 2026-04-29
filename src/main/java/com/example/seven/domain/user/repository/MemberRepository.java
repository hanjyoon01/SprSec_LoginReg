package com.example.seven.domain.user.repository;

import com.example.seven.domain.user.entity.Role;
import com.example.seven.domain.user.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// UserEntity, id 값 데이터 타입 => long
@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByUsername(String username);
    List<MemberEntity> findByRole(Role role);
}
