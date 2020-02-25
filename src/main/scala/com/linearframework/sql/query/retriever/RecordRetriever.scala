package com.linearframework.sql.query.retriever

import com.linearframework.sql.query.{Query, Row}

/**
  * Handles retrieval of a single record
  */
trait RecordRetriever[R] {

  /**
    * Executes the query, returning data from the first row of the result set.
    */
  def execute(): Option[R]

}

private[sql] object RecordRetriever {

  def apply[R](query: Query, map: Row => R): RecordRetriever[R] = {
    new RecordRetrieverImpl(query, map)
  }

  private class RecordRetrieverImpl[R](private val query: Query, map: Row => R) extends RecordRetriever[R] {
    override def execute(): Option[R] = {
      query.executeQuery { rs =>
        if (rs.next()) {
          Option(map(new Row(rs)))
        }
        else {
          None
        }
      }
    }
  }

}