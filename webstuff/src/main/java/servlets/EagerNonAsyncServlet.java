package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "eagerNonAsyncServlet", urlPatterns = "/eager", loadOnStartup = 1)
public class EagerNonAsyncServlet extends HttpServlet {
    public EagerNonAsyncServlet() {
        System.out.println("Now Intializing EagerNonAsyncServlet from " + Thread.currentThread().getName());
        System.out.println("The stacktrace is for EagerNonAsyncServlet() is:");
        StackTraceUtil.printStack();
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Inside EagerNonAsyncServlet doGet" + Thread.currentThread().getName());
        request.getRequestDispatcher("/WEB-INF/jsp/nonAsync.jsp").forward(request, response);
        System.out.println("Finished request.getRequestDispatcher(\"/WEB-INF/jsp/nonAsync.jsp\").forward(request, response) now leaving NonAsyncServlet.doGet");
    }
}
