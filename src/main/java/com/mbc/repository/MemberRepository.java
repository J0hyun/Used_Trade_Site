package com.mbc.repository;

import com.mbc.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    List<Member> findAllByEmail(String email); // 이메일로 여러 사용자 찾기

    Member findByname(String name);

    boolean existsByEmail(String email);  // 추가

    Member findByPhone(String phone);  // 전화번호로 사용자 찾기

    Member findByEmailAndName(String email, String name); // 이메일과 아이디로 사용자 찾기

    List<Member> findAllByPhone(String phone); // 전화번호로 여러 사용자 찾기


}
