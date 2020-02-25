# linear-sql
Database module of the **Linear Framework**.

## API
Linear SQL is a database-centric, SQL-first API.  
It is **NOT** an ORM or a SQL builder, nor does it attempt to be.

### Quick Demo
```scala
import com.linearframework.sql._

val db = Database(jdbcDataSource)

db.sql("""
    INSERT INTO persons (first_name, last_name)
    VALUES ({first}, {last})
  """)
  .params(
    "first" -> "Bill",
    "last" -> "Gates"
  )
  .execute()
```

### DataSource Configuration
A Linear `Database` object wraps an existing JDBC DataSource:
```scala
import com.linearframework.sql._

val db = Database(jdbcDataSource)
```  

Some frameworks provide access to their underlying DataSource, others do not.  If you need to manually create a DataSource, 
you can use a library like [commons-dbcp2](https://commons.apache.org/proper/commons-dbcp/), or use your database vendor's 
Java driver.

With a `Database` object in hand, the API mostly speaks for itself:

### Non-Parameterized Query
```scala
db.sql("""
    INSERT INTO persons (first_name, last_name)
    VALUES ('Bill', 'Gates')
  """)
  .execute()
```

### Parameterized Query
```scala
db.sql("""
    INSERT INTO persons (first_name, last_name)
    VALUES ({first}, {last})
  """)
  .params(
    "first" -> "Bill",
    "last" -> "Gates"
  )
  .execute()
```

### Insert and return the generated ID
```scala
val id: Long =
  db.sql("""
      INSERT INTO persons (first_name, last_name)
      VALUES ({first}, {last})
    """)
    .params(
      "first" -> "Bill",
      "last" -> "Gates"
    )
    .returningKey(_.getLong("person_id"))
    .execute()
```

### Batch Insert returning all generated IDs
```scala
val ids: Seq[Long] =
  db.sql("""
      INSERT INTO persons (first_name, last_name)
      VALUES ({first}, {last})
    """)
    .batch(
      Seq("first" -> "Bill", "last" -> "Gates"),
      Seq("first" -> "Melinda", "last" -> "Gates")
    )
    .returningKeys(_.getLong("person_id"))
    .execute()
```

### Select one record
```scala
val name: Option[(String, String)] =
  db.sql("""
      SELECT first_name, last_name
      FROM persons
      WHERE person_id = {id}
    """)
    .params(
      "id" -> 1L
    )
    .returningRecord { rs =>
      (rs.getString("first_name"), rs.getString("last_name"))
    }
    .execute()
```

### Select multiple records
```scala
val steves: Seq[String] =
  db.sql("""
      SELECT last_name
      FROM persons
      WHERE first_name = {name}
    """)
    .params(
      "name" -> "Steve"
    )
    .returningRecords(_.getString("last_name"))
    .execute()
```

### Transactions
A `Database` can also manage transactions.

The API of a `Transaction` is the same as the `Database` itself, 
but with the addition of `commit()` and `rollback()` functions.  

You must explicitly call `commit()` to commit a transaction.
Transactions will be automatically rolled back if `commit()` is missing, or if an exception is thrown
before `commit()` could be reached.

```scala
val petId: Long =
  db.transaction { tx =>
  
    val personId =
      tx.sql("""
          INSERT INTO persons (first_name, last_name)
          VALUES ({first}, {last})
        """)
        .params(
          "first" -> "Bill",
          "last" -> "Gates"
        )
        .returningKey(_.getLong("person_id"))
        .execute()

    val petId =
      tx.sql("""
          INSERT INTO pets (person_id, name)
          VALUES ({personId}, {name})
        """)
        .params(
          "personId" -> personId,
          "name" -> "Clippy"
        )
        .returningKey(_.getLong("pet_id"))
        .execute()

    tx.commit()
    petId
  }
```