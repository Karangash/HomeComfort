package com.example.myapp1;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyViewBinder implements SimpleCursorAdapter.ViewBinder{

    //  int red = getResources().getColor(R.color.Red);
    //   int orange = getResources().getColor(R.color.Orange);
    //  int green = getResources().getColor(R.color.Green);


    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

        // SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YY HH:mm");

        // Log.d("myLogs", "поле = " + cursor.getColumnName(columnIndex).toString());
        switch (cursor.getColumnName(columnIndex)) {
            case DatabaseHelper.COLUMN_RENTS_DtIn:
                ((TextView) view).setText(dateFormat.format(cursor.getLong(columnIndex)));
                return true;
            case DatabaseHelper.COLUMN_RENTS_DtOut:
                ((TextView) view).setText(dateFormat.format(cursor.getLong(columnIndex)));
                return true;
            case DatabaseHelper.COLUMN_RENTS_Reserv:

                if (cursor.getInt(columnIndex)== 1) {
                    ((TextView) view).setText("БРОНЬ");
                }
                else{
                    ((TextView) view).setText("");
                }
                return true;

        }


//        switch (view.getId()) {
//            case R.id.textView5:
//                ((TextView) view).setText(dateFormat.format(cursor.getLong(columnIndex)));
//
//                return true;
//
//            case R.id.textView6:
//                ((TextView) view).setText(dateFormat.format(cursor.getLong(columnIndex)));
//
//                return true;
//        }


        return false;
    }
}
