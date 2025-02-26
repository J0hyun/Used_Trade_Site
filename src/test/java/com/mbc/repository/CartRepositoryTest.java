//package com.mbc.repository;
//
//import com.mbc.dto.MemberFormDto;
//import com.mbc.entity.Cart;
//import com.mbc.entity.Member;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.persistence.PersistenceContext;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.annotation.Commit;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Transactional
//@Log4j2
//class CartRepositoryTest {
//
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @PersistenceContext
//    EntityManager em;
//
//    public Member createMember(){
//
//        MemberFormDto memberFormDto = new MemberFormDto().builder()
//                .email("test2@test2")
//                .name("test2")
//                .password("1234")
//                .address("test2")
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
//    @Test
//    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
//    @Commit
//    public void findCartAndMemberTest() {
//        Member member = createMember();
//        memberRepository.save(member);
//
//        Cart cart = new Cart();
//        cart.setMember(member);
//        cartRepository.save(cart);
//
//        em.flush();
//        em.clear();
//
//        Cart savedCart = cartRepository.findById(cart.getId())
//                .orElseThrow(EntityNotFoundException::new);
////        assertEquals(savedCart.getMember().getId(), member.getId());
//
//    }
//
//}