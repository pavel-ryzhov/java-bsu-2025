package lab2.bank.model.table;

import lab2.bank.model.Account;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AccountTableModel extends AbstractTableModel {

    private final List<Account> accounts = new ArrayList<>();
    private final String[] columnNames = {"ID Счёта", "ID Пользователя", "Баланс", "Заморожен"};

    @Override
    public int getRowCount() {
        return accounts.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0, 1 -> String.class;
            case 2 -> Double.class;
            case 3 -> Boolean.class;
            default -> Object.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var account = accounts.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> account.getId().toString();
            case 1 -> account.getUserId().toString();
            case 2 -> account.getBalance();
            case 3 -> account.isFrozen();
            default -> null;
        };
    }

    public void setData(List<Account> accountList) {
        this.accounts.clear();
        this.accounts.addAll(accountList);
        fireTableDataChanged();
    }
}