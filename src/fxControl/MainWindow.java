package fxControl;

        import javafx.event.ActionEvent;
        import javafx.event.Event;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.*;
        import javafx.stage.Stage;
        import model.*;
        import utils.DbOperations;
        import utils.utilOperations;

        import java.io.IOException;
        import java.sql.*;
        import java.util.ArrayList;

public class MainWindow {
    //-----------------------------Available courses------------------------------------------------------------
    @FXML
    public ListView allCourses;
    @FXML
    public Button enrollBtn;
    //-----------------------------My courses----------------------------------------------------------------
    @FXML
    public Tab studCourse;
    @FXML
    public TreeView courseTree;

    //-------------------------------My created courses--------------------------------------------------------
    @FXML
    public Tab myCreatedCourses;
    @FXML
    public ListView MyCourseInfo;
    //----------------------------------Account---------------------------------------------------------------
    @FXML
    public ListView accountInfo;
    @FXML
    public TextField changeInfoLogin;
    @FXML
    public TextField changeInfoEmail;
    @FXML
    public Label labelPhone;
    @FXML
    public TextField changeInfoPhoneAndName;
    @FXML
    public PasswordField changeInfoPsw;
    @FXML
    public Button ChangeInfoBtn;
    @FXML
    public Label nameLabel;
    @FXML
    public Label surnameLabel;
    @FXML
    public TextField changeInfoSurname;
    public TreeView foldersTree;
    public ComboBox myCourseBox;
    public Tab folderFiles;
    public Button viewCourseInfo;


    private int courseIS;
    private int currentUser;
    private boolean admin;
    private Connection connection;
    private PreparedStatement statement;



    public void setFormData(int courseIS, int loginId, boolean emp) throws SQLException {
        this.courseIS = courseIS;
        this.currentUser = loginId;
        this.admin = emp;


        if (admin == false) {

            myCreatedCourses.setDisable(true);
            enrollBtn.setDisable(false);
            studCourse.setDisable(false);
            folderFiles.setDisable(true);
        } else {
            folderFiles.setDisable(false);
            studCourse.setDisable(true);
            enrollBtn.setDisable(true);
            myCreatedCourses.setDisable(false);
        }
        printAllCourses();
    }

    //-----------------------------Available courses------------------------------------------------------------

    public void enroll(ActionEvent actionEvent) throws SQLException {
        if (allCourses.getSelectionModel().getSelectedItem() == null) {
            utilOperations.alertMessage("select course");
        } else {

            String course = allCourses.getSelectionModel().getSelectedItem().toString();
            int courseId = DbOperations.selectCourseByName(course).getId();

            DbOperations.EnrollToCourse(currentUser, courseId);
            printAllCourses();
        }
    }

    @FXML
    private void printAllCourses() throws SQLException {
        allCourses.getItems().clear();

        DbOperations.selectAllCourses(courseIS).forEach(c -> allCourses.getItems().add(c.getName()));
        DbOperations.checkEnrollCourse(currentUser).forEach(course -> allCourses.getItems().remove(course.getName()));

    }

    public void viewCourseInfo(ActionEvent actionEvent) throws SQLException {
        if (allCourses.getSelectionModel().getSelectedItem() == null) {
            utilOperations.alertMessage("select course");
        } else {
            String selectedCourse = allCourses.getSelectionModel().getSelectedItem().toString();
            Course course = DbOperations.selectCourseByName(selectedCourse);
            utilOperations.alertMessage(course.toString());
        }
    }
//-----------------------My courses-----------------------------------------------------------------------------------

    public void printStudCourseInfo(Event event) throws SQLException {

        courseTree.setRoot(new TreeItem<>("Courses"));
        DbOperations.checkEnrollCourse(currentUser).forEach(course -> {
            try {
                utilOperations.addTreeCourses(course, courseTree.getRoot());
            } catch (SQLException throwables) {
            }
        });

    }


    public void studLeaveCourse(ActionEvent actionEvent) throws SQLException {
        if (courseTree.getSelectionModel().getSelectedItem() == null) {
            utilOperations.alertMessage("select course");
        } else {
            String selectedCourse = courseTree.getSelectionModel().getSelectedItem().toString().split("\\:")[1].replace("]", "").trim();
            int courseID = DbOperations.selectCourseByName(selectedCourse).getId();
            DbOperations.deleteDbRecord(courseID, "user_enroll_course", "user_enroll_course.course_id");
            printStudCourseInfo(null);
        }
    }

//----------------------------------My created courses---------------------------------------------------

    public void printMyCourse(Event event) throws SQLException {
        MyCourseInfo.getItems().clear();
        DbOperations.printMyCreatedCourses(courseIS, currentUser).forEach(c -> MyCourseInfo.getItems().add(c.getName()));
    }

    public void createCourse(ActionEvent actionEvent) throws SQLException {

        Course course = utilOperations.coursePopup("test", 12.25, "2000-05-03", "2000-06-09");
        ArrayList<Course> allCourses = DbOperations.selectAllCourses(courseIS);
        if (allCourses.stream().anyMatch(course1 -> course1.getName().equals(course.getName()))) {
            utilOperations.alertMessage("Course already exist");
        } else {
            DbOperations.createCourse(course.getName(), Date.valueOf(String.valueOf(course.getStartDate())), Date.valueOf(String.valueOf(course.getEndDate())), currentUser, course.getCoursePrice(), courseIS);
            printMyCourse(null);
        }

    }

    public void updateCourse(ActionEvent actionEvent) throws SQLException {
        if (MyCourseInfo.getSelectionModel().getSelectedItem() == null) {
            utilOperations.alertMessage("select course");
        } else {
            String selectedCourse = MyCourseInfo.getSelectionModel().getSelectedItem().toString();

            Course oldCourse = DbOperations.selectCourseByName(selectedCourse);

            Course newCourse = utilOperations.coursePopup(oldCourse.getName(), oldCourse.getCoursePrice(), String.valueOf(oldCourse.getStartDate()), String.valueOf(oldCourse.getEndDate()));

            if (oldCourse.getName() != newCourse.getName()) {
                ArrayList<Course> allCourses = DbOperations.selectAllCourses(courseIS);
                if (allCourses.stream().anyMatch(course1 -> course1.getName().equals(newCourse.getName()))) {
                    utilOperations.alertMessage("Course already exist");
                } else {
                    DbOperations.updateDbRecord(oldCourse.getId(), "name", "course", newCourse.getName());
                }
            }
            if (oldCourse.getCoursePrice() != newCourse.getCoursePrice()) {
                DbOperations.updateDbRecord(oldCourse.getId(), "course_price", "course", newCourse.getCoursePrice());
            }
            if (oldCourse.getStartDate() != newCourse.getStartDate()) {
                DbOperations.updateDbRecord(oldCourse.getId(), "start_date", "course", (Date) newCourse.getStartDate());
            }
            if (oldCourse.getEndDate() != newCourse.getEndDate()) {
                DbOperations.updateDbRecord(oldCourse.getId(), "end_date", "course", (Date) newCourse.getEndDate());
            }
            printMyCourse(null);
        }

    }

    public void deleteCourse(ActionEvent actionEvent) throws SQLException {
        if (MyCourseInfo.getSelectionModel().getSelectedItem() == null) {
            utilOperations.alertMessage("select course");
        } else {
            String selectedCourse = MyCourseInfo.getSelectionModel().getSelectedItem().toString();
            Course course = DbOperations.selectCourseByName(selectedCourse);
            DbOperations.deleteDbRecord(course.getId(), "user_enroll_course", "user_enroll_course.course_id");

            DbOperations.checkCourseFolders(selectedCourse).forEach(folder -> {
                try {
                    DbOperations.deleteDbRecord(folder.getId(), "folder_files", "folder_files.folder_id");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            DbOperations.checkCourseFolders(selectedCourse).forEach(folder -> {
                try {
                    DbOperations.deleteDbRecord(folder.getId(), "folder", "id");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            DbOperations.deleteDbRecord(course.getId(), "course", "id");
        }
        printMyCourse(null);
    }

    public void viewMyCourseInfo(ActionEvent actionEvent) throws SQLException {
        if (MyCourseInfo.getSelectionModel().getSelectedItem() == null) {
            utilOperations.alertMessage("select course");
        } else {
            String selectedCourse = MyCourseInfo.getSelectionModel().getSelectedItem().toString();
            Course course = DbOperations.selectCourseByName(selectedCourse);
            utilOperations.alertMessage(course.toString());
        }
    }

    //-------------------------------------------------------Folder/Files----------------------------------------------
    public void printBox() throws SQLException {
        myCourseBox.getItems().clear();
        DbOperations.printMyCreatedCourses(courseIS, currentUser).forEach(c -> myCourseBox.getItems().add(c.getName()));
    }


    public void PrintFolders() throws SQLException {
        if (myCourseBox.getValue() != null) {
            String course = myCourseBox.getValue().toString();
            foldersTree.setRoot(new TreeItem("Folders"));
            DbOperations.checkCourseFolders(course).forEach(folder -> {
                try {
                    utilOperations.addTreeFolders(folder, foldersTree.getRoot());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        }
    }

    public void createFolder(ActionEvent actionEvent) throws SQLException {
        if (myCourseBox.getValue() == null) {
            utilOperations.alertMessage("select course in box");
        } else {
            String course = myCourseBox.getValue().toString();
            Folder folder = utilOperations.folderPopup("test");
            ArrayList<Folder> allFolders = DbOperations.selectAllFolders(DbOperations.selectCourseByName(course).getId());
            if (allFolders.stream().anyMatch(folder1 -> folder1.getName().equals(folder.getName()))) {
                utilOperations.alertMessage("Folder already exist");
            } else {
                DbOperations.createFolder(folder.getName(), DbOperations.selectCourseByName(course).getId());
                PrintFolders();
            }
        }
    }

    public void updateFolder(ActionEvent actionEvent) throws SQLException {
        if (myCourseBox.getValue() == null) {
            utilOperations.alertMessage("select course in box");
        } else {
            String course = myCourseBox.getValue().toString();
            if (foldersTree.getSelectionModel().getSelectedItem() == null) {
                utilOperations.alertMessage("select folder");
            } else {
                String selected = foldersTree.getSelectionModel().getSelectedItem().toString().split("\\:")[1].replace("]", "").trim();
                ArrayList<Folder> allFolders = DbOperations.selectAllFolders(DbOperations.selectCourseByName(course).getId());
                if (!allFolders.stream().anyMatch(folder1 -> folder1.getName().equals(selected))) {
                    utilOperations.alertMessage("you selected file");
                } else {
                    Folder oldFolder = DbOperations.selectFolderByName(selected);
                    Folder newFolder = utilOperations.folderPopup(oldFolder.getName());
                    if (allFolders.stream().anyMatch(folder1 -> folder1.getName().equals(newFolder.getName()))) {
                        utilOperations.alertMessage("Folder already exist");
                    } else {
                        DbOperations.updateDbRecord(oldFolder.getId(), "folder_name", "folder", newFolder.getName());
                        DbOperations.updateDbRecord(oldFolder.getId(), "date_modified", "folder", new Date(System.currentTimeMillis()));
                        PrintFolders();
                    }
                }
            }
        }
    }


    public void getFolderInfo(ActionEvent actionEvent) throws SQLException {
        if (myCourseBox.getValue() == null) {
            utilOperations.alertMessage("select course in box");
        } else {
            String course = myCourseBox.getValue().toString();
            if (foldersTree.getSelectionModel().getSelectedItem() == null) {
                utilOperations.alertMessage("select folder");
            } else {
                String selected = foldersTree.getSelectionModel().getSelectedItem().toString().split("\\:")[1].replace("]", "").trim();
                ArrayList<Folder> allFolders = DbOperations.selectAllFolders(DbOperations.selectCourseByName(course).getId());
                if (!allFolders.stream().anyMatch(folder1 -> folder1.getName().equals(selected))) {
                    utilOperations.alertMessage("you selected file");
                } else {
                    Folder folder = DbOperations.selectFolderByName(selected);
                    utilOperations.alertMessage(folder.toString());
                }
            }
        }
    }


    public void deleteFolder(ActionEvent actionEvent) throws SQLException {
        if (myCourseBox.getValue() == null) {
            utilOperations.alertMessage("select course in box");
        } else {
            String course = myCourseBox.getValue().toString();
            if (foldersTree.getSelectionModel().getSelectedItem() == null) {
                utilOperations.alertMessage("select folder");
            } else {
                String selected = foldersTree.getSelectionModel().getSelectedItem().toString().split("\\:")[1].replace("]", "").trim();
                ArrayList<Folder> allFolders = DbOperations.selectAllFolders(DbOperations.selectCourseByName(course).getId());
                if (!allFolders.stream().anyMatch(folder1 -> folder1.getName().equals(selected))) {
                    utilOperations.alertMessage("you selected file");
                } else {
                    Folder folder = DbOperations.selectFolderByName(selected);

                    DbOperations.checkFolderFiles(selected).forEach(file -> {
                        try {
                            DbOperations.deleteDbRecord(file.getId(), "folder_files", "id");
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    });
                    DbOperations.deleteDbRecord(folder.getId(), "folder", "id");
                    PrintFolders();
                }
            }
        }
    }

    //---------------------------------File-----------------------------------------------------------------------------

    public void addFile(ActionEvent actionEvent) throws SQLException {
        if (myCourseBox.getValue() == null) {
            utilOperations.alertMessage("select course in box");
        } else {
            String course = myCourseBox.getValue().toString();
            if (foldersTree.getSelectionModel().getSelectedItem() == null) {
                utilOperations.alertMessage("select folder");
            } else {
                String selected = foldersTree.getSelectionModel().getSelectedItem().toString().split("\\:")[1].replace("]", "").trim();
                ArrayList<Folder> allFolders = DbOperations.selectAllFolders(DbOperations.selectCourseByName(course).getId());
                if (!allFolders.stream().anyMatch(f -> f.getName().equals(selected))) {
                    utilOperations.alertMessage("you selected file");
                } else {
                    File file = utilOperations.filePopup("test", "url;test");
                    ArrayList<File> allFiles = DbOperations.selectAllFiles(DbOperations.selectFolderByName(selected).getId());
                    if (allFiles.stream().anyMatch(f -> f.getName().equals(file.getName()))) {
                        utilOperations.alertMessage("file already exist");
                    } else {
                        DbOperations.createFile(file.getName(), file.getLinkToFile(), DbOperations.selectFolderByName(selected).getId());
                        PrintFolders();
                    }
                }
            }
        }
    }

    public void editFile(ActionEvent actionEvent) throws SQLException {
        if (myCourseBox.getValue() == null) {
            utilOperations.alertMessage("select course in box");
        } else {
            String course = myCourseBox.getValue().toString();
            if (foldersTree.getSelectionModel().getSelectedItem() == null) {
                utilOperations.alertMessage("select file");
            } else {
                String selected = foldersTree.getSelectionModel().getSelectedItem().toString().split("\\:")[1].replace("]", "").trim();
                ArrayList<File> allFiles = DbOperations.selectAllFiles(DbOperations.selectFolderByName(selected).getId());
                if (!allFiles.stream().anyMatch(f -> f.getName().equals(selected))) {
                    utilOperations.alertMessage("you selected folder");
                } else {
                    File oldFile = DbOperations.selectFileByName(selected);
                    File newFile = utilOperations.filePopup(oldFile.getName(), oldFile.getLinkToFile());
                    if (allFiles.stream().anyMatch(f -> f.getName().equals(newFile.getName()))) {
                        utilOperations.alertMessage("Folder already exist");
                    } else {
                        DbOperations.updateDbRecord(oldFile.getId(), "name", "folder_files", newFile.getName());
                        DbOperations.updateDbRecord(oldFile.getId(), "file_path", "folder_files", newFile.getLinkToFile());
                        PrintFolders();
                    }
                }
            }
        }
    }

    public void deleteFile(ActionEvent actionEvent) throws SQLException {
        if (myCourseBox.getValue() == null) {
            utilOperations.alertMessage("select course in box");
        } else {
            if (foldersTree.getSelectionModel().getSelectedItem() == null) {
                utilOperations.alertMessage("select file");
            } else {
                String selected = foldersTree.getSelectionModel().getSelectedItem().toString().split("\\:")[1].replace("]", "").trim();
                ArrayList<File> allFiles = DbOperations.selectAllFiles(DbOperations.selectFolderByName(selected).getId());
                if (!allFiles.stream().anyMatch(f -> f.getName().equals(selected))) {
                    utilOperations.alertMessage("you selected folder");
                } else {
                    File file = DbOperations.selectFileByName(selected);
                    DbOperations.deleteDbRecord(file.getId(), "folder_files", "id");
                    PrintFolders();
                }
            }
        }
    }

    public void printFileInfo(ActionEvent actionEvent) throws SQLException {
        if (myCourseBox.getValue() == null) {
            utilOperations.alertMessage("select course in box");
        } else {
            if (foldersTree.getSelectionModel().getSelectedItem() == null) {
                utilOperations.alertMessage("select file");
            } else {
                String selected = foldersTree.getSelectionModel().getSelectedItem().toString().split("\\:")[1].replace("]", "").trim();
                ArrayList<File> allFiles = DbOperations.selectAllFiles(DbOperations.selectFolderByName(selected).getId());
                if (!allFiles.stream().anyMatch(f -> f.getName().equals(selected))) {
                    utilOperations.alertMessage("you selected folder");
                } else {
                    File file = DbOperations.selectFileByName(selected);
                    utilOperations.alertMessage(file.toString());
                }
            }
        }
    }

    //----------------------------Account-----------------------------------------------------------------------------

    public void printAccountInfo(Event event) throws SQLException {
        accountInfo.getItems().clear();
        if (admin == true) {
            Administrator administrator = DbOperations.selectAdminById(currentUser);
            labelPhone.setVisible(true);
            nameLabel.setVisible(false);
            surnameLabel.setVisible(false);
            changeInfoSurname.setVisible(false);
            accountInfo.getItems().add("account ID: " + administrator.getId());
            accountInfo.getItems().add("Login: " + administrator.getLogin());
            accountInfo.getItems().add("Password: " + administrator.getPsw());
            accountInfo.getItems().add("Email: " + administrator.getEmail());
            accountInfo.getItems().add("Phone number: " + administrator.getEmail());
        } else if (admin == false) {
            Student student = DbOperations.selectStudentById(currentUser);
            labelPhone.setVisible(false);
            nameLabel.setVisible(true);
            surnameLabel.setVisible(true);
            changeInfoSurname.setVisible(true);
            accountInfo.getItems().add("account ID: " + student.getId());
            accountInfo.getItems().add("Login: " + student.getLogin());
            accountInfo.getItems().add("Password: " + student.getPsw());
            accountInfo.getItems().add("Email: " + student.getEmail());
            accountInfo.getItems().add("Name: " + student.getName());
            accountInfo.getItems().add("Surname: " + student.getSurname());
        }
    }

    public void ChangeInfo(ActionEvent actionEvent) throws SQLException {
        User user = null;
        user = DbOperations.validateFromDb(changeInfoLogin.getText(), courseIS);
        if (changeInfoLogin.getText().equals("")) {
        } else {
            if (user != null) {
                utilOperations.alertMessage("login already exists");
            }
            DbOperations.updateDbRecord(currentUser, "login", "users", changeInfoLogin.getText());
        }
        if (changeInfoEmail.getText().equals("")) {
        } else {
            DbOperations.updateDbRecord(currentUser, "email", "users", changeInfoEmail.getText());
        }
        if (changeInfoPsw.getText().equals("")) {
        } else {
            DbOperations.updateDbRecord(currentUser, "psw", "users", changeInfoPsw.getText());
        }

        if (admin == true) {
            if (changeInfoPhoneAndName.getText().equals("")) {
            } else {
                DbOperations.updateDbRecord(currentUser, "phone_number", "users", changeInfoPhoneAndName.getText());
            }
        }else if(admin == false){
            if (changeInfoPhoneAndName.getText().equals("")) {
            }else{
                DbOperations.updateDbRecord(currentUser, "name", "users", changeInfoPhoneAndName.getText());
            }
            if (changeInfoSurname.getText().equals("")) {
            } else {
                DbOperations.updateDbRecord(currentUser, "surname", "users", changeInfoSurname.getText());
            }
        }
        accountInfo.getItems().clear();
        printAccountInfo(null);
    }

    public void deleteAccount(ActionEvent actionEvent) throws SQLException, IOException {
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) accountInfo.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        DbOperations.disconnectFromDb(connection, statement);
    }

}
