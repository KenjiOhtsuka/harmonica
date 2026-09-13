import com.improve_future.harmonica.core.AbstractMigration

object : AbstractMigration() {
    override fun up() {
        createTable("demo_plain") {
            varchar("name")
        }
        executeSql("INSERT INTO demo_plain (name) VALUES ('harmonica')")
    }

    override fun down() {
        dropTable("demo_plain")
    }
}
