package com.example.myapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ShowRentsActivity extends AppCompatActivity {


    private ListView rentList;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor rentCursor;
    private SimpleCursorAdapter rentAdapter;
    private CalendarView calendarView;
    private EditText editTextDate;
    private Button buttonMinusDay, buttonPlusDay, buttonAddRent;
    private Spinner spinnerRentsShow;
    private long idRent;
    private Calendar calendar = Calendar.getInstance();// выбранный день
    private Calendar beginDayCalendar = Calendar.getInstance();//начало дня
    private Calendar endDayCalendar = Calendar.getInstance();  // конец дня
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private String strSglSelectCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_rents);
        // выводим текущую дату
        editTextDate = (EditText) findViewById(R.id.editTextDate);

        // устанавлииваем время начала и конца дня для поиска в базе проживаний за данный периуд
        editTextDate.setText(dateFormat.format(calendar.getTime()));

        setBeginEndTimeInDay(calendar); // определяем время начала и конца дня
        // находим календарь
        calendarView = (CalendarView)findViewById(R.id.calendarView);
        calendarView.setDate(calendar.getTimeInMillis());

        // обработка выбора даты
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                beginDayCalendar.set(Calendar.YEAR,year);
                beginDayCalendar.set(Calendar.MONTH,month);
                beginDayCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                endDayCalendar.set(Calendar.YEAR,year);
                endDayCalendar.set(Calendar.MONTH,month);
                endDayCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                editTextDate.setText(dateFormat.format(calendar.getTime()));
                onResume();
            }
        });


        // Прибавление и убавление даты
        buttonMinusDay = (Button) findViewById(R.id.buttonMinusDay);
        buttonPlusDay = (Button) findViewById(R.id.buttonPlusDay);

        // создаем обработчик нажатия на добавление или прибавление дня
        View.OnClickListener oclBtnEditDate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.buttonMinusDay:
                        // кнопка минус день
                        calendar.add(Calendar.DATE, -1);
                        beginDayCalendar.add(Calendar.DATE, -1);
                        endDayCalendar.add(Calendar.DATE, -1);
                        break;
                    case R.id.buttonPlusDay:
                        // кнопка плюс день
                        calendar.add(Calendar.DATE, 1);
                        beginDayCalendar.add(Calendar.DATE, 1);
                        endDayCalendar.add(Calendar.DATE, 1);
                        break;
                }
                editTextDate.setText(dateFormat.format(calendar.getTime()));
                onResume();
            }
        };
        // присваиваем кнопкам обработчик
        buttonMinusDay.setOnClickListener(oclBtnEditDate);
        buttonPlusDay.setOnClickListener(oclBtnEditDate);
        //-------------------------------
        // условие для вывода на экран
        spinnerRentsShow = (Spinner) findViewById(R.id.spinnerRentsShow);

        spinnerRentsShow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onResume();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//---------------------------
        // обработка нажатия кнопки добавление проживания

        FloatingActionButton fab = findViewById(R.id.fabAddRent);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditRentActivity( 0, calendar);
            }
        });



        View.OnClickListener oclBtnAddRent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // вызываем форму редактирования проживания

                ShowEditRentActivity( 0, calendar);
            }
        };

        buttonAddRent = (Button) findViewById(R.id.buttonAddRent);
        buttonAddRent.setOnClickListener(oclBtnAddRent);



        //-------------------------------------------------

        // Обрабатываем нажатие элемента списка редактирование проживания
        rentList = (ListView)findViewById(R.id.list);
        rentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // вызываем форму редактирования проживания
                // idRent = (long)id;

                idRent = rentCursor.getLong(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_ID));
                // Log.d("myLogs", "---Передана запись с  _Id = " + idRent );
                // Log.d("myLogs", "---Передана время  " + calendar.getTime() );
                ShowEditRentActivity( idRent, calendar);
                //Toast.makeText(getBaseContext(),"выбран из списка  " + id , Toast.LENGTH_LONG).show();
            }
        });


        databaseHelper = new DatabaseHelper(getApplicationContext());

    }

    @Override
    public void onResume() {
        super.onResume();

        // устанавлииваем время начала и конца дня для поиска в базе проживаний за данный периуд
        editTextDate.setText(dateFormat.format(calendar.getTime()));
        calendarView.setDate(calendar.getTimeInMillis());
        //устанавливаем значение начала и конца дня
        setBeginEndTimeInDay(calendar);
        //-----------------------------
        // значение начала дня
    /*    beginDayCalendar.set(Calendar.HOUR, 00);
        beginDayCalendar.set(Calendar.MINUTE, 00);
        beginDayCalendar.set(Calendar.SECOND, 00);
        beginDayCalendar.set(Calendar.MILLISECOND, 000);
        // значение конца дня
        endDayCalendar.set(Calendar.HOUR, 23);
        endDayCalendar.set(Calendar.MINUTE, 59);
        endDayCalendar.set(Calendar.SECOND, 59);
        endDayCalendar.set(Calendar.MILLISECOND, 999);
*/

        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора
        String strinSQL = "SELECT *, r._id as _id FROM "+ DatabaseHelper.TABLE_Rents + " as r"
                +" INNER JOIN " + DatabaseHelper.TABLE_Flats + " as f"
                +" ON r.idFlat = f._id "
                +" INNER JOIN " + DatabaseHelper.TABLE_Guests + " as g "
                +" ON r.idGuest = g._id "
                +" where " + setStrSglSelectCondition(spinnerRentsShow.getSelectedItemPosition())
                + " Order by "+ DatabaseHelper.COLUMN_FLATS_NameFlat +" ;";

        //  +"where + r.dtIn >= "+ beginDayCalendar.getTimeInMillis()
        //    +"  and r.dtIn <= "+endDayCalendar.getTimeInMillis();
        //   +"WHERE "+calendar.getTimeInMillis() +" BETWEEN r.dtIn AND r.dtOut;";

        //  Log.d("myLogs", " ---"+strinSQL);
        //  Log.d("myLogs", " ---"+beginDayCalendar.getTimeInMillis()+" -----  " +endDayCalendar.getTimeInMillis());

        rentCursor =  db.rawQuery(strinSQL, null);

        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {"f."+DatabaseHelper.COLUMN_FLATS_NameFlat,
                DatabaseHelper.COLUMN_GUEST_Fio,DatabaseHelper.COLUMN_GUEST_Phone,
                DatabaseHelper.COLUMN_RENTS_DtIn, DatabaseHelper.COLUMN_RENTS_DtOut, DatabaseHelper.COLUMN_RENTS_Reserv  };
        // создаем адаптер, передаем в него курсор

        rentAdapter = new SimpleCursorAdapter(this, R.layout.list_item_rent,
                rentCursor, headers, new int[]{R.id.textViewRentName,
                R.id.textViewGuestFIO, R.id.textViewGuestPhone,
                R.id.textViewTimeIn,R.id.textViewTimeOut,R.id.textViewReserv  }, 0);
        // указываем адаптеру свой диндер
        rentAdapter.setViewBinder(new MyViewBinder());
        rentList.setAdapter(rentAdapter);

        // если известен idRent т.е. было редактирование или добавление ио переходим к этой записи
        //if (idRent > 0) {
        //   rentList.setSelector((int)idRent);
        //    rentList.setSelection(1);
        //}

    }


    // вызываем форму редактирования проживания
    public void ShowEditRentActivity(long id, Calendar cal){

        // вызываем форму редактирования проживания
        Intent intent = new Intent(getApplicationContext(), EditRentActivity.class);
        intent.putExtra("idRent", id);
        intent.putExtra("calendar", cal.getTimeInMillis());
        //idRent = rentCursor.getLong(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_ID));

        //  Log.d("myLogs", "---Передана запись с  _Id = " + id );
        //  Log.d("myLogs", "---Передана время  " + cal.getTime() );

        startActivityForResult(intent,1);
        // startActivity(intent);


    }
    // получение данных гостя из списка гостей возвращаемая ShowGuestActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode == RESULT_OK){
            idRent = data.getLongExtra("idR",0);
            // меняем дату окна на дату новой записи или отредактированной записи
            calendar.setTimeInMillis(data.getLongExtra("calendar",calendar.getTimeInMillis()));
            //устанавливаем начало и конец дня
            setBeginEndTimeInDay(calendar);
            //if (idRent > 0) {
            // получаем элемент по id из бд
            // rentCursor.moveToPosition((int)idRent);
            //}
        }

    }

    // устанавливаем время начала и конца дня у даты
    protected void setBeginEndTimeInDay(Calendar cal){
        // значение начала дня
        beginDayCalendar.setTimeInMillis(cal.getTimeInMillis());
        beginDayCalendar.set(Calendar.HOUR, 00);
        beginDayCalendar.set(Calendar.MINUTE, 00);
        beginDayCalendar.set(Calendar.SECOND, 00);
        beginDayCalendar.set(Calendar.MILLISECOND, 000);
        // значение конца дня
        endDayCalendar.setTimeInMillis(cal.getTimeInMillis());
        endDayCalendar.set(Calendar.HOUR, 23);
        endDayCalendar.set(Calendar.MINUTE, 59);
        endDayCalendar.set(Calendar.SECOND, 59);
        endDayCalendar.set(Calendar.MILLISECOND, 999);
    }

    // устанавливаем условие запроса повыбранному элементу в Spinner
    protected String setStrSglSelectCondition (int SelectCondition){
        switch (SelectCondition){
            case 0 : // заезжает- проживает- выезжает
                strSglSelectCondition = "( r.dtIn <= "+ endDayCalendar.getTimeInMillis()
                        +"  and r.dtOut >= "+beginDayCalendar.getTimeInMillis() + " ) " ;
                break;
            case 1 : //заезжает - проживает
                strSglSelectCondition = "( r.dtIn >= "+ beginDayCalendar.getTimeInMillis()
                        +"  and r.dtIn <= "+endDayCalendar.getTimeInMillis() + " ) or"
                        +"( r.dtIn < "+ beginDayCalendar.getTimeInMillis()
                        +"  and r.dtOut > "+endDayCalendar.getTimeInMillis() + " ) ";
                break;
            case 2 : //заезжает
                strSglSelectCondition = "( r.dtIn >= "+ beginDayCalendar.getTimeInMillis()
                        +"  and r.dtIn <= "+endDayCalendar.getTimeInMillis() + " )";
                break;
            case 3 : //проживает
                strSglSelectCondition = "( r.dtIn < "+ beginDayCalendar.getTimeInMillis()
                        +"  and r.dtOut > "+endDayCalendar.getTimeInMillis() + " )";
                break;
            case 4 : //выезжает
                strSglSelectCondition = "( r.dtOut >= "+ beginDayCalendar.getTimeInMillis()
                        +"  and r.dtOut <= "+endDayCalendar.getTimeInMillis() +" )";
                break;

        }

        return strSglSelectCondition;
    }


}
