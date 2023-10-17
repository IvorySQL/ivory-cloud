package com.highgo.cloud.auth.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 将返回内容写入HttpServletResponse
 * @author chushaolin
 *
 */
public class WriteResponse {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void write(HttpServletResponse httpServletResponse, RestResponse restResponse) throws IOException {
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        PrintWriter out = httpServletResponse.getWriter();
        out.write(mapper.writeValueAsString(restResponse));
        out.flush();
        out.close();
    }
}
