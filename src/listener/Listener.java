package listener; /**
 * Created by ChenhaoNee on 2016/12/25.
 */

import util.Database;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebListener()
public class Listener implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {

    int total = 0;
    int online = 0;
    String counterFilePath = "D:\\JEE\\2012\\workspace\\Login\\WebContent\\counter.txt";

    // Public constructor is required by servlet spec
    public Listener() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
      /* This method is called when the servlet context is
         initialized(when the Web application is deployed). 
         You can initialize servlet context related data here.
      */
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet result;
        try {
            connection = Database.getConnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = connection.prepareStatement("select total,online from user_count where 1=1");
            result = stmt.executeQuery();
            while (result.next()) {
                total = result.getInt(1);
                online = result.getInt(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("total", ""+total);
        servletContext.setAttribute("online",""+ online);

        System.out.println("Application initialized");

    }

    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context 
         (the Web application) is undeployed or 
         Application Server shuts down.
      */
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
      /* Session is created. */
    }

    public void sessionDestroyed(HttpSessionEvent se) {
      /* Session is destroyed. */
    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute 
         is added to a session.
      */
        System.out.println("ServletContextattribute added");

    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
    }

    public void attributeReplaced(ServletContextAttributeEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
        System.out.println("ServletContextattribute replaced");
        writeCounter(sbe);
    }

    synchronized void writeCounter(ServletContextAttributeEvent scae) {
        ServletContext servletContext = scae.getServletContext();

        Connection connection = null;
        PreparedStatement stmt = null;
        int result;


        total = Integer.parseInt((String) servletContext.getAttribute("total"));
        online = Integer.parseInt((String) servletContext.getAttribute("online"));

        try {
            connection = Database.getConnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = connection.prepareStatement("UPDATE user_count total,online SET total =? and  online = ?");
            stmt.setString(1, "" + total);
            stmt.setString(2, "" + online);

            result = stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
