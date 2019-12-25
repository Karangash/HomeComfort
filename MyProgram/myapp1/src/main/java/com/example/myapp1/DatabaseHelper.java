package com.example.myapp1;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbHomeComfort.db"; // название бд
    private static final int SCHEMA = 14; // версия базы данных
  //  static final String TABLE = "users"; // название таблицы в бд
    // названия столбцов
//    public static final String COLUMN_ID = "_id";
//    public static final String COLUMN_NAME = "name";
//    public static final String COLUMN_YEAR = "year";

    // названия столбцов таблицы FLATS (Квартира) - места размещения
    public static final String TABLE_Flats              = "flats"       ; // название таблицы в бд
    public static final String COLUMN_FLATS_ID          = "_id"         ;
    public static final String COLUMN_FLATS_NameFlat    = "nameFlat"    ;// Присвоенное имя месту проживания
    public static final String COLUMN_FLATS_Address     = "address"     ;// Адрес места проживания
    public static final String COLUMN_FLATS_CostFlat    = "costFlat"     ;// Адрес места проживания
    public static final String COLUMN_FLATS_NoteFlat    = "noteFlat"    ;// Примечание
    public static final String COLUMN_FLATS_DtEditFlat  = "dtEditFlat"  ;// Дата и время редактирования записи
    public static final String COLUMN_FLATS_IdCloudFlat = "idCloudFlat" ;// Признак добавления записи в облако

    //Таблица Guests (гости) и его поля
    public static final String TABLE_Guests             = "guests"       ; // название таблицы в бд
    public static final String COLUMN_GUEST_ID          = "_id"         ;
    public static final String COLUMN_GUEST_Fio         = "fio"    ;// Присвоенное имя месту проживания
    public static final String COLUMN_GUEST_Phone       = "phone"     ;// Адрес места проживания
    public static final String COLUMN_GUEST_RaitingGuest= "raitingGuest"     ;// Адрес места проживания
    public static final String COLUMN_GUEST_NoteGuest    = "noteGuest"    ;// Примечание
    public static final String COLUMN_GUEST_DtEditGuest  = "dtEditGuest"  ;// Дата и время редактирования записи
    public static final String COLUMN_GUEST_IdCloudGuest = "idCloudGuest" ;// Признак добавления записи в облако




    // Таблица RENTS (Аренда) и его поля
    public static final String TABLE_Rents              = "rents"       ;
    public static final String COLUMN_RENTS_ID          = "_id"         ;
    public static final String COLUMN_RENTS_IdFlat      =  "idFlat"     ;// код места проживания
    public static final String COLUMN_RENTS_IdGuest     = "idGuest"     ;// код гостя
    public static final String COLUMN_RENTS_DtIn        =  "dtIn"       ;// Дата и время заезда
    public static final String COLUMN_RENTS_DtOut       =  "dtOut"      ;// Дата и время выезда
    public static final String COLUMN_RENTS_Duration    = "duration"    ;// Длительность аренды 2-5,24часа
    public static final String COLUMN_RENTS_Reserv      = "reserv"      ;// Бронь
    public static final String COLUMN_RENTS_Cost        = "cost"        ;// Стоимость аренды за длительность
    public static final String COLUMN_RENTS_NoteRent    = "noteRent"    ;// Примечание
    public static final String COLUMN_RENTS_RatingRent  = "ratingRent"  ;// Присвоенный рейтинг проживанию
    public static final String COLUMN_RENTS_dtEditRent  = "dtEditRent"  ;// Дата и время редактирования записи
    public static final String COLUMN_RENTS_idCloudRent = "idCloudRent" ;// Признак добавления записи в облако








    final String LOG_TAG = "myLogs";// тэг для при выводе в лог файл


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        Calendar calendar = Calendar.getInstance();
        Log.d(LOG_TAG, "--- Создаем таблицу flats ---");

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        db.execSQL("CREATE TABLE " + TABLE_Flats +"("

                +COLUMN_FLATS_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_FLATS_NameFlat+" TEXT NOT NULL, "     // название места размещения
                +COLUMN_FLATS_Address+" TEXT, "               // полный адресс
                +COLUMN_FLATS_CostFlat+" Real NOT NULL DEFAULT 0, "
                +COLUMN_FLATS_NoteFlat+" TEXT, "           // примечание
                +COLUMN_FLATS_DtEditFlat+" INTEGER NOT NULL DEFAULT 0, " // дата последнего редактирования
                +COLUMN_FLATS_IdCloudFlat+" INTEGER NOT NULL DEFAULT 0 " // код записи в облаке//
                +");" );

        Log.d(LOG_TAG, "--- Создали таблицу flat ---");

//        // добавление начальных данных
//        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_NAME
//                + ", " + COLUMN_YEAR  + ") VALUES ('Том Смит', 1981);");

        // добавляем одну запись
        db.execSQL("INSERT INTO "+ TABLE_Flats +" ( "
             //   +COLUMN_FLATS_ID+", "
                +COLUMN_FLATS_NameFlat+", "
                +COLUMN_FLATS_Address+", "
                +COLUMN_FLATS_CostFlat+", "
                +COLUMN_FLATS_NoteFlat+", "
                +COLUMN_FLATS_IdCloudFlat+", "
                +COLUMN_FLATS_DtEditFlat+" "

                +") VALUES ("
                +"'Галеева 284', "
                +"'ул.Галеева, дом.23, кв.284', "
                +"1500, "
                +" 'Примечание', "
                + "0 ,"
                + new Date().getTime()              //Calendar.getInstance().getTime().toString()

                + ");");
        Log.d(LOG_TAG, "--- В таблицу flats добавили запись ---");

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        // создаем таблицу Guests
        db.execSQL("CREATE TABLE " + TABLE_Guests +" ( "
               +COLUMN_GUEST_ID              +" INTEGER PRIMARY KEY AUTOINCREMENT, "
               +COLUMN_GUEST_Fio             +" TEXT, "
               +COLUMN_GUEST_Phone           +" TEXT, "
               +COLUMN_GUEST_RaitingGuest    +" INTEGER NOT NULL DEFAULT 3,  "
               +COLUMN_GUEST_NoteGuest        +" TEXT, "
               +COLUMN_GUEST_DtEditGuest      +" INTEGER NOT NULL DEFAULT 0, "
               +COLUMN_GUEST_IdCloudGuest     +" INTEGER NOT NULL DEFAULT 0 "
               +");" );

        Log.d(LOG_TAG, "--- Создали таблицу Guests ---");

        // добавляем одну запись

        db.execSQL("INSERT INTO "+ TABLE_Guests +" ( "
                //   +COLUMN_FLATS_ID+", "
                +COLUMN_GUEST_Fio+", "
                +COLUMN_GUEST_Phone+", "
                +COLUMN_GUEST_RaitingGuest+", "
                +COLUMN_GUEST_NoteGuest+", "
                +COLUMN_GUEST_DtEditGuest+" "

                +") VALUES ("
                +"'Иванов', "
                +"'8917111111', "
                + "3 ,"
                +" 'Примечание', "
                + new Date().getTime() //+ Calendar.getInstance().getTime().toString()
                + " );");
        Log.d(LOG_TAG, "--- В таблицу Guest добавили запись ---");


  //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      // создаем таблицу аренды квартир

        db.execSQL("CREATE TABLE " + TABLE_Rents +"("
                +COLUMN_RENTS_ID            +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_RENTS_IdFlat        +" INTEGER NOT NULL, "
                +COLUMN_RENTS_IdGuest       +" INTEGER NOT NULL, "
                +COLUMN_RENTS_DtIn          +" INTEGER NOT NULL, "
                +COLUMN_RENTS_DtOut         +" INTEGER NOT NULL, "
                +COLUMN_RENTS_Duration      +" INTEGER NOT NULL DEFAULT 0, "
                +COLUMN_RENTS_Reserv        +" INTEGER , "
                +COLUMN_RENTS_Cost          +" REAL NOT NULL DEFAULT 0, "
                +COLUMN_RENTS_NoteRent      +" TEXT, "
                +COLUMN_RENTS_RatingRent    +" INTEGER NOT NULL DEFAULT 3 , "
                +COLUMN_RENTS_dtEditRent    +" INTEGER NOT NULL DEFAULT 0, "
                +COLUMN_RENTS_idCloudRent   +" INTEGER NOT NULL DEFAULT 0 "
                +");" );

        Log.d(LOG_TAG, "--- Создали таблицу Rents ---");

        // добавляем одну запись
      /*  db.execSQL("INSERT INTO "+ TABLE_Rents +" ( "
                +COLUMN_RENTS_IdFlat        +", "
                +COLUMN_RENTS_IdGuest       +", "
                +COLUMN_RENTS_DtIn          +", "
                +COLUMN_RENTS_DtOut         +", "
                +COLUMN_RENTS_Duration      +", "
                +COLUMN_RENTS_Reserv        +", "
                +COLUMN_RENTS_Cost          +", "
                +COLUMN_RENTS_NoteRent      +", "
                +COLUMN_RENTS_RatingRent    +", "
                +COLUMN_RENTS_dtEditRent    +" "

                +") VALUES ("
                +"1 , "
                +"1 , "
                + calendar.getTimeInMillis() +" , "
                + calendar.getTimeInMillis() +" , "
                +"3 ,"
                +"0 ,"
                +"1500 ,"
                +" 'Примечание', "
                +"3 ,"
                + calendar.getTimeInMillis()
                + " );");


        Log.d(LOG_TAG, "--- Добавили запись в таблицу Rents ---");

*/

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
       // db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_Flats);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_Guests);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_Rents);

        onCreate(db);
    }
}