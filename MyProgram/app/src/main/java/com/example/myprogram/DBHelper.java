package com.example.myprogram;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dbHomeComfort";
    private static final int SCHEMA = 1; // версия базы данных
    // названия столбцов таблицы FLAT (Квартира) - места размещения
    public static final String TABLE_Flat = "flat"; // название таблицы в бд
    public static final String COLUMN_FLAT_ID = "_id";
    public static final String COLUMN_FLAT_FlatName = "flatName";
    public static final String COLUMN_FLAT_Address = "address";
    public static final String COLUMN_FLAT_NoteFlat = "noteFlat";
    public static final String COLUMN_FLAT_IdCloudFlat = "idCloudFlat";
    public static final String COLUMN_FLAT_DtEditFlat = "dtEditFlat";


    final String LOG_TAG = "myLogs";// тэг для при выводе в лог файл
    SQLiteDatabase myDb;

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "dbHomeComfort", null, 1);




    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG, "--- onCreate database ---");
        // создаем таблицу с полями
      /*  db.execSQL(" CREATE TABLE rent ( "
                +"_id	INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"idFlat	INTEGER NOT NULL, "
                +"idQuest	INTEGER NOT NULL, "                 // код гостя
                +"DateTimeIn	NUMERIC NOT NULL, "             //дата заезда
                +"DateTimeOut	NUMERIC NOT NULL, "
                +"duration	INTEGER NOT NULL DEFAULT 2, "   // дата выезда
                +"reserv	NUMERIC NOT NULL DEFAULT 0, "   // бронь -1-забронировано 0-нет
                +"cost	REAL NOT NULL DEFAULT 0, "          // стоимость проживания
                +"noteRent	TEXT NOT NULL, "                // примечание
                +"ratingRent	INTEGER DEFAULT 0, "        // рейтинг выставленный жильцу
                +"idCloudRent	INTEGER NOT NULL DEFAULT 0, "   // код записи в облаке//
                +"dtEditRent	NUMERIC NOT NULL );"        // дата последнего редактирования
        );
        Log.d(LOG_TAG, "--- Создали таблицу rent ---");
       */
        db.execSQL("CREATE TABLE flat (" // места размещения
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"flatName TEXT NOT NULL, "     // название места размещения
                +"address TEXT, "               // полный адресс
                +"noteFlat TEXT, "           // примечание
                +"idCloudFlat INTEGER NOT NULL DEFAULT 0, " // код записи в облаке//
                +"dtEditFlat INTEGER NOT NULL DEFAULT 0);" // дата последнего редактирования

        );
        Log.d(LOG_TAG, "--- Создали таблицу flat ---");

        ContentValues cv = new ContentValues();
        for (int i = 1; i < 5; i++) {
            cv.put(COLUMN_FLAT_ID,  i);
            cv.put(COLUMN_FLAT_FlatName, "FLAT_FlatName " + i);
            cv.put(COLUMN_FLAT_Address, "FLAT_Address " + i);
            cv.put(COLUMN_FLAT_NoteFlat, "NoteFlat " + i);
            cv.put(COLUMN_FLAT_IdCloudFlat,  i);
            cv.put(COLUMN_FLAT_DtEditFlat,  i);

            if (db.insert(TABLE_Flat, null, cv)> 0){
                Log.d("myLogs", "--- Добавили запись в flat ---" + i);
            }
            else
                Log.d("myLogs", "--- Запись в flat не добавлена ---" + i);
        }



/*

        db.execSQL( "CREATE TABLE guest ( "  // Гости
                +"_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + "fio	TEXT NOT NULL, "        // ФИО
                +"phone	INTEGER NOT NULL, "     // Номер телефона
                +"raitingGuest	INTEGER NOT NULL DEFAULT 0, " // рейтинг  жильца
                +"noteGuest	TEXT, "                            // примечание
                +"idCloudGuest	INTEGER NOT NULL DEFAULT 0, "   // код записи в облаке//
                +"dtEditGuest	INTEGER);"                      // дата последнего редактирования
        );
        Log.d(LOG_TAG, "--- Создали таблицу guest ---");
*/
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Если таблица существует удаляем таблицу и создаем снова
        db.execSQL("DROP TABLE "+TABLE_Flat);
        onCreate(db);

    }



}
