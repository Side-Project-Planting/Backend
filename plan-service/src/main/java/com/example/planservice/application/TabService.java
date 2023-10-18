package com.example.planservice.application;

import static com.example.planservice.domain.tab.Tab.TAB_MAX_SIZE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.application.dto.TabServiceResponse;
import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.domain.tab.Tab;
import com.example.planservice.domain.tab.TabGroup;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.TabChangeNameRequest;
import com.example.planservice.presentation.dto.request.TabChangeOrderRequest;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.response.TabRetrieveResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TabService {

    private final PlanRepository planRepository;
    private final MemberOfPlanRepository memberOfPlanRepository;
    private final TabRepository tabRepository;

    @Transactional
    public Long create(Long memberId, TabCreateRequest request) {
        try {
            Plan plan = getPlanAfterCheckAuthorization(request.getPlanId(), memberId);
            List<Tab> tabsOfPlan = tabRepository.findAllByPlanId(plan.getId());
            if (tabsOfPlan.size() >= TAB_MAX_SIZE) {
                throw new ApiException(ErrorCode.TAB_SIZE_INVALID);
            }

            boolean isDuplicatedName = tabsOfPlan.stream()
                .anyMatch(tab -> Objects.equals(tab.getName(), request.getName()));
            if (isDuplicatedName) {
                throw new ApiException(ErrorCode.TAB_NAME_DUPLICATE);
            }

            Tab tab = Tab.create(plan, request.getName());

            Optional<Tab> lastOpt = findLastTab(tabsOfPlan);
            if (lastOpt.isPresent()) {
                Tab last = lastOpt.get();
                last.connect(tab);
            }

            Tab savedTab = tabRepository.save(tab);
            return savedTab.getId();
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ApiException(ErrorCode.REQUEST_CONFLICT);
        }
    }

    @Transactional
    public List<Long> changeOrder(Long memberId, TabChangeOrderRequest request) {
        Plan plan = getPlanAfterCheckAuthorization(request.getPlanId(), memberId);

        List<Tab> tabs = tabRepository.findAllByPlanId(request.getPlanId());
        TabGroup tabGroup = new TabGroup(plan, tabs);
        List<Tab> result = tabGroup.changeOrder(request.getTargetId(), request.getNewPrevId());
        return result.stream().map(Tab::getId).toList();
    }

    // TODO 해당 로직 역시 락이 필요한듯. 소속된 Plan을 기준으로. 동시에 이름 두 개 들어와버리면 GG -> 학습 후 구현
    // TODO Tab은 Plan에 강하게 의존관계를 가짐. 단독으로 쓰일일도 잘 없음.(앞으로도 그럴거로 예상됨)
    //  Plan 없이는 Tab 기능 수행 못하는데, 이럴거면 Plan에 List<Tab> 양방향 연관관계를 거는게 어떤지
    @Transactional
    public TabServiceResponse changeName(Long memberId, TabChangeNameRequest request) {
        Plan plan = getPlanAfterCheckAuthorization(request.getPlanId(), memberId);
        List<Tab> tabs = tabRepository.findAllByPlanId(plan.getId());
        TabGroup tabGroup = new TabGroup(plan, tabs);

        tabGroup.findByName(request.getName()).ifPresent(m -> {
            throw new ApiException(ErrorCode.TAB_NAME_DUPLICATE);
        });

        Tab target = tabGroup.findById(request.getTabId());
        target.changeName(request.getName());

        return TabServiceResponse.builder()
            .id(target.getId())
            .name(target.getName())
            .build();
    }

    @NotNull
    private Plan getPlanAfterCheckAuthorization(Long planId, Long memberId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));

        boolean existsInPlan = memberOfPlanRepository.existsByPlanIdAndMemberId(plan.getId(), memberId);
        if (!existsInPlan) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN);
        }
        return plan;
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
