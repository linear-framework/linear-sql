package com.linearframework.sql.query

import java.io.{InputStream, Reader}
import java.lang
import java.net.URL
import java.sql.{Blob, Clob}
import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneOffset, ZonedDateTime}

/**
  * Fetches data from an underlying [[java.sql.ResultSet]]
  */
class Row private[sql](private val inner: java.sql.ResultSet) {

  private def option[T](result: T): Option[T] = {
    if (result == null || inner.wasNull()) {
      None
    }
    else {
      Some(result)
    }
  }

  private def unbox[T](result: Option[T]): T = {
    result match {
      case Some(x) =>
        x
      case None =>
        null.asInstanceOf[T]
    }
  }

  def getStringOption(columnIndex: Int): Option[String] = option(inner.getString(columnIndex))
  def getBooleanOption(columnIndex: Int): Option[Boolean] = option(inner.getBoolean(columnIndex))
  def getByteOption(columnIndex: Int): Option[Byte] = option(inner.getByte(columnIndex))
  def getShortOption(columnIndex: Int): Option[Short] = option(inner.getShort(columnIndex))
  def getIntOption(columnIndex: Int): Option[Int] = option(inner.getInt(columnIndex))
  def getLongOption(columnIndex: Int): Option[Long] = option(inner.getLong(columnIndex))
  def getFloatOption(columnIndex: Int): Option[Float] = option(inner.getFloat(columnIndex))
  def getDoubleOption(columnIndex: Int): Option[Double] = option(inner.getDouble(columnIndex))
  def getBigDecimalOption(columnIndex: Int): Option[BigDecimal] = option(inner.getBigDecimal(columnIndex))
  def getBytesOption(columnIndex: Int): Option[Array[Byte]] = option(inner.getBytes(columnIndex))
  def getDateOption(columnIndex: Int): Option[LocalDate] = option(inner.getDate(columnIndex)).map(_.toLocalDate)
  def getTimeOption(columnIndex: Int): Option[LocalTime] = option(inner.getTime(columnIndex)).map(_.toLocalTime)
  def getTimestampOption(columnIndex: Int): Option[LocalDateTime] = option(inner.getTimestamp(columnIndex)).map(_.toLocalDateTime)
  def getZonedTimestampOption(columnIndex: Int): Option[ZonedDateTime] = option(inner.getTimestamp(columnIndex)).map(_.toLocalDateTime.atZone(ZoneOffset.UTC))
  def getAsciiStreamOption(columnIndex: Int): Option[InputStream] = option(inner.getAsciiStream(columnIndex))
  def getBinaryStreamOption(columnIndex: Int): Option[InputStream] = option(inner.getBinaryStream(columnIndex))
  def getCharacterStreamOption(columnIndex: Int): Option[Reader] = option(inner.getCharacterStream(columnIndex))
  def getBlobOption(columnIndex: Int): Option[Blob] = option(inner.getBlob(columnIndex))
  def getClobOption(columnIndex: Int): Option[Clob] = option(inner.getClob(columnIndex))
  def getURLOption(columnIndex: Int): Option[URL] = option(inner.getURL(columnIndex))

  def getStringOption(columnLabel: String): Option[String] = option(inner.getString(columnLabel))
  def getBooleanOption(columnLabel: String): Option[Boolean] = option(inner.getBoolean(columnLabel))
  def getByteOption(columnLabel: String): Option[Byte] = option(inner.getByte(columnLabel))
  def getShortOption(columnLabel: String): Option[Short] = option(inner.getShort(columnLabel))
  def getIntOption(columnLabel: String): Option[Int] = option(inner.getInt(columnLabel))
  def getLongOption(columnLabel: String): Option[Long] = option(inner.getLong(columnLabel))
  def getFloatOption(columnLabel: String): Option[Float] = option(inner.getFloat(columnLabel))
  def getDoubleOption(columnLabel: String): Option[Double] = option(inner.getDouble(columnLabel))
  def getBigDecimalOption(columnLabel: String): Option[BigDecimal] = option(inner.getBigDecimal(columnLabel))
  def getBytesOption(columnLabel: String): Option[Array[Byte]] = option(inner.getBytes(columnLabel))
  def getDateOption(columnLabel: String): Option[LocalDate] = option(inner.getDate(columnLabel)).map(_.toLocalDate)
  def getTimeOption(columnLabel: String): Option[LocalTime] = option(inner.getTime(columnLabel)).map(_.toLocalTime)
  def getTimestampOption(columnLabel: String): Option[LocalDateTime] = option(inner.getTimestamp(columnLabel)).map(_.toLocalDateTime)
  def getZonedTimestampOption(columnLabel: String): Option[ZonedDateTime] = option(inner.getTimestamp(columnLabel)).map(_.toLocalDateTime.atZone(ZoneOffset.UTC))
  def getAsciiStreamOption(columnLabel: String): Option[InputStream] = option(inner.getAsciiStream(columnLabel))
  def getBinaryStreamOption(columnLabel: String): Option[InputStream] = option(inner.getBinaryStream(columnLabel))
  def getCharacterStreamOption(columnLabel: String): Option[Reader] = option(inner.getCharacterStream(columnLabel))
  def getBlobOption(columnLabel: String): Option[Blob] = option(inner.getBlob(columnLabel))
  def getClobOption(columnLabel: String): Option[Clob] = option(inner.getClob(columnLabel))
  def getURLOption(columnLabel: String): Option[URL] = option(inner.getURL(columnLabel))

  def getString(columnIndex: Int): String = unbox(getStringOption(columnIndex))
  def getBoolean(columnIndex: Int): lang.Boolean = {
    val result = inner.getBoolean(columnIndex)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Boolean.valueOf(result)
    }
  }
  def getByte(columnIndex: Int): lang.Byte = {
    val result = inner.getByte(columnIndex)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Byte.valueOf(result)
    }
  }
  def getShort(columnIndex: Int): lang.Short = {
    val result = inner.getShort(columnIndex)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Short.valueOf(result)
    }
  }
  def getInt(columnIndex: Int): lang.Integer = {
    val result = inner.getInt(columnIndex)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Integer.valueOf(result)
    }
  }
  def getLong(columnIndex: Int): lang.Long = {
    val result = inner.getLong(columnIndex)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Long.valueOf(result)
    }
  }
  def getFloat(columnIndex: Int): lang.Float = {
    val result = inner.getFloat(columnIndex)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Float.valueOf(result)
    }
  }
  def getDouble(columnIndex: Int): lang.Double = {
    val result = inner.getDouble(columnIndex)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Double.valueOf(result)
    }
  }
  def getBigDecimal(columnIndex: Int): BigDecimal = unbox(getBigDecimalOption(columnIndex))
  def getBytes(columnIndex: Int): Array[Byte] = unbox(getBytesOption(columnIndex))
  def getDate(columnIndex: Int): LocalDate = unbox(getDateOption(columnIndex))
  def getTime(columnIndex: Int): LocalTime = unbox(getTimeOption(columnIndex))
  def getTimestamp(columnIndex: Int): LocalDateTime = unbox(getTimestampOption(columnIndex))
  def getZonedTimestamp(columnIndex: Int): ZonedDateTime = unbox(getZonedTimestampOption(columnIndex))
  def getAsciiStream(columnIndex: Int): InputStream = unbox(getAsciiStreamOption(columnIndex))
  def getBinaryStream(columnIndex: Int): InputStream = unbox(getBinaryStreamOption(columnIndex))
  def getCharacterStream(columnIndex: Int): Reader = unbox(getCharacterStreamOption(columnIndex))
  def getBlob(columnIndex: Int): Blob = unbox(getBlobOption(columnIndex))
  def getClob(columnIndex: Int): Clob = unbox(getClobOption(columnIndex))
  def getURL(columnIndex: Int): URL = unbox(getURLOption(columnIndex))

  def getString(columnLabel: String): String = unbox(getStringOption(columnLabel))
  def getBoolean(columnLabel: String): lang.Boolean = {
    val result = inner.getBoolean(columnLabel)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Boolean.valueOf(result)
    }
  }
  def getByte(columnLabel: String): lang.Byte = {
    val result = inner.getByte(columnLabel)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Byte.valueOf(result)
    }
  }
  def getShort(columnLabel: String): lang.Short = {
    val result = inner.getShort(columnLabel)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Short.valueOf(result)
    }
  }
  def getInt(columnLabel: String): lang.Integer = {
    val result = inner.getInt(columnLabel)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Integer.valueOf(result)
    }
  }
  def getLong(columnLabel: String): lang.Long = {
    val result = inner.getLong(columnLabel)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Long.valueOf(result)
    }
  }
  def getFloat(columnLabel: String): lang.Float = {
    val result = inner.getFloat(columnLabel)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Float.valueOf(result)
    }
  }
  def getDouble(columnLabel: String): lang.Double = {
    val result = inner.getDouble(columnLabel)
    if (inner.wasNull()) {
      null
    }
    else {
      lang.Double.valueOf(result)
    }
  }
  def getBigDecimal(columnLabel: String): BigDecimal = unbox(getBigDecimalOption(columnLabel))
  def getBytes(columnLabel: String): Array[Byte] = unbox(getBytesOption(columnLabel))
  def getDate(columnLabel: String): LocalDate = unbox(getDateOption(columnLabel))
  def getTime(columnLabel: String): LocalTime = unbox(getTimeOption(columnLabel))
  def getTimestamp(columnLabel: String): LocalDateTime = unbox(getTimestampOption(columnLabel))
  def getZonedTimestamp(columnLabel: String): ZonedDateTime = unbox(getZonedTimestampOption(columnLabel))
  def getAsciiStream(columnLabel: String): InputStream = unbox(getAsciiStreamOption(columnLabel))
  def getBinaryStream(columnLabel: String): InputStream = unbox(getBinaryStreamOption(columnLabel))
  def getCharacterStream(columnLabel: String): Reader = unbox(getCharacterStreamOption(columnLabel))
  def getBlob(columnLabel: String): Blob = unbox(getBlobOption(columnLabel))
  def getClob(columnLabel: String): Clob = unbox(getClobOption(columnLabel))
  def getURL(columnLabel: String): URL = unbox(getURLOption(columnLabel))

}
