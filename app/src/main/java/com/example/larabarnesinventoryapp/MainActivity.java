package com.example.larabarnesinventoryapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 100;
    private DatabaseHelper myDb;
    private EditText usernameText;
    private EditText passwordText;
    private EditText addItemName;
    private EditText addItemQty;
    private BottomNavigationView bottomNav;
    private final String CHANNEL_ID = "inventory_notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myDb = new DatabaseHelper(this);
        createNotificationChannel();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment selectedFragment = new HomeFragment();

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) { //bottom navigation setup
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.notifications:

                    selectedFragment = new NotificationsFragment();
                    break;
                case R.id.settings:
                    selectedFragment = new SettingsFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
            return true;
        }
    };

    public void login(View view) {
        usernameText = findViewById(R.id.editTextUsername); //get text from edit text fields
        passwordText = findViewById(R.id.editTextPassword);


        //compare input to db
        boolean[] userValid = myDb.validateUser(usernameText.getText().toString(), passwordText.getText().toString());
        if (userValid[1]){ //if password and username match, successful login
            setContentView(R.layout.activity_inventory);
            BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
            bottomNav.setOnNavigationItemSelectedListener(navListener);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        }
        else{ //error message
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }



    }

    public void signUp(View view) { //create new user and add into db
        usernameText = findViewById(R.id.editTextUsername); //get input from edit texts
        passwordText = findViewById(R.id.editTextPassword);
        //compare input to db
        boolean[] userValid = myDb.validateUser(usernameText.getText().toString(), passwordText.getText().toString());
        if (usernameText.getText().toString().length() < 6){ //username too short
            Toast.makeText(this, "Error: Username must be at least 6 characters", Toast.LENGTH_SHORT).show();
        }
        else if (userValid[0]){ //username already taken
            Toast.makeText(this, "Error: Username not available", Toast.LENGTH_SHORT).show();
        }
        else if(passwordText.getText().toString().length() < 8){ //password too short
            Toast.makeText(this, "Error: Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
        }
        else{ //add new user
            long result = myDb.addUser(usernameText.getText().toString(), passwordText.getText().toString());
            if (result > 0){
                Toast.makeText(this, "User Successfully Created", Toast.LENGTH_SHORT).show();
            }
            else{ //error user cant be created
                Toast.makeText(this, "Error " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void enableNotifs(View view) { //ask for permission to send notifications
        if(checkPermission(Manifest.permission.SEND_SMS)){
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.SEND_SMS }, SMS_PERMISSION_CODE);
        }
    }

    public void logOut(View view) { //return to login screen
        setContentView(R.layout.activity_login);
    }

    public void addItem(View view) {//change to add item page
        setContentView(R.layout.fragment_additem);
    }

    public void addItemConfirm(View view) { //add an item into the db
        addItemName = findViewById(R.id.editTextItemName); //get text from edit text fields
        addItemQty = findViewById(R.id.editTextItemQty);

        if (addItemName.getText().toString().length() < 1) { //item name too short
            Toast.makeText(this, "Error: Please enter an item name", Toast.LENGTH_SHORT).show();
        } else if (myDb.checkItem(addItemName.getText().toString())) { //item already taken
            Toast.makeText(this, "Error: Item already exists", Toast.LENGTH_SHORT).show();
        } else if (addItemQty.getText().toString().length() < 1) { //invalid quantity
            Toast.makeText(this, "Error: Please enter a numeric quantity", Toast.LENGTH_SHORT).show();
        } else { //add new item
            try {
                if (Integer.parseInt(addItemQty.getText().toString()) > 99999) { //convert text to num and check if valid qty
                    Toast.makeText(this, "Error: Quantity too large", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error: Please enter a numeric quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            long result = myDb.addItem(addItemName.getText().toString(), Integer.parseInt(addItemQty.getText().toString()));
            if(result > 0){ //success message
                Toast.makeText(this, "Item Successfully Created", Toast.LENGTH_SHORT).show();
                if((Integer.parseInt(addItemQty.getText().toString()) == 0 ) && (checkPermission(Manifest.permission.SEND_SMS))){ //notif if qty 0
                    addNotification(addItemName.getText().toString());
                }
            }
            else{ //error item cant be created
                Toast.makeText(this, "Error " + result, Toast.LENGTH_SHORT).show();
            }
            //return home
            setContentView(R.layout.activity_inventory);
            BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
            bottomNav.setOnNavigationItemSelectedListener(navListener);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();

        }
    }

    public void cancel(View view) { //cancel button return to home
        setContentView(R.layout.activity_inventory);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
    }

    public void addNotification(String itemName) { //create a low inventory notification for an item
        if (!(checkPermission(Manifest.permission.SEND_SMS))){ //do nothing if permission not granted
            return;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID) //create notification
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("Low Inventory")
                .setContentText(itemName + " is out of stock")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Add notification
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(new Random().nextInt(), builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "inventory", importance);
            channel.setDescription("low inventory alerts");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public boolean checkPermission(String permission) { //check if sms permissions activated
        return ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_DENIED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "SMS Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "SMS Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
