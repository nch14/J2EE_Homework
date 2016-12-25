package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ChenhaoNee on 2016/12/25.
 */
@WebFilter(urlPatterns = "/login")
public class Filter implements javax.servlet.Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        if (req instanceof HttpServletRequest
                && resp instanceof HttpServletResponse) {

            System.out.println(new Date()+"现在的字符集为" + req.getCharacterEncoding());
            req.setCharacterEncoding("utf-8");
            resp.setContentType("text/html; charset=utf-8");
            System.out.println(new Date()+"现在的字符集为" + req.getCharacterEncoding());

            chain.doFilter(req, resp);
            return;

        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
