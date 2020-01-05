package com.shresthagaurav.androidprojecttwitter;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shresthagaurav.androidprojecttwitter.api.ApiClass;
import com.shresthagaurav.androidprojecttwitter.api.LoginBLL;
import com.shresthagaurav.androidprojecttwitter.model.User;
import com.shresthagaurav.androidprojecttwitter.strictMode.StrictModeClass;

public class Login_activity extends AppCompatActivity {
    EditText et_email, et_password;
    ImageButton ib_show_P;
    Button btn_login;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login_activity );
        et_email = findViewById( R.id.login_email );
        et_password = findViewById( R.id.login_password );
        ib_show_P = findViewById( R.id.btn_SP );
        btn_login = findViewById( R.id.btn_login );
        ib_show_P.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0) {
                    et_password.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    i++;
                } else {
                    et_password.setInputType( InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD );
                    i = 0;
                }

            }
        } );
        btn_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty( et_email.getText().toString() )) {
                    if (!TextUtils.isEmpty( et_password.getText().toString() )) {
                        User u = new User( et_email.getText().toString(),
                                et_password.getText().toString() );
login( u );
                    } else {
                        et_password.setError( "empty" );
                    }
                } else {
                    et_email.setError( "empty" );
                }
            }
        } );
    }
    private void login(User u) {
        LoginBLL loginBLL = new LoginBLL();
        StrictModeClass.StrictMode();
        if (loginBLL.checkUser(u.getEmail(), u.getPassword())) {
            Toast.makeText( this, "welcome", Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText(this, "Either username or password is incorrect", Toast.LENGTH_SHORT).show();
           // etUsername.requestFocus();
        }
    }
}
