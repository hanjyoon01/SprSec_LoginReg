package com.example.seven.api;

import com.example.seven.domain.user.service.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 메인 홈 화면
    @GetMapping("/")
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        System.out.println(username);
        System.out.println(role);

        if (userDetails != null) {
            // 화면(Mustache)에서 써먹을 수 있게 "username"이라는 이름표로 데이터를 넘겨줍니다.
            model.addAttribute("username", userDetails.getUsername());
        }

        return "index";
    }

    // 총관리자 대시보드
    @GetMapping("/admin")
    public String admin() { return "admin"; }
}