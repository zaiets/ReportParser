import exceptions.InfoException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AppView extends Application {
    final static String DEFAULT_REPORTS_PATH = "FullReports";
    final static String DEFAULT_TARGET_PATH = "ResultReports";
    final static LocalDate DEFAULT_START_DATE = LocalDate.now();
    final static LocalDate DEFAULT_END_DATE = LocalDate.now().plusDays(31);
    public static final String MAIN_ERROR_MESSAGE = "Ошибка! Проверьте настройки.";
    public static final String MAIN_SUCCESS_MESSAGE = "Отчеты сформированы успешно!";
    static List<File> files = defineFiles(DEFAULT_REPORTS_PATH);
    final Text actiontargetResult = new Text();
    ;

    private static List<File> defineFiles(String path) {
        File[] filesArray = new File(path).listFiles();
        return filesArray != null ? Arrays.asList(filesArray) : null;
    }

    @Override
    public void start(Stage primaryStage) {

        final Text actiontargetStartDate = new Text();
        final Text actiontargetEndDate = new Text();
        final Text actiontargetFileNames = new Text();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(20);
        grid.setPadding(new Insets(30, 30, 30, 30));

        Scene scene = new Scene(grid, 600, 500);
        Text scenetitle = new Text("До начала работы ознакомьтесь с указаниями README");
        scenetitle.setFont(Font.font("Courier", FontWeight.NORMAL, 14));
        grid.add(scenetitle, 0, 0, 2, 1);


        Label objLabel1 = new Label("1. Месяц в заголовок отчетов:");
        objLabel1.setAlignment(Pos.CENTER_LEFT);
        grid.add(objLabel1, 0, 1);

        ObservableList<String> monthSelector = FXCollections.observableArrayList();
        for (Month month : Month.values()) {
            monthSelector.add(month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")));
        }
        ComboBox<String> monthCheckBox = new ComboBox<>(monthSelector);
        grid.add(monthCheckBox, 1, 1);


        Label objLabelReports = new Label("2. Файлы ведомостей:");
        objLabelReports.setAlignment(Pos.CENTER_LEFT);
        grid.add(objLabelReports, 0, 2);

        FileChooser inpFiles = new FileChooser();
        inpFiles.getExtensionFilters().add(new FileChooser.ExtensionFilter("Файлы excel (*.xlsx)", "*.xlsx"));
        inpFiles.setTitle("Выберите файлы общих ведомостей");
        Button chooseFiles = new Button();
        chooseFiles.setText("Выбрать ведомости");
        chooseFiles.setTextFill(Color.DARKGREEN);
        chooseFiles.setOnAction(event -> {
            try {
                actiontargetFileNames.setText(null);
                List<File> tempFiles = inpFiles.showOpenMultipleDialog(primaryStage);
                try {
                    String s = fileNames(tempFiles);
                    actiontargetFileNames.setFill(Color.DARKGREEN);
                    actiontargetFileNames.setFont(Font.font("Courier", FontWeight.BOLD, 10));
                    actiontargetFileNames.setText(s);
                    files = tempFiles;
                } catch (Exception ex) {
                    tempFiles = null;
                    files = defineFiles(DEFAULT_REPORTS_PATH);
                    actiontargetFileNames.setFill(Color.FIREBRICK);
                    actiontargetFileNames.setFont(Font.font("Courier", FontWeight.BOLD, 10));
                    actiontargetFileNames.setText(fileNames(files));
                }
            } catch (NullPointerException ex) {
                actiontargetFileNames.setFill(Color.FIREBRICK);
                actiontargetFileNames.setFont(Font.font("Courier", FontWeight.BOLD, 10));
                actiontargetFileNames.setText("Ошибка! Не указаны файлы общих отчетов!");
            }
        });
        grid.add(chooseFiles, 1, 2);
        grid.add(actiontargetFileNames, 1, 3);


        Label objLabel2 = new Label("3. Укажите начало диапазона отчета:");
        objLabel2.setAlignment(Pos.CENTER_LEFT);
        grid.add(objLabel2, 0, 4);


        DatePicker startDatePicker = new DatePicker(DEFAULT_START_DATE);
        startDatePicker.setOnAction(t -> actiontargetStartDate.setText(startDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd MMM uuuu"))));
        LocalDate startDate = startDatePicker.getValue();
        grid.add(startDatePicker, 1, 4);
        grid.add(actiontargetStartDate, 1, 5);


        Label objLabel3 = new Label("4. Укажите конец диапазона отчета:");
        objLabel3.setAlignment(Pos.CENTER_LEFT);
        grid.add(objLabel3, 0, 6);


        DatePicker endDatePicker = new DatePicker(DEFAULT_END_DATE);
        endDatePicker.setOnAction(t -> actiontargetEndDate.setText(endDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd MMM uuuu"))));
        LocalDate endDate = endDatePicker.getValue();
        grid.add(endDatePicker, 1, 6);
        grid.add(actiontargetEndDate, 1, 7);

        Label objLabel4 = new Label("5. Выполнить отчеты:");
        objLabel4.setAlignment(Pos.CENTER_LEFT);
        grid.add(objLabel4, 0, 8);

        Button btnFormReport = new Button();
        btnFormReport.setText("Сформировать отчеты");
        btnFormReport.setTextFill(Color.DARKRED);
        btnFormReport.setOnAction(e -> {
            try {
                List<Report> reports = RP_ExcelUtils.readFiles(files);
                String month = monthCheckBox.getValue() != null ? monthCheckBox.getValue().toLowerCase() : "";
                Set<String> engineerNames = EngineerNames.readEngineerNames();
                String pathName = DEFAULT_TARGET_PATH;
                RP_ExcelUtils.writeFiles(engineerNames, reports, pathName, startDate, endDate, month);
                changeActionTargetResult(MAIN_SUCCESS_MESSAGE, true);
            } catch (Exception ex) {
                ex.printStackTrace();
                changeActionTargetResult(MAIN_ERROR_MESSAGE, false);
            } catch (InfoException ex) {
                ex.printStackTrace();
                changeActionTargetResult(ex.getMessage(), false);
            }
        });
        grid.add(btnFormReport, 1, 8);


        Label resultLabel = new Label("6. Результат работы программы:");
        resultLabel.setAlignment(Pos.CENTER_LEFT);
        grid.add(resultLabel, 0, 9);
        grid.add(actiontargetResult, 1, 9);

        primaryStage.setTitle("Генератор отчетов v.1.0.0b by Zaiets A.Y.");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(600);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setMaxWidth(600);
        primaryStage.setMaxHeight(900);
        primaryStage.show();
    }

    private String fileNames(List<File> files) {
        StringBuilder fileNames = new StringBuilder();
        for (File file : files) {
            fileNames.append(file.getName());
            fileNames.append("\n");
        }
        return fileNames.toString();
    }

    private void changeActionTargetResult(String message, boolean success) {
        if (success) {
            actiontargetResult.setFill(Color.DARKGREEN);
            actiontargetResult.setFont(Font.font("Courier", FontWeight.BOLD, 11));
            actiontargetResult.setText(message);
        } else {
            actiontargetResult.setFill(Color.FIREBRICK);
            actiontargetResult.setFont(Font.font("Courier", FontWeight.BOLD, 11));
            actiontargetResult.setText(message);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}