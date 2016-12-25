package model;

/**
 * Created by ChenhaoNee on 2016/12/25.
 */
public class CourseTest {

    private String courseName;

    private String userName;

    private Double courseRemarks;

    public CourseTest() {
    }

    public CourseTest(String courseName, Double courseRemarks) {
        this.courseName = courseName;
        this.courseRemarks = courseRemarks;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getCourseRemarks() {
        return courseRemarks;
    }

    public void setCourseRemarks(double courseRemarks) {
        this.courseRemarks = courseRemarks;
    }
}
