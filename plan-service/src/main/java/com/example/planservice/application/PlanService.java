package com.example.planservice.application;

import java.util.List;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.member.Member;
import com.example.planservice.domain.member.repository.MemberRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.PlanCreateRequest;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final EmailService emailService;
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final TabRepository tabRepository;

    @Transactional
    public Long create(PlanCreateRequest request, Long userId) {
        try {
            Member member = memberRepository.findById(userId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND));
            Plan plan = Plan.builder()
                .title(request.getTitle())
                .intro(request.getIntro())
                .isPublic(request.isPublic())
                .owner(member)
                .build();

            sendInviteMail(request.getInvitedEmails(), request.getTitle());
            createDefaultTab(plan);

            Plan savedPlan = planRepository.save(plan);
            return savedPlan.getId();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ApiException(ErrorCode.REQUEST_CONFLICT);
        }
    }

    private void sendInviteMail(List<String> invitedEmails, String title) {
        invitedEmails.forEach(email -> emailService.sendEmail(email, title));
    }

    private void createDefaultTab(Plan plan) {
        Tab firstTab = Tab.builder().name("ToDo").plan(plan).build();
        Tab lastTab = Tab.builder().name("Done").plan(plan).build();
        firstTab.connect(lastTab);
        tabRepository.save(firstTab);
        tabRepository.save(lastTab);
    }

}
