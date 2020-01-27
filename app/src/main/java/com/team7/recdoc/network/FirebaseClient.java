package com.team7.recdoc.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team7.recdoc.model.Stats;
import com.team7.recdoc.model.User;

import java.util.HashMap;
import java.util.Map;

public class FirebaseClient {
    private static FirebaseClient firebaseClient;
    private FirebaseAuth mAuth;
    private static FirebaseDatabase database;
    private static String userId;
    private static DatabaseReference userRef;
    private static Stats stats;
    private static User profile;
    private static double consumed, burned, total;

    public FirebaseClient() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userId = mAuth.getCurrentUser().getUid();
    }

    public static synchronized FirebaseClient getInstance() {
        firebaseClient = new FirebaseClient();
        return firebaseClient;
    }

    public synchronized void setReference() {
        DatabaseReference ref = database.getReference();
        userRef = ref.child("users/" + userId).child("stats");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stats = dataSnapshot.getValue(Stats.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getMessage();
            }
        });
    }

    public void updateCalories(double consumed, double burned) {
        Map<String, Object> usersUpdate = new HashMap<>();

        double total_calories = stats.getTotal_calories();
        total_calories += consumed;
        total_calories -= burned;
        if (!(burned == 0)) usersUpdate.put("calories_burned", burned);
        if (!(consumed == 0)) usersUpdate.put("calories_consumed", consumed);
        usersUpdate.put("total_calories", total_calories);
        userRef.updateChildren(usersUpdate).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String getConsumed() {
        Log.d("cekcek", "Stats value of total_calories: ");
        return String.valueOf(stats.getCalories_consumed());
    }

    public String getBurned() {
        return String.valueOf(stats.getCalories_burned());
    }

    public String getTotal() {
        return String.valueOf(stats.getTotal_calories());
    }
}