package servlets;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet(name = "asyncServlet", urlPatterns = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
    private static AtomicInteger id = new AtomicInteger(0);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final int curId = id.getAndIncrement();

        System.out.println("Inside AsyncServlet.doGet id : " + curId + " and isAsyncStarted : " + request.isAsyncStarted());

        final AsyncContext asyncContext = request.getParameter("unwrap") == null ? request.startAsync() : request.startAsync(request, response);
        final long timeout = request.getParameter("timeout") == null ? 5000L : Long.parseLong(request.getParameter("timeout"));
        asyncContext.setTimeout(timeout);

        System.out.println("Starting async thread for id : " + id);
        new Thread(() -> asyncTask(curId, asyncContext)).start();

    }

    private static void asyncTask(final int id, final AsyncContext asyncContext) {
        System.out.println("Async Thread started for id : " + id);
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final HttpServletRequest httpServletRequest = (HttpServletRequest) asyncContext.getRequest();
        System.out.println("Finished sleeping 5 seconds for id : " + id + " which maps to URL : " + httpServletRequest.getRequestURL());

        System.out.println("running asyncContext.dispatch(\"/WEB-INF/jsp/async.jsp\") for id : " + id);
        asyncContext.dispatch("/WEB-INF/jsp/async.jsp");
        System.out.println("Async Thread finished for id " + id);
    }
}
