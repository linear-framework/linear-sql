package com.linearframework.sql.query.parser

import com.linearframework.sql.DatabaseException
import java.util

private[sql] object SingleStatementParser extends QueryParser {

  override def parse(sql: String, params: List[List[(String, Any)]]): ParsedQuery = {
    if (params.size != 1) {
      throw new DatabaseException(s"Single Statement includes ${params.size} parameter sets: $sql")
    }

    val param = params.head.toMap

    val paramMap =
      PARAM_TOKEN.r
        .findAllIn(sql)
        .zipWithIndex
        .map { case (k, i) =>
          val key = k.replaceAll("[{}]", "")
          if (!param.contains(key)) {
            throw new DatabaseException(s"Parameter '$key' was not specified")
          }
          val index = i + 1
          val numFields =
            param(key) match {
              case p: Iterable[_] => p.size
              case p: util.Collection[_] => p.size
              case p: scala.Array[_] => p.length
              case _ => 1
            }
          key -> ParamIndex(index, numFields)
        }
        .toMap

    val paramSql =
      PARAM_TOKEN.r
        .replaceAllIn(sql, m => {
          val key = m.toString().replaceAll("[{}]", "")
          val entry = paramMap(key)
          List.fill(entry.size)("?").mkString(", ")
        })

    ParsedQuery(paramSql, paramMap)
  }

}
