package edu.jespinoza.coreservlets.cap18.gui;

import edu.jespinoza.coreservlets.cap18.DBResults;
import edu.jespinoza.coreservlets.cap18.DatabaseUtilites;
import edu.jespinoza.coreservlets.cap18.DriverUtilities;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QueryViewer extends JFrame implements ActionListener {
    private JTextField hostField, dbNameField, queryField, usernameField;
    private JRadioButton mySqlButton, postgresButton;
    private JPasswordField passwordField;
    private JButton showResultsButton;
    private Container contentPane;
    private JPanel tablePanel;

    public QueryViewer() {
        super("Database Query Viewer");
        WindowUtilities.setNativeLookAndFeel();
        addWindowListener(new ExitListener());
        contentPane = getContentPane();
        contentPane.add(makeControlPanel(), BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String host = hostField.getText();
        String dbName = dbNameField.getText();
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String query = queryField.getText();
        int vendor;
        if(postgresButton.isSelected()) {
            vendor = DriverUtilities.POSTGRES;
        } else {
            vendor = DriverUtilities.MYSQL;
        }
        if(tablePanel != null) {
            contentPane.remove(tablePanel);
        }
        tablePanel = makeTablePanel(host, dbName, vendor, username, password, query);
        contentPane.add(tablePanel, BorderLayout.CENTER);
        pack();
    }

    private JPanel makeTablePanel(String host, String dbName, int vendor,
                                  String username, String password,
                                  String query) {
        JPanel panel = new JPanel(new BorderLayout());
        if(host == null || host.isEmpty() || dbName == null || dbName.isEmpty()
            || vendor == -1 || username == null || username.isEmpty()
            || password == null || password.isEmpty()
            || query == null || query.isEmpty()) {
            panel.add(makeErrorLabel());
            return panel;
        }
        String driver = DriverUtilities.getDriver(vendor);
        String url = DriverUtilities.makeURL(host, "", dbName, vendor);
        DBResults results = DatabaseUtilites.getQueryResults(driver, url,
                username, password, query, true);
        if(results == null) {
            panel.add(makeErrorLabel());
            return panel;
        }
        DBResultsTableModel model = new DBResultsTableModel(results);
        JTable table = new JTable(model);
        table.setFont(new Font("Serif", Font.PLAIN, 17));
        table.setRowHeight(28);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(table, BorderLayout.CENTER);
        panel.add(header, BorderLayout.NORTH);
        panel.setBorder(BorderFactory.createTitledBorder("Query Results"));
        return panel;
    }

    private JPanel makeControlPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(makeHostPanel());
        panel.add(makeUsernamePanel());
        panel.add(makeQueryPanel());
        panel.add(makeButtonPanel());
        panel.setBorder(BorderFactory.createTitledBorder("Query Data"));
        return panel;
    }

    // The panel that has the host and db name textfield and
    // the driver radio buttons. Placed in control panel.
    private JPanel makeHostPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Host:"));
        hostField = new JTextField(15);
        panel.add(hostField);
        panel.add(new JLabel("DB Name:"));
        dbNameField = new JTextField(15);
        panel.add(dbNameField);
        panel.add(new JLabel("Driver:"));
        ButtonGroup vendorGroup = new ButtonGroup();
        postgresButton = new JRadioButton("Postgres", true);
        vendorGroup.add(postgresButton);
        panel.add(postgresButton);
        mySqlButton = new JRadioButton("MySQL");
        vendorGroup.add(mySqlButton);
        panel.add(mySqlButton);
        return panel;
    }

    // The panel that has the username and password textfields.
    // Placed in control panel.
    private JPanel makeUsernamePanel() {
        JPanel panel = new JPanel();
        usernameField = new JTextField(10);
        passwordField = new JPasswordField(10);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        return panel;
    }

    private JPanel makeQueryPanel() {
        JPanel panel = new JPanel();
        queryField = new JTextField(40);
        queryField.addActionListener(this);
        panel.add(new JLabel("Query:"));
        panel.add(queryField);
        return panel;
    }

    private JPanel makeButtonPanel() {
        JPanel panel = new JPanel();
        showResultsButton = new JButton("Show Results");
        showResultsButton.addActionListener(this);
        panel.add(showResultsButton);
        return panel;
    }

    private JLabel makeErrorLabel() {
        JLabel label = new JLabel("No Results", JLabel.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 36));
        return label;
    }
}
