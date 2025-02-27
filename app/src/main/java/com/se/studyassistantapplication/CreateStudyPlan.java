package com.se.studyassistantapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateStudyPlan extends AppCompatActivity {
    // 생성할 학습 계획 객체
    public StudyPlan study_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_study_plan);

        TextView tv_startDay = findViewById(R.id.startDay);
        TextView tv_endDay = findViewById(R.id.endDay);

//        Calendar cal = Calendar.getInstance();
//        startDay.setText(cal.get(Calendar.YEAR) +"-"+ (cal.get(Calendar.MONTH)+1) +"-"+ cal.get(Calendar.DATE));

        //참고 코드 https://blog.naver.com/wizand02/221691801438
        //시작일
        tv_startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDatePick(view, startDateSetListener);
            }
        });

        //종료일
        tv_endDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onClickDatePick(view, endDateSetListener);
            }
        });

        //저장 버튼
        FloatingActionButton fab_save = findViewById(R.id.saveButton);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createStudyPlan();
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });
    }

    //시작일 클릭시 작동
    DatePickerDialog.OnDateSetListener startDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                    // Date Picker에서 선택한 날짜를 TexstartDayiew에 설정
                    TextView tv_startDay = findViewById(R.id.startDay);
                    tv_startDay.setText(String.format("%d-%d-%d", yy,mm+1,dd));
                }
            };

    //종료일 클릭시 작동
    DatePickerDialog.OnDateSetListener endDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                    // Date Picker에서 선택한 날짜를 TexstartDayiew에 설정
                    TextView tv_endDay = findViewById(R.id.endDay);
                    tv_endDay.setText(String.format("%d-%d-%d", yy,mm+1,dd));
                }
            };

    //DatePicker 작동
    public void onClickDatePick(View view, DatePickerDialog.OnDateSetListener listener){
        // DatePicker가 처음 떴을 때, 오늘 날짜가 보이도록 설정.
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show();
    }

    /**
     * 학습 계획 작성 화면에서 입력받는 학습 계획
     * 정보로 StudyPlan 클래스의 메소드를 통해 객
     * 체를 생성하고 데이터베이스에 저장하는 메소
     * 드를 호출하여 학습 계획 작성 기능을 수행한
     * 다.
     */
    public void createStudyPlan()
    {
        EditText et_title, et_content;
        TextView tv_startDay, tv_endDay;

        et_title = findViewById(R.id.planTitle);
        et_content = findViewById(R.id.planContent);
        tv_startDay = findViewById(R.id.startDay);
        tv_endDay = findViewById(R.id.endDay);

        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = null;
        Date endDay = null;
        try {
            startDay = fm.parse(tv_startDay.getText().toString());
            endDay = fm.parse(tv_endDay.getText().toString());
        }catch (ParseException e){
            e.printStackTrace();
            Log.e(this.getClass().getName(), "error");
        }
        
        //id는 DB에 삽입시 자동으로 처리되므로 생성자에 사용하지 않음
        study_plan = new StudyPlan(et_title.getText().toString()
                , et_content.getText().toString(), startDay, endDay, false);

        insertStudyPlanDB(study_plan);
        //for test
        Toast.makeText(getApplicationContext(), study_plan.toDBInsertString(), Toast.LENGTH_LONG).show();
    }

    /**
     * 입력받은 StudyPlan 객체의 plan_id를 입력으로
     * 하여 데이터베이스에 작성한 학습 계획 정보를
     * 저장한다.
     */
    public void insertStudyPlanDB(StudyPlan studyPlan)
    {
        SQLiteDatabase database;

        database = openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);
        //예외처리 필요

        database.execSQL(studyPlan.toDBInsertString());
        //for test
        Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
    }
}