package com.linearframework.sql.query.preparer

import com.linearframework.sql.DatabaseException
import java.sql._
import java.time.{LocalDate, LocalDateTime, ZonedDateTime}
import java.util

private[sql] trait StatementPreparer {

  def prepare(connection: Connection, sql: String, params: List[List[(String, Any)]], returnGeneratedKeys: Boolean): PreparedStatement

  protected def setParam(stmt: PreparedStatement, value: Any, position: Int): Unit = {
    val rawValue =
      if (value == null) {
        null
      }
      else {
        value match {
          case v: Option[_] =>
            v match {
              case Some(x) => x
              case None => null
            }
          case v: Any => v
        }
      }

    if (rawValue == null) {
      stmt.setObject(position, rawValue)
    }
    else {
      rawValue match {
        case v: scala.BigDecimal => stmt.setBigDecimal(position, v.bigDecimal)
        case v: java.math.BigDecimal => stmt.setBigDecimal(position, v)
        case v: Boolean => stmt.setBoolean(position, v)
        case v: java.lang.Boolean => stmt.setBoolean(position, v)
        case v: Byte => stmt.setByte(position, v)
        case v: java.lang.Byte => stmt.setByte(position, v)
        case v: Double => stmt.setDouble(position, v)
        case v: java.lang.Double => stmt.setDouble(position, v)
        case v: Float => stmt.setFloat(position, v)
        case v: java.lang.Float => stmt.setFloat(position, v)
        case v: Int => stmt.setInt(position, v)
        case v: java.lang.Integer => stmt.setInt(position, v)
        case v: Long => stmt.setLong(position, v)
        case v: java.lang.Long => stmt.setLong(position, v)
        case v: Short => stmt.setShort(position, v)
        case v: java.lang.Short => stmt.setShort(position, v)

        case v: String => stmt.setString(position, v)

        case v: Date => stmt.setDate(position, v)
        case v: LocalDate => stmt.setDate(position, java.sql.Date.valueOf(v))

        case v: Timestamp => stmt.setTimestamp(position, v)
        case v: Time => stmt.setTime(position, v)
        case v: java.util.Date => stmt.setTimestamp(position, new Timestamp(v.getTime))
        case v: LocalDateTime => stmt.setTimestamp(position, Timestamp.valueOf(v))
        case v: ZonedDateTime => stmt.setTimestamp(position, Timestamp.valueOf(v.toLocalDateTime))

        case v: Clob => stmt.setClob(position, v)
        case v: Blob => stmt.setBlob(position, v)

        case v: scala.Array[_] => throw new DatabaseException("Array data types are not supported as query parameters")
        case v: Iterable[_] => throw new DatabaseException("Iterable data types are not supported as query parameters")
        case v: util.Collection[_] => throw new DatabaseException("Collection data types are not supported as query parameters")

        case v: Any => throw new DatabaseException(s"Data type [${v.getClass}] is not supported. Parameter must be converted to a supported type.")
      }
    }
  }

}
