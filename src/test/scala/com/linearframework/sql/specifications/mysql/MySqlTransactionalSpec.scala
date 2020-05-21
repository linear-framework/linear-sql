package com.linearframework.sql.specifications.mysql

import com.linearframework.BaseSpec
import com.linearframework.sql.datasources.MySqlDataSource
import org.scalatest.Ignore

@Ignore // requires Postgres to be running - see docker-compose.yml
class MySqlTransactionalSpec extends BaseSpec with MySqlDataSource {

  override def beforeEach(): Unit = {
    super.beforeEach()
    execute("DROP TABLE IF EXISTS persons;")
    execute("DROP TABLE IF EXISTS pets;")
    execute("CREATE TABLE IF NOT EXISTS persons(id INTEGER AUTO_INCREMENT PRIMARY KEY, enabled BOOLEAN, fname VARCHAR(255));")
    execute("CREATE TABLE IF NOT EXISTS pets(id INTEGER AUTO_INCREMENT PRIMARY KEY, person_id INTEGER, name VARCHAR(255));")
  }

  override def afterEach(): Unit = {
    super.afterEach()
    execute("DROP TABLE IF EXISTS persons;")
    execute("DROP TABLE IF EXISTS pets;")
  }

  "A MySQL transaction" can "commit multiple queries" in {
    val id =
      db.transaction(tx => {
        val billyId =
          tx.sql("INSERT INTO persons(fname, enabled) VALUES ({name}, {enabled})")
            .params("name" -> "Billy", "enabled" -> true)
            .returningKey(_.getLong("id"))
            .execute()

        val petId =
          tx.sql("INSERT INTO pets(person_id, name) VALUES({id}, {name})")
            .params("id" -> billyId, "name" -> "Rover")
            .returningKey(_.getLong("id"))
            .execute()

        tx.returning(petId).commit()
      })

    val personId = execute("SELECT id FROM persons", rs => {
      rs.next()
      rs.getLong("id")
    })
    val petPersonId = execute("SELECT person_id FROM pets", rs => {
      rs.next()
      rs.getLong("person_id")
    })
    val petId = execute("SELECT id FROM pets", rs => {
      rs.next()
      rs.getLong("id")
    })

    personId > 0 should be(true)
    personId should be(petPersonId)
    petId should be (id)
  }

  it can "rollback queries in the transaction" in {
    db.transaction(tx => {
      val billyId =
        tx.sql("INSERT INTO persons(fname, enabled) VALUES ({name}, {enabled})")
          .params("name" -> "Billy", "enabled" -> true)
          .returningKey(_.getLong("id"))
          .execute()

      tx.sql("INSERT INTO pets(person_id, name) VALUES({id}, {name})")
        .params("id" -> billyId, "name" -> "Rover")
        .execute()

      tx.rollback()
    })

    val personId = execute("SELECT id FROM persons", rs => {
      if (rs.next()) rs.getLong("id") else -2
    })
    val petId = execute("SELECT person_id FROM pets", rs => {
      if (rs.next()) rs.getLong("id") else -3
    })

    personId should be(-2)
    petId should be(-3)
  }

  it should "rollback if an exception is thrown" in {
    try {
      db.transaction(tx => {
        val billyId =
          tx.sql("INSERT INTO persons(fname, enabled) VALUES ({name}, {enabled})")
            .params("name" -> "Billy", "enabled" -> true)
            .returningKey(_.getLong("id"))
            .execute()

        tx.sql("INSERT INTO pets(person_id, name) VALUES({id}, {name})")
          .params("id" -> billyId, "name" -> "Rover")
          .execute()

        throw new IllegalStateException("Oops!")

        tx.commit()
      })
    }
    catch {
      case e: IllegalStateException =>
    }

    val personId = execute("SELECT id FROM persons", rs => {
      if (rs.next()) rs.getLong("id") else -2
    })
    val petId = execute("SELECT person_id FROM pets", rs => {
      if (rs.next()) rs.getLong("id") else -3
    })

    personId should be(-2)
    petId should be(-3)
  }
}

