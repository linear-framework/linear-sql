package com.linearframework.sql

/**
 * An interaction with commit/rollback capabilities
 */
trait Committable {

  /**
    * Whether or not this interaction is closed (i.e., it has already been committed or rolled back,
    * or the underlying connection is closed)
    */
  def isClosed: Boolean

  /**
    * Commits this interaction
    */
  def commit(): Unit

  /**
    * Rolls back this interaction
    */
  def rollback(): Unit

}
