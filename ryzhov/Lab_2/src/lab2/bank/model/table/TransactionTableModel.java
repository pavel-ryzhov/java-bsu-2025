package lab2.bank.model.table;

import lab2.bank.model.Transaction;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TransactionTableModel extends AbstractTableModel {

    private final List<Transaction> transactions = new ArrayList<>();
    private final String[] columnNames = {"ID Транзакции", "Тип", "Статус", "Сумма", "ID Отправителя", "ID Получателя"};

    @Override
    public int getRowCount() {
        return transactions.size();
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
        if (columnIndex == 3) {
            return Double.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction tx = transactions.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> tx.getId().toString();
            case 1 -> tx.getType().name();
            case 2 -> tx.getStatus().name();
            case 3 -> tx.getAmount();
            case 4 -> tx.getSourceAccountId() != null ? tx.getSourceAccountId().toString() : "";
            case 5 -> tx.getDestinationAccountId() != null ? tx.getDestinationAccountId().toString() : "";
            default -> null;
        };
    }

    public void setData(List<Transaction> transactionList) {
        this.transactions.clear();
        this.transactions.addAll(transactionList);
        fireTableDataChanged();
    }

    public void addTransaction(Transaction transaction) {
        transactions.addFirst(transaction);
        fireTableRowsInserted(0, 0);
    }
}