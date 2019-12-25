package com.example.myapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ShowGuestActivity extends AppCompatActivity  {

    ListView guestList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor guestCursor;
    SimpleCursorAdapter guestAdapter;
    Button buttonAddGuesButton;
    long idGuest = 0;
    int intActionView = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_guest);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idGuest = extras.getLong("idGuest",0);
            intActionView = extras.getInt("intActionView",0);
        }


        guestList = (ListView)findViewById(R.id.list);

        guestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (intActionView == 1)  {  // "SELECT"
                    onClickSelectGuest(view);
                }
                else{
                    //------------- Редактирование гостя
                    Intent intent = new Intent(getApplicationContext(), EditGuestActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            }
        });




        databaseHelper = new DatabaseHelper(getApplicationContext());
    }
    @Override
    public void onResume() {
        super.onResume();
        // открываем подключение

        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора
        guestCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE_Guests +" order by "+DatabaseHelper.COLUMN_GUEST_Fio, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DatabaseHelper.COLUMN_GUEST_Fio, DatabaseHelper.COLUMN_GUEST_Phone};
        // создаем адаптер, передаем в него курсор
        guestAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                guestCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        guestList.setAdapter(guestAdapter);
        if (idGuest>0) {
            guestList.setSelection((int)idGuest);
        }
    }
    // по нажатию на кнопку запускаем EditGuestActivity для добавления гостя
    public void addGuest(View view){
        Intent intent = new Intent(this, EditGuestActivity.class);
        startActivity(intent);
    }
    // запоминаем выбранного гостя и закрываем окно
    public void onClickSelectGuest(View v) {

        Intent intent = new Intent();
        intent.putExtra("idGuest",guestCursor.getInt( guestCursor.getColumnIndex(DatabaseHelper.COLUMN_GUEST_ID)));
        setResult(RESULT_OK, intent);
        finish();
    }






    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        guestCursor.close();
    }






}