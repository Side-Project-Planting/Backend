package com.example.planservice.application;

import org.springframework.stereotype.Service;

import com.example.planservice.presentation.dto.request.TabCreateRequest;
import com.example.planservice.presentation.dto.response.TabRetrieveResponse;

@Service
public class TabService {
    public Long create(Long userId, TabCreateRequest request) {
        return null;
    }

    public TabRetrieveResponse retrieve(Long id, Long userId) {
        return null;
    }
}
