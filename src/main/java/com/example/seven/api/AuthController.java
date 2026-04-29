package com.example.seven.api;

import com.example.seven.domain.user.dto.UserRequestDTO;
import com.example.seven.domain.user.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final MemberService memberService;
    public AuthController(MemberService memberService) { this.memberService = memberService; }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @PostMapping("/join")
    public String join(UserRequestDTO dto) {
        memberService.join(dto);
        return "redirect:/";
    }

    // 회원가입 페이지 제공 메소드
    @GetMapping("/join")
    public String joinPage() {
        return "join";
    }
}
