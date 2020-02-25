package com.linearframework.sql.query

import com.linearframework.sql.DatabaseException
import com.linearframework.sql.query.preparer.{BatchStatementPreparer, SingleStatementPreparer}
import java.sql._

private[sql] class Query private[sql](
  private[sql] val sql: String,
  private[sql] val isTransactional: Boolean,
  private[sql] val connection: Connection,
  private[sql] val params: List[List[(String, Any)]] = List(),
  private[sql] val isBatch: Boolean = false
) {

  def withParams(params: List[List[(String, Any)]]): Query = {
    new Query(this.sql, this.isTransactional, this.connection, params, this.isBatch)
  }

  def enableBatchMode(): Query = {
    new Query(this.sql, this.isTransactional, this.connection, this.params, true)
  }

  def execute(): Unit = {
    executeUpdate(_ => ())
  }

  def executeQuery[R](map: ResultSet => R): R = {
    if (connection == null) {
      throw new DatabaseException("Database connection not established")
    }

    if (connection.isClosed) {
      throw new DatabaseException("Database connection is closed")
    }

    var stmt: PreparedStatement = null
    var rs: ResultSet = null

    try {
      stmt = prepareStatement(false)
      rs = stmt.executeQuery()
      map(rs)
    }
    catch {
      case e: SQLException => throw new DatabaseException(e)
    }
    finally {
      close(rs)
      close(stmt)
      if (!isTransactional) {
        close(connection)
      }
    }
  }

  def executeUpdate[K](map: ResultSet => K): K = {
    if (connection == null) {
      throw new DatabaseException("Database connection not established")
    }

    if (connection.isClosed) {
      throw new DatabaseException("Database connection is closed")
    }

    var stmt: PreparedStatement = null
    var rs: ResultSet = null

    try {
      stmt = prepareStatement(true)

      if (isBatch) {
        stmt.executeBatch()
      }
      else {
        stmt.executeUpdate()
      }
      val rs = stmt.getGeneratedKeys
      map(rs)
    }
    catch {
      case e: SQLException => throw new DatabaseException(e)
    }
    finally {
      close(rs)
      close(stmt)
      if (!isTransactional) {
        close(connection)
      }
    }
  }

  private def prepareStatement(returnGeneratedKeys: Boolean): PreparedStatement = {
    if (sql == null || sql.trim.isEmpty) {
      throw new DatabaseException("Cannot prepare empty SQL statement")
    }
    else if (params == null || params.isEmpty) {
      if (returnGeneratedKeys) {
        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
      }
      else {
        connection.prepareStatement(sql)
      }
    }
    else if (isBatch) {
      BatchStatementPreparer.prepare(connection, sql, params, returnGeneratedKeys)
    }
    else {
      SingleStatementPreparer.prepare(connection, sql, params, returnGeneratedKeys)
    }
  }

  private def close(asset: AutoCloseable): Unit = {
    if (asset != null) {
      try {
        asset.close()
      }
      catch {
        case _: SQLException => ;
      }
    }
  }

}

