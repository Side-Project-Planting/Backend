package com.example.planservice.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateResponse {
    private Long id;

    @Builder
    private CreateResponse(Long id) {
        this.id = id;
    }

    public static CreateResponse of(Long id) {
        return CreateResponse.builder()
            .id(id)
            .build();
    }
}
