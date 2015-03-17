package com.ohyea.extendedtodo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    //public ArrayList<Object> parentItems;
    private ArrayList<String> child;
    private dadExpandableListView lv;

    private DatePicker datePicker;
    final public Calendar calendar = Calendar.getInstance();
    private String year, month, day;
    final SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE");

    int parent_index = -1;
    int child_index = -1;
    View component_view = null;

    public mySQLiteHelper dbHelper;

    public MyExpandableAdapter(dadExpandableListView lv){//, ArrayList parentItems) {
        //this.parentItems = parentItems;
        child = new ArrayList<String>();
        child.add("Modify");
        child.add("Delete");
        this.lv = lv;
        this.year = Integer.toString(calendar.get(Calendar.YEAR));
        this.month = Integer.toString(calendar.get(Calendar.MONTH)+1);
        this.day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setInflater(LayoutInflater inflater, Activity activity) {
        this.inflater = inflater;
        this.activity = activity;
    }

    public void setSQLiteHelper(mySQLiteHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.group, null);
        }

        Button button = (Button) convertView.findViewById(R.id.myButton);
        final EditText edittext = (EditText) convertView.findViewById(R.id.etItem);
        edittext.setText(dbHelper.getContent(groupPosition));
        edittext.setFocusable(true);
        edittext.setSelected(true);
        edittext.setActivated(true);
        edittext.setVisibility(View.VISIBLE);
        edittext.setCursorVisible(true);
        if(child.get(childPosition).equals("Delete")){
            edittext.setVisibility(View.GONE);
            button.setBackgroundResource(R.drawable.redbuttonshape);
        }
        else{
            button.setBackgroundResource(R.drawable.buttonitemshape);
        }
        button.setText(child.get(childPosition));
        button.setFocusable(false);
        button.setTag(groupPosition);
        button.setOnClickListener(childListener);
        return convertView;
    }

    OnClickListener childListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            final int groupPosition = (Integer)((Button) view).getTag();
            if(((Button) view).getText().toString().equals("Modify")){
                final EditText et = (EditText)(view.getRootView()).findViewById(R.id.etItem);
                CheckedTextView ctv = (CheckedTextView)(lv.getChildAt(groupPosition)).findViewById(R.id.textView1);
                if(!et.getText().toString().equals("")) {
                    //((ArrayList<String>) parentItems.get(groupPosition)).set( 0, et.getText().toString());
                    dbHelper.updateContent(groupPosition, et.getText().toString());
                    ctv.setText(et.getText().toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dbHelper.updateContent(groupPosition, et.getText().toString());
                        }
                    }).run();
                    et.setText("");
                    lv.collapseGroup(groupPosition);
                }else{
                    Toast.makeText(activity, "No Empty Item", Toast.LENGTH_SHORT).show();
                }
            }
            else if(((Button) view).getText().toString().equals("Delete")){
                lv.collapseGroup(groupPosition);
                dbHelper.deleteItem(groupPosition);
                MyExpandableAdapter.this.notifyDataSetChanged();
            }
        }
    };
/*
    @Override
    public void onGroupExpanded(int groupPosition){
        //collapse the old expanded group, if not the same
        //as new group to expand
        if(groupPosition != lastExpandedGroupPosition){
            listView.collapseGroup(lastExpandedGroupPosition);
        }

        super.onGroupExpanded(groupPosition);
        lastExpandedGroupPosition = groupPosition;
    }
*/

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row, null);
        }
        final Cursor cursor = dbHelper.getData(groupPosition);
        //final ArrayList<String> ppt = (ArrayList<String>)parentItems.get(groupPosition);
        final Spinner spr = (Spinner) convertView.findViewById(R.id.pspiner);
        CheckedTextView ctvItem = (CheckedTextView) convertView.findViewById(R.id.textView1);
        //ctvItem.setText(ppt.get(0)+"____"+ppt.get(6));
        ctvItem.setText((Integer.parseInt(cursor.getString(cursor.getColumnIndex("idx")))+1)+". "+cursor.getString(cursor.getColumnIndex("content")));
        ctvItem.setChecked(isExpanded);

        ArrayAdapter<CharSequence> arrayadapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.p_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spr.setAdapter(arrayadapter);
        spr.setFocusable(false);
        String prty = cursor.getString(cursor.getColumnIndex("prty"));
        spr.setSelection(arrayadapter.getPosition(prty));
        spinnerColorCode(spr, prty);
        spr.setTag(groupPosition);
        spr.setOnItemSelectedListener(sprItenListener);
        /*spr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String item = parent.getItemAtPosition(pos).toString();
                ppt.set(1, item);
                spinnerColorCode(spr, item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });*/

        spr.setOnTouchListener(clickingListener);

        final Button datebtn = (Button) convertView.findViewById(R.id.datebtn);
        //ArrayList<String> parentItem = (ArrayList<String>) parentItems.get(groupPosition);
        String item_month = cursor.getString(cursor.getColumnIndex("month"));
        String item_day = cursor.getString(cursor.getColumnIndex("DoM"));
        String item_year = cursor.getString(cursor.getColumnIndex("year"));
        if(item_month.equals("")){
            datebtn.setText("Due\nDate");
        }
        else if((item_month+item_day+item_year).equals(month+day+year))
            datebtn.setText("Today");
        else
            datebtn.setText(item_month+"/"+item_day+"/"+Integer.parseInt(item_year)%1000+"\n"+cursor.getString(cursor.getColumnIndex("DoW")));
        datebtn.setFocusable(false);
        datebtn.setTag(groupPosition);
        datebtn.setOnClickListener(dateBtnListener);

        return convertView;
    }

    AdapterView.OnItemSelectedListener sprItenListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if(component_view != null) {
                final String item = parent.getItemAtPosition(pos).toString();
                //((ArrayList<String>) parentItems.get(parent_index)).set(1, item);
                spinnerColorCode((Spinner) component_view, item);
                dbHelper.updatePriority(parent_index, item);
                component_view = null;
                parent_index = -1;
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    Spinner.OnTouchListener clickingListener = new Spinner.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            component_view = v;
            parent_index = (Integer) v.getTag();
            return false;
        }
    };

    private OnClickListener dateBtnListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            //final ArrayList<String> parentItem = (ArrayList<String>) parentItems.get((Integer)v.getTag());
            ArrayList<String> item_date = dbHelper.getDateNum((Integer)v.getTag());
            int  set_year, set_month, set_day;
            if(!item_date.get(0).equals("")){
                set_year =  Integer.parseInt(item_date.get(0));
                set_month =  Integer.parseInt(item_date.get(1)) - 1;
                set_day =  Integer.parseInt(item_date.get(2));
            }else{
                set_year =  calendar.get(Calendar.YEAR);
                set_month =  calendar.get(Calendar.MONTH);
                set_day =  calendar.get(Calendar.DAY_OF_MONTH);
            }
            final Dialog dialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int oyear, int monthOfYear, int dayOfMonth) {
                    GregorianCalendar cal = new GregorianCalendar(oyear, monthOfYear, dayOfMonth, 0, 0);
                    final String item_dow = simpledateformat.format(cal.getTime());
                    final String item_month = Integer.toString(monthOfYear + 1);
                    final String item_day = Integer.toString(dayOfMonth);
                    final String item_year = Integer.toString(oyear);
                    if((item_month+item_day+item_year).equals(month+day+year))
                        ((Button) v).setText("Today");
                    else
                        ((Button) v).setText(item_month + "/" + item_day + "/" + Integer.parseInt(item_year) % 1000 + "\n" + item_dow);
                    dbHelper.updateDate((Integer)v.getTag(), item_year,item_month, item_day, item_dow);
                }
            }, set_year, set_month, set_day);
            dialog.setTitle("Pick Due Date");
            dialog.show();
        }
    };

    public void spinnerColorCode(Spinner spr, String code){
        if(code.equals("Hi")){spr.setBackgroundResource(R.drawable.redspinnershape);}
        else if(code.equals("M+")){spr.setBackgroundResource(R.drawable.orangespinnershape);}
        else if(code.equals("M-")){spr.setBackgroundResource(R.drawable.yellowspinnershape);}
        else if(code.equals("Lo")){spr.setBackgroundResource(R.drawable.greenspinnershape);}
        else {spr.setBackgroundResource(R.drawable.defaultspinnershape);}
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return dbHelper.numberOfRows();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}