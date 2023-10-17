package com.example.planservice.domain.tab;

import static com.example.planservice.domain.tab.Tab.TAB_MAX_SIZE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.example.planservice.domain.plan.Plan;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import lombok.Getter;

public class TabGroup {
    private Map<Long, Tab> hash;

    @Getter
    private Tab first;

    public TabGroup(Plan plan, List<Tab> tabs) {
        validate(plan, tabs);

        this.hash = new HashMap<>();
        for (Tab tab : tabs) {
            hash.put(tab.getId(), tab);
        }

        this.first = tabs.stream()
            .filter(Tab::isFirst)
            .findAny()
            .orElseThrow(() -> new ApiException(ErrorCode.SERVER_ERROR));
    }

    private void validate(Plan plan, List<Tab> tabs) {
        if (tabs.isEmpty() || TAB_MAX_SIZE < tabs.size()) {
            throw new ApiException(ErrorCode.TAB_SIZE_INVALID);
        }
        if (!Objects.equals(plan, tabs.get(0).getPlan())) {
            throw new ApiException(ErrorCode.PLAN_TAB_MISMATCH);
        }
    }

    public List<Tab> changeOrder(long targetId, long newPrevId) {
        if (targetId == newPrevId) {
            throw new RuntimeException("옮기려는 대상의 ID와 옮길 위치 이전에 위치한 탭의 ID는 동일할 수 없습니다");
        }
        Tab target = findById(targetId);
        if (target == first) {
            throw new RuntimeException("첫 번째 탭은 순서를 변경할 수 없습니다");
        }

        Tab newPrev = findById(newPrevId);
        Tab oldPrev = findPrev(target);

        oldPrev.connect(target.getNext());
        target.connect(newPrev.getNext());
        newPrev.connect(target);

        List<Tab> result = new ArrayList<>();
        Tab temp = first;
        while (temp != null) {
            result.add(temp);
            temp = temp.getNext();
        }
        return result;
    }

    public Tab findById(long id) {
        if (!hash.containsKey(id)) {
            throw new ApiException(ErrorCode.TAB_NOT_FOUND_IN_PLAN);
        }
        return hash.get(id);
    }

    private Tab findPrev(@NotNull Tab target) {
        return hash.values().stream()
            .filter(tab -> Objects.equals(tab.getNext(), target))
            .findAny()
            .orElseThrow(() -> new ApiException(ErrorCode.SERVER_ERROR));
    }

}
