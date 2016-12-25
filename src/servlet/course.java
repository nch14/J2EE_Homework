package servlet;

import model.CourseTest;
import util.Database;

import javax.servlet.ServletContext;
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


    private void showResults(HttpServletRequest req, HttpServletResponse res) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            connection = Database.getConnect();
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

    private void handleTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showResults(request, response);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleTask(request, response);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleTask(request, response);

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
            list.stream().forEach(courseTest -> out.println(courseTest.getCourseName() + ":" + ((courseTest.getCourseRemarks() == (-1)) ? "未参加测试" : courseTest.getCourseRemarks()) + "<br>"));
            out.println("</p>");
        }
        out.println("<p>" + "总人数为" + getTotalCount() + "</p>");
        out.println("<p>" + "当前在线人数为" + getOnlineCount() + "</p>");
        out.println("<p>" + "游客人数为" + (getTotalCount() - getOnlineCount()) + "</p>");
        out.println("<form method='GET' action='" + res.encodeURL(req.getContextPath() + "/login") + "'>");
        out.println("</p>");
        out.println("<input type='submit' name='Logout' value='Logout'>");
        out.println("</form>");
        out.println("</body></html>");
    }


    private int getTotalCount() {
        ServletContext Context = getServletContext();
        int count = Integer.parseInt((String) Context.getAttribute("total"));
        return count;
    }

    private int getOnlineCount() {
        ServletContext Context = getServletContext();
        int count = Integer.parseInt((String) Context.getAttribute("online"));
        return count;
    }
}
