package servlet;

import model.CourseTest;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by ChenhaoNee on 2016/12/25.
 */
@WebServlet(name = "course", urlPatterns = "/course")
public class course extends HttpServlet {

    private DataSource datasource = null;


    @Override
    public void init() throws ServletException {
        super.init();
        InitialContext jndiContext = null;

        Properties properties = new Properties();
        properties.put(javax.naming.Context.PROVIDER_URL, "jnp:///");
        properties.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        try {
            jndiContext = new InitialContext(properties);
            datasource = (DataSource) jndiContext.lookup("java:comp/env/jdbc/javaee");
            System.out.println("got context");
            System.out.println("About to get ds---J2EE Homework");
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean checkUserValid(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = datasource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = connection.prepareStatement("select pwd from user where username=?");
            stmt.setString(1, (String) req.getParameter("username"));
            result = stmt.executeQuery();
            while (result.next()) {
                System.out.println("读到的密码为" + result.getString(1));
                if (result.getString(1).equals(req.getParameter("pwd")))
                    return true;
                else
                    return false;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        throw new Exception("no such stu id");
    }

    private void showResults(HttpServletRequest req, HttpServletResponse res) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = datasource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = connection.prepareStatement("select courseName,courseRemarks from course_test where userName=?");
            stmt.setString(1, (String) req.getAttribute("login"));
            result = stmt.executeQuery();

            ArrayList courseTests = new ArrayList<>();
            while (result.next()) {
                String courseName = result.getString(1);
                System.out.println(result.getDouble(2));

                Double courseRemarks = result.getDouble(2);
                courseTests.add(new CourseTest(courseName, courseRemarks));
            }
            req.setAttribute("list", courseTests);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            displayMyStocklistPage(req, res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        boolean cookieFound = false;
        System.out.println(request.getParameter("username") + " req");
        System.out.println(request.getParameter("pwd") + " req");

        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");

        boolean valid = false;

        HttpSession session = request.getSession(false);
        if (session==null){
            try {
                valid = checkUserValid(request, response);
            } catch (Exception e) {
                response.sendRedirect("/user/login_error.html");
            }
            if (valid) {
                session = request.getSession();
                session.setAttribute("login", username);
                request.setAttribute("login", username);
                showResults(request, response);
            } else
                response.sendRedirect("/user/login_error.html");
        }else {
            username = (String) session.getAttribute("login");
            request.setAttribute("login", username);
            showResults(request, response);
        }




    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleTask(request,response);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleTask(request,response);

    }

    public void displayMyStocklistPage(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ArrayList<CourseTest> list = (ArrayList) req.getAttribute("list"); // resp.sendRedirect(req.getContextPath()+"/MyStockList");

        /**
         * 是否有未完成测试的科目
         */
        boolean invalid = list.stream().anyMatch(c -> c.getCourseRemarks() == -1);
        res.setContentType("text/html; charset=utf-8");
        PrintWriter out = res.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head> <meta charset=\"utf-8\"> <title>我的课程</title></head><body>");
        out.println("<p>Hey " + req.getAttribute("login") + "</p>");
        if (invalid) {
            out.println("Pay attention! Your have some test unfinished! Your Course Test List: <br> <p>");
            for (int i = 0; i < list.size(); i++) {
                CourseTest courseTest = list.get(i);
                out.println(courseTest.getCourseName() + ":" + courseTest.getCourseRemarks() + "<br>");
            }
            out.println("</p>");
        } else {
            out.println("Your Course Test List: <br><p> ");
            list.stream().forEach(courseTest -> out.println(courseTest.getCourseName() + ":" + ((courseTest.getCourseRemarks()==(-1))?"未参加测试":courseTest.getCourseRemarks()) + "<br>"));
            out.println("</p>");
        }
    }
}
