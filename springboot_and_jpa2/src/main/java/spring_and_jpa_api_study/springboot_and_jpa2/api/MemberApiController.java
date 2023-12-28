package spring_and_jpa_api_study.springboot_and_jpa2.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Member;
import spring_and_jpa_api_study.springboot_and_jpa2.service.MemberService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("api/v1/members")
    public List<Member> membersV1(){
        return memberService.findAll();
    }

    @GetMapping("api/v2/members")
    public Result membersV2(){
        List<MemberDto> memberDtos = memberService.findAll().stream().map(member ->
                new MemberDto(member.getName())).collect(Collectors.toList());

        return new Result<>(memberDtos.size(), memberDtos);
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int cnt;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

    @PostMapping("api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        return new CreateMemberResponse(memberService.join(member));
    }

    @PostMapping("api/v2/members")
    public CreateMemberResponse saveMemberv2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());
        return new CreateMemberResponse(memberService.join(member));
    }

    @PutMapping("api/v2/members/{id}")
    public UpdateMemberResponse updateMemberv1(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName());
        Member member = memberService.findOne(id);
        return new UpdateMemberResponse(member.getId(), member.getName());
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;

    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }
}
