package utils;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbOperations {

    private static Connection connection;
    private static PreparedStatement statement;

    public static Connection connectToDb() {

        String DB_URL = "jdbc:mysql://localhost/coursera";
        String USER = "root";
        String PASS = "";
        connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void disconnectFromDb(Connection connection, Statement statement) {
        try {
            if (connection != null && statement != null) {
                connection.close();
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //login/SignUp-----------------------------------------------------------------------------------------------

    public static List<String> getAllCourseIsFromDb() throws SQLException {
        connection = connectToDb();
        List<String> allCourseIs = new ArrayList<>();
        statement = connection.prepareStatement("SELECT * FROM course_is");
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            allCourseIs.add(rs.getString(2) + "(" + rs.getInt(1) + ")");
        }
        disconnectFromDb(connection, statement);
        return allCourseIs;
    }

    public static User validateLoginFromDb(String login, String psw, int courseIs) throws SQLException {
        User user = null;
        connection = connectToDb();
        String sql = "SELECT * FROM users AS u WHERE u.login = ? AND u.psw = ? AND u.course_is = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, login);
        statement.setString(2, psw);
        statement.setInt(3, courseIs);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            user = new User(rs.getInt(1), rs.getBoolean(2), rs.getString(3), rs.getString(4), rs.getString(5));
        }
        disconnectFromDb(connection, statement);
        return user;
    }

    public static User validateFromDb(String login, int courseIs) throws SQLException {
        User user = null;
        connection = connectToDb();
        String sql = "SELECT * FROM users AS u WHERE u.login = ? AND u.course_is = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, login);
        statement.setInt(2, courseIs);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            user = new User(rs.getInt(1), rs.getBoolean(2), rs.getString(3), rs.getString(4), rs.getString(5));
        }
        disconnectFromDb(connection, statement);
        return user;
    }

    public static void insertUserToDb(String login, String psw, String email, int courseIs, boolean emp) throws SQLException {
        connection = connectToDb();
        String query = " INSERT INTO users (course_is, is_admin, login, psw, email)" + " values (?, ?, ?, ?, ?)";
        statement = connection.prepareStatement(query);
        statement.setInt(1, courseIs);
        statement.setBoolean(2, emp);
        statement.setString(3, login);
        statement.setString(4, psw);
        statement.setString(5, email);

        statement.execute();

        disconnectFromDb(connection, statement);
    }

    //Print-------------------------------------------------------------------------------------------------------

    public static Administrator selectAdminById(int id) throws SQLException {
        Administrator administrator = null;
        connection = DbOperations.connectToDb();
        String sql = "SELECT * FROM users AS u WHERE u.id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            administrator = new Administrator(rs.getInt(1), rs.getBoolean(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
        }
        disconnectFromDb(connection, statement);
        return administrator;
    }

    public static Student selectStudentById(int id) throws SQLException {
        Student student = null;
        connection = DbOperations.connectToDb();
        String sql = "SELECT * FROM users AS u WHERE u.id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            student = new Student(rs.getInt(1), rs.getBoolean(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(7), rs.getString(8));
        }

        disconnectFromDb(connection, statement);
        return student;
    }

    public static Course selectCourseByName(String name) throws SQLException {
        Course course = null;
        connection = DbOperations.connectToDb();

        String sql = "SELECT c.*, u.login FROM course AS c, users AS u WHERE u.id = c.admin_id AND c.name = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, name);

        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            course = new Course(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getDate(4), rs.getString(8), rs.getDouble(6));
        }

        disconnectFromDb(connection, statement);
        return course;
    }

    public static Course selectCourseById(int Id) throws SQLException {
        Course course = null;
        connection = DbOperations.connectToDb();

        String sql = "SELECT c.*, u.login FROM course AS c, users AS u WHERE u.id = c.admin_id AND c.id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, Id);

        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            course = new Course(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getDate(4), rs.getString(8), rs.getDouble(6));
        }

        disconnectFromDb(connection, statement);
        return course;
    }

    public static Folder selectFolderByName(String name) throws SQLException {
        Folder folder = null;
        connection = DbOperations.connectToDb();
        String sql = "SELECT f.* FROM folder AS f, course AS c WHERE f.course_id = c.id AND f.folder_name = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            folder = new Folder(rs.getInt(1), rs.getString(2), rs.getDate(3));
        }
        disconnectFromDb(connection, statement);
        return folder;
    }

    public static File selectFileByName(String name) throws SQLException {
        File file = null;
        connection = DbOperations.connectToDb();

        String sql = "SELECT ff.* FROM folder_files AS ff, folder AS f WHERE ff.folder_id = f.id AND ff.name = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            file = new File(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4));
        }
        disconnectFromDb(connection, statement);
        return file;
    }


    public static ArrayList<Course> selectAllCourses(int courseIs) throws SQLException {
        ArrayList<Course> allCourses = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT * FROM course AS c WHERE c.course_is = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, courseIs);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            allCourses.add(new Course(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getDate(4), rs.getInt(5), rs.getDouble(6)));

        }
        disconnectFromDb(connection, statement);
        return allCourses;
    }

    public static ArrayList<Folder> selectAllFolders(int courseId) throws SQLException {
        ArrayList<Folder> allFolders = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT * FROM Folder AS f WHERE f.course_id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, courseId);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            allFolders.add(new Folder(rs.getInt(1),rs.getString(2),rs.getDate(3)));
        }
        disconnectFromDb(connection, statement);
        return allFolders;
    }

    public static ArrayList<Course> printMyCreatedCourses(int courseIs, int currentUser) throws SQLException {
        ArrayList<Course> MyCourses = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT * FROM course AS c WHERE c.course_is = ? AND c.admin_id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, courseIs);
        statement.setInt(2, currentUser);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            MyCourses.add(new Course(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getDate(4), rs.getInt(5), rs.getDouble(6)));
        }
        disconnectFromDb(connection, statement);
        return MyCourses;

    }

    public static ArrayList<File> selectAllFiles(int folderId) throws SQLException {
        ArrayList<File> allFiles = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT * FROM folder_files AS f WHERE f.folder_id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, folderId);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            allFiles.add(new File(rs.getInt(1),rs.getString(2),rs.getDate(3),rs.getString(4)));
        }
        disconnectFromDb(connection, statement);
        return allFiles;
    }


    //Insert------------------------------------------------------------------------------------------------------------

    public static void EnrollToCourse(int currentUser, int courseId) throws SQLException {
        connection = connectToDb();
        String query = " INSERT INTO user_enroll_course (user_id, course_id)" + " values (?, ?)";
        statement = connection.prepareStatement(query);
        statement.setInt(1, currentUser);
        statement.setInt(2, courseId);
        statement.execute();
        disconnectFromDb(connection, statement);
    }

    public static void createCourse(String name, Date start, Date end, int currentUser, Double price, int courseIs) throws SQLException {
        connection = connectToDb();
        String query = " INSERT INTO course (name, start_date, end_date, admin_id, course_price, course_is)" + " values (?, ?, ?, ?, ?, ?)";

        statement = connection.prepareStatement(query);
        statement.setString(1, name);
        statement.setDate(2, start);
        statement.setDate(3, end);
        statement.setInt(4, currentUser);
        statement.setDouble(5, price);
        statement.setInt(6, courseIs);

        statement.execute();
        disconnectFromDb(connection, statement);
    }

    public static void createFolder(String name, int courseId) throws SQLException {
        connection = connectToDb();
        String query = " INSERT INTO folder (folder_name, course_id)" + " values (?, ?)";
        statement = connection.prepareStatement(query);
        statement.setString(1, name);
        statement.setInt(2, courseId);
        statement.execute();
        disconnectFromDb(connection, statement);
    }

    public static void createFile(String name, String link, int folderId) throws SQLException {
        connection = connectToDb();
        String query = " INSERT INTO folder_files (name, file_path, folder_id )" + " values (?, ?, ?)";
        statement = connection.prepareStatement(query);
        statement.setString(1, name);
        statement.setString(2, link);
        statement.setInt(3, folderId);
        statement.execute();
        disconnectFromDb(connection, statement);
    }

    //Check-------------------------------------------------------------------------------------------------------------

    public static ArrayList<Course> checkEnrollCourse(int currentUser) throws SQLException {
        ArrayList<Course> enrolledCourses = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT c.* FROM `user_enroll_course` ue, `course` c WHERE ue.user_id = ? AND c.id = ue.course_id ";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, currentUser);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            enrolledCourses.add(new Course(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getDate(4), rs.getInt(5), rs.getDouble(6)));
        }
        return enrolledCourses;
    }

    public static ArrayList<Folder> checkCourseFolders(String courseName) throws SQLException {
        ArrayList<Folder> courseFolders = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT f.* FROM `folder` AS f, `course` AS c WHERE c.id = f.course_id AND c.name = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, courseName);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            courseFolders.add(new Folder(rs.getInt(1), rs.getString(2), rs.getDate(3)));
        }
        return courseFolders;
    }

    public static ArrayList<File> checkFolderFiles(String FolderName) throws SQLException {
        ArrayList<File> folderFiles = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT ff.* FROM `folder` AS f, `folder_files` AS ff WHERE ff.folder_id = f.id AND f.folder_name = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, FolderName);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            folderFiles.add(new File(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4)));
        }
        return folderFiles;
    }

        public static ArrayList<Integer> checkEnrolledCourses(int id) throws SQLException {
            ArrayList<Integer> enrolledCourses = new ArrayList<>();
            connection = connectToDb();
            String sql = "SELECT ff.* FROM `user_enroll_course` AS ec, `course` AS c WHERE ec.course_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                enrolledCourses.add(rs.getInt(2));
            }
            return enrolledCourses;
        }





    //Delete------------------------------------------------------------------------------------------------------------

    public static void deleteDbRecord(int id, String fromName, String whereName) throws SQLException {
        connection = DbOperations.connectToDb();
        String sql = "DELETE FROM " + fromName + " WHERE " + whereName + " = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.execute();
        DbOperations.disconnectFromDb(connection, statement);
    }

    //Update------------------------------------------------------------------------------------------------------------

    public static void updateDbRecord(int id, String colName, String updName,  Double newValue) throws SQLException {
        if (newValue != 0) {
            connection = DbOperations.connectToDb();
            String sql = "UPDATE " + updName + " SET `" + colName + "`  = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setDouble(1, newValue);
            statement.setInt(2, id);
            statement.executeUpdate();
            DbOperations.disconnectFromDb(connection, statement);
        }
    }

    public static void updateDbRecord(int id, String colName, String updName, String newValue) throws SQLException {
        if (!newValue.equals("")) {
            connection = DbOperations.connectToDb();
            String sql = "UPDATE " + updName + " SET `" + colName + "`  = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, newValue);
            statement.setInt(2, id);
            statement.executeUpdate();
            DbOperations.disconnectFromDb(connection, statement);
        }
    }

    public static void updateDbRecord(int id, String colName, String updName, Date newValue) throws SQLException {
        connection = DbOperations.connectToDb();
        String sql = "UPDATE " + updName + " SET `" + colName + "`  = ? WHERE id = ?";
        statement = connection.prepareStatement(sql);
        statement.setDate(1, Date.valueOf(String.valueOf(newValue)));
        statement.setInt(2, id);
        statement.executeUpdate();
        DbOperations.disconnectFromDb(connection, statement);
    }

    //JUST FOR WEB-----------------------------------------------------------------------------------------------------

    public static ArrayList<Administrator> getAllAdminsFromDb(int courseIs) throws SQLException {
        ArrayList<Administrator> allAdmins = new ArrayList<>();
        connection = DbOperations.connectToDb();
        String sql = "SELECT * FROM `users` AS c WHERE c.course_is = ? AND c.phone_number is not NULL";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, courseIs);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            allAdmins.add(new Administrator(rs.getInt(1), rs.getBoolean(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
        }
        DbOperations.disconnectFromDb(connection, statement);
        return allAdmins;
    }
    public static void deleteUser(boolean admin, int currentUser ) throws SQLException {
        connection = DbOperations.connectToDb();

        if (admin == true) {
            String sql3 = "SELECT c.id,f.id  FROM `course` AS c, folder AS f WHERE c.admin_id = ? AND f.course_id = c.id";
            statement = connection.prepareStatement(sql3);
            statement.setInt(1, currentUser);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String sql = "DELETE FROM `user_enroll_course` WHERE user_enroll_course.course_id = ?";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, rs.getInt(1));
                statement.executeUpdate();

                String sql2 = "DELETE FROM `folder_files` WHERE folder_files.folder_id = ?";
                statement = connection.prepareStatement(sql2);
                statement.setInt(1, rs.getInt(2));
                statement.executeUpdate();

                String sql4 = "DELETE FROM `folder` WHERE folder.id = ?";
                statement = connection.prepareStatement(sql4);
                statement.setInt(1, rs.getInt(2));
                statement.executeUpdate();
            }


            String sql2 = "DELETE FROM `course`  WHERE course.admin_id = ?";
            statement = connection.prepareStatement(sql2);
            statement.setInt(1, currentUser);
            statement.executeUpdate();

        } else if (admin == false) {

            String sql = "DELETE FROM `user_enroll_course` WHERE user_enroll_course.user_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, currentUser);
            statement.executeUpdate();


            String sql2 = "DELETE FROM `course`  WHERE course.admin_id = ?";
            statement = connection.prepareStatement(sql2);
            statement.setInt(1, currentUser);
            statement.executeUpdate();

        }

        String sql = "DELETE FROM `users` WHERE users.id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, currentUser);
        statement.executeUpdate();
    }

}
