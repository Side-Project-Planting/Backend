package com.example.planservice.domain.tab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;

@SpringBootTest
@Transactional
class TabGroupTest {
    @Autowired
    PlanRepository planRepository;

    @Autowired
    TabRepository tabRepository;

    @Test
    @DisplayName("탭그룹을 생성한다")
    void create() {
        // given
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "탭1", null);
        Tab tab2 = createTab(plan, "탭2", tab1);
        Tab tab3 = createTab(plan, "탭3", tab2);
        Tab tab4 = createTab(plan, "탭4", tab3);
        Tab tab5 = createTab(plan, "탭5", tab4);

        // when
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab1, tab2, tab3, tab4, tab5));

        // then
        assertThat(tabGroup.getFirst()).isEqualTo(tab1);
        assertThat(tabGroup.findById(tab1.getId())).isEqualTo(tab1);
        assertThat(tabGroup.findById(tab2.getId())).isEqualTo(tab2);
        assertThat(tabGroup.findById(tab3.getId())).isEqualTo(tab3);
        assertThat(tabGroup.findById(tab4.getId())).isEqualTo(tab4);
        assertThat(tabGroup.findById(tab5.getId())).isEqualTo(tab5);
    }

    @Test
    @DisplayName("하나의 탭그룹에는 다섯개까지의 탭만 가능하다")
    void createFailSizeOver() {
        // given
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "탭1", null);
        Tab tab2 = createTab(plan, "탭2", tab1);
        Tab tab3 = createTab(plan, "탭3", tab2);
        Tab tab4 = createTab(plan, "탭4", tab3);
        Tab tab5 = createTab(plan, "탭5", tab4);
        Tab tab6 = createTab(plan, "탭6", tab5);

        // when & then
        assertThatThrownBy(() -> new TabGroup(plan.getId(), List.of(tab1, tab2, tab3, tab4, tab5, tab6)))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_SIZE_INVALID.getMessage());
    }

    @Test
    @DisplayName("하나의 탭그룹에는 한 개 이상의 탭이 있어야 한다")
    void createFailSizeDown() {
        // given
        Plan plan = createPlan();

        // when & then
        assertThatThrownBy(() -> new TabGroup(plan.getId(), Collections.emptyList()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_SIZE_INVALID.getMessage());
    }


    @Test
    @DisplayName("탭그룹 생성 시 입력되는 Plan과 Tab들의 Plan은 동일해야 한다")
    void createFailNotSamePlan() {
        // given
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "탭1", null);
        Tab tab2 = createTab(plan, "탭2", tab1);
        Tab tab3 = createTab(plan, "탭3", tab2);
        Tab tab4 = createTab(plan, "탭4", tab3);

        Plan otherPlan = createPlan();

        // when & then
        assertThatThrownBy(() -> new TabGroup(otherPlan.getId(), List.of(tab1, tab2, tab3, tab4)))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.PLAN_TAB_MISMATCH.getMessage());
    }

    @Test
    @DisplayName("탭그룹은 시작 탭이 꼭 있어야 한다")
    void createFailNotHaveFirstTab() {
        Plan plan = createPlan();
        Tab tab = Tab.builder()
            .first(false)
            .plan(plan)
            .build();

        // when & then
        assertThatThrownBy(() -> new TabGroup(plan.getId(), List.of(tab)))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.SERVER_ERROR.getMessage());
    }

    @Test
    @DisplayName("탭들의 순서를 변경한다")
    void changeOrder() {
        // given
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "탭1", null);
        Tab tab2 = createTab(plan, "탭2", tab1);
        Tab tab3 = createTab(plan, "탭3", tab2);
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab1, tab2, tab3));

        // when
        List<Tab> tabs = tabGroup.changeOrder(tab3.getId(), tab1.getId());

        // then
        assertThat(tabs).hasSize(3)
            .containsExactly(tab1, tab3, tab2);
    }

    @Test
    @DisplayName("탭이 가득차 있을 때 탭의 순서를 변경한다")
    void changeOrderFullTabInPlan() {
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "탭1", null);
        Tab tab2 = createTab(plan, "탭2", tab1);
        Tab tab3 = createTab(plan, "탭3", tab2);
        Tab tab4 = createTab(plan, "탭4", tab3);
        Tab tab5 = createTab(plan, "탭5", tab4);

        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab1, tab2, tab3, tab4, tab5));

        // when
        List<Tab> tabs = tabGroup.changeOrder(tab5.getId(), tab1.getId());

        // then
        assertThat(tabs).hasSize(5)
            .containsExactly(tab1, tab5, tab2, tab3, tab4);
    }

    @Test
    @DisplayName("첫 번째 순서의 탭은 변경할 수 없다")
    void changeOrderFailAboutFirstTab() {
        // given
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "탭1", null);
        Tab tab2 = createTab(plan, "탭2", tab1);
        Tab tab3 = createTab(plan, "탭3", tab2);
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab1, tab2, tab3));

        // when
        assertThatThrownBy(() -> tabGroup.changeOrder(tab1.getId(), tab2.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_ORDER_FIXED.getMessage());
    }

    @Test
    @DisplayName("타겟 탭과 옮기려는 위치 이전에 위치하는 탭은 동일할 수 없다")
    void changeOrderFailCantSameTargetAndNewPrev() {
        // given
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "탭1", null);
        Tab tab2 = createTab(plan, "탭2", tab1);
        Tab tab3 = createTab(plan, "탭3", tab2);
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab1, tab2, tab3));

        // when & then
        assertThatThrownBy(() -> tabGroup.changeOrder(tab2.getId(), tab2.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TARGET_TAB_SAME_AS_NEW_PREV.getMessage());
    }

    @Test
    @DisplayName("탭을 찾는다")
    void findById() {
        // given
        Plan plan = createPlan();
        Tab tab = createTab(plan, "탭", null);
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab));

        assertThat(tabGroup.findById(tab.getId())).isEqualTo(tab);
    }

    @Test
    @DisplayName("찾으려는 탭이 없으면 예외를 반환한다")
    void findByIdFailNotFound() {
        // given
        Plan plan = createPlan();
        Tab tab = createTab(plan, "탭", null);
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab));

        Tab otherTab = createTab(null, "다른탭", null);

        // when & then
        assertThatThrownBy(() -> tabGroup.findById(otherTab.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_NOT_FOUND_IN_PLAN.getMessage());
    }

    @Test
    @DisplayName("마지막에 탭을 추가한다")
    void addLast() {
        // given
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "탭1", null);
        Tab tab2 = createTab(plan, "탭2", tab1);
        Tab tab3 = createTab(plan, "탭3", tab2);
        Tab tab4 = createTab(plan, "탭4", tab3);

        Tab addedTab = Tab.builder()
            .build();
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab1, tab2, tab3, tab4));

        // when
        tabGroup.addLast(addedTab);

        // then
        assertThat(tab4.getNext()).isEqualTo(addedTab);
        assertThat(addedTab.getNext()).isNull();
    }

    @Test
    @DisplayName("탭그룹에는 개수 제한이 있다")
    void addLastFailTabSizeLimit() {
        // given
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "탭1", null);
        Tab tab2 = createTab(plan, "탭2", tab1);
        Tab tab3 = createTab(plan, "탭3", tab2);
        Tab tab4 = createTab(plan, "탭4", tab3);
        Tab tab5 = createTab(plan, "탭5", tab4);

        Tab addedTab = Tab.builder()
            .build();
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab1, tab2, tab3, tab4, tab5));

        // when & then
        assertThatThrownBy(() -> tabGroup.addLast(addedTab))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_SIZE_INVALID.getMessage());
    }

    @Test
    @DisplayName("탭그룹에는 중복된 이름이 있을 수 없다")
    void addLastFailTabNameDuplicated() {
        // given
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "탭1", null);

        Tab addedTab = Tab.builder()
            .title("탭1")
            .build();
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab1));

        // when & then
        assertThatThrownBy(() -> tabGroup.addLast(addedTab))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_NAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("탭 삭제 시 자신의 prev탭과 next탭을 연결한다")
    void checkConnectingIfTabDelete() {
        // given
        Plan plan = createPlan();
        Tab first = createTab(plan, "TODO", null);
        Tab second = createTab(plan, "두번째", first);
        Tab third = createTab(plan, "세번째", second);
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(first, second, third));

        // when
        tabGroup.deleteById(second.getId());

        // then
        assertThat(first.getNext()).isEqualTo(third);
    }

    @Test
    @DisplayName("첫 번째 탭은 삭제할 수 없다")
    void cannotDeleteFirstTab() {
        // given
        Plan plan = createPlan();
        Tab tab1 = createTab(plan, "TODO", null);
        TabGroup tabGroup = new TabGroup(plan.getId(), List.of(tab1));

        // when
        assertThatThrownBy(() -> tabGroup.deleteById(tab1.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_CANNOT_DELETE.getMessage());
    }


    @NotNull
    private Plan createPlan() {
        Plan plan = Plan.builder()
            .build();
        planRepository.save(plan);
        return plan;
    }

    private Tab createTab(Plan plan, String title, Tab prev) {
        Tab.TabBuilder tabBuilder = Tab.builder()
            .plan(plan)
            .title(title);

        Tab tab;
        if (prev == null) {
            tab = tabBuilder.first(true)
                .build();
        } else {
            tab = tabBuilder.build();
            prev.connect(tab);
        }

        tabRepository.save(tab);
        return tab;
    }

}
