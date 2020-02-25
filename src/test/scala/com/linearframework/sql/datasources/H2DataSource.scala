package com.linearframework.sql.datasources

import com.linearframework.BaseSpec
import javax.sql.DataSource

trait H2DataSource extends BaseSpec {
  override protected val dataSource: DataSource = {
    basicDataSource(
      url = "jdbc:h2:mem:sprout_sql_test;DB_CLOSE_DELAY=-1",
      driver = "org.h2.Driver",
      username = "",
      password = ""
    )
  }
}
