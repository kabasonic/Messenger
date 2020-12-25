package com.kabasonic.messenger.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kabasonic.messenger.models.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    //@Query("DELETE FROM note_table")
    //void deleteAllNotes();


    @Query("SELECT * FROM user_table ORDER BY firstName DESC")
    LiveData<List<User>> getAllUser();
}
