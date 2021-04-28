package com.mpproject.delivery_together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private String lifeCycleTag ="Login Activity";
    private String tag ="LoginActivity Message";

    private long backKeyPressedTime=0;
    
    private Button loginBtn;
    private Button forgot;
    private Button register;
    private EditText emailEditText;
    private EditText passwordEditText;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(lifeCycleTag, "In the onCreate() event");  // life cycle 확인용

        //로그인 페이지의 각종 컴포넌트 불러오기
        loginBtn = (Button) findViewById(R.id.loginBtn);
        emailEditText = (EditText) findViewById(R.id.inputEmail);
        passwordEditText = (EditText) findViewById(R.id.inputPassword);
        forgot = (Button) findViewById(R.id.goForgotBtn);
        register = (Button) findViewById(R.id.goRegisterBtn);

        firebaseAuth = firebaseAuth.getInstance();//firebaseAuth의 인스턴스를 가져옴

        //이메일 로그인 버튼 클릭
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Login Button Clicked");  // 로그인 버튼 클릭됨

                String email = emailEditText.getText().toString().trim();
                String pwd = passwordEditText.getText().toString().trim();

                if(email.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"이메일을 입력하세요",Toast.LENGTH_SHORT).show();
                    Log.d(tag, "Not Input Email");  // 이메일 입력 안됨
                }


                else if(pwd.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요",Toast.LENGTH_SHORT).show();
                    Log.d(tag, "Not Input Password Failed");  // 비밀번호 입력 안됨
                }


                else
                {
                    firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //성공했을때
                            if (task.isSuccessful())
                            {
                                Log.d(tag, "Login Success");  // 로그인 성공

                                //로그인 화면은 닫고 메인 화면으로 넘어감
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            //실패했을때
                            else
                            {
                                Toast.makeText(getApplicationContext(),"이메일 또는 비밀번호가 틀렸습니다",Toast.LENGTH_SHORT).show();
                                Log.d(tag, "Login Failed");  // 로그인 실패
                            }

                        }
                    });
                }
            }
        });

        //회원가입 버튼 눌렀을 때 회원가입 창으로 넘어감
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Sign up Button Clicke");  // 회원가입 버튼 클릭됨

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        //ID,비번 찾기 버튼 눌렀을 때 ID,비번 찾기 창으로 넘어감
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Forgot ID/PWD Button Clicked");  // 아이디, 비밀번호 찾기 버튼 클릭됨

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(lifeCycleTag, "In the onStart() event");  // life cycle 확인용

        //이미 로그인 되어있다면 로그인 후 상황으로 업데이트
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser!=null)
        {
            Log.d(tag, "Auto Login");  // 자동 로그인
            updateUI(currentUser);
        }

        else
            Log.d(tag, "Need Login");  // 로그인 필요
    }

    //다른 life cycle도 추가

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        // 로그인 페이지는 로그인 한 상태가 아니기 때문에 그냥 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
        }
    }

    private void updateUI(FirebaseUser user)
    {
        //로그인 된 상태가 아니라면
        if(user==null)
            Log.d(tag, "UPdate UI Error");  // UI 업데이트 오류 발생

        // 로그인 되어 있는 상태라면
        // 메인 페이지로 넘어감
        // 로그인 페이지는 닫아서 메인 페이지에서 나가면 앱이 종료되게 함
        else
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}