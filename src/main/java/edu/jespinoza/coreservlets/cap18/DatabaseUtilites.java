package edu.jespinoza.coreservlets.cap18;

import java.sql.*;

public class DatabaseUtilites {

    /**
     * Connect to database, execute specified query,
     * and accumulate results into DBResults object.
     * If the database connection is left open (use the
     * close argument to specify), you can retrieve the
     * connection with DBResults.getConnection.
     */
    public static DBResults getQueryResults(String driver, String url,
                                            String username, String password,
                                            String query,
                                            boolean close) {
        try {
            Class.forName(driver);
            Connection connection =
                    DriverManager.getConnection(url, username, password);
            return getQueryResults(connection, query, close);
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading driver: " + e);
            e.printStackTrace();
        } catch (SQLException throwables) {
            System.err.println("Error connecting: " + throwables);
            throwables.printStackTrace();
        }
        return null;
    }

    public static DBResults getQueryResults(Connection connection,
                                            String query, boolean close) {
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            String productName = dbMetaData.getDatabaseProductName();
            String productVersion = dbMetaData.getDatabaseProductVersion();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            // Column index starts at 1 (a la SQL) not 0 (a la Java).
            for(int i = 1; i <= columnCount; ++i) {
                columnNames[i - 1] = resultSetMetaData.getColumnName(i).trim();
            }
            DBResults dbResults = new DBResults(connection, productName, productVersion,
                    columnCount, columnNames);
            while(resultSet.next()) {
                String[] row = new String[columnCount];
                // Again, ResultSet index starts at 1, not 0.
                for(int i = 1; i <= columnCount; ++i) {
                    String entry = resultSet.getString(i);
                    if(entry != null && !entry.isEmpty()) {
                        entry = entry.trim();
                    }
                    row[i - 1] = entry;
                }
                dbResults.addRow(row);
            }
            if(close) {
                connection.close();
            }
            return dbResults;
        } catch (SQLException throwables) {
            System.err.println("Error connecting: " + throwables);
            throwables.printStackTrace();
        }
        return null;
    }

    /** Build a table with the specified format and rows */
    public static Connection createTable(String driver, String url,
                                         String username, String password,
                                         String tableName, String tableFormat,
                                         String[] tableRows, boolean close) {
        try {
            Class.forName(driver);
            Connection connection =
                    DriverManager.getConnection(url, username, password);
            return createTable(connection, username, password,
                    tableName, tableFormat, tableRows, close);
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading driver: " + e);
        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
        }
        return null;
    }

    /** Like the previous method, but uses existing connection. */
    public static Connection createTable(Connection connection,
                                         String username,
                                         String password,
                                         String tableName,
                                         String tableFormat,
                                         String[] tableRows,
                                         boolean close) {
        try {
            Statement statement = connection.createStatement();
            // Drop previous table if it exists, but don't get
            // error if it doesn't. Thus the separate try/catch here.
            try {
                statement.execute("DROP TABLE " + tableName);
            } catch (SQLException sqle) {}
            String createCommand = "CREATE TABLE " + tableName + " " + tableFormat;
            statement.execute(createCommand);
            String insertPrefix = "INSERT INTO " + tableName + " VALUES";
            for(int i = 0; i < tableRows.length; ++i) {
                statement.execute(insertPrefix + tableRows[i]);
            }
            if(close) {
                connection.close();
                return null;
            }
            return connection;
        } catch (SQLException sqle) {
            System.err.println("Error creating table: " + sqle);
            return null;
        }
    }

    public static void printTable(String driver,
                                  String url,
                                  String username,
                                  String password,
                                  String tableName,
                                  int entrywidth,
                                  boolean close) {
        String query = "SELECT * FROM " + tableName;
        DBResults results = getQueryResults(driver, url, username, password,
                query, close);
        printTableData(tableName, results, entrywidth, true);
    }

    /**
     * Prints out all entries in a table. Each entry will
     * be printed in a column that is entryWidth characters
     * wide, so be sure to provide a value at least as big
     * as the widest result.
     */
    public static void printTable(Connection connection,
                                  String tableName,
                                  int entryWidth,
                                  boolean close) {
        String query = "SELECT * FROM " + tableName;
        DBResults results = getQueryResults(connection, query, close);
        printTableData(tableName, results, entryWidth, true);
    }

    public static void printTableData(String tableName,
                                      DBResults results,
                                      int entryWidth,
                                      boolean printMetaData) {
        if(results == null) {
            return;
        }
        if(printMetaData) {

            System.out.println("Database: " + results.getProductName());
            System.out.println("Version: " + results.getProductVersion());
            System.out.println();
        }
        System.out.println(tableName + ":");
        String underline = padString("", tableName.length() + 1, "=");
        System.out.println(underline);
        int columnCount = results.getColumnCount();
        String separator = makeSeparator(entryWidth, columnCount);
        System.out.println(separator);
        String row = makeRow(results.getColumnNames(), entryWidth);
        System.out.println(row);
        System.out.println(separator);
        int rowCount = results.getRowCount();
        for(int i = 0; i < rowCount; ++i) {
            row = makeRow(results.getRow(i), entryWidth);
            System.out.println(row);
        }
        System.out.println(separator);
    }

    // A String of the form "| xxx | xxx | xxx |"
    private static String makeRow(String[] entries, int entryWidth) {
        String row = "|";

        for(int i = 0; i < entries.length; ++i) {
            row = row + padString(entries[i], entryWidth, " ");
            row = row + " |";
        }

        return row;
    }

    // A String of the form "+------+------+------+"
    private static String makeSeparator(int entryWidth, int columnCount) {
        String entry = padString("", entryWidth + 1, "-");
        String separator = "+";
        for(int i = 0; i < columnCount; ++i) {
            separator = separator + entry + "+";
        }
        return separator;
    }

    private static String padString(String orig, int size, String padChar) {
        if(orig == null) {
            orig = "<null>";
        }
        StringBuilder buffer = new StringBuilder();
        int extraChars = size - orig.length();
        for(int i = 0; i < extraChars; ++i) {
            buffer.append(padChar);
        }
        buffer.append(orig);
        return buffer.toString();
    }
}
