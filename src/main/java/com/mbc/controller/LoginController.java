package com.mbc.controller;

import com.mbc.dto.MemberFormDto;
import com.mbc.entity.Member;
import com.mbc.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
public class LoginController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/signup") // 회원가입 페이지
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/signup") // 회원가입 처리
    public String memberForm(@Valid MemberFormDto memberFormdto,
                             BindingResult bindingResult, Model model, @RequestParam("profileImg") MultipartFile profileImgFile) throws Exception
    {
        if(bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        try{
            memberService.saveMember(memberFormdto, profileImgFile);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login") // 로그인 페이지
    public String login(HttpServletRequest request) {
        // 이미 로그인한 경우, 로그인 페이지로 접근하지 못하게 막고 홈 페이지로 리다이렉트
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            return "redirect:/"; // 이미 로그인한 경우 홈 페이지로 리다이렉트
        }

        return "/member/memberLoginForm"; // 로그인 페이지로 이동
    }

    @GetMapping(value = "/login/error") // 로그인 실패 시 처리
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해 주세요");
        return "member/memberLoginForm";
    }

    @PostMapping("/get-user-name")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserName(@RequestBody Map<String, String> request) {
        log.info("컨트롤러 진입");
        String phoneNumber = request.get("phone");

        log.info("번호확인:"+ phoneNumber);

        // 서비스 레이어 호출하여 사용자 이름 조회
        List<String> userNames = memberService.findUsernameByPhone(phoneNumber);

        // 결과 반환
        Map<String, Object> response = new HashMap<>();
        if (userNames != null && !userNames.isEmpty()) {
            response.put("names", userNames);
        } else {
            response.put("names", new ArrayList<>()); // 사용자 없음
            response.put("message", "해당 전화번호로 등록된 사용자가 없습니다.");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/find/id")
    public String findIdForm() {
        return "member/idFind";  // templates/member/idFind.html을 찾음
    }

    // 이메일로 아이디 찾기
    @PostMapping("/find/id")
    public String findId(@RequestParam("email") String email, Model model) {
        List<String> userIds = memberService.findUsernameByEmail(email);
        if (userIds == null || userIds.isEmpty()) {
            model.addAttribute("error", "이메일에 해당하는 아이디가 없습니다.");
            return "member/idFind";
        }
        model.addAttribute("userIds", userIds);  // 아이디 목록을 모델에 추가
        return "member/idFind";  // 동일 페이지로 다시 돌아가도록
    }

    @GetMapping("/find/password")
    public String findPasswordForm() {
        return "member/passwordFind"; // passwordFind.html로 이동
    }

}
