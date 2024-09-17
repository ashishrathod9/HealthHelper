package com.example.healthhelper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.io.IOException;
import java.util.HashMap;

public class VerifyOtp extends AppCompatActivity {

    private static final String TAG = "VerifyOtp";
    private EditText editTextOTP;
    private Button btnVerify;
    private String phoneNumber;
    private String receivedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        editTextOTP = findViewById(R.id.editTextNumberDecimal);
        btnVerify = findViewById(R.id.button2);

        // Get phone number from the Intent
        phoneNumber = getIntent().getStringExtra("phone_number");

        // Make sure phone number includes country code
        if (phoneNumber != null && !phoneNumber.startsWith("+")) {
            phoneNumber = "+91" + phoneNumber;  // Assuming Indian phone numbers
        }

        // Send OTP via backend (Twilio)
        sendOtpToBackend();

        // Verify button click
        btnVerify.setOnClickListener(v -> verifyOTP());
    }

    private void sendOtpToBackend() {
        // Create a logging interceptor to log network requests and responses
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create an OkHttpClient with the logging interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")  // Use this for emulator or your local IP for real devices
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        // Create the service
        OtpService otpService = retrofit.create(OtpService.class);

        // Create request body
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("phoneNumber", phoneNumber);

        // Send OTP request
        otpService.sendOtp(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseBody = response.body();
                    if (responseBody != null && responseBody.has("otp")) {
                        receivedOtp = responseBody.get("otp").getAsString();
                        Log.d(TAG, "OTP sent to " + phoneNumber);
                        Toast.makeText(VerifyOtp.this, "OTP sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Unexpected response format: " + responseBody);
                        Toast.makeText(VerifyOtp.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.e(TAG, "Failed to send OTP. Response code: " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response body", e);
                    }
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void verifyOTP() {
        String enteredOtp = editTextOTP.getText().toString();
        if (enteredOtp.equals(receivedOtp)) {
            Log.d(TAG, "OTP verified successfully!");
            navigateToHome();
        } else {
            Log.e(TAG, "Invalid OTP entered.");
        }
    }

    private void navigateToHome() {
        // Navigate to the HomeActivity
        Intent intent = new Intent(VerifyOtp.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }



    // Retrofit interface for sending OTP
    interface OtpService {
        @Headers("Content-Type: application/json")
        @POST("send-otp")
        Call<JsonObject> sendOtp(@Body HashMap<String, String> requestBody);
    }
}
