package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.BalanceRow;
import model.BalanceType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * контроллер вспомогательного окна приложения
 */
public class BalanceModifyController {

    // компоненты ввода и отображения данных

    @FXML
    TextField nameTextField;

    @FXML
    TextField amountTextField;

    @FXML
    RadioButton incomeRadioButton;

    @FXML
    RadioButton expenseRadioButton;

    @FXML
    DatePicker datePicker;

    private Stage stage;
    private BalanceRow balanceRow;

    @FXML
    /**
     * метод инициализации вспомогательного окна приложения
     */
    private void initialize() {
        amountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d{0,8}([\\.]\\d{0,2})?")){
                amountTextField.setText(oldValue);
            }
        });
    }

    /**
     * конструктор класса
     * @param stage
     */
    public BalanceModifyController(Stage stage) {
        String prefix = balanceRow == null ? "Добавление" : "Удаление";
        stage.setTitle(prefix + " суммы");
        this.stage = stage;
        this.stage.initModality(Modality.WINDOW_MODAL);
    }

    /**
     * метод отображения полей записи в компонентах окна
     */
    public void loadAction() {
        if(balanceRow == null){
            clearAction();
        } else {
            nameTextField.setText(balanceRow.getName());
            amountTextField.setText(balanceRow.getAmount().toString());
            incomeRadioButton.setSelected(balanceRow.getType().equals(BalanceType.INCOME));
            expenseRadioButton.setSelected(balanceRow.getType().equals(BalanceType.EXPENSE));
            datePicker.setValue(balanceRow.getDate());
        }
    }

    @FXML
    /**
     * обработчик нажатия кнопки "Сохранить"
     */
    private void saveAction() {
        if(nameTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Сохранение записи");
            alert.setHeaderText("Не заполнено поле \"Наименование\"!");
            alert.showAndWait();
            return;
        }
        if(amountTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Сохранение записи");
            alert.setHeaderText("Не заполнено поле \"Сумма\"!");
            alert.showAndWait();
            return;
        }
        String name = nameTextField.getText();
        BalanceType type = incomeRadioButton.isSelected()
                ? BalanceType.INCOME : expenseRadioButton.isSelected()
                ? BalanceType.EXPENSE : null;
        BigDecimal amount = new BigDecimal(amountTextField.getText());
        LocalDate date = datePicker.getValue();
        balanceRow = new BalanceRow(name, type, amount, date);
        stage.close();
    }

    @FXML
    /**
     * обработчик нажатия кнопки "Очистить"
     */
    private void clearAction() {
        nameTextField.clear();
        amountTextField.clear();
        incomeRadioButton.setSelected(true);
        expenseRadioButton.setSelected(false);
        datePicker.setValue(LocalDate.now());
    }

    public BalanceRow getBalanceRow() {
        return balanceRow;
    }

    public void setBalanceRow(BalanceRow balanceRow) {
        this.balanceRow = balanceRow;
    }
}
