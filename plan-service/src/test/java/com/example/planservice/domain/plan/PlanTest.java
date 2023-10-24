package com.example.planservice.domain.plan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.planservice.domain.member.Member;

class PlanTest {

    @Test
    @DisplayName("플랜을 생성한다")
    void createPlan() {
        // given
        final Member member = mock(Member.class);

        // when
        final Plan plan = Plan.builder()
                              .owner(member)
                              .title("플랜 제목")
                              .intro("플랜 소개")
                              .isPublic(true)
                              .starCnt(0)
                              .viewCnt(0)
                              .isDeleted(false)
                              .build();

        // then
        assertThat(plan.getOwner()).isEqualTo(member);
        assertThat(plan.getTitle()).isEqualTo("플랜 제목");
        assertThat(plan.getIntro()).isEqualTo("플랜 소개");
        assertThat(plan.isPublic()).isTrue();
    }
}
