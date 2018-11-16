package com.example.quizuno.whasapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Chat extends AppCompatActivity {

    ListView lista;
    EditText mensaje;
    Button enviar;

    FirebaseAuth auth;
    FirebaseListAdapter listaAdapter;
    FirebaseDatabase database;

    String nameUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        lista = findViewById(R.id.lista);
        mensaje = findViewById(R.id.mensaje);
        enviar = findViewById(R.id.btn_send);

        auth.getInstance();


        database.getInstance();

        DatabaseReference reference = database.getReference();

        Query chat = reference.child("chats");

        FirebaseListOptions options = new FirebaseListOptions.Builder<Mensaje>().setLayout(R.layout.renglon).setQuery(chat, Mensaje.class).build();

        listaAdapter = new FirebaseListAdapter<Mensaje>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Mensaje model, final int position) {

                TextView nombre = v.findViewById(R.id.user);
                TextView mensaje = v.findViewById(R.id.mensaje);

                nombre.setText(model.nombre);
                nombre.setText(model.mensaje);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listaAdapter.getRef(position).removeValue();
                    }
                });

            }
        };

        lista.setAdapter(listaAdapter);

        FirebaseUser user = auth.getCurrentUser();

        DatabaseReference ref = database.getReference().child("usuarios").child(user.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot user : dataSnapshot.getChildren()){
                    Usuario u = user.getValue(Usuario.class);

                    nameUser = u.nombre;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = mensaje.getText().toString();

                Mensaje m = new Mensaje();
                m.nombre = nameUser;
                m.mensaje = msg;

                DatabaseReference publicar = database.getReference();

                publicar.child("chats").push().setValue(m);


            }
        });
    }

    @Override
    protected void onStart() {
        listaAdapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        listaAdapter.stopListening();
        super.onStop();
    }
}
