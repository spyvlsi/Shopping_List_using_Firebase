package com.training.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FloatingActionButton fab_btn;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    private TextView totalsumResult;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter adapter;
    public LinearLayout root;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shopping List");

        totalsumResult = findViewById(R.id.total_amount);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);
        mDatabase.keepSynced(true);

        recyclerView = findViewById(R.id.recycler_home);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();

        //Total sum number

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int totalammount = 0;

                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    Data data = snap.getValue(Data.class);

                    totalammount += data.getAmount();

                    String sttotal = String.valueOf(totalammount + ".00");

                    totalsumResult.setText(sttotal);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        fab_btn = findViewById(R.id.fab);

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("dedomena");

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(query, Data.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_data, parent, false);

                return new MyViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(MyViewHolder holder, final int position, Data model) {
                holder.setDate(model.getDate());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setAmount(model.getAmount());

//                holder.root.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(HomeActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
//                    }
//                });
            }

        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void customDialog() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.input_data, null);
        final AlertDialog dialog = mydialog.create();
        dialog.setView(myview);

        final EditText type = myview.findViewById(R.id.edt_type);
        final EditText amount = myview.findViewById(R.id.edt_amount);
        final EditText note = myview.findViewById(R.id.edt_note);
        Button btnSave = myview.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mType = type.getText().toString().trim();
                String mAmount = amount.getText().toString().trim();
                String mNote = note.getText().toString().trim();

                int amount2 = Integer.parseInt(mAmount);

                if (TextUtils.isEmpty(mType)) {
                    type.setError("No type found!");
                    return;
                }
                if (TextUtils.isEmpty(mAmount)) {
                    amount.setError("Please insert amount!");
                    return;
                }
                if (TextUtils.isEmpty(mNote)) {
                    note.setError("Please insert a note!");
                    return;
                }


                String id = mDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(mType, amount2, mNote, date, id);

                mDatabase.child(id).setValue(data);

                Toast.makeText(getApplicationContext(), "Data Add", Toast.LENGTH_SHORT).show();


                dialog.dismiss();
            }
        });

        dialog.show();

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        View myview;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setType(String type) {
            TextView mType = myview.findViewById(R.id.type);
            mType.setText(type);
        }

        public void setNote(String note) {
            TextView mNote = myview.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date) {
            TextView mDate = myview.findViewById(R.id.date);
            mDate.setText(date);
        }

        public void setAmount(int amount) {
            TextView mAmount = myview.findViewById(R.id.amount);
            String stam = String.valueOf(amount);
            mAmount.setText(stam);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.log_out:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
