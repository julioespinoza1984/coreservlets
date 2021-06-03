package edu.jespinoza.coreservlets.cap18;

/** Some simple utilities for building Oracle, Postgres and MySQL
 * JDBC connections. This is <I>not</I> general-purpose
 * code -- it is specific to my local setup.
 */

public class DriverUtilities {
    public static final int ORACLE = 1;
    public static final int POSTGRES = 2;
    public static final int MYSQL = 3;
    public static final int UNKNOWN = -1;

    /** Build a URL in the format needed by the
     * Oracle, Postgres and MySQL drivers I am using
     */
    public static String makeURL(String host, String port, String dbName,
                                 int vendor) {
        if(vendor == ORACLE) {
            return "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName;
        }

        if(vendor == POSTGRES) {
            return "";
        }

        if(vendor == MYSQL) {
            return "";
        }

        return null;
    }

    /** Get the fully qualified name of a driver. */
    public static String getDriver(int vendor) {
        if(vendor == ORACLE) {
            return "oracle.jdbc.driver.OracleDriver";
        }

        if(vendor == POSTGRES) {
            return "";
        }

        if(vendor == MYSQL) {
            return "";
        }

        return null;
    }

    public static int getVendor(String vendorName) {
        if(vendorName.equalsIgnoreCase("oracle")) {
            return ORACLE;
        }

        if(vendorName.equalsIgnoreCase("postgres")) {
            return POSTGRES;
        }

        if(vendorName.equalsIgnoreCase("mysql")) {
            return MYSQL;
        }

        return UNKNOWN;
    }
}
