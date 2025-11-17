package lab2.bank.dialogs;

import lab2.bank.model.Transaction;

import javax.swing.*;
import java.awt.*;

public class CreateTransactionDialog extends JDialog {

    private JTextField sourceAccountField;
    private JTextField destAccountField;
    private JTextField amountField;
    private boolean confirmed = false;

    public CreateTransactionDialog(Frame parent, Transaction.TransactionType type) {
        super(parent, "Создать транзакцию: " + type.name(), true);
        setLayout(new BorderLayout(5, 5));
        var fieldsPanel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;
        if (type == Transaction.TransactionType.TRANSFER || type == Transaction.TransactionType.WITHDRAW || type == Transaction.TransactionType.FREEZE) {
            sourceAccountField = new JTextField(30);
            gbc.gridx = 0;
            gbc.gridy = row;
            fieldsPanel.add(new JLabel("Счет-источник:"), gbc);
            gbc.gridx = 1;
            gbc.gridy = row++;
            fieldsPanel.add(sourceAccountField, gbc);
        }
        if (type == Transaction.TransactionType.TRANSFER || type == Transaction.TransactionType.DEPOSIT) {
            destAccountField = new JTextField(30);
            gbc.gridx = 0;
            gbc.gridy = row;
            fieldsPanel.add(new JLabel("Счет-получатель:"), gbc);
            gbc.gridx = 1;
            gbc.gridy = row++;
            fieldsPanel.add(destAccountField, gbc);
        }
        if (type == Transaction.TransactionType.DEPOSIT || type == Transaction.TransactionType.WITHDRAW || type == Transaction.TransactionType.TRANSFER) {
            amountField = new JTextField(15);
            gbc.gridx = 0;
            gbc.gridy = row;
            fieldsPanel.add(new JLabel("Сумма:"), gbc);
            gbc.gridx = 1;
            gbc.gridy = row++;
            fieldsPanel.add(amountField, gbc);
        }
        add(fieldsPanel, BorderLayout.CENTER);
        var buttonPanel = new JPanel();
        var okButton = new JButton("Выполнить");
        var cancelButton = new JButton("Отмена");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        okButton.addActionListener(e -> {
            // TODO: Добавить валидацию полей
            confirmed = true;
            dispose();
        });
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        pack();
        setLocationRelativeTo(parent);
    }

    public String getSourceAccountId() {
        return sourceAccountField != null ? sourceAccountField.getText().trim() : null;
    }

    public String getDestAccountId() {
        return destAccountField != null ? destAccountField.getText().trim() : null;
    }

    public String getAmount() {
        return amountField != null ? amountField.getText().trim() : null;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setSourceAccountId(String id) {
        sourceAccountField.setText(id);
    }

    public void setDestAccountId(String id) {
        destAccountField.setText(id);
    }
}