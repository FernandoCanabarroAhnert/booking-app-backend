package com.fernandocanabarro.booking_app_backend.utils;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.LoginRequestDTO;

public class AccessTokenUtils {

    public static String obtainAccessToken(String email, String password, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequestDTO(email, password)))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("token").toString();
    }

}
