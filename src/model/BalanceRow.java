package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * сериализуемый класс - запись о доходе или расходе
 */
public class BalanceRow implements Serializable {

    /**
     * наименование
     */
    private String name;

    /**
     * тип
     */
    private BalanceType type;

    /**
     * сумма
     */
    private BigDecimal amount;

    /**
     * дата
     */
    private LocalDate date;

    /**
     * конструктор класса
     * @param name
     * @param type
     * @param amount
     * @param date
     */
    public BalanceRow(String name, BalanceType type, BigDecimal amount, LocalDate date) {
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public BalanceType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

}
