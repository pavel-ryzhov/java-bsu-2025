package lab2.bank.model.table;

import lab2.bank.model.User;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UserTableModel extends AbstractTableModel {

    private final List<User> users = new ArrayList<>();
    private final String[] columnNames = {"ID", "Ник"};

    @Override
    public int getRowCount() {
        return users.size();
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        var user = users.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> user.getId();
            case 1 -> user.getNickname();
            default -> null;
        };
    }

    public void setData(List<User> userList) {
        this.users.clear();
        this.users.addAll(userList);
        fireTableDataChanged();
    }

    public void addUser(User user) {
        users.add(user);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }
}