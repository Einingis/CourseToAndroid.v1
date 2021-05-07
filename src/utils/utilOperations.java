package utils;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import model.Course;
import model.File;
import model.Folder;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class utilOperations {


    public static void alertMessage(String unable_to_connect) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(unable_to_connect);
        alert.showAndWait();

    }

    public static void addTreeCourses(Course course, TreeItem parentItem) throws SQLException {
        TreeItem<Course> treeItem = new TreeItem(course.getName());
        parentItem.getChildren().add(treeItem);
        DbOperations.checkCourseFolders(course.getName()).forEach(folder -> {
            try {
                addTreeFolders(folder, treeItem);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

    }

    public static void addTreeFolders(Folder folder, TreeItem parentItem) throws SQLException {
        TreeItem<Folder> treeItem = new TreeItem(folder.getName());
        parentItem.getChildren().add(treeItem);
        DbOperations.checkFolderFiles(folder.getName()).forEach(file -> addTreeFiles(file, treeItem));
        folder.getFolderFiles().forEach(sec -> addTreeFiles(sec, treeItem));

    }

    public static void addTreeFiles(File file, TreeItem parentItem) {
        TreeItem<File> treeItem = new TreeItem(file.getName());
        parentItem.getChildren().add(treeItem);

    }

    public static final LocalDate LOCAL_DATE(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return localDate;
    }


    public static Course coursePopup(String oldname, double oldprice, String oldStart, String oldEnd) {
        final Course[] course = {null};
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Course creation");
        dialog.setHeaderText("Enter course info: \nname, \nprice (format 30.99), \nstart date (format 2000-01-01),\nend date (format 2000-01-01) \n");


        ButtonType create = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(create, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20, 150, 20, 20));

        TextField name = new TextField(oldname);
        name.setPromptText(oldname);
        TextField price = new TextField(String.valueOf(oldprice));
        price.setPromptText(String.valueOf(oldprice));
        DatePicker startDate = new DatePicker();
        startDate.setValue(LOCAL_DATE(oldStart));
        DatePicker endDate = new DatePicker();
        endDate.setValue(LOCAL_DATE(oldEnd));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(price, 1, 1);
        grid.add(new Label("Start Date:"), 0, 2);
        grid.add(startDate, 1, 2);
        grid.add(new Label("End Date:"), 0, 3);
        grid.add(endDate, 1, 3);

        Node createBtn = dialog.getDialogPane().lookupButton(create);
        createBtn.disableProperty().bind(
                Bindings.isEmpty(name.textProperty())
                        .or(Bindings.isEmpty(price.textProperty()))
        );


        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> name.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == create) {


                course[0] = new Course(name.getText(), Date.valueOf(startDate.getValue()), Date.valueOf(endDate.getValue()), Double.parseDouble(price.getText()));


            }
            return null;
        });
        dialog.showAndWait();

            return course[0];

    }
    public static Folder folderPopup(String oldname) {
        final Folder[] folder = {null};
    Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create Folder");
        dialog.setHeaderText("Fill in the section info");

    ButtonType confirm = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirm, ButtonType.CANCEL);

    GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

    TextField sectionName = new TextField(oldname);
        sectionName.setPromptText(oldname);

        grid.add(new Label("Section name:"), 0, 0);
        grid.add(sectionName, 1, 0);


    Node loginButton = dialog.getDialogPane().lookupButton(confirm);
        loginButton.setDisable(true);


        sectionName.textProperty().addListener((observable, oldValue, newValue) -> {
        loginButton.setDisable(newValue.trim().isEmpty());
    });

        dialog.getDialogPane().setContent(grid);


        Platform.runLater(() -> sectionName.requestFocus());


        dialog.setResultConverter(dialogButton -> {
        if (dialogButton == confirm) {

        folder[0] = new Folder(sectionName.getText());

        }

        return null;
    });
        dialog.showAndWait();

        return folder[0];
}

    public static File filePopup(String oldName, String oldLink) {
        final File[] files = {null};
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create File");
        dialog.setHeaderText("Fill in the section info");

        ButtonType confirm = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirm, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        //Tik pavadinimo reik
        TextField sectionName = new TextField(oldName);
        sectionName.setPromptText(oldName);
        TextField sectionLink = new TextField(oldLink);
        sectionLink.setPromptText(oldLink);

        grid.add(new Label("Section name:"), 0, 0);
        grid.add(sectionName, 1, 0);
        grid.add(new Label("Section link::"), 0, 1);
        grid.add(sectionLink, 1, 1);

        // Jei nenurodo pavadinimo, neleisim OK paspaust, kad neprikurtų neteisingų duomenų
        Node createBtn = dialog.getDialogPane().lookupButton(confirm);
        createBtn.disableProperty().bind(
                Bindings.isEmpty(sectionName.textProperty())
                        .or(Bindings.isEmpty(sectionLink.textProperty()))
        );

        dialog.getDialogPane().setContent(grid);

        // Fokusas iš karto section pavadinimui
        Platform.runLater(() -> sectionName.requestFocus());

        // Paspaudimo metu kuriam naują skyrių ir pridedam prie parent
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirm) {

                files[0] =new File(sectionName.getText(), sectionLink.getText());
            }
            return null;
        });
        dialog.showAndWait();

      return files[0];
    }
}

