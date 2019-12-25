package com.example.myprogram;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;

public class FlatsShowActivity extends AppCompatActivity  {

    DBHelper dbHelper;
    ListView userList ;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    ListView lvData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flats_show);

        userList = (ListView) findViewById(R.id.listViewFlats);
        // создаем подключение подключение к базе

        dbHelper = new DBHelper(this);
      //  dbHelper.onCreate();

        db = dbHelper.getWritableDatabase();




/*
        ContentValues cv = new ContentValues();
        for (int i = 1; i < 5; i++) {
            cv.put(DBHelper.COLUMN_FLAT_FlatName, "sometext " + i);
            cv.put(DBHelper.COLUMN_FLAT_Address, " sometext address" + i);

            if (db.insert(DBHelper.TABLE_Flat, null, cv)> 0){
                Log.d("myLogs", "--- Добавили запись в flat ---" + i);
            }
            else
                Log.d("myLogs", "--- Запись в flat не добавлена ---" + i);
        }

*/

    }

    @Override
    public void onResume(){
        super.onResume();
        // открываем подключение к БД
        db = dbHelper.getWritableDatabase();
        userCursor = db.query("flat", null, null,null, null, null, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        Log.d("myLogs", "--- В таблице Flat записей ---"+userCursor.getCount());;
        String[] from = new String[] {DBHelper.COLUMN_FLAT_ID,DBHelper.COLUMN_FLAT_FlatName, DBHelper.COLUMN_FLAT_Address };
        int[] to = new int[] {R.id.textViewID,R.id.textViewFlatName,R.id.textViewAddress};
        // создаем адаптер, передаем в него курсор
        userAdapter = new SimpleCursorAdapter(this,R.layout.item_flat, null, from, to,0);
        lvData = (ListView) findViewById(R.id.listViewFlats);
        lvData.setAdapter(userAdapter);




    }





    @Override // создаем меню
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        menu.add("Добавит");
        menu.add("Редактировать");
        menu.add("Удалить");
        menu.add("Выбрать");
        return super.onCreateOptionsMenu(menu);
    }

    @Override // обрабатываем выбор меню
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
        dbHelper.close();
    }




}
