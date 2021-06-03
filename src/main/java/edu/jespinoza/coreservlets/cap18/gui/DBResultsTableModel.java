package edu.jespinoza.coreservlets.cap18.gui;

import edu.jespinoza.coreservlets.cap18.DBResults;

import javax.swing.table.AbstractTableModel;

/** Simple class that tells a JTable how to extract
 * relevant data from a DBResults object (which is
 * used to store the results from a database query).
 */

public class DBResultsTableModel extends AbstractTableModel {
    private DBResults results;

    public DBResultsTableModel(DBResults results) {
        this.results = results;
    }

    @Override
    public int getRowCount() {
        return results.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return results.getColumnCount();
    }

    @Override
    public String getColumnName(int column) {
        return results.getColumnNames()[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return results.getRow(rowIndex)[columnIndex];
    }
}