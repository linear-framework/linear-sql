package com.linearframework.sql

import javax.sql.DataSource
import java.sql.{Connection, SQLException}

/**
  * Enables transactions over a [[javax.sql.DataSource]]
  */
trait Transactional {
  protected val dataSource: DataSource

  /**
    * Encapsulates a transaction
    */
  def transaction[T](useTransaction: Transaction => T): T = {
    var conn: Connection = null
    var transaction: Transaction = null
    try {
      conn = openConnection()
      transaction = Transaction(conn)
      useTransaction(transaction)
    }
    catch {
      case e: SQLException =>
        if (transaction != null && !transaction.isClosed) {
          transaction.rollback()
        }
        throw new DatabaseException(cause = e)
      case t: Throwable =>
        if (transaction != null && !transaction.isClosed) {
          transaction.rollback()
        }
        throw t;
    }
    finally {
      if (transaction != null) {
        try {
          transaction.rollback()
        }
        catch {
          case e: SQLException => ;
          case e: DatabaseException => ;
        }
      }
      if (conn != null) {
        try {
          conn.close()
        }
        catch {
          case e: SQLException => ;
        }
      }
    }
  }

  protected def openConnection(): Connection = {
    try {
      dataSource.getConnection()
    }
    catch {
      case e: SQLException => throw new DatabaseException(cause = e)
    }
  }
}
