package com.example.seven.api;

import com.example.seven.domain.user.dto.PasswordChangeDTO;
import com.example.seven.domain.user.dto.UserUpdateDTO;
import com.example.seven.domain.user.entity.UserEntity;
import com.example.seven.domain.user.service.CustomUserDetails;
import com.example.seven.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String userPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        // CustomUserDetails에 getEntity()와 각 필드 getter가 이미 정의되어 있습니다.
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("email", userDetails.getEmail());
        model.addAttribute("phoneNumber", userDetails.getPhoneNumber());
        model.addAttribute("address", userDetails.getAddress());
        model.addAttribute("role", userDetails.getRole());

        // 2. 권한 확인도 더 직관적으로 처리 가능합니다.
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            // 관리자라면 'USER' 권한을 가진 회원들 목록을 가져옴
            List<UserEntity> userList = userService.findAllUsersByRole("USER");
            model.addAttribute("userList", userList);
            model.addAttribute("isAdmin", true); // 화면 제어용 플래그
            System.out.println(userList);
        }

        return "user";
    }

    // 사용자 정보 변경
    @PostMapping("/user/update")
    public String updateUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 UserUpdateDTO dto) {
        try {
            // 로그인한 사용자의 정보 변경
            userService.updateUserInfo(userDetails.getUsername(), dto);

            // 성공하면 성공했다는 표시(?success=true)를 달고 내 정보 페이지로 튕겨냄
            return "redirect:/user?success=true";

        } catch (IllegalArgumentException e) {
            // 실패(정보 없음 등)하면 에러 표시(?error=true)를 달고 튕겨냄
            return "redirect:/user?error=true";
        }
    }

    // 비밀번호 변경
    @PostMapping("/user/password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 PasswordChangeDTO dto) {
        try {
            // 로그인한 사람의 아이디와 입력받은 비밀번호 데이터를 서비스로 넘김
            userService.changePassword(userDetails.getUsername(), dto);

            // 성공하면 성공했다는 표시(?success=true)를 달고 내 정보 페이지로 튕겨냄
            return "redirect:/user?success=true";

        } catch (IllegalArgumentException e) {
            // 실패(비밀번호 틀림 등)하면 에러 표시(?error=true)를 달고 튕겨냄
            return "redirect:/user?error=true";
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')") // ADMIN 권한이 없으면 이 메서드 실행 자체가 안 됨
    @PostMapping("/user/admin/leave")
    public String adminWithdrawUser(@RequestParam("username") String username) {

        // 서비스의 기존 삭제 로직을 그대로 활용합니다.
        // (단, 관리자 본인이 탈퇴되는 것이 아니므로 세션 파기 로직은 필요 없습니다.)
        userService.leave(username);

        // 삭제 후 다시 목록 페이지로 리다이렉트
        return "redirect:/user?deleted=true";
    }

    @PostMapping("/user/leave")
    public String leave(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            // 서비스의 탈퇴 로직 호출
            userService.leave(auth.getName());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        // 탈퇴 후에는 세션 정보가 사라져야 하므로 로그아웃 주소로 보냅니다.
        return "redirect:/";
    }
}
