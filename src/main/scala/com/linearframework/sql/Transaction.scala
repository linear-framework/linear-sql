package com.linearframework.sql

import com.linearframework.sql.query.{Query, QueryBuilder}
import java.sql.{Connection, SQLException}

/**
  * A transactional [[com.linearframework.sql.SqlRunner]]
  */
trait Transaction extends SqlRunner with Committable[Unit] {

  /**
    * Builds a transactional query
    */
  override def sql(query: String): QueryBuilder

  /**
    * Builds a transactional query
    */
  override def sql(sqlAndParams: (String, List[(String, Any)])): QueryBuilder

  /**
    * Whether or not this transaction is closed (i.e., it has already been committed or rolled back,
    * or the underlying connection is closed)
    */
  override def isClosed: Boolean

  /**
    * Sets the value that this transaction should return upon commit.
    */
  def returning[T](value: T): Committable[T]

  /**
    * Commits this transaction
    */
  override def commit(): Unit

  /**
    * Rolls back this transaction
    */
  override def rollback(): Unit

}

private[sql] object Transaction {

  def apply(connection: Connection): Transaction = {
    connection.setAutoCommit(false)
    new TransactionImpl(connection)
  }

  private class TransactionImpl(private val connection: Connection) extends Transaction {
    private var isCommitted = false
    private var isRolledBack = false

    override def sql(sql: String): QueryBuilder = {
      if (isClosed) {
        throw new DatabaseException("Transaction is closed")
      }
      val query = new Query(sql, isTransactional = true, connection)
      QueryBuilder(query)
    }

    override def sql(sqlAndParams: (String, List[(String, Any)])): QueryBuilder = {
      if (isClosed) {
        throw new DatabaseException("Transaction is closed")
      }
      val query = new Query(sqlAndParams._1, isTransactional = true, connection, params = List(sqlAndParams._2))
      QueryBuilder(query)
    }

    override def isClosed: Boolean = {
      isCommitted || isRolledBack || connection.isClosed
    }

    override def returning[T](value: T): Committable[T] = {
      val parent = this
      new Committable[T] {
        override def isClosed: Boolean = parent.isClosed
        override def commit(): T = { parent.commit(); value }
        override def rollback(): Unit = parent.rollback()
      }
    }

    override def commit(): Unit = {
      if (isClosed) {
        throw new DatabaseException("Transaction is closed")
      }

      try {
        connection.commit()
        isCommitted = true
      }
      catch {
        case e: SQLException =>
          try {
            rollback()
          }
          catch {
            case e: DatabaseException =>
              throw new DatabaseException("Transaction failed to commit; attempt to rollback also failed", e)
          }
          throw new DatabaseException("Failed to commit transaction; rolled back", e)
      }
      finally {
        try {
          connection.close()
        }
        catch {
          case _: SQLException => ;
        }
      }
    }

    override def rollback(): Unit = {
      if (isClosed) {
        throw new DatabaseException("Transaction is closed")
      }

      try {
        connection.rollback()
        isRolledBack = true
      }
      catch {
        case e: SQLException =>
          throw new DatabaseException("Transaction rollback failed", e)
      }
      finally {
        try {
          connection.close()
        }
        catch {
          case _: SQLException => ;
        }
      }
    }
  }

}