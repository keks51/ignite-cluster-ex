package com.keks.ex

import com.keks.ex.model.{Person, PersonKey}
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.SqlFieldsQuery
import org.apache.ignite.configuration.CacheConfiguration

import scala.jdk.CollectionConverters.{CollectionHasAsScala, IterableHasAsScala}


object ThickClientSqlWebConsoleEx {

  def main(args: Array[String]): Unit = {

    //  you can query data using web-console
    // for this example: select * from CACHE_SCHEMA.PERSON;

    val CACHE_NAME = "person-cache-v1"
    val CACHE_SCHEMA = "SCALA_CACHE_SCHEMA" // defined in docker-ignite.xml

    IgniteUtils.withIgniteDockerThickClient() { implicit thickClient =>
      val ccfg: CacheConfiguration[PersonKey, Person] = new CacheConfiguration[PersonKey, Person](CACHE_NAME)
        .setIndexedTypes(classOf[PersonKey], classOf[Person])
        .setSqlSchema(CACHE_SCHEMA)

      if (thickClient.cacheNames().asScala.toArray.contains(CACHE_NAME)) thickClient.destroyCache(CACHE_NAME)
      val cache: IgniteCache[PersonKey, Person] = thickClient.getOrCreateCache(ccfg)

      // putting
      (0 to 1_000).foreach { i =>
        cache.put(new PersonKey(i), new Person(i, "alex" + i, i, 1.0f))
      }

      // querying. ResultSet contains only fields id and name.
      val cursor = cache.query(new SqlFieldsQuery(s"SELECT * FROM Person limit 10"))
      cursor.asScala.foreach { row =>
        println(row)
      }


    }

  }


}
