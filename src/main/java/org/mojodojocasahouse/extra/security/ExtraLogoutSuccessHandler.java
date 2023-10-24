package org.mojodojocasahouse.extra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExtraLogoutSuccessHandler extends HttpStatusReturningLogoutSuccessHandler {
    public ExtraLogoutSuccessHandler() {
        super();
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", "Logged out successfully!");

        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
    }
}
