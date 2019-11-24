package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.BalanceRow;
import model.BalanceType;
import util.FileUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * контроллер основного окна приложения
 */
public class BalanceViewController {

    @FXML
    /**
     * таблица, отображающая список записей
     */
    private TableView<BalanceRow> balanceTableView;

    // столбцы таблицы

    @FXML
    private TableColumn<BalanceRow, String> nameColumn;

    @FXML
    private TableColumn<BalanceRow, BalanceType> typeColumn;

    @FXML
    private TableColumn<BalanceRow, BigDecimal> amountColumn;

    @FXML
    private TableColumn<BalanceRow, LocalDate> dateColumn;

    // кнопки

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    /**
     * метка для отображения итогового баланса
     */
    private Label totalLabel;

    // элементы управления параметрами поиска

    @FXML
    private ComboBox typeComboBox;

    @FXML
    private CheckBox fromCheckBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private CheckBox toCheckBox;

    @FXML
    private DatePicker toDatePicker;

    private static Stage stage = new Stage();
    private static BalanceModifyController controller;

    /**
     * подпись для итоговой суммы
     */
    private final String totalString = "Баланс на указанный период: ";

    /**
     * список записей
     */
    private static ArrayList<BalanceRow> balanceRowList = new ArrayList<>();

    /**
     * конструктор класса
     * @param parentStage
     * @throws Exception
     */
    public BalanceViewController(Stage parentStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/balanceModify.fxml"));
        controller = new BalanceModifyController(stage);
        loader.setController(controller);
        stage.setScene(new Scene(loader.load()));
        stage.setOnShown(event -> controller.loadAction());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
    }

    @FXML
    /**
     * метод инициализации основного окна приложения
     */
    private void initialize() throws Exception {
        typeComboBox.getItems().addAll("любой", "доход", "расход");
        typeComboBox.getSelectionModel().select(0);
        fromDatePicker.setValue(LocalDate.now());
        toDatePicker.setValue(LocalDate.now());
        balanceRowList = FileUtil.readBalanceRows();
        setTableColumnConverters();
        fillTableView();
    }

    /**
     * метод отображения записей в таблице
     */
    private void fillTableView() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        balanceTableView.setItems(FXCollections.observableArrayList(balanceRowList));
        editButton.setDisable(balanceRowList.isEmpty());
        deleteButton.setDisable(balanceRowList.isEmpty());
        calculateTotal();
    }

    /**
     * метод подсчёта итогового баланса по выбранным параметрам
     */
    private void calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (BalanceRow balanceRow : balanceRowList) {
            boolean isValidType = true;
            switch (typeComboBox.getSelectionModel().getSelectedIndex()) {
                case 1:
                    isValidType = balanceRow.getType().equals(BalanceType.INCOME);
                    break;
                case 2:
                    isValidType = balanceRow.getType().equals(BalanceType.EXPENSE);
                    break;
            }
            boolean isValidDateFrom = fromCheckBox.isSelected() &&
                    (balanceRow.getDate().isEqual(fromDatePicker.getValue()) ||
                            balanceRow.getDate().isAfter(fromDatePicker.getValue()));
            boolean isValidDateTo = toCheckBox.isSelected() &&
                    (balanceRow.getDate().isEqual(toDatePicker.getValue()) ||
                            balanceRow.getDate().isBefore(toDatePicker.getValue()));
            if (isValidType &&
                    (!fromCheckBox.isSelected() || isValidDateFrom) &&
                    (!toCheckBox.isSelected() || isValidDateTo)) {
                if (balanceRow.getType().equals(BalanceType.INCOME)) {
                    total = total.add(balanceRow.getAmount());
                    continue;
                }
                if (balanceRow.getType().equals(BalanceType.EXPENSE)) {
                    total = total.subtract(balanceRow.getAmount());
                    continue;
                }
            }
        }
        totalLabel.setText(totalString + total.toString());
    }

    /**
     * метод настройки отображения в таблице поля "тип"
     */
    private void setTableColumnConverters(){
        typeColumn.setCellFactory(param -> new ComboBoxTableCell<>(new StringConverter<>() {
            @Override
            public String toString(BalanceType object) {
                return object.getDescription();
            }
            @Override
            public BalanceType fromString(String string) {
                return BalanceType.valueOf(string);
            }
        }));
    }

    @FXML
    /**
     * обработчик нажатия кнопки "Добавить"
     */
    private void addButtonAction() {
        controller.setBalanceRow(null);
        stage.showAndWait();
        if (controller.getBalanceRow() != null) {
            balanceRowList.add(controller.getBalanceRow());
            try {
                FileUtil.writeBalanceRows(balanceRowList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fillTableView();
    }

    @FXML
    /**
     * обработчик нажатия кнопки "Редактировать"
     */
    private void editButtonAction() {
        if (!balanceTableView.getSelectionModel().isEmpty()) {
            BalanceRow balanceRow = balanceTableView.getSelectionModel().getSelectedItem();
            int index = balanceRowList.indexOf(balanceRow);
            controller.setBalanceRow(balanceRow);
            stage.showAndWait();
            if (controller.getBalanceRow() != null) {
                balanceRowList.set(index, controller.getBalanceRow());
                try {
                    FileUtil.writeBalanceRows(balanceRowList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fillTableView();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Редактирование записи");
            alert.setHeaderText("Не выбрана запись для редактирования!");
            alert.showAndWait();
        }
    }

    @FXML
    /**
     * обработчик нажатия кнопки "Удалить"
     */
    private void deleteButtonAction() {
        BalanceRow balanceRow = balanceTableView.getSelectionModel().getSelectedItem();
        if(balanceRow!=null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление записи");
            alert.setHeaderText("Выделенная запись будет удалена.\nПродолжить?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                balanceRowList.remove(balanceRow);
                try {
                    FileUtil.writeBalanceRows(balanceRowList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fillTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Удаление записи");
            alert.setHeaderText("Не выбрана запись для удаления!");
            alert.showAndWait();
        }
    }

    @FXML
    /**
     * обработчик выбора типа записи
     */
    private void typeComboBoxAction() {
        calculateTotal();
    }

    @FXML
    /**
     * обработчик смены состояния флажка "с"
     */
    private void fromCheckBoxAction() {
        fromDatePicker.setDisable(!fromCheckBox.isSelected());
        calculateTotal();
    }

    @FXML
    /**
     * обработчик смены состояния флажка "до"
     */
    private void toCheckBoxAction() {
        toDatePicker.setDisable(!toCheckBox.isSelected());
        calculateTotal();
    }

    @FXML
    /**
     * обработчик события выбора начальной даты
     */
    private void fromDatePickerAction() {
        calculateTotal();
    }

    @FXML
    /**
     * обработчик события выбора конечной даты
     */
    private void toDatePickerAction() {
        calculateTotal();
    }

}