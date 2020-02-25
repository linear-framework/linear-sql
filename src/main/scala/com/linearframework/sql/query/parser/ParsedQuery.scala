package com.linearframework.sql.query.parser

private[sql] case class ParsedQuery(
  sql: String,
  params: Map[String, ParamIndex]
)
