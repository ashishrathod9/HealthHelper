package com.example.healthhelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private EditText searchPhoneEdt;
    private Button searchBtn;
    private TextView gotoSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Employees");


        searchBtn = findViewById(R.id.button);
        searchPhoneEdt=findViewById(R.id.editTextPhone);
        gotoSignUp=findViewById(R.id.textView2);


        gotoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = searchPhoneEdt.getText().toString();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    searchUserByPhone(phoneNumber);
                } else {
                    Toast.makeText(Login.this, "Please enter a phone number to search.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void searchUserByPhone(String phoneNumber) {
        Query query = databaseReference.orderByChild("employeeContactNumber").equalTo(phoneNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        EmployeeInfo employee = snapshot.getValue(EmployeeInfo.class);
                        if (employee != null) {
                            Toast.makeText(Login.this, "User found: " + employee.getEmployeeName(), Toast.LENGTH_LONG).show();
                            // You can update UI or perform other actions here
                            Intent intent=new Intent(Login.this,VerifyOtp.class);
                            intent.putExtra("phone_number",phoneNumber);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(Login.this, "No user found with this phone number.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, "Search failed: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}