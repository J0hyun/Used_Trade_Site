//package com.mbc.service;
//
//import com.mbc.dto.MemberFormDto;
//import com.mbc.entity.Member;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@Transactional  //Test 이후 db제거
//@Log4j2
//class MemberServiceTest {
//
//    @Autowired
//    private MemberService memberService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public Member createMember(){
//
//        MemberFormDto memberFormDto = new MemberFormDto().builder()
//                .email("test1@test1.com")
//                .name("test1")
//                .password("1234")
//                .address("수원시 권선구 오목천동")
//                .build();
//        /*memberFormDto.setAddress("서울시 마포구 함정동");
//        memberFormDto.setEmail("test@test.com");
//        memberFormDto.setName("홍길동");
//        //memberFormDto.setPassword(passwordEncoder.encode("1234"));
//        memberFormDto.setPassword("1234");*/
//
//        return Member.createMember(memberFormDto, passwordEncoder);
//
//    }
//
////    @Test
////    @DisplayName("회원가입 테스트")
////    public void saveMemberTest() {
////        Member member = createMember();
////
////        Member savedMember = memberService.saveMember(member);
////
////        assertEquals(savedMember.getEmail(), member.getEmail());
////        assertEquals(savedMember.getName(), member.getName());
////        assertEquals(savedMember.getAddress(), member.getAddress());
////        assertEquals(savedMember.getPassword(), member.getPassword());
////        assertEquals(savedMember.getRole(), member.getRole());
////
////    }
//
//}