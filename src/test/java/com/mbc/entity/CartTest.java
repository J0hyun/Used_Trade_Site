//package com.mbc.entity;
//
//import com.mbc.dto.MemberFormDto;
//import com.mbc.repository.CartRepository;
//import com.mbc.repository.MemberRepository;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.persistence.PersistenceContext;
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
//@Transactional
//@Log4j2
//class CartTest {
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @PersistenceContext
//    EntityManager em;
//
//    public Member createMember(){
//        MemberFormDto memberFormDto = MemberFormDto.builder()
//                .email("test05@test.com")
//                .name("신사임당")
//                .address("서울시 마포구 합정동")
//                .password("1234")
//                .build();
//
//        return  Member.createMember(memberFormDto, passwordEncoder);
//    }
//
//    @Test
//    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
//    //@Commit  // @Transactional DB에 반영 안되는것 저장되도록해줌
//    public void findCartAndMemberTest(){
//        Member member = createMember();
//        memberRepository.save(member);
//
//        Cart cart = new Cart();
//        cart.setMember(member);
//        cartRepository.save(cart);
//
//        em.flush();  // JPA는 영속성 컨텍스트에 데이터를 저장 후 트랜잭션이 끝날 때 flush()를 호출하여 데이터베이스에 반영
//        em.clear();  // JPA는 영속성 컨텍스트를 비워줌
//
//        Cart savedCart = cartRepository.findById(cart.getId())
//                .orElseThrow(EntityNotFoundException::new);  // 저장된 장바구니 엔티티 조회
//        assertEquals(member.getId(), savedCart.getMember().getId());  // 처음 저장한 member 엔티티의 id와 savedCart에 매핑된 member엔티티의 id 비교
//    }
//}