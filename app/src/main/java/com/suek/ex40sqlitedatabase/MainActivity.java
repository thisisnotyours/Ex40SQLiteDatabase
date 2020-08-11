package com.suek.ex40sqlitedatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText etName;
    EditText etAge;
    EditText etEmail;

    String dbName= "test.db";   //file 이름을 변수로 정해놓으면 나중에 변경하기 쉽다
    String tableName= "member";  //table 이름

    SQLiteDatabase db;  //참조변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName= findViewById(R.id.et_name);
        etAge= findViewById(R.id.et_age);
        etEmail= findViewById(R.id.et_email);

        //dbName 으로 데이텁이스 파일 열기 또는 생성
        //문서를 열면 그 DB 를 제어하는 객체 SQLightDatabase 를 리턴해줌
        db= openOrCreateDatabase(dbName, MODE_PRIVATE, null );   //데이터베이스 관리는 무조건 private 으로!  //여기서 dbName 은 "test.db" //new 한거아님

        //db 객체를 이용해서 DBMS 시스템에 명령(SQL)을 줄 수 있음.
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(num integer primary key autoincrement, name text not null, age integer, email text)");  //데이터베이스 테이블 만들기
                                                                  //primary key 를 쓰면 중복된 num 이 들어가지 않음, autoincrement-> 1부터 자동증가 ++
                                                                  //not null -> 필수 저장값
    }




    public void clickInsert(View view) {

        String name= etName.getText().toString();    // name 이라는 String 변수
        int age= Integer.parseInt(etAge.getText().toString());
        String email= etEmail.getText().toString();

        //데이터를 삽입하는  SQL 명령실행
        db.execSQL("INSERT INTO " + tableName + "(name, age, email) VALUES('"+name+"', '"+age+"', '"+email+"')");  //values 값 안에 문자열은 작은따옴표' '안에 넣어줌,
                                                                                                                  //숫자는 없이(써도 에러는 안남)
        etName.setText("");
        etAge.setText("");
        etEmail.setText("");
    }



    public void clickSelectAll(View view) {   //리턴값(cursor)이 있는 함수

        // where 구문이 없으면 모든 데이터(record)를 읽어오라는 뜻임   //record= 그 데이터의 한줄
       Cursor cursor = db.rawQuery("SELECT * FROM "+tableName, null);    // * (모든칸) -> num, name, age, email
        //리턴된 결과표를 가리키는 Cursor 객체를 이용해서 데이터를 한줄씩 (row, record) 이동하여 읽어들임
        if(cursor==null) return;

        StringBuffer buffer= new StringBuffer();     //String 을 쌓아놓는 녀석
        while( cursor.moveToNext() ){
            //현재 가리키는 줄(row)의 각 칸(column)들의 값을 얻어오기
            int num= cursor.getInt(0);                     //column [0]num [1]name [2]age [3]email    row [0]1 [1]2 [2]3
            String name= cursor.getString(1);
            int age= cursor.getInt(2);
            //String email= cursor.getString(3);
            String email= cursor.getString(cursor.getColumnIndex("email"));  //위에줄과 같은말임. 컬룸의 [3]번방 값을 묻는것

            buffer.append(num+"  "+name+"  "+age+"  "+email+"\n");
        }//while..

        //누적된 문자데이터를 AlertDialog 에 보여주기
        AlertDialog.Builder builder= new AlertDialog.Builder(this);             //건축가 Builder 를 만듦
        builder.setMessage(buffer.toString()).create().show();

    }//clickSelectAll



    public void clickSelectByName(View view) {
        //member 테이블에서 검색할 이름 얻어오기
        String name= etName.getText().toString();

        Cursor cursor= db.rawQuery("SELECT name,email FROM "+tableName+" WHERE name=?", new String[]{name});  //?의 갯수만큼 값 넣어주기
        if(cursor==null) return;

        StringBuffer buffer= new StringBuffer();
        while ( cursor.moveToNext() ){
            String name2= cursor.getString(0);  //[0]name
            String email= cursor.getString(1);  //[1]email

            buffer.append(name2 +"  "+email+"\n");
        }//while..

        new AlertDialog.Builder(this).setMessage(buffer.toString()).create().show();
    }




    public void clickUpdateByName(View view) {
        String name= etName.getText().toString();
        db.execSQL("UPDATE "+tableName+" set age=30, email='aa@aa.com' WHERE name=?" , new String[]{name});
    }





    public void clickDelete(View view) {
        String name= etName.getText().toString();
            db.execSQL("DELETE FROM "+tableName+" WHERE name=?", new String[]{name});
        }
    }

