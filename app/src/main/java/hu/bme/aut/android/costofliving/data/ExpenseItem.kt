package hu.bme.aut.android.costofliving.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenseitem")
data class ExpenseItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "cost") var cost: Int,
    @ColumnInfo(name = "is_expense") var isExpense: Boolean,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "year") var year: Int,
    @ColumnInfo(name = "month") var month: Int,
    @ColumnInfo(name = "is_shared") var isShared: Boolean
)
