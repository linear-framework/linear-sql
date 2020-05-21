package com.linearframework.sql

/**
  * Adds syntactic sugar for generating parameterized SQL statements
  */
trait ParameterizedSql {

  implicit class ParameterizedSqlInterpolator(context: StringContext) {

    /**
      * Converts the interpolated string into a parameterized SQL string
      */
    def parameterized(substitutions: Any*): (String, List[(String, Any)]) = {
      val paramMap =
        substitutions
          .zipWithIndex
          .map { case (sub, i) =>  i -> sub }
          .toMap

      val sql =
        context
          .parts
          .zipWithIndex
          .foldLeft("") { (string, part) =>
            val param =
              if (paramMap.contains(part._2)) {
                s"{_interpolated_value_${part._2}}"
              }
              else {
                ""
              }
            string + part._1 + param
          }

      val params =
        paramMap
        .map { case (i, value) =>
          s"_interpolated_value_$i" -> value
        }
        .toList

      (sql, params)
    }

    /**
      * Converts the interpolated string into a parameterized SQL string
      */
    def p(substitutions: Any*): (String, List[(String, Any)]) = {
      parameterized(substitutions: _*)
    }

  }

}
