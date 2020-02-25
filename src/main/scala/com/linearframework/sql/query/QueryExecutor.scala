package com.linearframework.sql.query

import com.linearframework.sql.query.retriever._

/**
  * Configures the execution mode for a SQL query.
  */
trait QueryExecutor {

  /**
    * Sets the query execution mode.
    * In "Record" mode, the query will return a single record from the database, if one was found.
    * @param asObject a function which converts a single row of a result set to an object
    */
  def returningRecord[R](asObject: Row => R): RecordRetriever[R]

  /**
    * Sets the query execution mode.
    * In "Records" mode, the query will return multiple records from the database.
    * @param asObject a function which converts a single row of a result set to an object
    */
  def returningRecords[R](asObject: Row => R): RecordsRetriever[R]

  /**
    * Sets the query execution mode.
    * In "Key" mode, the query will return a single generated key from the database,
    * if one was found.
    * @param selectKey a function which, given a row of a ResultSet, returns the key from that row
    */
  def returningKey[K](selectKey: Row => K): KeyRetriever[K]

  /**
    * Sets the query execution mode.
    * In "Keys" mode, the query will return multiple generated keys from the database,
    * if one was found.
    * @param selectKey a function which, given a row of a ResultSet, returns the key from that row
    */
  def returningKeys[K](selectKey: Row => K): KeysRetriever[K]

  /**
    * Executes this query, returning nothing from the database.
    */
  def execute(): Unit

}

private[sql] object QueryExecutor {

  def apply(query: Query): QueryExecutor = {
    new QueryExecutorImpl(query)
  }

  private class QueryExecutorImpl(private val query: Query) extends QueryExecutor {
    override def returningRecord[R](asObject: Row => R): RecordRetriever[R] = {
      RecordRetriever(query, asObject)
    }

    override def returningRecords[R](asObject: Row => R): RecordsRetriever[R] = {
      RecordsRetriever(query, asObject)
    }

    override def returningKey[K](selectKey: Row => K): KeyRetriever[K] = {
      KeyRetriever(query, selectKey)
    }

    override def returningKeys[K](selectKey: Row => K): KeysRetriever[K] = {
      KeysRetriever(query, selectKey)
    }

    override def execute(): Unit = {
      query.execute()
    }
  }

}