package fxControl;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
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

public class SignUpForm implements Initializable {
    @FXML
    public PasswordField pswField;
    @FXML
    public PasswordField psw2Field;
    @FXML
    public TextField loginField;
    @FXML
    public TextField emailField;
    @FXML
    public ComboBox coursesBox;
    @FXML
    public CheckBox empChk;


    private Connection connection;
    private PreparedStatement statement;
    private int courseIsId = 0;

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


    public void createUser(ActionEvent actionEvent) throws SQLException, IOException {
        if (coursesBox.getValue() == null) {
            utilOperations.alertMessage("select Course IS");
        } else {
            courseIsId = Integer.parseInt(coursesBox.getValue().toString().split("\\(")[1].replace(")", ""));
            if (loginField.getText().equals("") || pswField.getText().equals("") || psw2Field.getText().equals("") || emailField.getText().equals("")) {
                utilOperations.alertMessage("Fill all information");
            } else {
                User user = null;
                user = DbOperations.validateFromDb(loginField.getText(), courseIsId);
                if (user != null) {
                    utilOperations.alertMessage("Login already exists");
                } else {
                    if(!pswField.getText().equals(psw2Field.getText())) {
                        utilOperations.alertMessage("passwords don't mach");
                    }else{
                        if(empChk.isSelected()){

                            DbOperations.insertUserToDb(loginField.getText(),pswField.getText(),emailField.getText(),courseIsId,true);
                        }else{

                            DbOperations.insertUserToDb(loginField.getText(),pswField.getText(),emailField.getText(),courseIsId,false);
                        }
                        returnToLogin(actionEvent);
                    }
                }
            }
        }
    }

public void returnToLogin(ActionEvent actionEvent)throws IOException{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../fxml/login.fxml"));
        Parent root=loader.load();


        Stage stage=(Stage)loginField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        }


        }
