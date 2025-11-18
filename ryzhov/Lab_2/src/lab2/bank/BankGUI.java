package lab2.bank;

import lab2.bank.dao.AccountDao;
import lab2.bank.dao.TransactionDao;
import lab2.bank.dao.UserDao;
import lab2.bank.dialogs.CreateAccountDialog;
import lab2.bank.dialogs.CreateTransactionDialog;
import lab2.bank.dialogs.CreateUserDialog;
import lab2.bank.model.Account;
import lab2.bank.model.Transaction;
import lab2.bank.model.User;
import lab2.bank.model.table.AccountTableModel;
import lab2.bank.model.table.TransactionTableModel;
import lab2.bank.model.table.UserTableModel;
import lab2.bank.service.AsyncTransactionProcessor;
import lab2.bank.service.TransactionService;
import lab2.bank.service.command.ProcessTransactionCommand;
import lab2.bank.service.observer.EventType;
import lab2.bank.service.observer.Listener;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.UUID;

public class BankGUI extends JFrame implements Listener {
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final TransactionService transactionService;
    private final AsyncTransactionProcessor processor;

    private final UserTableModel userTableModel = new UserTableModel();
    private final AccountTableModel accountTableModel = new AccountTableModel();
    private final TransactionTableModel transactionTableModel = new TransactionTableModel();

    private final JTable usersTable;
    private final JTable accountsTable;

    public BankGUI(UserDao u, AccountDao a, TransactionDao t, TransactionService ts, AsyncTransactionProcessor p) {
        this.userDao = u;
        this.accountDao = a;
        this.transactionDao = t;
        this.transactionService = ts;
        this.processor = p;
        setTitle("Банковская система");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var size = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(size.width * 3 / 4, size.height * 3 / 4);
        setLocationRelativeTo(null);
        loadInitialData();
        var usersPanel = new JPanel(new BorderLayout(5, 5));
        usersTable = new JTable(userTableModel);
        configureTable(usersTable);
        usersPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        var tabbedPane = new JTabbedPane();
        var accountsPanel = new JPanel(new BorderLayout(5, 5));
        accountsTable = new JTable(accountTableModel);
        configureTable(accountsTable);
        accountsPanel.add(new JScrollPane(accountsTable), BorderLayout.CENTER);
        var transactionsPanel = new JPanel(new BorderLayout(5, 5));
        var transactionsTable = new JTable(transactionTableModel);
        configureTable(transactionsTable);
        transactionsPanel.add(new JScrollPane(transactionsTable), BorderLayout.CENTER);
        tabbedPane.addTab("Пользователи", usersPanel);
        tabbedPane.addTab("Счета", accountsPanel);
        tabbedPane.addTab("Транзакции", transactionsPanel);
        add(tabbedPane, BorderLayout.CENTER);
        var globalActionsPanel = createGlobalActionsPanel();
        add(globalActionsPanel, BorderLayout.SOUTH);
    }

    private JPanel createGlobalActionsPanel() {
        var panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        var addUserButton = new JButton("Добавить пользователя...");
        var openAccountButton = new JButton("Открыть счет...");
        var depositButton = new JButton("Пополнить...");
        var withdrawButton = new JButton("Снять...");
        var freezeButton = new JButton("Заморозить...");
        var transferButton = new JButton("Перевести...");

        panel.add(addUserButton);
        panel.add(openAccountButton);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(freezeButton);
        panel.add(transferButton);

        addUserButton.addActionListener(e -> handleAddUser());
        openAccountButton.addActionListener(e -> handleOpenAccount());
        depositButton.addActionListener(e -> handleCreateTransaction(Transaction.TransactionType.DEPOSIT));
        withdrawButton.addActionListener(e -> handleCreateTransaction(Transaction.TransactionType.WITHDRAW));
        freezeButton.addActionListener(e -> handleCreateTransaction(Transaction.TransactionType.FREEZE));
        transferButton.addActionListener(e -> handleCreateTransaction(Transaction.TransactionType.TRANSFER));

        return panel;
    }

    private void handleAddUser() {
        var dialog = new CreateUserDialog(this);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            String nickname = dialog.getNickname();
            var user = new User(nickname);
            userDao.save(user);
            userTableModel.addUser(user);
        }
    }

    private void loadInitialData() {
        try {
            userTableModel.setData(userDao.findAll());
            accountTableModel.setData(accountDao.findAll());
            transactionTableModel.setData(transactionDao.findAll());
        } catch (Exception e) {
            showError("Не удалось загрузить начальные данные!\n" + e.getMessage());
            System.exit(1);
        }
    }

    private String getSelectedValueFromTable(JTable table, int col) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Object value = table.getValueAt(selectedRow, col);
            return value != null ? value.toString() : null;
        }
        return null;
    }

    private void configureTable(JTable table) {
        var header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        header.setBackground(Color.LIGHT_GRAY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setFillsViewportHeight(true);
        var rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        var copyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                int selectedColumn = table.getSelectedColumn();
                if (selectedRow != -1 && selectedColumn != -1) {
                    Object value = table.getValueAt(selectedRow, selectedColumn);
                    String stringValue = (value == null) ? "" : value.toString();
                    var stringSelection = new StringSelection(stringValue);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                }
            }
        };
        var ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
        table.getInputMap(JComponent.WHEN_FOCUSED).put(ctrlC, "CopyAction");
        table.getActionMap().put("CopyAction", copyAction);
        var popupMenu = new JPopupMenu();
        var copyMenuItem = new JMenuItem("Копировать ячейку");
        popupMenu.add(copyMenuItem);
        copyMenuItem.addActionListener(copyAction);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseEvent(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseEvent(e);
            }

            private void handleMouseEvent(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if (row >= 0 && col >= 0) {
                        table.changeSelection(row, col, false, false);
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void handleOpenAccount() {
        var defaultUserId = getSelectedValueFromTable(usersTable, 0);
        var dialog = new CreateAccountDialog(this, defaultUserId);
        dialog.setUserId(defaultUserId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            var userId = dialog.getUserId();
            if (userDao.findById(userId).isPresent()) {
                var account = new Account(userId);
                accountDao.save(account);
                accountTableModel.setData(accountDao.findAll());
                showMessage("Счет открыт", "Новый счет успешно открыт.\nID Счета: " + account.getId());
            } else {
                showError("Пользователь с ID " + userId + " не найден.");
            }
        }
    }

    private void handleCreateTransaction(Transaction.TransactionType type) {
        var dialog = new CreateTransactionDialog(this, type);
        var selectedAccountId = getSelectedValueFromTable(accountsTable, 0);
        if (selectedAccountId != null) {
            if (type == Transaction.TransactionType.DEPOSIT) {
                dialog.setDestAccountId(selectedAccountId);
            } else {
                dialog.setSourceAccountId(selectedAccountId);
            }
        }
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                Transaction tx = null;
                switch (type) {
                    case DEPOSIT:
                        UUID destIdDeposit = UUID.fromString(dialog.getDestAccountId());
                        double amountDeposit = Double.parseDouble(dialog.getAmount());
                        tx = Transaction.createDeposit(destIdDeposit, amountDeposit);
                        break;
                    case WITHDRAW:
                        UUID sourceIdWithdraw = UUID.fromString(dialog.getSourceAccountId());
                        double amountWithdraw = Double.parseDouble(dialog.getAmount());
                        tx = Transaction.createWithdrawal(sourceIdWithdraw, amountWithdraw);
                        break;
                    case FREEZE:
                        UUID sourceIdFreeze = UUID.fromString(dialog.getSourceAccountId());
                        tx = Transaction.createFreeze(sourceIdFreeze);
                        break;
                    case TRANSFER:
                        var sourceIdTransfer = UUID.fromString(dialog.getSourceAccountId());
                        var destIdTransfer = UUID.fromString(dialog.getDestAccountId());
                        double amountTransfer = Double.parseDouble(dialog.getAmount());
                        tx = Transaction.createTransfer(sourceIdTransfer, destIdTransfer, amountTransfer);
                        break;
                }
                if (tx != null) {
                    processor.submit(new ProcessTransactionCommand(tx, transactionService));
                }
            } catch (NumberFormatException ex) {
                showError("Ошибка ввода: Сумма должна быть числом.");
            } catch (IllegalArgumentException ex) {
                showError("Ошибка ввода: " + ex.getMessage());
            } catch (Exception ex) {
                showError("Произошла непредвиденная ошибка: " + ex.getMessage());
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void update(EventType type, Transaction transaction) {
        SwingUtilities.invokeLater(() -> {
            transactionTableModel.addTransaction(transaction);
            switch (type) {
                case TRANSACTION_SUCCESS -> {
                    accountTableModel.setData(accountDao.findAll());
                    showMessage("УРА!", "Транзакция произведена успешно!");
                }
                case TRANSACTION_FAILURE -> showError("Транзакция не произведена!");
            }
        });
    }
}
