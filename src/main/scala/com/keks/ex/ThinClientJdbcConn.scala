package com.keks.ex

import java.sql.{DriverManager, ResultSet}


object ThinClientJdbcConn {

  def main(args: Array[String]): Unit = {

    val conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10802;")
    val stmt = conn.createStatement()
    val resultSet = stmt.executeQuery("select * from INFORMATION_SCHEMA.TABLES;")

    val count = resultSet.getMetaData.getColumnCount
    println((1 to count).map { i =>
      resultSet.getMetaData.getColumnName(i)
    }.mkString(","))

    new Iterator[ResultSet] {
      def hasNext: Boolean = resultSet.next()
      def next(): ResultSet = resultSet
    }.foreach { rs =>
      println((1 to count).map(i => if (i == 6) "null" else rs.getObject(i)).mkString(","))
    }
    stmt.close()
    conn.close()
  }


}
