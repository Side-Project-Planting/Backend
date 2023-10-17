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

import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.domain.tab.repository.TabRepository;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;

@SpringBootTest
@SuppressWarnings("squid:S5778")
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
        Tab tab5 = createTab(plan, "탭5", null);
        Tab tab4 = createTab(plan, "탭4", tab5);
        Tab tab3 = createTab(plan, "탭3", tab4);
        Tab tab2 = createTab(plan, "탭2", tab3);
        Tab tab1 = createTab(plan, "탭1", tab2);

        // when
        TabGroup tabGroup = new TabGroup(plan, List.of(tab1, tab2, tab3, tab4, tab5));

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
        Tab tab6 = createTab(plan, "탭6", null);
        Tab tab5 = createTab(plan, "탭5", tab6);
        Tab tab4 = createTab(plan, "탭4", tab5);
        Tab tab3 = createTab(plan, "탭3", tab4);
        Tab tab2 = createTab(plan, "탭2", tab3);
        Tab tab1 = createTab(plan, "탭1", tab2);

        // when & then
        assertThatThrownBy(() -> new TabGroup(plan, List.of(tab1, tab2, tab3, tab4, tab5, tab6)))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_SIZE_INVALID.getMessage());
    }

    @Test
    @DisplayName("하나의 탭그룹에는 한 개 이상의 탭이 있어야 한다")
    void createFailSizeDown() {
        // given
        Plan plan = createPlan();

        // when & then
        assertThatThrownBy(() -> new TabGroup(plan, Collections.emptyList()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TAB_SIZE_INVALID.getMessage());
    }


    @Test
    @DisplayName("탭그룹 생성 시 입력되는 Plan과 Tab들의 Plan은 동일해야 한다")
    void createFailNotSamePlan() {
        // given
        Plan plan = createPlan();
        Tab tab4 = createTab(plan, "탭4", null);
        Tab tab3 = createTab(plan, "탭3", tab4);
        Tab tab2 = createTab(plan, "탭2", tab3);
        Tab tab1 = createTab(plan, "탭1", tab2);

        Plan otherPlan = createPlan();

        // when & then
        assertThatThrownBy(() -> new TabGroup(otherPlan, List.of(tab1, tab2, tab3, tab4)))
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
        assertThatThrownBy(() -> new TabGroup(plan, List.of(tab)))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.SERVER_ERROR.getMessage());
    }

    @Test
    @DisplayName("탭들의 순서를 변경한다")
    void changeOrder() {
        // given
        Plan plan = createPlan();
        Tab tab3 = createTab(plan, "탭3", null);
        Tab tab2 = createTab(plan, "탭2", tab3);
        Tab tab1 = createTab(plan, "탭1", tab2);
        TabGroup tabGroup = new TabGroup(plan, List.of(tab1, tab2, tab3));

        // when
        List<Tab> tabs = tabGroup.changeOrder(tab3.getId(), tab1.getId());
        assertThat(tabs).hasSize(3)
            .containsExactly(tab1, tab3, tab2);
    }

    @Test
    @DisplayName("첫 번째 순서의 탭은 변경할 수 없다")
    void changeOrderFailAboutFirstTab() {
        // given
        Plan plan = createPlan();
        Tab tab3 = createTab(plan, "탭3", null);
        Tab tab2 = createTab(plan, "탭2", tab3);
        Tab tab1 = createTab(plan, "탭1", tab2);
        TabGroup tabGroup = new TabGroup(plan, List.of(tab1, tab2, tab3));

        // when
        assertThatThrownBy(() -> tabGroup.changeOrder(tab1.getId(), tab2.getId()))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("타겟 탭과 옮기려는 위치 이전에 위치하는 탭은 동일할 수 없습니다")
    void changeOrderFailCantSameTargetAndNewPrev() {
        // given
        Plan plan = createPlan();
        Tab tab3 = createTab(plan, "탭3", null);
        Tab tab2 = createTab(plan, "탭2", tab3);
        Tab tab1 = createTab(plan, "탭1", tab2);
        TabGroup tabGroup = new TabGroup(plan, List.of(tab1, tab2, tab3));

        // when & then
        assertThatThrownBy(() -> tabGroup.changeOrder(tab2.getId(), tab2.getId()))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("탭을 찾는다")
    void findById() {
        // given
        Plan plan = createPlan();
        Tab tab = createTab(plan, "탭", null);
        TabGroup tabGroup = new TabGroup(plan, List.of(tab));

        assertThat(tabGroup.findById(tab.getId())).isEqualTo(tab);
    }

    @Test
    @DisplayName("찾으려는 탭이 없으면 예외를 반환한다")
    void findByIdFailNotFound() {
        // given
        Plan plan = createPlan();
        Tab tab = createTab(plan, "탭", null);
        TabGroup tabGroup = new TabGroup(plan, List.of(tab));

        Tab otherTab = createTab(null, "다른탭", null);

        // when & then
        assertThatThrownBy(() -> tabGroup.findById(otherTab.getId()))
            .isInstanceOf(ApiException.class);
    }

    @NotNull
    private Plan createPlan() {
        Plan plan = Plan.builder().build();
        planRepository.save(plan);
        return plan;
    }

    private Tab createTab(Plan plan, String name, Tab next) {
        Tab tab = Tab.builder()
            .plan(plan)
            .name(name)
            .next(next)
            .first(true)
            .build();
        tabRepository.save(tab);
        if (next != null) {
            next.makeNotFirst();
        }
        return tab;
    }
}
