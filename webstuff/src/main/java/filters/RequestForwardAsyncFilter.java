package filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestForwardAsyncFilter implements Filter {
    private String name;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        System.out.println("initializing filter " + this.name + " " + counter.incrementAndGet() + " time(s)");
        name = filterConfig.getFilterName();
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        System.out.println("Inside " + name + ".doFilter");
        filterChain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) servletRequest),
                new HttpServletResponseWrapper((HttpServletResponse) servletResponse));

        if (servletRequest.isAsyncSupported() && servletRequest.isAsyncStarted()) {
            final AsyncContext asyncContext = servletRequest.getAsyncContext();
            System.out.println("Finishing " + name + ".doFilter and asyncContext.hasOriginalRequestAndResponse is " + asyncContext.hasOriginalRequestAndResponse());
            return;
        }

        System.out.println("Finishing " + name + ".doFilter");
    }

    @Override
    public void destroy() { }
}
