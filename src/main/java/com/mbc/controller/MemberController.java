package com.mbc.controller;

import com.mbc.dto.ItemSearchDto;
import com.mbc.dto.MemberFormDto;
import com.mbc.dto.OrderHistDto;
import com.mbc.dto.ReviewFormDto;
import com.mbc.entity.Item;
import com.mbc.entity.Member;
import com.mbc.entity.Review;
import com.mbc.security.PrincipalDetails;
import com.mbc.service.ItemService;
import com.mbc.service.MemberImgService;
import com.mbc.service.MemberService;
import com.mbc.service.OrderService;
import com.mbc.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberImgService memberImgService;
    private final MemberService memberService;
    private final ItemService itemService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    @GetMapping(value = "/member/mystore")
    public String Mystore(ItemSearchDto itemSearchDto,
                          Model model, Principal principal) {

        MemberFormDto memberFormDto = memberService.getMemberByUserName(principal.getName());
        // 프로필 이미지 가져오기
        String imgUrl = memberFormDto.getViewPofile();
        model.addAttribute("imgUrl", imgUrl);
        // 상점 정보 - 등록 날짜
        LocalDateTime startdate = memberFormDto.getRegTime();
        LocalDateTime enddate = LocalDateTime.now();
        Duration duration = Duration.between(startdate, enddate);
        Long openDay = duration.getSeconds() / 60 / 60 / 24;
        model.addAttribute("openDay", openDay);

        // 상품 등록 총 갯수
        Long totalItem = itemService.getItemCount();
        model.addAttribute("totalItem", totalItem);

        // 판매내역
        String userEmail = principal.getName();
        Pageable pageable = PageRequest.of(0, 10000);
        Page<Item> items = itemService.getUserItems(userEmail, itemSearchDto, pageable);
        model.addAttribute("userName", userEmail);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);

        // 구매내역
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(userEmail, pageable);
        model.addAttribute("orders", orderHistDtoList);

        // 판매자가 자신이 판매한 상품에 대한 리뷰를 확인할 수 있도록 수정
        List<Review> reviews = reviewService.getReviewsWithImagesByItemOwner(userEmail); // 판매자의 상품에 대한 리뷰 조회
        model.addAttribute("reviews", reviews);

        return "member/mystore";
    }


    @GetMapping(value = "/store/{memberId}")
    public String store(@PathVariable Long memberId, ItemSearchDto itemSearchDto,
                        Model model, RedirectAttributes rttr) {
        MemberFormDto memberFormDto;
        String userEmail;
        Long openDay;

        try {
            memberFormDto = memberService.getStoreMember(memberId);
            // 프로필 이미지 가져오기
            String imgUrl = memberFormDto.getViewPofile();
            model.addAttribute("imgUrl", imgUrl);

            // 상점 이름 가져오기
            userEmail = memberFormDto.getName();
            // 상점 정보 - 등록 날짜
            LocalDateTime startdate = memberFormDto.getRegTime();
            LocalDateTime enddate = LocalDateTime.now();
            Duration duration = Duration.between(startdate, enddate);
            openDay = duration.getSeconds() / 60 / 60 / 24;
            model.addAttribute("openDay", openDay);

            // 상품 등록 총 갯수
            Long totalItem = itemService.getItemCount();
            model.addAttribute("totalItem", totalItem);

        } catch (IllegalArgumentException e) {
            // 예외 발생 시 메시지 전달
            rttr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }

        // 판매내역
        Pageable pageable = PageRequest.of(0, 10000);
        Page<Item> items = itemService.getUserItems(userEmail, itemSearchDto, pageable);
        model.addAttribute("userName", userEmail);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);

        // 구매내역
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(userEmail, pageable);
        model.addAttribute("orders", orderHistDtoList);

        // 판매자가 자신이 판매한 상품에 대한 리뷰를 확인할 수 있도록 수정
        List<Review> reviews = reviewService.getReviewsWithImagesByItemOwner(userEmail); // 판매자의 상품에 대한 리뷰 조회
        model.addAttribute("reviews", reviews);

        return "member/mystore";
    }

    // 회원 정보 수정 페이지
    @GetMapping(value = "/member/edit")
    public String editForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // 현재 로그인한 사용자 정보로 회원 정보 가져오기
        String username = userDetails.getUsername();
        MemberFormDto memberFormDto = memberService.getMemberInfo(username);

        // OAuth2 사용자 여부 확인
        boolean isOAuth2User = false;
        if (userDetails instanceof PrincipalDetails) {
            isOAuth2User = ((PrincipalDetails) userDetails).isOAuth2User();
        }

        model.addAttribute("memberFormDto", memberFormDto);
        model.addAttribute("isOAuth2User", isOAuth2User);
        return "member/edit";
    }

    // 회원 정보 수정 처리
    @PostMapping(value = "/member/edit")
    public String editMember(@Valid MemberFormDto memberFormDto,
                             BindingResult bindingResult,
                             @RequestParam("profileImg") MultipartFile profileImgFile,
                             Model model) {
        
        log.info("회원 정보 수정 처리 컨트롤러 진입");

        if (bindingResult.hasErrors()) {
            return "member/edit";
        }

        try {
            memberService.updateMember(memberFormDto, profileImgFile);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/edit";
        }
        log.info("회원 정보 수정 처리 컨트롤러 종료");
        return "redirect:/"; // 수정 완료 후 리다이렉트
    }

    // 회원 탈퇴 처리
    @PostMapping("/member/delete")
    public String deleteMember(@AuthenticationPrincipal UserDetails userDetails) {
        memberService.deleteMember(userDetails.getUsername()); // 사용자 이름 또는 ID를 통해 삭제
        return "redirect:/member/logout"; // 탈퇴 후 로그아웃
    }


}
