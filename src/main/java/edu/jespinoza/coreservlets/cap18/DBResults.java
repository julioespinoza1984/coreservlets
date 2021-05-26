package edu.jespinoza.coreservlets.cap18;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to store a completed results of a JDBC Query.
 * Differs from a ResultSet in several ways:
 * <ul>
 * 		<li>ResultSet doesn't necessarily have all the data;
 * 			reconnection to database occurs as you ask for
 * 			later rows.</li>
 * 		<li>This class stores results as strings, in arrays</li>
 * 		<li>This class includes DatabaseMetaData (database product
 * 			name and version) and ResultSetMetaData
 * 			(the column names).</li>
 * 		<li>This class has a toHTMLTable method that turns
 * 			the results into a long string corresponding to
 * 			an HTML table.</li>
 * </ul>
 */

public class DBResults {
    String[] rowData;
    private final Connection connection;
    private final String productName;
    private final String productVersion;
    private final int columnCount;
    private final String[] columnNames;
    private final List<String[]> queryResults;

    public DBResults(Connection connection, String productName,
                     String productVersion, int columnCount,
                     String[] columnNames) {
        this.connection = connection;
        this.productName = productName;
        this.productVersion = productVersion;
        this.columnCount = columnCount;
        this.columnNames = columnNames;
        rowData = new String[columnCount];
        queryResults = new ArrayList<>();
    }

    public Connection getConnection() {
        return connection;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public int getRowCount() {
        return queryResults.size();
    }

    public String[] getRow(int index) {
        return queryResults.get(index);
    }

    public void addRow(String[] row) {
        queryResults.add(row);
    }

    /**
     * Output the results as an HTML table, with
     * the column names as heading and the rest of
     * the results filling regular data cells.
     */
    public String toHTMLTable(String headingColor) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<table border=1>\n");
        if(headingColor != null && !headingColor.isEmpty()) {
        	buffer.append("\t<tr bgcolor=\"");
        	buffer.append(headingColor);
        	buffer.append("\">\n");
		} else {
        	buffer.append("\t<tr>\n");
		}
        for(String columnName : columnNames) {
        	buffer.append("<th>");
        	buffer.append(columnName);
        	buffer.append("</th>");
		}
        buffer.append("\n\t</th>");
        for (String[] rowData : queryResults) {
            buffer.append("\n\t<tr>\n\t\t");
            for (String s : rowData) {
				buffer.append("<td>");
				buffer.append(s);
				buffer.append("</td>");
            }
            buffer.append("\n\t</tr>\n");
        }

        buffer.append("\n</table>");
		System.out.println(buffer.toString());
        return buffer.toString();
    }

}
