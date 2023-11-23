package com.example.planservice.presentation.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TabChangeOrderResponse {
    private List<Long> sortedTabs;
}
