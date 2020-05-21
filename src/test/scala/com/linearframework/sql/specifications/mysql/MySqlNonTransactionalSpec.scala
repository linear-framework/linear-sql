package com.linearframework.sql.specifications.mysql

import com.linearframework.BaseSpec
import com.linearframework.sql.DatabaseException
import com.linearframework.sql.datasources.MySqlDataSource
import org.scalatest.Ignore

@Ignore // requires Postgres to be running - see docker-compose.yml
class MySqlNonTransactionalSpec extends BaseSpec with MySqlDataSource {

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    execute("DROP TABLE IF EXISTS persons;")
    execute("CREATE TABLE persons(id INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(60), age INTEGER)")
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    execute("DROP TABLE IF EXISTS persons;")
  }

  "A MySQL DataSource" can "create a record returning its auto-generated key" in {
    val id =
      db.sql("INSERT INTO persons(name, age) VALUES ({name}, {age})")
        .params("name" -> "Billy", "age" -> 33)
        .returningKey(_.getLong("id"))
        .execute()

    (id > 0L) should be(true)
  }

  it can "create multiple records, returning all auto-generated keys" in {
    val keys =
      db
        .sql("INSERT INTO persons(name, age) VALUES ({name}, {age})")
        .batch(
          List("name" -> "Steve", "age" -> 22),
          List("name" -> "Jenny", "age" -> 25)
        )
        .returningKeys(_.getLong("id"))
        .execute()

    keys.length should be(2)
    keys.foreach(key => (key > 0) should be(true))
  }

  it should "throw an exception when an auto-generated key is requested but could not be found" in {
    a[DatabaseException] should be thrownBy {
      db
        .sql("UPDATE persons SET age = {age} WHERE name = {name}")
        .params("age" -> 100, "name" -> "old dude")
        .returningKey(_.getLong("id"))
        .execute()
    }
  }

  it can "execute a SQL statement without returning anything" in {
    db
      .sql("INSERT INTO persons(name, age) VALUES ({name}, {age})")
      .params("name" -> "Annie", "age" -> 12)
      .execute()

    db
      .sql("INSERT INTO persons(name, age) VALUES ({name}, {age})")
      .batch(
        List("name" -> "Keith", "age" -> 40),
        List("name" -> "Kevin", "age" -> 18),
        List("name" -> "Kevin", "age" -> null)
      )
      .execute()

    val count = execute("SELECT COUNT(1) FROM persons", rs => {
      rs.next()
      rs.getLong(1)
    })
    count should be(4)
  }

  it can "fetch a record from the database" in {
    execute("INSERT INTO persons(name, age) VALUES ('Leslie', 30)")
    val (name, age) =
      db
        .sql("SELECT * FROM persons WHERE name = 'Leslie'")
        .returningRecord(rs => {
          rs.getString("name") -> rs.getInt("age")
        })
        .execute()
        .get

    name should be("Leslie")
    age should be(30)
  }

  it should "return None when a record could not be found" in {
    val person =
      db
        .sql("SELECT * FROM persons WHERE name IN({names})")
        .params("names" -> List("Donald", "George"))
        .returningRecord(_.getString("name"))
        .execute()

    person should be(None)
  }

  it can "fetch multiple records from the database" in {
    execute("INSERT INTO persons(name, age) VALUES ('Bert', 65)")
    execute("INSERT INTO persons(name, age) VALUES ('Ernie', 62)")

    val muppets =
      db
        .sql("SELECT * FROM persons ORDER BY name")
        .returningRecords(rs => {
          rs.getString("name") -> rs.getInt("age")
        })
        .execute()

    muppets.head._1 should be("Bert")
    muppets.head._2 should be(65)

    muppets.last._1 should be("Ernie")
    muppets.last._2 should be(62)
  }

  it should "handle nulls correctly" in {
    execute("INSERT INTO persons(name, age) VALUES (NULL, NULL)")

    val (name, age) =
      db.sql("SELECT * FROM persons")
        .returningRecord(rs => (rs.getString("name"), rs.getInt("age")))
        .execute()
        .get

    name should be (null)
    age should be (null)
  }

}

