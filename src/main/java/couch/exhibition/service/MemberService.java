package couch.exhibition.service;

import couch.exhibition.dto.UpdatedMemberDTO;
import couch.exhibition.entity.Member;
import couch.exhibition.exception.CustomException;
import couch.exhibition.exception.ErrorCode;
import couch.exhibition.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

@Slf4j
@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
    }

    //회원 등록
    @Transactional
    public Member register(String memberName, String nickname, String id) {
        Member registeredMember = Member.builder()
                .memberName(memberName)
                .nickname(nickname)
                .id(id)
                .build();

        memberRepository.save(registeredMember);

        return registeredMember;
    }

    //닉네임 수정
    @Transactional
    public void editNickname(String id, UpdatedMemberDTO updatedMemberDTO) {
        Optional<Member> member = memberRepository.findById(id);

        Member updatedMember = Member.builder()
                .memberName(updatedMemberDTO.getMemberName())
                .nickname(updatedMemberDTO.getNickname())
                .id(updatedMemberDTO.getId())
                .build();

        // Optional의 .get() function을 이용, memberRepository에 있는 member 객체 가져옴.
        member.get().updateMember(updatedMember);
    }

    //회원 탈퇴
    @Transactional
    public void deleteMember(String id) throws CustomException {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.DELETED_USER));
        memberRepository.delete(member); //entity 직접 제거?
    }
}
