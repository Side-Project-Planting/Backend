package com.example.planservice.domain.tab.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.example.planservice.config.TestConfig;
import com.example.planservice.domain.plan.Plan;
import com.example.planservice.domain.plan.repository.PlanRepository;
import com.example.planservice.domain.tab.Tab;

@SpringBootTest
@Import(TestConfig.class)
@Transactional
class TabRepositoryTest {
    @Autowired
    TabRepository tabRepository;

    @Autowired
    PlanRepository planRepository;

    @Test
    @DisplayName("플랜 아이디로 모든 Tab을 조회한다")
    void findAllByPlanId() {
        // given
        final Plan plan = Plan.builder()
            .build();
        planRepository.save(plan);

        final Tab tab1 = Tab.builder()
            .title("탭1")
            .plan(plan)
            .build();
        final Tab tab2 = Tab.builder()
            .title("탭2")
            .plan(plan)
            .build();
        final Tab tab3 = Tab.builder()
            .title("탭3")
            .build();
        tabRepository.saveAll(List.of(tab1, tab2, tab3));

        // when
        final List<Tab> tabs = tabRepository.findAllByPlanId(plan.getId());

        // then
        assertThat(tabs).hasSize(2)
            .extracting("title")
            .contains("탭1", "탭2");
    }
}
