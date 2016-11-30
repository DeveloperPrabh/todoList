package com.example.prabhmacbook.todolist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.prabhmacbook.todolist.db.TaskContract;
import com.example.prabhmacbook.todolist.db.TaskHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainAcitvity" ;
    private TaskHelper helper;

    private ListView mTaskListView;
    private ArrayAdapter<String> habitAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTaskListView = (ListView) findViewById(R.id.lstTasks);

        helper = new TaskHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, new String[] {TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COL_TASK_TITLE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            Log.d(TAG, "Habit: " + cursor.getString(index));

        }
        updateUI();

        cursor.close();
        db.close();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_task:

                final EditText taskEditTask = new EditText(this);

                AlertDialog ad = new AlertDialog.Builder(this)
                        .setTitle("New Habit")
                        .setMessage("Add a new Habit")
                        .setView(taskEditTask)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditTask.getText());
                                SQLiteDatabase db = helper.getWritableDatabase();
                                ContentValues cv = new ContentValues();
                                cv.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE, null,
                                        cv, SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUI();


                            }


                        })

                        .setNegativeButton("Cancel", null)
                        .create();
                ad.show();
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI () {
        ArrayList<String> habitList = new ArrayList<>();
        helper = new TaskHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, new String[] {TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COL_TASK_TITLE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            habitList.add(cursor.getString(index));

        }

        if (habitAdapter == null) {
            habitAdapter = new ArrayAdapter<>(this, R.layout.item_todo, R.id.taskTitle, habitList);

            mTaskListView.setAdapter(habitAdapter);
        }

        else {
            habitAdapter.clear();
            habitAdapter.addAll(habitList);
            habitAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    //Deletes the habit

    public void deleteTask(View view) {

        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.taskTitle);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE, TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[] {task}
        );
        db.close();
        updateUI();

    }
}

