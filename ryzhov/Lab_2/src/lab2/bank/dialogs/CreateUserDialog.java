package lab2.bank.dialogs;

import javax.swing.*;
import java.awt.*;

public class CreateUserDialog extends JDialog {
    private final JTextField nicknameField;
    private boolean confirmed = false;

    public CreateUserDialog(Frame parent) {
        super(parent, "Создать нового пользователя", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        var panel = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nicknameField = new JTextField(20);
        var okButton = new JButton("Ок");
        var cancelButton = new JButton("Отмена");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Никнейм:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(nicknameField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(okButton, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(cancelButton, gbc);
        add(panel);
        okButton.addActionListener(e -> {
            if (getNickname() != null && !getNickname().isEmpty()) {
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Никнейм не может быть пустым.", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
    }

    public String getNickname() {
        return nicknameField.getText().trim();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}