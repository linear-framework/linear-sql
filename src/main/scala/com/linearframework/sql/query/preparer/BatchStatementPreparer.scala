package com.linearframework.sql.query.preparer

import com.linearframework.sql.DatabaseException
import com.linearframework.sql.query.parser.BatchParser
import java.sql.{Connection, PreparedStatement, Statement}

private[sql] object BatchStatementPreparer extends StatementPreparer {

  override def prepare(connection: Connection, sql: String, params: List[List[(String, Any)]], returnGeneratedKeys: Boolean): PreparedStatement = {
    val parsed = BatchParser.parse(sql, params)

    val stmt = {
      if (returnGeneratedKeys) {
        connection.prepareStatement(parsed.sql, Statement.RETURN_GENERATED_KEYS)
      }
      else {
        connection.prepareStatement(parsed.sql)
      }
    }

    for (param <- params) {
      param.foreach({ case (key, value) =>
        if (!parsed.params.contains(key)) {
          throw new DatabaseException(s"Parameter '$key' was not specified")
        }
        val position = parsed.params(key).index
        setParam(stmt, value, position)
      })
      stmt.addBatch()
    }

    stmt
  }

}
