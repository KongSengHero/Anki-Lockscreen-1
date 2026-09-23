package com.ankilock.anki

import java.sql.Connection 

class JdbcAnkiDbConnection(private val conn: Connection) : AnkiDbConnection { 

    override fun execSQL(sql: String) { 
        conn.createStatement().use { stmt -> 
            stmt.execute(sql) 
        } 
    } 

    override fun bindAndExecute(sql: String, args: Array<Any?>) { 
        conn.prepareStatement(sql).use { stmt -> 
            args.forEachIndexed { i, arg -> 
                stmt.setObject(i + 1, arg) 
            } 
            stmt.execute() 
        } 
    } 

    override fun close() { 
        conn.close() 
    } 
} 
