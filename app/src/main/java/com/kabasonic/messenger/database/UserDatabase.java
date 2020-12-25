package com.kabasonic.messenger.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kabasonic.messenger.models.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {

    public static final String TAG = "UserDatabase";

    private static UserDatabase instance;

    public abstract UserDao userDao();

    public static synchronized UserDatabase getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG,"UserDatabase instance NULL");
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    UserDatabase.class, "user_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
            Log.d(TAG,"UserDatabase create user_database");
        }
        Log.d(TAG,"return instance for User database");
        return instance;

    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d(TAG,"On create database");
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDao userDao;

        private PopulateDbAsyncTask(UserDatabase db) {
            userDao = db.userDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            noteDao.insert(new Note("Title 1", "Description 1", 1));
//            noteDao.insert(new Note("Title 2", "Description 2", 2));
//            noteDao.insert(new Note("Title 3", "Description 3", 3));
//            userDao.update();
            Log.d(TAG,"[START] Write record to database");
            User user = new User("aaa", "aaa");
            userDao.insert(user);
            userDao.insert(user);
            Log.d(TAG,"[END] Write record to database");
            ///TODO Update and write to database
            return null;
        }
    }

}
