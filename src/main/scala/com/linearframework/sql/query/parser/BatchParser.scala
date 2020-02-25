package com.linearframework.sql.query.parser

private[sql] object BatchParser extends QueryParser {

  override def parse(sql: String, params: List[List[(String, Any)]]): ParsedQuery = {
    val paramSql = sql.replaceAll(PARAM_TOKEN, "?")

    val positions =
      PARAM_TOKEN.r
        .findAllIn(sql)
        .zipWithIndex
        .map { case (key, index) =>
          key.replaceAll("[{}]", "") -> ParamIndex(index + 1, 1)
        }
        .toMap

    ParsedQuery(paramSql, positions)
  }

}
