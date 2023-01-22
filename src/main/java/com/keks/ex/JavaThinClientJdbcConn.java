package com.keks.ex;

import java.sql.*;


public class JavaThinClientJdbcConn {

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10802;")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.TABLES;");
            ResultSetMetaData meta = rs.getMetaData();
            int colCnt = meta.getColumnCount();
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= colCnt; i++) {
                if (i != 6) header.append(meta.getColumnName(i)).append(",");
            }
            System.out.println(header);
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= colCnt; i++) {
                    if (i != 6) row.append(rs.getObject(i)).append(",");
                }
                System.out.println(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
