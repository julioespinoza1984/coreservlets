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


}
