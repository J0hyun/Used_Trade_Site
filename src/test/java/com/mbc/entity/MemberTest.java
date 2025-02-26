//package com.mbc.entity;
//
//import com.mbc.repository.MemberRepository;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Transactional
//@Log4j2
//class MemberTest {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    EntityManager em;
//
//    @Test
//    @DisplayName("Auditing 테스트")
//    @WithMockUser(username = "gildong", roles = "USER")
//    public void auditingTest(){
//        Member newMember = new Member();
//        memberRepository.save(newMember);
//
//        em.flush();
//        em.clear();
//
//        Member member = memberRepository.findById(newMember.getId())
//                .orElseThrow(EntityNotFoundException::new);
//        log.info("register time: " + member.getRegTime());
//        log.info("update time: " + member.getUpdateTime());
//        log.info("create member : " + member.getCreatedBy());
//        log.info("modify member : " + member.getModifiedBy());
//
//    }
//
//}