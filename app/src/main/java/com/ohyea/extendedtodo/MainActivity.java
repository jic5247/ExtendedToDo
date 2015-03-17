package com.ohyea.extendedtodo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private ArrayList<Object> parentItems;
    public int lastExpandedPosition = -1;
    private Toast toast ;
    private int priority = 0;
    public mySQLiteHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // this is not really  necessary as ExpandableListActivity contains an ExpandableList
        setContentView(R.layout.activity_main);

        final dadExpandableListView expandableList = (dadExpandableListView) findViewById(R.id.exlvlo);
        expandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expandableList.collapseGroup(lastExpandedPosition);
                    expandableList.smoothScrollToPosition(groupPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

        //setGroupParents();

        dbHelper = new mySQLiteHelper(this.getBaseContext());
        //dbHelper.delete_all();
        dbHelper.max_index = dbHelper.numberOfRows() - 1;
        //parentItems = dbHelper.getAllItems();
        final MyExpandableAdapter adapter = new MyExpandableAdapter(expandableList);//, parentItems);
        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        adapter.setSQLiteHelper(dbHelper);
        expandableList.setAdapter(adapter);
        //expandableList.setParents(parentItems);
        expandableList.setmyAdapter(adapter);
        expandableList.setActivity(this);
        expandableList.setDBhelper(dbHelper);

        final TextView ptvItem = (TextView) findViewById(R.id.ptvItem);
        final SeekBar skbr = (SeekBar) findViewById(R.id.skBr);
        final Button button = (Button) findViewById(R.id.addbtn);
        final EditText et = (EditText) findViewById(R.id.etItem);
        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        final View vgbottom = (View) findViewById(R.id.llo);
/*
        final Dialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            int iii = 0;
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                GregorianCalendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth, 0, 0);
                String dayOfWeek = adapter.simpledateformat.format(cal.getTime());
                String pty = "";
                if(et.getText().toString().equals("")){
                    vgbottom.setVisibility(View.INVISIBLE);
                    et.setVisibility(View.VISIBLE);
                    button.setText("Add");
                    button.setBackgroundResource(R.drawable.buttonitemshape);
                    et.setText("");
                }
                if(priority != 0) {
                    switch (priority / 25) {
                        case 0: pty = "Lo"; break;
                        case 1: pty = "M-"; break;
                        case 2: pty = "M+"; break;
                        case 3:
                        case 4: pty = "Hi"; break;
                    }
                }
                final ArrayList<String> item = new ArrayList();
                item.add(et.getText().toString()+"_"+(++iii)); //0
                item.add(pty);                     //1
                item.add(Integer.toString(year));  //2
                item.add(Integer.toString(monthOfYear + 1)); //3
                item.add(Integer.toString(dayOfMonth)); //4
                item.add(dayOfWeek);               //5
                dbHelper.insertItem(item);
                vgbottom.setVisibility(View.INVISIBLE);
                et.setVisibility(View.VISIBLE);
                button.setText("Add");
                button.setBackgroundResource(R.drawable.buttonitemshape);
                et.setText("");
                expandableList.collapseGroup(lastExpandedPosition);
                adapter.notifyDataSetChanged();
            }
        }, adapter.calendar.get(Calendar.YEAR), adapter.calendar.get(Calendar.MONTH), adapter.calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setTitle("Pick Due Date");
*/
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(button.getText().toString().equals("Add")) {
                    if (et.getText().toString().equals("")) {
                        return;
                    }
                    button.setBackgroundResource(R.drawable.defaultspinnershape);
                    button.setText("Non");
                    priority = 0;
                    skbr.setProgress(0);
                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                    et.setVisibility(View.INVISIBLE);
                    vgbottom.setVisibility(View.VISIBLE);
                }else{
                    String pty = "";
                    if(et.getText().toString().equals("")){
                        vgbottom.setVisibility(View.INVISIBLE);
                        et.setVisibility(View.VISIBLE);
                        button.setText("Add");
                        button.setBackgroundResource(R.drawable.buttonitemshape);
                        et.setText("");
                    }
                    if(priority != 0) {
                        switch (priority / 25) {
                            case 0: pty = "Lo"; break;
                            case 1: pty = "M-"; break;
                            case 2: pty = "M+"; break;
                            case 3:
                            case 4: pty = "Hi"; break;
                        }
                    }
                    final ArrayList<String> item = new ArrayList();
                    item.add(et.getText().toString()); //0
                    item.add(pty);                     //1
                    item.add("");  //2
                    item.add(""); //3
                    item.add(""); //4
                    item.add("");               //5
                    dbHelper.insertItem(item);
                    vgbottom.setVisibility(View.INVISIBLE);
                    et.setVisibility(View.VISIBLE);
                    button.setText("Add");
                    button.setBackgroundResource(R.drawable.buttonitemshape);
                    et.setText("");
                    expandableList.collapseGroup(lastExpandedPosition);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        skbr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                priority = progress;
                if(progress == 0) {
                    button.setBackgroundResource(R.drawable.defaultspinnershape);
                    button.setText("Non");
                }
                else switch (priority/25){
                    case 0: button.setBackgroundResource(R.drawable.greenspinnershape);
                        button.setText("Lo");
                        break;
                    case 1: button.setBackgroundResource(R.drawable.yellowspinnershape);
                        button.setText("M-");
                        break;
                    case 2: button.setBackgroundResource(R.drawable.orangespinnershape);
                        button.setText("M+");
                        break;
                    case 3:
                    case 4: button.setBackgroundResource(R.drawable.redspinnershape);
                        button.setText("Hi");
                        break;
                }
            }
        });


    }
/*
    public void setGroupParents() {
        ArrayList<String> parent = new ArrayList<>();
        parentItems.add(new ArrayList<>(Arrays.asList("Android", "Hi", "3", "15", "2015", "Sun")));
        parentItems.add(new ArrayList<>(Arrays.asList("Core Java", "Lo", "3", "16", "2015", "Mon")));
        parentItems.add(new ArrayList<>(Arrays.asList("Desktop Java", "M+", "4", "15", "2015", "Wed")));
        parentItems.add(new ArrayList<>(Arrays.asList("Enterprise Java", "Lo", "12", "30", "2025", "Sat")));
    }
*/
}
