package com.example.planservice.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import com.example.planservice.presentation.dto.request.TabChangeRequest;
import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.response.TabRetrieveResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TabService {
    private static final int TAB_MAX_SIZE = 5;

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
                throw new ApiException(ErrorCode.TAB_SIZE_LIMIT);
            }

            boolean isDuplicatedName = tabsOfPlan.stream()
                .anyMatch(tab -> Objects.equals(tab.getName(), request.getName()));
            if (isDuplicatedName) {
                throw new ApiException(ErrorCode.TAB_NAME_DUPLICATE);
            }

            Tab tab = Tab.builder()
                .name(request.getName())
                .plan(plan)
                .build();

            Optional<Tab> lastOpt = findLastTab(tabsOfPlan);
            if (lastOpt.isPresent()) {
                tab.makeNotFirst();
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
    public List<Long> changeOrder(Long memberId, TabChangeRequest request) {
        Plan plan = planRepository.findById(request.getPlanId())
            .orElseThrow(() -> new ApiException(ErrorCode.PLAN_NOT_FOUND));

        boolean existsInPlan = memberOfPlanRepository.existsByPlanIdAndMemberId(plan.getId(), memberId);
        if (!existsInPlan) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND_IN_PLAN);
        }

        List<Tab> tabs = tabRepository.findAllByPlanId(request.getPlanId());
        Map<Long, Tab> hash = new HashMap<>();
        for (Tab tab : tabs) {
            hash.put(tab.getId(), tab);
        }
        Tab target = getTab(request.getTargetId(), hash);
        Tab newPrev = getTab(request.getNewPrevId(), hash);
        Tab oldPrev = tabs.stream()
            .filter(tab -> Objects.equals(tab.getNext(), target))
            .findAny()
            .orElseThrow(() -> new ApiException(ErrorCode.SERVER_ERROR));
        Tab todoTab = tabs.stream()
            .filter(Tab::isFirst)
            .findAny()
            .orElseThrow(() -> new ApiException(ErrorCode.SERVER_ERROR));

        target.connect(newPrev.getNext());
        newPrev.connect(target);
        oldPrev.connect(null);

        List<Long> result = new ArrayList<>();
        Tab temp = todoTab;
        while (temp != null) {
            result.add(temp.getId());
            temp = temp.getNext();
        }
        return result;
    }

    @NotNull
    private Tab getTab(Long id, Map<Long, Tab> hash) {
        Tab target = hash.get(id);
        if (target == null) {
            throw new ApiException(ErrorCode.TAB_NOT_FOUND);
        }
        return target;
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
