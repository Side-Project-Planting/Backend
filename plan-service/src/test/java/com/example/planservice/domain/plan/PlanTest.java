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
        Member member = mock(Member.class);

        // when
        Plan plan = Plan.builder()
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

    @Test
    @DisplayName("플랜을 수정한다")
    void updatePlan() {
        //given
        Member prevOwner = mock(Member.class);
        Member nextOwner = mock(Member.class);
        Plan plan = Plan.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .owner(prevOwner)
            .isPublic(true)
            .build();

        //when
        plan.update("수정된 플랜 제목", "수정된 플랜 소개", nextOwner, false);

        //then
        assertThat(plan.getTitle()).isEqualTo("수정된 플랜 제목");
        assertThat(plan.getIntro()).isEqualTo("수정된 플랜 소개");
        assertThat(plan.getOwner()).isEqualTo(nextOwner);
        assertThat(plan.isPublic()).isFalse();
    }

    @Test
    @DisplayName("플랜을 삭제한다")
    void deletePlan() {
        //given
        Plan plan = Plan.builder()
            .title("플랜 제목")
            .intro("플랜 소개")
            .isPublic(true)
            .build();

        //when
        plan.softDelete();

        //then
        assertThat(plan.isDeleted()).isTrue();
    }

}
