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
    private final Tab first;

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

    public void add(Tab newPrev, Tab target) {
        if (hash.size() >= TAB_MAX_SIZE) {
            throw new ApiException(ErrorCode.TAB_SIZE_INVALID);
        }
        checkDuplicatedName(target.getName());

        Tab temp = newPrev.getNext();
        newPrev.connect(target);
        target.connect(temp);
    }

    public void addLast(Tab target) {
        Tab last = findPrev(null);
        add(last, target);
    }

    public List<Tab> changeOrder(long targetId, long newPrevId) {
        if (targetId == newPrevId) {
            throw new ApiException(ErrorCode.TARGET_TAB_SAME_AS_NEW_PREV);
        }
        Tab target = findById(targetId);
        if (target == first) {
            throw new ApiException(ErrorCode.TAB_ORDER_FIXED);
        }

        Tab oldPrev = findPrev(target);
        oldPrev.connect(target.getNext());

        Tab newPrev = findById(newPrevId);
        add(newPrev, target);

        return getSortedTabs();
    }

    public Tab findById(long id) {
        if (!hash.containsKey(id)) {
            throw new ApiException(ErrorCode.TAB_NOT_FOUND_IN_PLAN);
        }
        return hash.get(id);
    }

    private Tab findPrev(Tab target) {
        return hash.values().stream()
            .filter(tab -> Objects.equals(tab.getNext(), target))
            .findAny()
            .orElseThrow(() -> new ApiException(ErrorCode.SERVER_ERROR));
    }

    private void checkDuplicatedName(@NotNull String name) {
        List<Tab> sortedTabs = getSortedTabs();
        boolean duplicated = sortedTabs.stream()
            .anyMatch(tab -> Objects.equals(name, tab.getName()));
        if (duplicated) {
            throw new ApiException(ErrorCode.TAB_NAME_DUPLICATE);
        }
    }

    private List<Tab> getSortedTabs() {
        List<Tab> result = new ArrayList<>();

        Tab temp = first;
        while (temp != null) {
            result.add(temp);
            temp = temp.getNext();
        }
        return result;
    }

    private void validate(Plan plan, List<Tab> tabs) {
        if (tabs.isEmpty() || TAB_MAX_SIZE < tabs.size()) {
            throw new ApiException(ErrorCode.TAB_SIZE_INVALID);
        }
        if (!Objects.equals(plan, tabs.get(0).getPlan())) {
            throw new ApiException(ErrorCode.PLAN_TAB_MISMATCH);
        }
    }

    public void deleteById(Long tabId) {
        List<Tab> sortedTabs = getSortedTabs();
        Tab prev = sortedTabs.get(0);
        if (Objects.equals(prev.getId(), tabId)) {
            throw new ApiException(ErrorCode.TAB_CANNOT_DELETE);
        }

        for (Tab tab : sortedTabs) {
            if (Objects.equals(tabId, tab.getId())) {
                prev.connect(tab.getNext());
                return;
            }
            prev = tab;
        }
    }
}
