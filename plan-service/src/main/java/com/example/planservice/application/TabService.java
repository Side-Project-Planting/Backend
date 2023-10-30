package com.example.planservice.application;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.application.dto.TabChangeNameResponse;
import com.example.planservice.application.dto.TabChangeNameServiceRequest;
import com.example.planservice.application.dto.TabDeleteServiceRequest;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.TabGroup;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.TabChangeOrderRequest;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.response.TabRetrieveResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TabService {
    private final PlanMembershipVerificationService planMembershipVerificationService;
    private final TabRepository tabRepository;

    @Transactional
    public Long create(Long memberId, TabCreateRequest request) {
        try {
            Plan plan = planMembershipVerificationService.verifyAndReturnPlan(request.getPlanId(), memberId);
            List<Tab> tabsOfPlan = tabRepository.findAllByPlanId(plan.getId());

            Tab createdTab = Tab.create(plan, request.getName());
            TabGroup tabGroup = new TabGroup(plan.getId(), tabsOfPlan);
            tabGroup.addLast(createdTab);

            Tab savedTab = tabRepository.save(createdTab);
            return savedTab.getId();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ApiException(ErrorCode.REQUEST_CONFLICT);
        }
    }

    @Transactional
    public List<Long> changeOrder(Long memberId, TabChangeOrderRequest request) {
        Plan plan = planMembershipVerificationService.verifyAndReturnPlan(request.getPlanId(), memberId);

        List<Tab> tabs = tabRepository.findAllByPlanId(request.getPlanId());
        TabGroup tabGroup = new TabGroup(plan.getId(), tabs);
        List<Tab> result = tabGroup.changeOrder(request.getTargetId(), request.getNewPrevId());
        return result.stream().map(Tab::getId).toList();
    }

    // TODO Tab은 Plan에 강하게 의존관계를 가짐. 단독으로 쓰일일도 잘 없음.(앞으로도 그럴거로 예상됨)
    //  Plan 없이는 Tab 기능 수행 못하는데, 이럴거면 Plan에 List<Tab> 양방향 연관관계를 거는게 어떤지
    @Transactional
    public TabChangeNameResponse changeName(TabChangeNameServiceRequest request) {
        Plan plan = planMembershipVerificationService.verifyAndReturnPlan(request.getPlanId(), request.getMemberId());
        List<Tab> tabs = tabRepository.findAllByPlanId(plan.getId());
        TabGroup tabGroup = new TabGroup(plan.getId(), tabs);

        Tab target = tabGroup.findById(request.getTabId());
        target.changeName(request.getName());

        try {
            tabRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.TAB_NAME_DUPLICATE);
        }

        return TabChangeNameResponse.builder()
            .id(target.getId())
            .name(target.getName())
            .build();
    }

    @Transactional
    public Long delete(TabDeleteServiceRequest request) {
        Long planId = request.getPlanId();
        Long tabId = request.getTabId();
        Long memberId = request.getMemberId();

        boolean isAdmin = planMembershipVerificationService.validateOwner(planId, memberId);
        if (!isAdmin) {
            throw new ApiException(ErrorCode.AUTHORIZATION_FAIL);
        }

        List<Tab> tabs = tabRepository.findAllByPlanId(planId);
        TabGroup tabGroup = new TabGroup(planId, tabs);
        tabGroup.deleteById(tabId);
        tabRepository.deleteById(tabId);
        return tabId;
    }


    @NotNull
    private Optional<Tab> findLastTab(List<Tab> tabsOfPlan) {
        List<Tab> tabs = tabsOfPlan.stream()
            .filter(each -> each.getNext() == null)
            .toList();
        if (tabs.size() > 1) {
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }
        if (tabs.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(tabs.get(0));
    }

    public TabRetrieveResponse retrieve(Long id, Long userId) {
        return null;
    }

}
