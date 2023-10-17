package com.example.planservice.application;

import static com.example.planservice.domain.tab.Tab.TAB_MAX_SIZE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.memberofplan.repository.MemberOfPlanRepository;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
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

    private final PlanRepository planRepository;
    private final MemberOfPlanRepository memberOfPlanRepository;
    private final TabRepository tabRepository;

    @Transactional
    public Long create(Long userId, TabCreateRequest request) {
        try {
            Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));

            boolean existsInPlan = memberOfPlanRepository.existsByPlanIdAndMemberId(plan.getId(), userId);
            if (!existsInPlan) {
                throw new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN);
            }

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
        Plan plan = planRepository.findById(request.getPlanId())
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));

        boolean existsInPlan = memberOfPlanRepository.existsByPlanIdAndMemberId(plan.getId(), memberId);
        if (!existsInPlan) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN);
        }

        List<Tab> tabs = tabRepository.findAllByPlanId(request.getPlanId());
        TabGroup tabGroup = new TabGroup(plan, tabs);
        List<Tab> result = tabGroup.changeOrder(request.getTargetId(), request.getNewPrevId());
        return result.stream().map(Tab::getId).toList();
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
