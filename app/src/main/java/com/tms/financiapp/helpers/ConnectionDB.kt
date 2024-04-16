package com.tms.financiapp.helpers

import android.os.StrictMode
import java.sql.Connection
import java.sql.DriverManager

class ConnectionDB {
    private val url = "jdbc:mysql://viaduct.proxy.rlwy.net:40225/financiapp"
    private val user = "root"
    private val password = "MPwygNVueWBQaXdjNVpeBujrrIvAaQfE"



    fun getConnection(): Connection? {
        return try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            DriverManager.getConnection(url, user, password)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun close(connection: Connection?) {
        if (connection != null) {
            try {
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}