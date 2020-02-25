package com.linearframework.sql.query

import com.linearframework.sql.query.retriever._


/**
  * Builds/configures a SQL query.
  */
trait QueryBuilder extends QueryExecutor {

  /**
    * Sets the parameters of this query.
    */
  def params(params: (String, Any)*): QueryExecutor

  /**
    * Sets the parameters of this query in batch execution mode.
    */
  def batch(params: List[(String, Any)]*): QueryExecutor

  /**
    * Sets the query execution mode without using parameters.
    * In "Row" mode, the query will return a single record from the database, if one was found.
    * @param asObject a function which converts a single row of a result set to an object
    */
  override def returningRecord[R](asObject: Row => R): RecordRetriever[R]

  /**
    * Sets the query execution mode without using parameters.
    * In "Records" mode, the query will return multiple records from the database.
    * @param asObject a function which converts a single row of a result set to an object
    */
  override def returningRecords[R](asObject: Row => R): RecordsRetriever[R]

  /**
    * Sets the query execution mode without using parameters.
    * In "Key" mode, the query will return a single generated key from the database,
    * if one was found.
    * @param selectKey a function which, given a row of a ResultSet, returns the key from that row
    */
  override def returningKey[K](selectKey: Row => K): KeyRetriever[K]

  /**
    * Sets the query execution mode without using parameters.
    * In "Keys" mode, the query will return multiple generated keys from the database,
    * if one was found.
    * @param selectKey a function which, given a row of a ResultSet, returns the key from that row
    */
  override def returningKeys[K](selectKey: Row => K): KeysRetriever[K]

  /**
    * Executes this query without parameters, returning nothing from the database.
    */
  override def execute(): Unit

}

private[sql] object QueryBuilder {

  def apply(query: Query): QueryBuilder = {
    new QueryBuilderImpl(query)
  }

  private class QueryBuilderImpl(private val query: Query) extends QueryBuilder with QueryExecutor {
    private lazy val executor = QueryExecutor(query)

    override def params(params: (String, Any)*): QueryExecutor = {
      QueryExecutor(query.withParams(List(params.toList)))
    }

    override def batch(params: List[(String, Any)]*): QueryExecutor = {
      QueryExecutor(query.enableBatchMode().withParams(params.toList))
    }

    override def execute(): Unit = {
      query.execute()
    }

    override def returningRecord[R](asObject: Row => R): RecordRetriever[R] = executor.returningRecord(asObject)

    override def returningRecords[R](asObject: Row => R): RecordsRetriever[R] = executor.returningRecords(asObject)

    override def returningKey[K](selectKey: Row => K): KeyRetriever[K] = executor.returningKey(selectKey)

    override def returningKeys[K](selectKey: Row => K): KeysRetriever[K] = executor.returningKeys(selectKey)
  }

}