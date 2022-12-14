package bg.softuni.onlinebookstore.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Component
public class MaintenanceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        boolean isClosed = now.getHour() <= 5 && now.getDayOfWeek().equals(DayOfWeek.SUNDAY);

        if (!request.getRequestURI().equals("/maintenance") && isClosed) {
            response.sendRedirect("/maintenance");
            return false;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
