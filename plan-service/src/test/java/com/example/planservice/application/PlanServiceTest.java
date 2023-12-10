package com.example.planservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import com.example.planservice.presentation.dto.request.LabelCreateRequest;
import com.example.planservice.presentation.dto.request.PlanCreateRequest;
import com.example.planservice.presentation.dto.request.PlanUpdateRequest;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.request.TaskCreateRequest;
import com.example.planservice.presentation.dto.response.PlanMainResponse;
import com.example.planservice.presentation.dto.response.PlanResponse;
import com.example.planservice.presentation.dto.response.PlanTitleIdResponse;
import com.example.planservice.util.RedisUtils;

@SpringBootTest
@Transactional
class PlanServiceTest {
    @Autowired
    PlanService planService;

    @MockBean
    EmailService emailService;

    @Autowired
    TaskService taskService;

    @Autowired
    TabService tabService;

    @Autowired
    LabelService labelService;

    @Autowired
    RedisUtils redisUtils;

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

    private List<String> defaultTabTitle = List.of("To Do", "In Progress", "Done");

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
            .sendInviteEmail(anyString(),
                anyString(), anyString());
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
        Plan savedPlan = planRepository.findById(savedId)
            .get();

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
            .getTitle()).isEqualTo("To Do");
        assertThat(tabs.get(1)
            .getTitle()).isEqualTo("In Progress");
        assertThat(tabs.get(2)
            .getTitle()).isEqualTo("Done");
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
        DummyRelation dummyRelation = createDefaultMemberPlanTabTaskRelation();

        // when
        PlanResponse planResponse = planService.getTotalPlanResponse(dummyRelation.plan1.getId());

        // then
        assertThat(planResponse).isNotNull();
        assertThat(planResponse.getTitle()).isEqualTo(dummyRelation.plan1.getTitle());
        assertThat(planResponse.getDescription()).isEqualTo(dummyRelation.plan1.getIntro());
        assertThat(planResponse.getMembers()
            .get(0)
            .getMail()).isEqualTo(dummyRelation.member.getEmail());
        assertThat(planResponse.getTabs()
            .stream().filter(tabInfo -> !defaultTabTitle.contains(tabInfo.getTitle()))
            .count()).isEqualTo(2);
        assertThat(planResponse.getLabels()
            .get(0)
            .getValue()).isEqualTo(dummyRelation.label1.getName());
        assertThat(planResponse.getLabels()
            .get(1)
            .getValue()).isEqualTo(dummyRelation.label2.getName());
        assertThat(planResponse.isPublic()).isTrue();
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
            .owner(tester)
            .isPublic(true)
            .build();
        planRepository.save(plan);

        Member member1 = Member.builder()
            .name("tester1")
            .email("test1@example.com")
            .build();
        Member member2 = Member.builder()
            .name("tester2")
            .email("test2@example.com")
            .build();
        memberRepository.saveAll(List.of(member1, member2));
        String uuid = "uuid";
        redisUtils.setData(uuid, plan.getId()
            .toString(), 1000L);


        // when
        Long memberOfPlanId1 = planService.inviteMember(uuid, member1.getId());
        Long memberOfPlanId2 = planService.inviteMember(uuid, member2.getId());

        // then
        assertThat(memberOfPlanRepository.findAllByPlanId(plan.getId())
            .get()).hasSize(2);
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
        String uuid = "uuid";
        redisUtils.setData(uuid, plan.getId()
            .toString(), 1000L);

        // when & then
        assertThatThrownBy(() -> planService.inviteMember(uuid, notRegisteredMemberId))
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
        String uuid = "uuid";
        redisUtils.setData(uuid, String.valueOf(10L), 1000L);
        // when & then
        assertThatThrownBy(() -> planService.inviteMember(uuid, savedMember.getId()))
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
    @DisplayName("플랜을 삭제하고 플랜을 호줄한다")
    void deleteAndReadPlan() {
        // given
        DummyRelation dummyRelation = createDefaultMemberPlanTabTaskRelation();
        Plan plan = dummyRelation.plan1;
        planService.delete(plan.getId(), plan.getOwner().getId());

        // when
        PlanResponse planResponse = planService.getTotalPlanResponse(plan.getId());

        // then
        assertThat(planResponse).isNotNull();
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
            .invitedEmails(List.of())
            .kickingMemberIds(List.of())
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
    @DisplayName("플랜을 수정할 때 기존 멤버를 삭제한다")
    void updateDeleteMember() {
        // given
        Member tester1 = Member.builder()
            .name("tester1")
            .email("test@test.com")
            .build();
        Plan plan = Plan.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .owner(tester)
            .isPublic(true)
            .build();
        memberRepository.save(tester1);
        Plan savedPlan = planRepository.save(plan);
        String uuid = "uuid";
        redisUtils.setData(uuid, plan.getId()
            .toString(), 10000L);
        planService.inviteMember(uuid, tester1.getId());

        PlanUpdateRequest planUpdateRequest = PlanUpdateRequest.builder()
            .title("수정된 플랜 제목")
            .intro("수정된 플랜 소개")
            .invitedEmails(List.of())
            .kickingMemberIds(List.of(tester1.getId()))
            .ownerId(tester1.getId())
            .isPublic(false)
            .build();

        // when
        planService.update(savedPlan.getId(), planUpdateRequest, userId);

        // then
        Plan updatedPlan = planRepository.findById(plan.getId())
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));
        assertThat(updatedPlan.getMembers()).isEmpty();
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
        String uuid = "uuid";
        redisUtils.setData(uuid, plan.getId().toString(), 1000L);
        Long memberOfPlanId = planService.inviteMember(uuid, member.getId());

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
        Member member = createDefaultMember("tester1", "tester1");
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

    @Test
    @DisplayName("멤버 아이디로 메인페이지에 보여줄 모든 플랜을 가져온다")
    void getMainResponse() {
        // given
        DummyRelation dummyRelation = createDefaultMemberPlanTabTaskRelation();

        // when
        List<PlanMainResponse> allPlan = planService.getMainResponse(dummyRelation.member.getId());

        // then
        assertThat(allPlan.size()).isEqualTo(2);
        assertThat(allPlan.get(0)
            .getTitle()).isEqualTo(dummyRelation.plan1.getTitle());
        assertThat(allPlan.get(1)
            .getTitle()).isEqualTo(dummyRelation.plan2.getTitle());
    }

    @Test
    @DisplayName("멤버 아이디로 메인페이지에 보여줄 모든 플랜에 속한 탭 아이디를 확인한다")
    void getMainResponseTabId() {
        // given
        DummyRelation dummyRelation = createDefaultMemberPlanTabTaskRelation();

        // when
        List<PlanMainResponse> allPlan = planService.getMainResponse(dummyRelation.member.getId());

        // then
        assertThat(allPlan.get(0)
            .getTabs()
            .stream().filter(tabInfo -> !defaultTabTitle.contains(tabInfo.getTitle()))
            .count()).isEqualTo(2);
    }

    @Test
    @DisplayName("멤버 아이디로 메인페이지에 보여줄 플랜의 각 탭에 속한 테스크 아이디를 확인한다")
    void getMainResponseTaskId() {
        // given
        DummyRelation dummyRelation = createDefaultMemberPlanTabTaskRelation();

        // when
        List<PlanMainResponse> allPlan = planService.getMainResponse(dummyRelation.member.getId());

        // then
        assertThat(allPlan.get(0)
            .getTabs()
            .get(3)
            .getTaskList()
            .get(0)
            .getTaskId()).isEqualTo(dummyRelation.task1.getId());
    }


    private Plan creatDefaultPlan(String planTitle) {
        return planRepository.save(Plan.builder()
            .title(planTitle)
            .intro("플랜 소개")
            .owner(tester)
            .isPublic(true)
            .build());
    }

    private Member createDefaultMember(String name, String email) {
        return memberRepository.save(Member.builder()
            .name(name)
            .email(email)
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

    private DummyRelation createDefaultMemberPlanTabTaskRelation() {
        Member member = createDefaultMember("tester1", "email@exam.com");

        Plan plan1 = createPlan("testplan1", member.getId());
        Plan plan2 = createPlan("testplan2", member.getId());

        Tab tab1 = createTab("tab1", plan1.getId(), member.getId());
        Tab tab2 = createTab("tab2", plan1.getId(), member.getId());

        Task task1 = createTask("task1", tab1.getId(), plan1.getId(), member.getId(), 1);
        Task task2 = createTask("task2", tab1.getId(), plan1.getId(), member.getId(), 1);
        Task task3 = createTask("task3", tab2.getId(), plan1.getId(), member.getId(), 2);

        Label label1 = createLabel("testLabel1", plan1.getId(), member.getId());
        Label label2 = createLabel("testLabel2", plan1.getId(), member.getId());

        return new DummyRelation(member, plan1, plan2, tab1, tab2, task1, task2, task3, label1, label2);
    }

    private Plan createPlan(String title, Long memberId) {
        PlanCreateRequest request = PlanCreateRequest.builder()
            .title(title)
            .intro("플랜 소개")
            .isPublic(true)
            .invitedEmails(List.of())
            .build();
        Long planId = planService.create(request, memberId);
        return planRepository.findById(planId).get();
    }

    private Tab createTab(String title, Long planId, Long memberId) {
        TabCreateRequest request = TabCreateRequest.builder()
            .title(title)
            .planId(planId)
            .build();
        Long tabId = tabService.create(memberId, request);
        return tabRepository.findById(tabId).get();
    }

    private Task createTask(String title, Long tabId, Long planId, Long memberId, int days) {
        TaskCreateRequest request = TaskCreateRequest.builder()
            .title(title)
            .tabId(tabId)
            .planId(planId)
            .startDate(LocalDate.now().atStartOfDay())
            .endDate(LocalDate.now().plusDays(days).atStartOfDay())
            .build();
        Long taskId = taskService.create(memberId, request);
        return taskRepository.findById(taskId).get();
    }

    private Label createLabel(String name, Long planId, Long memberId) {
        LabelCreateRequest request = LabelCreateRequest.builder()
            .planId(planId)
            .name(name)
            .build();
        Long labelId = labelService.create(memberId, request);
        return labelRepository.findById(labelId).get();
    }

    class DummyRelation {
        private Member member;
        private Plan plan1;
        private Plan plan2;
        private Tab tab1;
        private Tab tab2;
        private Task task1;
        private Task task2;
        private Task task3;

        private Label label1;
        private Label label2;


        public DummyRelation(Member member, Plan plan1, Plan plan2, Tab tab1, Tab tab2, Task task1, Task task2,
                             Task task3, Label label1, Label label2) {
            this.member = member;
            this.plan1 = plan1;
            this.plan2 = plan2;
            this.tab1 = tab1;
            this.tab2 = tab2;
            this.task1 = task1;
            this.task2 = task2;
            this.task3 = task3;
            this.label1 = label1;
            this.label2 = label2;
        }
    }
}
