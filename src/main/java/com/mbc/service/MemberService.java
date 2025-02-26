package com.mbc.service;


import com.mbc.constant.MemberStatus;
import com.mbc.dto.MemberFormDto;
import com.mbc.dto.MemberImgDto;
import com.mbc.entity.Member;
import com.mbc.entity.MemberImg;
import com.mbc.repository.MemberImgRepository;
import com.mbc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberImgRepository memberImgRepository;
    private final MemberImgService memberImgService;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService; // 파일 저장 유틸리티

    public Long saveMember(MemberFormDto memberFormDto, MultipartFile profileImgFile) throws Exception {
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        validateDuplicateMember(member);
        memberRepository.save(member);

        // 프로필 이미지 저장
        if (profileImgFile != null && !profileImgFile.isEmpty()) {
            MemberImg memberImg = new MemberImg();
            memberImg.setMember(member);
            memberImgService.saveMemberImg(memberImg, profileImgFile);
        }

        return member.getId();
    }

    private void validateDuplicateMember(Member member) {

        Member findMember = memberRepository.findByname(member.getName());

        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 아이디입니다");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        log.info("------loadUserByUsername 진입확인------");
        Member member = memberRepository.findByname(name);

        if (member == null) {
            throw new UsernameNotFoundException(name);
        }

        return User.builder()
                .username(member.getName())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

//    public Member getStoreMember(Long id) {
//        Member member = memberRepository.findById(id).orElseThrow(
//                () -> new IllegalArgumentException("회원이 존재하지 않습니다."));
//
//        log.info(member);
//
//        return member;
//    }

    public MemberFormDto getStoreMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 프로필 이미지 가져오기
        MemberImg memberImg = memberImgRepository.findByMemberId(id);
        MemberFormDto memberFormDto;
        if (memberImg != null) {
            MemberImgDto memberImgDto = MemberImgDto.of(memberImg);
            // MemberFormDto 생성 (Builder 사용)
            memberFormDto = MemberFormDto.builder()
                    .memberId(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .address(member.getAddress())
                    .phone(member.getPhone())
                    .regTime(member.getRegTime())
                    .viewPofile(memberImgDto.getImgUrl())
                    .build();
        } else {
            // MemberFormDto 생성 (Builder 사용)
            memberFormDto = MemberFormDto.builder()
                    .memberId(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .address(member.getAddress())
                    .phone(member.getPhone())
                    .regTime(member.getRegTime())
                    .build();
        }
        return memberFormDto;
    }

    public MemberFormDto getMemberByUserName(String userName) {
        Member member = memberRepository.findByname(userName); // memberRepository에서 이름으로 찾기

        Long memberId = member.getId();
        // 프로필 이미지 가져오기
        MemberImg memberImg = memberImgRepository.findByMemberId(memberId);
        MemberFormDto memberFormDto;
        if (memberImg != null) {
            MemberImgDto memberImgDto = MemberImgDto.of(memberImg);
            // MemberFormDto 생성 (Builder 사용)
            memberFormDto = MemberFormDto.builder()
                    .memberId(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .address(member.getAddress())
                    .phone(member.getPhone())
                    .regTime(member.getRegTime())
                    .viewPofile(memberImgDto.getImgUrl())
                    .build();
        } else {
            // MemberFormDto 생성 (Builder 사용)
            memberFormDto = MemberFormDto.builder()
                    .memberId(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .address(member.getAddress())
                    .phone(member.getPhone())
                    .regTime(member.getRegTime())
                    .build();
        }
        return memberFormDto;
    }

    // 회원 정보와 프로필 이미지 정보를 DTO로 반환
    public MemberFormDto getMemberInfo(String username) {
        Member member = memberRepository.findByname(username);

        // MemberImg 조회
        MemberImg memberImg = memberImgRepository.findByMemberId(member.getId());

        return new MemberFormDto(member, memberImg);
    }

    // 회원 정보와 프로필 이미지 업데이트
    public void updateMember(MemberFormDto memberFormDto, MultipartFile profileImgFile) throws Exception {
        log.info("회원 정보 업데이트 서비스 진입");
        log.info("MemberFormDto created: " + memberFormDto);
        log.info("멤버폼dto값:", memberFormDto.toString());
        Member member = memberRepository.findByname(memberFormDto.getName());

        log.info("회원 정보 찾기 완료");

        // 회원 정보 업데이트
        member.setEmail(memberFormDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword())); // 암호화된 비밀번호
        member.setAddress(memberFormDto.getAddress());
        member.setPhone(memberFormDto.getPhone());

        memberRepository.save(member);
        log.info("회원 정보 업데이트 완료(이미지제외)");

        // 프로필 이미지 업데이트
        if (profileImgFile != null && !profileImgFile.isEmpty()) {
            MemberImg memberImg = memberImgRepository.findByMemberId(member.getId());

            if (memberImg == null) {
                memberImg = new MemberImg(member); // 프로필 이미지가 없으면 새로 생성
            }

            // 기존 이미지 삭제 (이미지 경로가 있다면)
            if (memberImg.getImgName() != null) {
                String existingFilePath = "C:/shop/member/" + memberImg.getImgName();
                File existingFile = new File(existingFilePath);
                if (existingFile.exists()) {
                    boolean deleted = existingFile.delete();
                    if (deleted) {
                        log.info("기존 프로필 이미지 삭제 완료: " + existingFilePath);
                    } else {
                        log.warn("기존 프로필 이미지 삭제 실패: " + existingFilePath);
                    }
                }
            }

            // MultipartFile에서 원본 파일명과 파일 데이터를 가져와서 uploadFile 메서드 호출
            String profileImgUrl = fileService.uploadFile("C:/shop/member", profileImgFile.getOriginalFilename(), profileImgFile.getBytes());
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

            memberImg.setImgUrl("/images/member/" + datePath + "/" + profileImgUrl);
            memberImg.setOriImgName(profileImgFile.getOriginalFilename());
            memberImg.setImgName(profileImgUrl);  // 저장된 파일 이름 설정 (UUID 포함)

            memberImgRepository.save(memberImg);
        } else {
            // profileImgFile이 null일 경우, 기존 이미지를 삭제
            MemberImg memberImg = memberImgRepository.findByMemberId(member.getId());
            if (memberImg != null && memberImg.getImgName() != null) {
                String existingFilePath = "C:/shop/member/" + memberImg.getImgName();
                File existingFile = new File(existingFilePath);
                if (existingFile.exists()) {
                    boolean deleted = existingFile.delete();
                    if (deleted) {
                        log.info("기존 프로필 이미지 삭제 완료: " + existingFilePath);
                    } else {
                        log.warn("기존 프로필 이미지 삭제 실패: " + existingFilePath);
                    }
                }

                // DB에서 이미지 정보 삭제
                memberImgRepository.delete(memberImg);
            }
        }
    }

    // 회원 삭제
    public void deleteMember(String username) {
        Member member = memberRepository.findByname(username);
        if (member != null) {
            // 상태정보변경 및 개인정보 삭제
            member.setStatus(MemberStatus.DELETED);
            member.setAddress(null);
            member.setEmail(null);
            member.setName(null);
            member.setPassword(null);
            member.setPhone(null);

            memberRepository.save(member); // 변경 사항 저장
        }
    }

    @Transactional
    public boolean emailExist(String email) {
        return memberRepository.existsByEmail(email); // UsersRepository에 existsByEmail 메서드가 있어야 함
    }


    @Transactional
    public String updatePasswordToken(String email, String name) {
        Member member = memberRepository.findByEmailAndName(email, name);

        // 이메일 또는 아이디에 해당하는 회원 정보가 없는 경우
        if (member == null) {
            return "NOT_FOUND"; // 회원 정보가 없음
        }

        // 토큰 발급 제한 시간 확인 (1시간 제한)
        if (member.getTokenExpiration() != null && member.getTokenExpiration().isAfter(LocalDateTime.now())) {
            return "TOKEN_LIMIT"; // 1시간 이내 재요청
        }

        // 새로운 토큰 생성 및 저장
        String newToken = UUID.randomUUID().toString(); // 랜덤 토큰 생성
        member.setPasswordResetToken(newToken);
        member.setTokenExpiration(LocalDateTime.now().plusHours(1)); // 토큰 유효 시간 설정 (1시간)

        memberRepository.save(member); // 변경된 사용자 정보 저장
        return "SUCCESS"; // 토큰이 성공적으로 업데이트됨
    }


    public String getTmpPassword() {
        char[] charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder tmpPassword = new StringBuilder();
        for (int i = 0; i < 10; i++) { // 10자리 길이의 비밀번호 생성
            int idx = (int) (Math.random() * charSet.length);
            tmpPassword.append(charSet[idx]);
        }
        return tmpPassword.toString();
    }



    @Transactional
    public void updatePassword(String newPassword, String email, String name) {
        // 이메일과 아이디로 사용자 찾기
        Member member = memberRepository.findByEmailAndName(email, name);

        // 비밀번호 해싱 후 저장
        String hashedPassword = passwordEncoder.encode(newPassword);
        member.setPassword(hashedPassword);
        memberRepository.save(member); // 변경된 사용자 정보 저장
    }

    // 이메일로 사용자 아이디 목록 찾기
    public List<String> findUsernameByEmail(String email) {
        List<Member> members = memberRepository.findAllByEmail(email); // 이메일에 해당하는 모든 회원 찾기
        if (members != null && !members.isEmpty()) {
            return members.stream()
                    .map(Member::getName)  // 사용자 이름(아이디)만 추출
                    .collect(Collectors.toList());
        }
        return null; // 해당 이메일에 사용자 없을 경우
    }

    // 전화번호로 사용자 이름 목록 조회
    public List<String> findUsernameByPhone(String phone) {
        List<Member> members = memberRepository.findAllByPhone(phone); // 전화번호로 모든 회원 찾기
        if (members != null && !members.isEmpty()) {
            return members.stream()
                    .map(Member::getName)  // 사용자 이름(아이디)만 추출
                    .collect(Collectors.toList());
        }
        return null; // 해당 전화번호에 사용자 없을 경우
    }


}