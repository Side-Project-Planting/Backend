package com.example.planservice.domain.tab;

import static com.example.planservice.domain.tab.Tab.TAB_MAX_SIZE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import lombok.Getter;

public class TabGroup {
    private Map<Long, Tab> hash;

    @Getter
    private final Tab first;

    public TabGroup(Long planId, List<Tab> tabs) {
        validate(planId, tabs);

        this.hash = new HashMap<>();
        for (Tab tab : tabs) {
            hash.put(tab.getId(), tab);
        }

        this.first = tabs.stream()
            .filter(Tab::isFirst)
            .findFirst()
            .orElseThrow(() -> new ApiException(ErrorCode.SERVER_ERROR));
    }

    public void add(Tab newPrev, Tab target) {
        if (hash.size() >= TAB_MAX_SIZE) {
            throw new ApiException(ErrorCode.TAB_SIZE_INVALID);
        }
        checkDuplicatedName(target.getTitle());

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
        hash.remove(target.getId());
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
            .anyMatch(tab -> Objects.equals(name, tab.getTitle()));
        if (duplicated) {
            throw new ApiException(ErrorCode.TAB_NAME_DUPLICATE);
        }
    }

    public List<Tab> getSortedTabs() {
        List<Tab> result = new ArrayList<>();

        Tab temp = first;
        while (temp != null) {
            result.add(temp);
            temp = temp.getNext();
        }
        return result;
    }

    private void validate(Long planId, List<Tab> tabs) {
        if (tabs.isEmpty() || TAB_MAX_SIZE < tabs.size()) {
            throw new ApiException(ErrorCode.TAB_SIZE_INVALID);
        }
        if (!Objects.equals(planId, tabs.get(0).getPlan().getId())) {
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

    public Tab changeName(Long tabId, String name) {
        checkDuplicatedName(name);
        Tab target = findById(tabId);
        target.changeName(name);
        return target;
    }
}
