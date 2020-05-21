package com.linearframework.sql

import com.linearframework.sql.query.QueryBuilder

/**
  * An object with the ability to run SQL.
  */
trait SqlRunner {

  /**
    * Builds an executable SQL query
    */
  def sql(sql: String): QueryBuilder

  /**
    * Builds an executable SQL query
    */
  def sql(sqlAndParams: (String, List[(String, Any)])): QueryBuilder

}
