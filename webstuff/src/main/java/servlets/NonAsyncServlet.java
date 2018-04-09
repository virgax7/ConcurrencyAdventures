package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "nonAsyncServlet", urlPatterns = "/nonAsync")
public class NonAsyncServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Inside NonAsyncServlet doGet");
        request.getRequestDispatcher("/WEB-INF/jsp/nonAsync.jsp").forward(request, response);
        System.out.println("Finished request.getRequestDispatcher(\"/WEB-INF/jsp/nonAsync.jsp\").forward(request, response) now leaving NonAsyncServlet.doGet");
    }
}
