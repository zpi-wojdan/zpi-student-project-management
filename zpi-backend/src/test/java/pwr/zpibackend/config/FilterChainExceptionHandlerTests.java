package pwr.zpibackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

@SpringBootTest
public class FilterChainExceptionHandlerTests {

    @Autowired
    private FilterChainExceptionHandler filterChainExceptionHandler;

    @Test
    public void testDoFilterInternal() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
        doThrow(exception).when(filterChain).doFilter(request, response);

        filterChainExceptionHandler.doFilterInternal(request, response, filterChain);

        verify(response).sendError(HttpStatus.BAD_REQUEST.value(), "Bad Request");
    }
}
