import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.exposed.exposedTransaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object DemoExposedTable : Table("demo_exposed") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    override val primaryKey = PrimaryKey(id)
}

object : AbstractMigration() {
    override fun up() {
        exposedTransaction {
            SchemaUtils.create(DemoExposedTable)
            DemoExposedTable.insert { it[name] = "harmonica" }
            println("exposed rows: " + DemoExposedTable.selectAll().count())
        }
    }

    override fun down() {
        exposedTransaction {
            SchemaUtils.drop(DemoExposedTable)
        }
    }
}
