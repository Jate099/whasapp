package com.example.quizuno.whasapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    EditText nombre;
    EditText usuario;
    EditText contra;

    Button btn_ingresar;
    Button btn_regis;

    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre = findViewById(R.id.nombre);
        usuario = findViewById(R.id.user);
        contra = findViewById(R.id.contra);

        btn_ingresar = findViewById(R.id.btn_ingresar);
        btn_regis = findViewById(R.id.btn_regis);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



        btn_regis.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String name = nombre.getText().toString();
                final String user = usuario.getText().toString();
                final String pas = contra.getText().toString();

                auth.createUserWithEmailAndPassword(user, pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            String uid = auth.getCurrentUser().getUid();
                            Usuario usuario = new Usuario();
                            usuario.nombre = name;
                            usuario.correo = user;
                            usuario.contraseña = pas;
                            usuario.uid = uid;

                            DatabaseReference reference = database.getReference();

                            reference.child("usuarios").child(uid).setValue(usuario);

                            Intent chat = new Intent(MainActivity.this, Chat.class);
                            startActivity(chat);
                        }else{
                            Toast.makeText(MainActivity.this, ""+task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });

        btn_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nombre.getText().toString();
                final String user = usuario.getText().toString();
                final String pas = contra.getText().toString();

                auth.signInWithEmailAndPassword(user, pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Intent chat = new Intent(MainActivity.this, Chat.class);
                            startActivity(chat);
                        }else{
                            Toast.makeText(MainActivity.this, ""+task.getException().toString(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });




    }
}
