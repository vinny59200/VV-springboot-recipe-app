package com.vv.recipe.vv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Slf4j
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static String path;
    private static String message;

    public static void setPath(String path) {
        RestAuthenticationEntryPoint.path = path;
    }

    public static void setMessage(String message) {
        RestAuthenticationEntryPoint.message = message;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter out = response.getWriter();
        out.print("{\n" +
                "  \"timestamp\": \"" + new Date() + "\",\n" +
                "  \"status\": " + 401 + ",\n" +
                "  \"error\": \"Unauthorized\",\n" +
                "  \"message\": \"Not authorized\",\n" +
                "  \"path\": \"" + request.getServletPath() + "\"\n" +
                "}");
        out.flush();
        out.close();
    }


}