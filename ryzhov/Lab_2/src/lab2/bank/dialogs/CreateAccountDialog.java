package lab2.bank.dialogs;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class CreateAccountDialog extends JDialog {

    private final JTextField userIdField;
    private boolean confirmed = false;
    private UUID userId = null;

    public CreateAccountDialog(Frame parent, String defaultUserId) {
        super(parent, "Открыть новый счет", true);
        setLayout(new BorderLayout(10, 10));
        var fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        userIdField = new JTextField(30);
        if (defaultUserId != null) {
            userIdField.setText(defaultUserId);
        }
        gbc.gridx = 0; gbc.gridy = 0; fieldsPanel.add(new JLabel("ID Пользователя:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; fieldsPanel.add(userIdField, gbc);
        add(fieldsPanel, BorderLayout.CENTER);
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        var okButton = new JButton("Открыть счет");
        var cancelButton = new JButton("Отмена");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        okButton.addActionListener(e -> onConfirm());
        cancelButton.addActionListener(e -> onCancel());
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void onConfirm() {
        String userIdStr = userIdField.getText().trim();
        if (userIdStr.isEmpty()) {
            showError("Поле User ID не может быть пустым.");
            return;
        }
        try {
            this.userId = UUID.fromString(userIdStr);
            this.confirmed = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            showError("Введен некорректный формат User ID.");
        }
    }

    private void onCancel() {
        this.confirmed = false;
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        userIdField.setText(id);
    }
}
