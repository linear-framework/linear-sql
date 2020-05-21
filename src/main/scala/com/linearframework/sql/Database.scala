package com.linearframework.sql

import com.linearframework.sql.query.{Query, QueryBuilder}
import javax.sql.DataSource

/**
  * Convenience methods for creating [[com.linearframework.sql.Database]] objects
  */
object Database {

  /**
    * Creates a Linear Database over the given JDBC DataSource.
    */
  def apply(ds: DataSource): Database = new Database {
    override protected val dataSource: DataSource = ds
  }

}

/**
  * Provides SQL-first access to a JDBC DataSource.
  *
  * This class functions as a non-transactional [[com.linearframework.sql.SqlRunner]]
  * as well a provider for a transactional [[com.linearframework.sql.SqlRunner]].
  */
trait Database extends SqlRunner with Transactional {

  /**
    * Builds a non-transactional query
    */
  override def sql(sql: String): QueryBuilder = {
    val query = new Query(sql, isTransactional = false, openConnection())
    QueryBuilder(query)
  }

  /**
    * Builds a non-transactional query
    */
  override def sql(sqlAndParams: (String, List[(String, Any)])): QueryBuilder = {
    val query = new Query(sqlAndParams._1, isTransactional = false, openConnection(), params = List(sqlAndParams._2))
    QueryBuilder(query)
  }
}
