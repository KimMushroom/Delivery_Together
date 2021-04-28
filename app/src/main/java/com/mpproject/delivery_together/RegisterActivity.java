package com.mpproject.delivery_together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private String lifeCycleTag = "RegisterActivity";
    private String tag = "RegisterActivity Message";

    private Button registerBtn;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText checkEditText;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(tag, "In the onCreate() event");  // life cycle 확인용

        registerBtn = (Button) findViewById(R.id.check);

        firebaseAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(lifeCycleTag, "In the onStart() event");  // life cycle 확인용
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(lifeCycleTag, "In the onRestart() event");  // life cycle 확인용
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(lifeCycleTag, "In the onResume() event");  // life cycle 확인용
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(lifeCycleTag, "In the onPause() event");  // life cycle 확인용
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(lifeCycleTag, "In the onStop() event");  // life cycle 확인용
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(lifeCycleTag, "In the onDestroy() event");  // life cycle 확인용
    }

    private void signUp() {
        nameEditText = (EditText) findViewById(R.id.user_name);
        emailEditText = (EditText) findViewById(R.id.user_id);
        passwordEditText = (EditText) findViewById(R.id.user_password);
        checkEditText = (EditText) findViewById(R.id.user_password_check);

        String inputName = nameEditText.getText().toString().trim();
        String inputEmail = emailEditText.getText().toString().trim();
        String inputPwd = passwordEditText.getText().toString().trim();
        String inputCheckPwd = checkEditText.getText().toString().trim();

        //이름 입력 안했을 때
        if (inputName.length() == 0)
            Toast.makeText(getApplicationContext(), "이름을 입력하세요", Toast.LENGTH_SHORT).show();

            //이메일 입력 안했을 때
        else if (!isValidEmail(inputEmail))
            Toast.makeText(getApplicationContext(), "이메일을 잘못 입력하셨습니다", Toast.LENGTH_SHORT).show();

            //비밀번호 입력 안했을 때
        else if (!isValidPassword(inputPwd, inputCheckPwd))
            Toast.makeText(getApplicationContext(), "비밀번호를 잘못 입력하셨습니다", Toast.LENGTH_SHORT).show();

            //입력이 잘 되었을 때때
        else {
            //회원가입 시도
            firebaseAuth.createUserWithEmailAndPassword(inputEmail, inputPwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //유저 정보 해쉬맵 형태로 저장
                        HashMap<String, String> userInformation = new HashMap<>();

                        userInformation.put("userId", firebaseAuth.getUid());
                        userInformation.put("userEmail", inputEmail);
                        userInformation.put("userName", inputName);

                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();  //데이터 베이스에 접근 권한 갖기

                        //user_info의 자식을 만들고 userInformation 값을 넣음
                        database.child("user_info").push().setValue(userInformation); //그 자식에 HashMap 집어넣기

                        Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_LONG).show();
                        popupMessage();
                    } else {
                        if (task.getException().toString() != null)
                            Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }

    // 회원가입 성공 팝업 메세지 띄움
    private void popupMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원가입 성공"); //타이틀설정
        builder.setMessage("회원가입을 완료했습니다\n바로 로그인 하시겠습니까?"); //내용설정

        builder.setNegativeButton("아니요",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                        finish();
                    }
                });

        builder.setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
        builder.show();
    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty())
            return false;

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return false;

        else
            return true;
    }

    private boolean isValidPassword(String password, String passwordCheck) {
        if (password.isEmpty())
            return false;

        else if (!password.equals(passwordCheck))
            return false;

        else
            return true;
    }
}