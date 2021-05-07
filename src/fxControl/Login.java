package fxControl;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import utils.DbOperations;
import utils.utilOperations;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Login implements Initializable {
    @FXML
    public Button logInBtn;
    @FXML
    public Button signUpBtnE;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField pswField;
    @FXML
    public Button signUpBtnS;
    @FXML
    public ComboBox coursesBox;

    private Connection connection;
    private PreparedStatement statement;
    private int courseIsId = 0;
    private User currentUser = null;
    private boolean emp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> options = new ArrayList<>();
        connection = DbOperations.connectToDb();
        if (connection == null) {
            utilOperations.alertMessage("Unable to connect");
            Platform.exit();
        } else {
            try {
                options = DbOperations.getAllCourseIsFromDb();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            coursesBox.getItems().addAll(options);
        }
        DbOperations.disconnectFromDb(connection, statement);

    }


    public void validateAndLogin(ActionEvent actionEvent) throws SQLException, IOException {
        if (coursesBox.getValue() == null) {
            utilOperations.alertMessage("select Course IS");
        } else {
            courseIsId = Integer.parseInt(coursesBox.getValue().toString().split("\\(")[1].replace(")", ""));
            currentUser = DbOperations.validateLoginFromDb(loginField.getText(), pswField.getText(), courseIsId);
            if (currentUser != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/mainWindow.fxml"));
                Parent root = loader.load();

                MainWindow mainWindow = loader.getController();
                mainWindow.setFormData(courseIsId, currentUser.getId(), currentUser.getEmp());

                Stage stage = (Stage) loginField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } else {
                utilOperations.alertMessage("User doesn't exist");
            }
        }
    }

    public void loadSignUpForm(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/signUpForm.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

}
