package com.linearframework.sql.query.preparer

import com.linearframework.sql.query.parser.SingleStatementParser
import java.sql.{Connection, PreparedStatement, Statement}
import java.util
import scala.jdk.CollectionConverters._

private[sql] object SingleStatementPreparer extends StatementPreparer {

  override def prepare(connection: Connection, sql: String, params: List[List[(String, Any)]], returnGeneratedKeys: Boolean): PreparedStatement = {
    val parsed = SingleStatementParser.parse(sql, params)

    val stmt = {
      if (returnGeneratedKeys) {
        connection.prepareStatement(parsed.sql, Statement.RETURN_GENERATED_KEYS)
      }
      else {
        connection.prepareStatement(parsed.sql)
      }
    }

    var index = 0
    val param = params.head.toMap
    parsed.params.toList.sortWith(_._2.index < _._2.index).foreach(entry => {
      param(entry._1) match {
        case p: Iterable[_] => for (innerP <- p) {
          index = index + 1
          setParam(stmt, innerP, index)
        }
        case p: util.Collection[_] => for (innerP <- p.asScala) {
          index = index + 1
          setParam(stmt, innerP, index)
        }
        case p: scala.Array[_] => for (innerP <- p) {
          index = index + 1
          setParam(stmt, innerP, index)
        }
        case p =>
          index = index + 1
          setParam(stmt, p, index)
      }
    })

    stmt
  }

}
