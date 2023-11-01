package com.example.auth.client;

import com.example.auth.client.dto.MemberRegisterRequest;
import com.example.auth.client.dto.MemberRegisterResponse;

public interface MemberServiceClient {
    MemberRegisterResponse register(MemberRegisterRequest request);

}
