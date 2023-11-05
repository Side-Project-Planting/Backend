package com.example.planservice.presentation.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskUpdateRequestTest {
    @Test
    @DisplayName("TaskUpdateRequest를 생성한다")
    void testCreateRequest() throws Exception {
        // when
        TaskUpdateRequest request = TaskUpdateRequest.builder()
            .labels(List.of(1L, 2L))
            .build();

        // then
        assertThat(request.getLabels()).hasSize(2);
    }

    @Test
    @DisplayName("태스크의 라벨이 없으면 비어있는 라벨을 반환한다")
    void testCreateRequestIfLabelIsEmpty() throws Exception {
        // when
        TaskUpdateRequest request = TaskUpdateRequest.builder().build();

        // then
        assertThat(request.getLabels()).isEmpty();
    }
}