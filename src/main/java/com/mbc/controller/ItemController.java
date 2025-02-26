package com.mbc.controller;

import com.mbc.constant.ItemSellStatus;
import com.mbc.dto.ItemFormDto;
import com.mbc.dto.ItemSearchDto;
import com.mbc.dto.MemberFormDto;
import com.mbc.entity.Category;
import com.mbc.entity.Item;
import com.mbc.entity.Member;
import com.mbc.repository.MemberRepository;
import com.mbc.service.CategoryService;
import com.mbc.service.ItemService;
import com.mbc.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@Log4j2
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public ItemController(ItemService itemService, CategoryService categoryService, MemberService memberService, MemberRepository memberRepository) {
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    // 공통 URL 액션 로직을 처리하는 메소드
    private String getActionUrl(Principal principal) {
        Authentication authentication = (Authentication) principal;
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin ? "/admin/items/" : "/member/items/";
    }

    // 카테고리 설정을 위한 헬퍼 메소드
    private void setCategoryAttributes(Model model) {
        List<Category> parentCategories = categoryService.getParentCategories();
        model.addAttribute("parentCategories", parentCategories);
    }

    @GetMapping(value = "/member/item/new")
    public String itemForm(Model model) {
        setCategoryAttributes(model);
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @GetMapping(value = "/member/item/subCategories")
    @ResponseBody
    public List<Category> getSubCategories(@RequestParam Long parentId) {
        return categoryService.getSubCategories(parentId);
    }

    @PostMapping(value = "/member/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Principal principal) {

        setCategoryAttributes(model);

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "필수 입력값을 입력해주세요!");
            return "item/itemForm";
        }

        try {
            // 로그인한 사용자의 정보를 가져오기
            String userName = principal.getName(); // Principal에서 로그인한 사용자 이름을 가져옵니다.

            // MemberService를 통해 MemberFormDto로 회원 정보를 가져옵니다.
            MemberFormDto memberFormDto = memberService.getMemberByUserName(userName);

            // itemFormDto에 memberId 설정
            itemFormDto.setMemberId(memberFormDto.getMemberId());

            // 아이템 저장
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/member/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {

        setCategoryAttributes(model);

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            System.out.println("ItemFormDto 상태: " + itemFormDto.toString());
            model.addAttribute("itemFormDto", itemFormDto);

        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    @PostMapping(value = "/member/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {

        setCategoryAttributes(model);

        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty()) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String adminItemManage(ItemSearchDto itemSearchDto,
                                  @PathVariable("page") Optional<Integer> page,
                                  Model model, Principal principal) {

        String actionUrl = getActionUrl(principal);
        log.info("actionUrl is {}", actionUrl);
        model.addAttribute("actionUrl", actionUrl);


        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "item/itemMng";
    }



    @GetMapping(value = {"/member/items", "/member/items/{page}"})
    public String memberItemManage(ItemSearchDto itemSearchDto,
                                   @PathVariable("page") Optional<Integer> page,
                                   Model model, Principal principal) {



        String actionUrl = getActionUrl(principal);
        log.info("actionUrl is {}", actionUrl);
        model.addAttribute("actionUrl", actionUrl);

        String userEmail = principal.getName();

        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<Item> items = itemService.getUserItems(userEmail, itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId, Principal principal) {

        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);

        // 모델에 아이템과 카테고리 경로를 추가합니다.
        model.addAttribute("item", itemFormDto);

        boolean isDeletable = false; // 기본적으로 삭제 버튼은 비활성화

        if (principal != null) {
            // 로그인한 사용자 정보를 Authentication 객체로 가져오기
            Authentication authentication = (Authentication) principal;

            // 관리자 권한 체크
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

            // 상품을 등록한 사용자 이메일 가져오기
            String userEmail = authentication.getName(); // principal.getName()으로 로그인한 이메일을 가져옴

            // 상품 등록자와 로그인한 사용자가 일치하는지 확인
            boolean isOwner = userEmail.equals(itemFormDto.getUserName());

            // 관리자나 상품 소유자만 삭제 버튼을 표시
            isDeletable = isAdmin || isOwner;
        }

        MemberFormDto memberFormDto = memberService.getMemberByUserName(itemFormDto.getUserName());
        if (memberFormDto != null) {
            // memberId를 model에 추가하여 화면에서 사용하도록 전달
            model.addAttribute("memberId", memberFormDto.getMemberId());
        }

        model.addAttribute("isDeletable", isDeletable);

        return "item/itemDtl";
    }

    @PostMapping("/item/delete")
    public String deleteItem(@RequestParam("itemId") Long itemId, RedirectAttributes redirectAttributes) {
        try {
            itemService.deleteItem(itemId);  // 아이템 삭제 처리
            redirectAttributes.addFlashAttribute("successMessage", "아이템이 성공적으로 삭제되었습니다.");  // 성공 메시지 추가
        } catch (Exception e) {
            System.out.println("삭제 실패: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "삭제에 실패하였습니다. 다른 사용자에 의해 장바구니에 담겼거나 주문된 상품입니다.");  // 실패 메시지 추가
        }

        return "redirect:/";  // 홈 페이지로 리다이렉트
    }
}
