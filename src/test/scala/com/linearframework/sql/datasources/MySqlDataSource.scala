package com.linearframework.sql.datasources

import com.linearframework.BaseSpec
import javax.sql.DataSource

trait MySqlDataSource extends BaseSpec {
  override protected val dataSource: DataSource = {
    basicDataSource(
      url = "jdbc:mariadb://localhost:3310/sprout_sql_test",
      driver = "org.mariadb.jdbc.Driver",
      username = "sprout_user",
      password = "sprout_password"
    )
  }
}
