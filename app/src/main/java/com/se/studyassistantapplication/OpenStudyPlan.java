package com.se.studyassistantapplication;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 학습 계획을 열람, 수정, 삭제, 상태변경하는 기능 수행
 */
public class OpenStudyPlan extends AppCompatActivity {
    // 열람할 학습 계획 객체
    public StudyPlan study_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_study_plan);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        study_plan = bundle.getParcelable("StudyPlan");

        showStudyPlan();
        
        // 상태 버튼
        Button btn_status = findViewById(R.id.statusBtn);
        btn_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStudyPlanStatus();
                updatePlanStatusDB(study_plan);
            }
        });
        
        // 수정 버튼
        Button btn_modify = findViewById(R.id.modifyBtn);
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickUpdateStudyPlan();
            }
        });
        
        // 삭제 버튼
        Button btn_delete = findViewById(R.id.deleteBtn);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteStudyPlanDB(study_plan);
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });
    }
    
    // UpdateStudyPlan 액티비티로부터 돌아오는 경우
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            setResult(RESULT_OK, new Intent());
            finish();
            // for test
            Toast.makeText(getApplicationContext(), "onAcitivyResult called", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Main 클래스의 selectStudyPlan 메소드 호출
     * 시 호출되며, selectStudyPlan 메소드에서 전
     * 달받은 StudyPlan 객체의 정보로 showStudy
     * PlanDB 메소드 호출을 통해 학습 계획을 조
     * 회하여 학습 계획 열람 기능을 수행한다.
     * -> 메서드 설명 변경해야함
     */
    public void showStudyPlan()
    {
        Cursor cursor = showStudyPlanDB(study_plan);
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");

        while(cursor.moveToNext())
        {
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            Date startDay = null;
            Date endDay = null;
            try{
                startDay = fm.parse(cursor.getString(3));
                endDay = fm.parse(cursor.getString(4));
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            boolean status = Boolean.parseBoolean(cursor.getString(5));
        }

        TextView tv_planTitle = findViewById(R.id.planTitle);
        TextView tv_planContent = findViewById(R.id.planContent);
        TextView tv_startDay = findViewById(R.id.startDay);
        TextView tv_endDay = findViewById(R.id.endDay);
        Button btn_status = findViewById(R.id.statusBtn);

        tv_planTitle.setText(study_plan.plan_title);
        tv_planContent.setText(study_plan.plan_content);
        tv_startDay.setText(fm.format(study_plan.plan_start_day));
        tv_endDay.setText(fm.format(study_plan.plan_end_day));
        btn_status.setText(study_plan.plan_status ? "TRUE" : "FALSE");
    }

    /**
     * 입력받은 StudyPlan 객체의 plan_id를 통하
     * 여 해당 변수의 정보로 데이터베이스에서 학
     * 습 계획을 조회한 후에 Cursor 형태로 결과
     * 를 반환한다.
     * @param studyPlan StudyPlan 객체
     * @return 데이터베이스에서 조회한 학습 계획
     */
    public Cursor showStudyPlanDB(StudyPlan studyPlan)
    {
        SQLiteDatabase database;
        database = openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);

        //SELECT * FROM study_plan_tb WHERE _id = id
        Cursor cursor = database.rawQuery("SELECT * FROM study_plan_tb WHERE _id = " + studyPlan.plan_id, null);
        return cursor;
    }

    /**
     * 해당 학습 계획이 완료 상태일 경우, 미완료
     * 상태로 변경하고 미완료 상태일 경우, 완료
     * 상태로 변경한다.
     */
    public void setStudyPlanStatus()
    {
        Button btn_status = findViewById(R.id.statusBtn);
        if(btn_status.getText().equals("FALSE")){
            btn_status.setText("TRUE");
            Toast.makeText(getApplicationContext(), "to true", Toast.LENGTH_SHORT).show();
        }else{
            btn_status.setText("FALSE");
            Toast.makeText(getApplicationContext(), "to false", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 학습 계획 수정 액티비티를 실행한다
     */
    public void clickUpdateStudyPlan()
    {
        Intent intent = new Intent(getApplicationContext(), UpdateStudyPlan.class);

        intent.putExtra("StudyPlan", study_plan);
        startActivityForResult(intent, MainActivity.REQUEST_CODE_UPDATESTUDYPLAN);
    }

    /**
     * 입력받은 StudyPlan 객체의 plan_id를 통하
     * 여 데이터베이스에서 학습 계획을 조회하여
     * 학습 계획 상태를 변경한다.
     * @param studyPlan 상태를 변경할 StudyPlan 객체
     */
    public void updatePlanStatusDB(StudyPlan studyPlan)
    {
        SQLiteDatabase database;
        database = openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);

        Button btn_status = findViewById(R.id.statusBtn);
        if(btn_status.getText().equals("FALSE")){
            //UPDATE study_plan_tb SET status='false' WHERE _id = id
            database.execSQL("UPDATE study_plan_tb SET status='false' WHERE _id = " + studyPlan.plan_id);
        }else{
            //UPDATE study_plan_tb SET status='true' WHERE _id = id
            database.execSQL("UPDATE study_plan_tb SET status='true' WHERE _id = " + studyPlan.plan_id);
        }
    }

    /**
     * 입력받은 StudyPlan 객체의 plan_id를 통하
     * 여 해당 학습 계획 정보를 데이터베이스에서
     * 삭제한다.
     * @param studyPlan 삭제할 StudyPlan 객체
     */
    public void deleteStudyPlanDB(StudyPlan studyPlan)
    {
        SQLiteDatabase database;
        database = openOrCreateDatabase(MainActivity.DB_NAME, MODE_PRIVATE, null);

        //DELETE FROM study_plan_tb WHERE _id = id
        database.execSQL("DELETE FROM study_plan_tb WHERE _id = " + studyPlan.plan_id);
    }
    
}