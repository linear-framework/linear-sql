package com.linearframework

import com.linearframework.sql.Database
import javax.sql.DataSource
import org.apache.commons.dbcp2.BasicDataSource
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.junit.JUnitRunner
import org.scalatestplus.mockito.MockitoSugar
import java.sql.{Connection, ResultSet}

@RunWith(classOf[JUnitRunner])
abstract class BaseSpec extends AnyFlatSpec with Matchers with MockitoSugar with BeforeAndAfterAll with BeforeAndAfterEach {

  protected val dataSource: DataSource

  protected lazy val db: Database = Database(dataSource)

  protected def basicDataSource(url: String, driver: String, username: String, password: String): DataSource = {
    val ds = new BasicDataSource
    ds.setDriverClassName(driver)
    ds.setUrl(url)
    ds.setUsername(username)
    ds.setPassword(password)
    ds
  }

  private def useConnection[T](use: Connection => T): T = {
    var conn: Connection = null
    try {
      conn = dataSource.getConnection
      use(conn)
    }
    finally {
      if (conn != null) {
        conn.close()
      }
    }
  }

  protected def execute(sql: String): Unit = {
    useConnection(conn => {
      conn.setAutoCommit(true)
      conn.createStatement.execute(sql)
    })
  }

  protected def execute[T](sql: String, mapper: ResultSet => T): T = {
    useConnection(conn => {
      conn.setAutoCommit(true)
      var rs: ResultSet = null
      try {
        rs = conn.createStatement.executeQuery(sql)
        mapper(rs)
      }
      finally {
        if (rs != null) {
          rs.close()
        }
      }
    })
  }

}
