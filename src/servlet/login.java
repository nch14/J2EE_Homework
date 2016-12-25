package servlet;

import util.Database;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by ChenhaoNee on 2016/12/25.
 */
@WebServlet(name = "login", urlPatterns = "/login")
public class login extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = Database.getConnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = connection.prepareStatement("select pwd from user where username=?");
            stmt.setString(1, request.getParameter("username"));
            result = stmt.executeQuery();
            while (result.next()) {
                System.out.println("读到的密码为" + result.getString(1));
                if (result.getString(1).equals(request.getParameter("pwd"))) {
                    addSession(request, response);
                    updateOnlineCount();

                    response.sendRedirect("/course");
                } else
                    response.sendRedirect("/user/login_error.html");
            }
            response.sendRedirect("/user/login_error.html");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession httpSession = request.getSession(false);


        // Logout action removes session, but the cookie remains
        if (null != request.getParameter("Logout")) {
            if (null != httpSession) {
                httpSession.invalidate();
                httpSession = null;
            }
        }else {
            updateTotalCount();
        }

        System.out.println("logout"+request.getParameter("Logout"));

        if (httpSession != null && httpSession.getAttribute("login") != null)
            response.sendRedirect("/course");
        else
            response.sendRedirect("/user/login.html");

    }

    private void addSession(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        HttpSession session = request.getSession();
        session.setAttribute("login", username);
        request.setAttribute("login", username);
    }

    private void updateOnlineCount() {
        ServletContext Context = getServletContext();
        int online = Integer.parseInt((String) Context.getAttribute("online"));
        online++;
        Context.setAttribute("online", "" + online);
    }

    private void updateTotalCount() {
        ServletContext Context = getServletContext();
        int total = Integer.parseInt((String) Context.getAttribute("total"));
        total++;
        Context.setAttribute("total", "" + total);

    }

}
