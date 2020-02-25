package com.linearframework.sql.datasources

import com.linearframework.BaseSpec
import javax.sql.DataSource

trait PostgresDataSource extends BaseSpec {
  override protected val dataSource: DataSource = {
    basicDataSource(
      url = "jdbc:postgresql://localhost:5436/linear_sql_test",
      driver = "org.postgresql.Driver",
      username = "linear_user",
      password = "linear_password"
    )
  }
}
