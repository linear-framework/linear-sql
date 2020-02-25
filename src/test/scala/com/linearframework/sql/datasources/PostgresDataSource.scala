package com.linearframework.sql.datasources

import com.linearframework.BaseSpec
import javax.sql.DataSource

trait PostgresDataSource extends BaseSpec {
  override protected val dataSource: DataSource = {
    basicDataSource(
      url = "jdbc:postgresql://localhost:5436/sprout_sql_test",
      driver = "org.postgresql.Driver",
      username = "sprout_user",
      password = "sprout_password"
    )
  }
}
