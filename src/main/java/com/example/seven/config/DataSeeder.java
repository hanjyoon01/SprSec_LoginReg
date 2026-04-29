package com.example.seven.config;

import com.example.seven.domain.user.entity.RoleEntity;
import com.example.seven.domain.user.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // 스프링이 관리하는 부품으로 등록
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // 1. ADMIN 권한 DB에 저장
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            RoleEntity adminRole = new RoleEntity();
            adminRole.setName("ADMIN");
            adminRole.setKname("관리자");
            roleRepository.save(adminRole);
        }

        // 2. USER 권한 DB에 저장
        if (roleRepository.findByName("USER").isEmpty()) {
            RoleEntity userRole = new RoleEntity();
            userRole.setName("USER");
            userRole.setKname("사용자");
            roleRepository.save(userRole);
        }
    }
}