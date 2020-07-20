package com.plato.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.plato.NetworkHandlerThread;
import com.plato.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.plato.server.User;
import com.plato.server.*;
public class LoginActivity extends AppCompatActivity {

    private Button singUpButton;
    private Button continueBtn;
    private NetworkHandlerThread networkHandlerThread = null;
    private EditText username , password;
    private String svMessage;
    private boolean correctUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        User user = new User("1","@" );
        Log.i("svUserLog",user.toString());

                try {
                    networkHandlerThread = NetworkHandlerThread.getInstance();
                    networkHandlerThread.start();

                    Thread.sleep(500);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }


                Log.i("Thread","Start");




        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_password);
        continueBtn = findViewById(R.id.continue_button);
        singUpButton = findViewById(R.id.sing_up_instead);

        singUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("svUser", String.valueOf(correctUser));
                if(correctUser){

                    try {
                        networkHandlerThread.sendString("login");
                        networkHandlerThread.getWorker().join();
                        networkHandlerThread.sendString(username.getText().toString());
                        networkHandlerThread.getWorker().join();
                        networkHandlerThread.sendString(password.getText().toString());
                        networkHandlerThread.getWorker().join();

                        networkHandlerThread.readObject();
                        networkHandlerThread.getWorker().join();
                        Thread.sleep(500);

                        Object  user =  networkHandlerThread.getServerObject();


                        Log.i("svUserObj",user.toString());


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // after user changes focus from entering username
                if(!hasFocus){
                        Log.i("sv","hasFocusBlockReached");

                    try {
                        networkHandlerThread.sendString("checkUsername");
                        networkHandlerThread.getWorker().join();

                        String enteredUsername = username.getText().toString();

                        Log.i("username:", enteredUsername);
                        networkHandlerThread.sendString(enteredUsername);
                        networkHandlerThread.getWorker().join();

                        networkHandlerThread.readUTF();
                        networkHandlerThread.getWorker().join();


                        svMessage = networkHandlerThread.getServerMessage();

                        if(svMessage.equals("username-false")) {
                            username.setError("Username not found");
                            correctUser = false;
                        }
                        else if(svMessage.equals("username-true"))
                            correctUser = true;

                        Log.i("svMessage", svMessage);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}
