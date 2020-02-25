package com.linearframework.sql.query.retriever

import com.linearframework.sql.query.{Query, Row}
import scala.collection.mutable

/**
  * Handles retrieval of multiple records
  */
trait RecordsRetriever[R] {

  /**
    * Executes the query, return data from all rows of the result set.
    */
  def execute(): Seq[R]

}

private[sql] object RecordsRetriever {

  def apply[R](query: Query, map: Row => R): RecordsRetriever[R] = {
    new RecordsRetrieverImpl(query, map)
  }

  private class RecordsRetrieverImpl[R](private val query: Query, private val map: Row => R) extends RecordsRetriever[R] {
    override def execute(): Seq[R] = {
      query.executeQuery { rs =>
        val result = new mutable.ListBuffer[R]()
        while (rs.next()) {
          result += map(new Row(rs))
        }
        result.toSeq
      }
    }
  }

}