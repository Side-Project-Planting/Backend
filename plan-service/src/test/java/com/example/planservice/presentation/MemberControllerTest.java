package com.example.planservice.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.planservice.application.MemberService;
import com.example.planservice.application.dto.MemberRegisterResponse;
import com.example.planservice.presentation.dto.request.MemberRegisterRequest;
import com.example.planservice.presentation.dto.response.MemberFindResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @Test
    @DisplayName("Member를 생성한다")
    void create() throws Exception {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder()
            .name("김태훈")
            .email("ds@naver.com")
            .receiveEmails(true)
            .profileUri("https://sda.com")
            .build();

        MemberRegisterResponse response = MemberRegisterResponse.builder()
            .id(1L)
            .name("김태훈")
            .email("ds@naver.com")
            .receiveEmails(true)
            .profileUri("https://sda.com")
            .build();

        // stub
        when(memberService.register(any(MemberRegisterRequest.class)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(post("/members")
                .header("X-User-Id", -1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/members/" + response.getId()))
            .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    @DisplayName("로그인하지 않아도 Member를 생성할 수 있다")
    void createFailUnAuthentication() throws Exception {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder()
            .name("김태훈")
            .email("ds@naver.com")
            .receiveEmails(true)
            .profileUri("https://sda.com")
            .build();

        MemberRegisterResponse response = MemberRegisterResponse.builder()
            .build();

        // stub
        when(memberService.register(any(MemberRegisterRequest.class)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/members/" + response.getId()))
            .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    @DisplayName("Member를 조회한다")
    void testFindMember() throws Exception {
        // given
        Long targetMemberId = 1L;
        MemberFindResponse response = MemberFindResponse.builder()
            .id(targetMemberId)
            .name("김태훈")
            .email("ds@naver.com")
            .profileUri("https://sda.com")
            .build();

        // stub
        when(memberService.find(targetMemberId))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/members/" + targetMemberId)
                .header("X-User-Id", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.name").value(response.getName()))
            .andExpect(jsonPath("$.email").value(response.getEmail()))
            .andExpect(jsonPath("$.profileUri").value(response.getProfileUri()));
    }

    @Test
    @DisplayName("로그인한 유저만 Member를 조회할 수 있다")
    void testFindMemberFailNotLogin() throws Exception {
        // given
        Long targetMemberId = 1L;

        // when & then
        mockMvc.perform(get("/members/" + targetMemberId))
            .andExpect(status().isUnauthorized());
    }
}
