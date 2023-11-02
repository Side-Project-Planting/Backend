package com.example.planservice.application;

import java.util.List;

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
    private final PlanMembershipService planMembershipService;
    private final TabRepository tabRepository;

    @Transactional
    public Long create(Long memberId, TabCreateRequest request) {
        try {
            Plan plan = planMembershipService.getPlanAfterValidateAuthorization(request.getPlanId(), memberId);

            Tab createdTab = Tab.create(plan, request.getName());
            List<Tab> tabsOfPlan = tabRepository.findAllByPlanId(plan.getId());
            TabGroup tabGroup = new TabGroup(plan.getId(), tabsOfPlan);
            tabGroup.addLast(createdTab);
            plan.getTabs().add(createdTab);

            Tab savedTab = tabRepository.save(createdTab);
            return savedTab.getId();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ApiException(ErrorCode.REQUEST_CONFLICT);
        }
    }

    @Transactional
    public List<Long> changeOrder(Long memberId, TabChangeOrderRequest request) {
        Plan plan = planMembershipService.getPlanAfterValidateAuthorization(request.getPlanId(), memberId);

        List<Tab> tabs = tabRepository.findAllByPlanId(request.getPlanId());
        TabGroup tabGroup = new TabGroup(plan.getId(), tabs);
        List<Tab> result = tabGroup.changeOrder(request.getTargetId(), request.getNewPrevId());
        return result.stream().map(Tab::getId).toList();
    }

    // TODO Tab은 Plan에 강하게 의존관계를 가짐. 단독으로 쓰일일도 잘 없음.(앞으로도 그럴거로 예상됨)
    //  Plan 없이는 Tab 기능 수행 못하는데, 이럴거면 Plan에 List<Tab> 양방향 연관관계를 거는게 어떤지
    @Transactional
    public TabChangeNameResponse changeName(TabChangeNameServiceRequest request) {
        Plan plan = planMembershipService.getPlanAfterValidateAuthorization(request.getPlanId(), request.getMemberId());
        List<Tab> tabs = tabRepository.findAllByPlanId(plan.getId());
        TabGroup tabGroup = new TabGroup(plan.getId(), tabs);
        Tab tab = tabGroup.changeName(request.getTabId(), request.getName());

        return TabChangeNameResponse.builder()
            .id(tab.getId())
            .name(tab.getName())
            .build();
    }

    @Transactional
    public Long delete(TabDeleteServiceRequest request) {
        Long planId = request.getPlanId();
        Long tabId = request.getTabId();
        Long memberId = request.getMemberId();

        boolean isAdmin = planMembershipService.validatePlanOwner(planId, memberId);
        if (!isAdmin) {
            throw new ApiException(ErrorCode.AUTHORIZATION_FAIL);
        }

        List<Tab> tabs = tabRepository.findAllByPlanId(planId);
        TabGroup tabGroup = new TabGroup(planId, tabs);
        tabGroup.deleteById(tabId);

        Tab target = tabGroup.findById(tabId);
        target.delete();
        return tabId;
    }

    public TabRetrieveResponse retrieve(Long id, Long userId) {
        return null;
    }

}
