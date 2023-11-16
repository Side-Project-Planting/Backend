package com.example.planservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.label.Label;
import com.example.planservice.domain.label.repository.LabelRepository;
import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.memberofplan.MemberOfPlan;
import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.domain.task.Task;
import com.example.planservice.domain.task.repository.TaskRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.PlanCreateRequest;
import com.example.planservice.presentation.dto.request.PlanUpdateRequest;
import com.example.planservice.presentation.dto.response.PlanResponse;
import com.example.planservice.presentation.dto.response.PlanTitleIdResponse;

@SpringBootTest
@Transactional
class PlanServiceTest {
    @Autowired
    PlanService planService;

    @MockBean
    EmailService emailService;

    @Autowired
    PlanRepository planRepository;

    @Autowired
    TabRepository tabRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    MemberOfPlanRepository memberOfPlanRepository;

    @Autowired
    TaskRepository taskRepository;
    private Long userId;
    private Member tester;

    @BeforeEach
    void testSetUp() {
        tester = Member.builder()
            .name("tester")
            .email("testEach@example.com")
            .build();
        Member savedMember = memberRepository.save(tester);
        userId = savedMember.getId();

        Mockito.doNothing()
            .when(emailService)
            .sendInviteEmail(ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("플랜을 생성한다")
    void create() {
        // given
        List<String> invitedEmails = List.of("test@example.com");

        PlanCreateRequest request = PlanCreateRequest.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .invitedEmails(invitedEmails)
            .build();

        // when
        Long savedId = planService.create(request, userId);

        // then
        assertThat(savedId).isNotNull();
        Plan savedPlan = planRepository.findById(savedId).get();

        assertThat(savedPlan.getTitle()).isEqualTo(request.getTitle());
        assertThat(savedPlan.getIntro()).isEqualTo(request.getIntro());
    }

    @Test
    @DisplayName("플랜 생성시 기본 탭이 생성 되었다")
    void createPlanWithDefaultTab() {
        // given
        List<String> invitedEmails = List.of("test@example.com");

        PlanCreateRequest request = PlanCreateRequest.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .invitedEmails(invitedEmails)
            .build();

        // when
        Long savedId = planService.create(request, userId);

        // then
        List<Tab> tabs = tabRepository.findAllByPlanId(savedId);
        assertThat(tabs.size()).isEqualTo(3);
        assertThat(tabs.get(0)
            .getName()).isEqualTo("To Do");
        assertThat(tabs.get(1)
            .getName()).isEqualTo("In Progress");
        assertThat(tabs.get(2)
            .getName()).isEqualTo("Done");
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 플랜을 생성하려고 하면 실패한다")
    void createFailNotExistUser() {
        // given
        Long notRegisteredUserId = 10L;
        List<String> invitedEmails = List.of("test@example.com");

        PlanCreateRequest request = PlanCreateRequest.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .invitedEmails(invitedEmails)
            .build();

        // when & then
        assertThatThrownBy(() -> planService.create(request, notRegisteredUserId))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("플랜의 전체 정보를 가져온다")
    void getTotalPlanResponse() {
        // given
        Member member1 = Member.builder()
            .email("test1@example.com")
            .name("test1")
            .profileUri("www.test1")
            .build();

        Member member2 = Member.builder()
            .email("test2@example.com")
            .name("test2")
            .profileUri("www.test2")
            .build();

        Plan plan = Plan.builder()
            .owner(member1)
            .title("testPlan")
            .intro("hi")
            .build();


        Tab tab2 = Tab.builder()
            .plan(plan)
            .name("testTab2")
            .first(false)
            .build();

        Tab tab1 = Tab.builder()
            .plan(plan)
            .name("testTab1")
            .next(tab2)
            .first(true)
            .build();

        Task task2 = Task.builder()
            .name("testTask2")
            .tab(tab1)
            .description("testTaskDesc2")
            .build();

        Task task1 = Task.builder()
            .name("testTask1")
            .tab(tab1)
            .description("testTaskDesc1")
            .next(task2)
            .build();

        Task task3 = Task.builder()
            .name("testTask3")
            .tab(tab2)
            .description("testTaskDesc3")
            .build();

        Label label1 = Label.builder()
            .name("testLabel1")
            .plan(plan)
            .build();

        Label label2 = Label.builder()
            .name("testLabel2")
            .plan(plan)
            .build();

        MemberOfPlan memberOfPlan1 = MemberOfPlan.builder()
            .member(member1)
            .plan(plan)
            .build();

        MemberOfPlan memberOfPlan2 = MemberOfPlan.builder()
            .member(member2)
            .plan(plan)
            .build();
        plan.getTabs()
            .add(tab1);
        plan.getTabs()
            .add(tab2);

        plan.getMembers()
            .add(memberOfPlan1);
        plan.getMembers()
            .add(memberOfPlan2);

        plan.getLabels()
            .add(label1);
        plan.getLabels()
            .add(label2);

        tab1.getTasks()
            .add(task1);
        tab1.getTasks()
            .add(task2);
        tab2.getTasks()
            .add(task3);
        memberRepository.saveAll(List.of(member1, member2));
        planRepository.save(plan);
        memberOfPlanRepository.saveAll(List.of(memberOfPlan1, memberOfPlan2));
        tabRepository.saveAll(List.of(tab1, tab2));
        taskRepository.saveAll(List.of(task1, task2, task3));

        // when
        PlanResponse planResponse = planService.getTotalPlanResponse(plan.getId());

        // then
        assertThat(planResponse).isNotNull();
        assertThat(planResponse.getTitle()).isEqualTo("testPlan");
        assertThat(planResponse.getDescription()).isEqualTo("hi");
        assertThat(planResponse.getTabOrder()).isEqualTo(List.of(tab1.getId(), tab2.getId()));
        assertThat(planResponse.getMembers()
            .get(0)
            .getMail()).isEqualTo("test1@example.com");
        assertThat(planResponse.getMembers()
            .get(1)
            .getMail()).isEqualTo("test2@example.com");
        assertThat(planResponse.getTabs()
            .get(0)
            .getTitle()).isEqualTo("testTab1");
        assertThat(planResponse.getTabs()
            .get(1)
            .getTitle()).isEqualTo("testTab2");
        assertThat(planResponse.getTasks()
            .get(0)
            .getTitle()).isEqualTo("testTask1");
        assertThat(planResponse.getTasks()
            .get(1)
            .getTitle()).isEqualTo("testTask2");
        assertThat(planResponse.getTasks()
            .get(2)
            .getTitle()).isEqualTo("testTask3");
        assertThat(planResponse.getLabels()
            .get(0)
            .getValue()).isEqualTo("testLabel1");
        assertThat(planResponse.getLabels()
            .get(1)
            .getValue()).isEqualTo("testLabel2");
        assertThat(planResponse.isPublic()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 플랜 ID로 전체 정보를 요청하면 PLAN_NOT_FOUND 예외를 발생시킨다")
    void getTotalPlanResponse_withNonExistentPlanId_throwsException() {
        // given
        Long nonExistentPlanId = 9999L;

        // when / then
        assertThatThrownBy(() -> planService.getTotalPlanResponse(nonExistentPlanId))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("멤버를 플랜에 초대한다")
    void inviteMember() {
        // given
        Plan plan = Plan.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .build();
        planRepository.save(plan);

        Member member = Member.builder()
            .name("tester")
            .email("test@example.com")
            .build();
        memberRepository.save(member);

        // when
        Long memberOfPlanId = planService.inviteMember(plan.getId(), member.getId());

        // then
        assertThat(memberOfPlanRepository.findById(memberOfPlanId)).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 멤버로 초대를 시도하면 실패한다")
    void inviteFailNotExistMember() {
        // given
        Plan plan = Plan.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .build();
        Plan savedPlan = planRepository.save(plan);
        Long notRegisteredMemberId = 20L;

        // when & then
        assertThatThrownBy(() -> planService.inviteMember(savedPlan.getId(), notRegisteredMemberId))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());


    }

    @Test
    @DisplayName("존재하지 않는 플랜으로 초대를 시도하면 실패한다")
    void inviteFailNotExistPlan() {
        // given
        Long notRegisteredPlanId = 10L;

        Member member = Member.builder()
            .name("tester")
            .email("test@example.com")
            .build();


        Member savedMember = memberRepository.save(member);

        // when & then
        assertThatThrownBy(() -> planService.inviteMember(notRegisteredPlanId, savedMember.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());


    }


    @Test
    @DisplayName("플랜을 삭제한다")
    void delete() {
        // given
        Plan plan = creatDefaultPlan("testPlan");
        saveDefaultMemberOfPlan(plan, tester);

        // when
        planService.delete(plan.getId(), tester.getId());

        // then
        assertThat(plan.isDeleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("플랜을 수정한다")
    void update() {
        // given
        Member nextOwner = Member.builder()
            .name("nextOwner")
            .email("test@test.com")
            .build();
        Plan plan = Plan.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .owner(tester)
            .isPublic(true)
            .build();
        memberRepository.save(nextOwner);
        Plan savedPlan = planRepository.save(plan);


        PlanUpdateRequest planUpdateRequest = PlanUpdateRequest.builder()
            .title("수정된 플랜 제목")
            .intro("수정된 플랜 소개")
            .ownerId(nextOwner.getId())
            .isPublic(false)
            .build();

        // when
        planService.update(savedPlan.getId(), planUpdateRequest, userId);

        // then
        Plan updatedPlan = planRepository.findById(plan.getId())
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));

        assertThat(updatedPlan.getTitle()).isEqualTo("수정된 플랜 제목");
        assertThat(updatedPlan.getIntro()).isEqualTo("수정된 플랜 소개");
        assertThat(updatedPlan.getOwner()).isEqualTo(nextOwner);
        assertThat(updatedPlan.isPublic()).isFalse();
    }

    @Test
    @DisplayName("플랜에서 나간다")
    void exit() {
        // given
        Plan plan = Plan.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .build();
        planRepository.save(plan);

        Member member = Member.builder()
            .name("tester")
            .email("test@example.com")
            .build();
        memberRepository.save(member);
        Long memberOfPlanId = planService.inviteMember(plan.getId(), member.getId());

        // when
        planService.exit(plan.getId(), member.getId());

        // then
        assertThat(memberOfPlanRepository.findById(memberOfPlanId)).isEmpty();
        assertThat(planService.getAllPlanTitleIdByMemberId(member.getId())).isEmpty();
    }


    @Test
    @DisplayName("멤버 아이디로 모든 플랜을 가져온다")
    void getAllPlanByMemberId() {
        // given
        Member member = createDefaultMember();
        Plan plan1 = creatDefaultPlan("plan1");
        Plan plan2 = creatDefaultPlan("plan2");
        Plan plan3 = creatDefaultPlan("plan3");
        saveDefaultMemberOfPlan(plan1, member);
        saveDefaultMemberOfPlan(plan2, member);
        saveDefaultMemberOfPlan(plan3, member);

        // when
        List<PlanTitleIdResponse> allPlanByMemberId = planService.getAllPlanTitleIdByMemberId(member.getId());

        // then
        assertThat(allPlanByMemberId.size()).isEqualTo(3);
        assertThat(allPlanByMemberId.get(0)
            .getTitle()).isEqualTo("plan1");
        assertThat(allPlanByMemberId.get(1)
            .getTitle()).isEqualTo("plan2");
        assertThat(allPlanByMemberId.get(2)
            .getTitle()).isEqualTo("plan3");

    }

    private Plan creatDefaultPlan(String planTitle) {
        return planRepository.save(Plan.builder()
            .title(planTitle)
            .intro("플랜 소개")
            .owner(tester)
            .isPublic(true)
            .build());
    }

    private Member createDefaultMember() {
        return memberRepository.save(Member.builder()
            .name("tester1")
            .email("tester@example.com")
            .build());
    }

    private void saveDefaultMemberOfPlan(Plan plan, Member member) {

        MemberOfPlan memberOfPlan = MemberOfPlan.builder()
            .member(member)
            .plan(plan)
            .build();
        plan.getMembers()
            .add(memberOfPlan);
        memberOfPlanRepository.save(memberOfPlan);
    }
}
