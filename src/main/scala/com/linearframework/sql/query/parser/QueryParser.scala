package com.linearframework.sql.query.parser

private[sql] trait QueryParser {

  protected val PARAM_TOKEN = "(\\{)([\\s\\w!#$%&()*+,-./:;<=>?@^_`|~]*)(\\})"

  def parse(sql: String, params: List[List[(String, Any)]]): ParsedQuery

}
