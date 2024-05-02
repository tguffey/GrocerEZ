package com.example.grocerez.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.MyPlateItem

@Dao
interface MyPlateDao {

    @Query("SELECT * FROM my_plate_item")
    suspend fun getAllMyPlateItems(): List<MyPlateItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyPlateItem(myPlateItem: MyPlateItem)

    @Delete
    suspend fun deleteMyPlateItem(myPlateItem: MyPlateItem)

    @Update
    suspend fun updateMyPlateItem(myPlateItem: MyPlateItem)

    @Query("SELECT * FROM my_plate_item WHERE categoryName = :categoryName")
    suspend fun findItemByCategoryName(categoryName: String): MyPlateItem?

}