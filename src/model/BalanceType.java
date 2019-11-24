package model;

/**
 * перечисление - типы записи
 */
public enum BalanceType {

    INCOME("доход"), EXPENSE("расход");

    /**
     * описание типа записи
     */
    private String description;

    BalanceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
