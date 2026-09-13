package com.improve_future.harmonica.demo.jarmonica.config

import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.core.Dbms

class Default : DbConfig({
    dbms = Dbms.PostgreSQL
    user = "developer"
    password = "developer"
    host = "127.0.0.1"
    dbName = "harmonica_demo"
})
