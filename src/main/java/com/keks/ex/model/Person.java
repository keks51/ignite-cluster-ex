package com.keks.ex.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;


public class Person {

    @QuerySqlField(index = true)
    private final Integer id;

    @QuerySqlField()
    private final String name;


    private final Integer age;

    private final Float salary;


    public Person(Integer id, String name, Integer age, Float salary) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.salary = salary;
    }
}

