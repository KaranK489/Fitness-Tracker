package com.example.fitnessapp;

//https://programmerworld.co/android/how-to-create-walking-step-counter-app-using-accelerometer-sensor-and-shared-preference-in-android/
//https://www.youtube.com/watch?v=EcfUkjlL9RI&t=2s
//https://www.youtube.com/watch?v=dWq5CJDBDVE&t=380s&ab_channel=PracticalCoding
//https://www.youtube.com/watch?v=4BuRMScaaI4&ab_channel=EasyTuto
//https://www.youtube.com/watch?v=ZcWN-d3tTT4&ab_channel=DevNami
//https://www.youtube.com/watch?v=YsHHXg1vbcc&ab_channel=CodinginFlow

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_1_ID = "channel1";

    private TextView textViewSteps, textViewTimer, textViewDistance, textViewCalories;
    private Button buttonWorkout, buttonInfo, buttonResetSteps;
    private Switch switchTravelMode;
    private ProgressBar progressBarSteps, progressBarCalories, progressBarDistance;

    private boolean runOnce = true;
    private double MagnitudePrevious = 0;
    private long startTime = 0;
    private long oldTime = 0;
    private long millis;
    private int seconds, minutes;

    private boolean walking = true;
    private boolean reachedStepGoal = false;
    private boolean reachedCalorieGoal = false;
    private boolean reachedDistanceGoal = false;

    private int height1 = 0;
    private int height2 = 0;
    private double weight = 0;
    private int dailyStepGoal, dailyCalorieGoal = 0;
    private double dailyDistanceGoal = 0;
    private double strideLength = 0;

    private int stepCount = 0;
    private double calories = 0;
    private double distance = 0;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    String dateTime = simpleDateFormat.format(calendar.getTime());



    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        //TIMER
        @Override
        public void run() {

            millis = System.currentTimeMillis() - startTime + oldTime;
            seconds = (int) (millis / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;

//            textViewTimer.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);


        loadStepGoal();
        loadCalorieGoal();
        loadDistanceGoal();

        loadStrideLength();
        loadWeight();
        loadHeight1();
        loadHeight2();



        strideLength/=5280;




        textViewSteps = findViewById(R.id.textViewSteps);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewDistance = findViewById(R.id.textViewDistance);
        textViewCalories = findViewById(R.id.textViewCalories);
        textViewTimer.setText(dateTime);

        loadDistance();
        loadCalories();

//        buttonResetSteps = findViewById(R.id.buttonResetSteps);
        buttonInfo = findViewById(R.id.buttonInfo);

        switchTravelMode = findViewById(R.id.switchTravelMode);

        progressBarSteps = findViewById(R.id.progressBarSteps);
        progressBarCalories = findViewById(R.id.progressBarCalories);
        progressBarDistance = findViewById(R.id.progressBarDistance);

        loadSteps();

        double t = Math.round(distance*100);
        t/=100;
        textViewDistance.setText("Distance: " + t + " miles");
        textViewCalories.setText("Calories: " + (int)calories);





        if (dailyStepGoal!=0){
            if (stepCount>=dailyStepGoal){
                reachedStepGoal=true;
            }
            progressBarSteps.setVisibility(View.VISIBLE);
            double temp = (double)stepCount/dailyStepGoal;
            temp*=100;
            progressBarSteps.setProgress((int)temp);
        } else {
            progressBarSteps.setVisibility(View.INVISIBLE);
        }

        if (dailyCalorieGoal!=0){
            if (calories>=dailyCalorieGoal){
                reachedCalorieGoal=true;
            }
            progressBarCalories.setVisibility(View.VISIBLE);
            double temp = calories/dailyCalorieGoal;
            temp*=100;
            progressBarCalories.setProgress((int)temp);
        } else {
            progressBarCalories.setVisibility(View.INVISIBLE);
        }

        if (dailyDistanceGoal!=0){
            if (distance>=dailyDistanceGoal){
                reachedDistanceGoal=true;
            }
            progressBarDistance.setVisibility(View.VISIBLE);
            double temp = distance/dailyDistanceGoal;
            temp*=100;
            progressBarDistance.setProgress((int)temp);
        } else {
            progressBarDistance.setVisibility(View.INVISIBLE);
        }


        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is notification");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }



        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if (sensorEvent != null) {
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];

                    double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration*y_acceleration+ z_acceleration*z_acceleration);
//                    double Magnitude = Math.sqrt(x_acceleration * x_acceleration);
//                    double Magnitude = Math.sqrt(x_acceleration*x_acceleration + z_acceleration*z_acceleration);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    double threshold;
                    if (walking){
                        threshold = 4.4;
                    } else {
                        threshold = 11;
                    }

                    if (MagnitudeDelta > threshold && millis>=100) {//CHANGE IF NEEDED, DEFAULT WAS 6
                        stepCount++;
                        if (walking){
                            calories+=0.04;
                        } else {
                            calories+=0.06;
                        }
                        textViewCalories.setText("Calories: " + (int)calories);

                        if (dailyStepGoal!=0){
                            progressBarSteps.setVisibility(View.VISIBLE);
                            double temp = (double)stepCount/dailyStepGoal;
                            temp*=100;
                            progressBarSteps.setProgress((int)temp);
                        } else {
                            progressBarSteps.setVisibility(View.INVISIBLE);
                        }


                        if (dailyCalorieGoal!=0){
                            progressBarCalories.setVisibility(View.VISIBLE);
                            double temp = calories/dailyCalorieGoal;
                            temp*=100;
                            progressBarCalories.setProgress((int)temp);
                        } else {
                            progressBarCalories.setVisibility(View.INVISIBLE);
                        }

                        if (dailyDistanceGoal!=0){

                            progressBarDistance.setVisibility(View.VISIBLE);
                            double temp = distance/dailyDistanceGoal;
                            temp*=100;
                            progressBarDistance.setProgress((int)temp);
                        } else {
                            progressBarDistance.setVisibility(View.INVISIBLE);
                        }


                        if (strideLength!=0){
                            if (walking){
                                distance+=(strideLength);
                            } else {
                                distance+=(strideLength*1.5);
                            }
                            double temp = (int)(distance*100);
                            temp/=100;
                            textViewDistance.setText("Distance: " + temp + " miles");
                        } else {
                            textViewDistance.setText("Distance: " + "N/A");
                        }

                        if (runOnce){
                            saveTotalSteps();
                            saveDistance();
                            saveCalories();
                            runOnce = false;
                        }

                        if (stepCount>=dailyStepGoal && dailyStepGoal!=0 && !reachedStepGoal){

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My Notification");
                            builder.setContentTitle("You reached your daily step goal!");
                            builder.setContentText("");
                            builder.setSmallIcon(R.drawable.man);
                            builder.setAutoCancel(true);



                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                            managerCompat.notify(1, builder.build());

                            reachedStepGoal = true;
                        }

                        if (calories>=dailyCalorieGoal && dailyCalorieGoal!=0 && !reachedCalorieGoal){

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My Notification");
                            builder.setContentTitle("You reached your daily calorie goal!");
                            builder.setContentText("");
                            builder.setSmallIcon(R.drawable.man);
                            builder.setAutoCancel(true);



                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                            managerCompat.notify(1, builder.build());

                            reachedCalorieGoal = true;
                        }

                        if (distance>=dailyDistanceGoal && dailyDistanceGoal!=0 && !reachedDistanceGoal){

                            System.out.println(distance);
                            System.out.println(dailyDistanceGoal);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My Notification");
                            builder.setContentTitle("You reached your daily distance goal!");
                            builder.setContentText("");
                            builder.setSmallIcon(R.drawable.man);
                            builder.setAutoCancel(true);



                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                            managerCompat.notify(1, builder.build());

                            reachedDistanceGoal = true;
                        }


                    }
                    textViewSteps.setText("Steps: "+ stepCount);
                }
                runOnce = true;
            }




            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };


//        if (stepCount!=0){
//           System.out.println("NOT FIRST RUN");
//        } else {
//            System.out.println("FIRST RUN");
//        }

            sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);






//        buttonWorkout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!startWorkout) {
//                    startWorkout = true;
//                    startTime = System.currentTimeMillis();
//                    timerHandler.postDelayed(timerRunnable, 0);
//                    buttonWorkout.setText("STOP WORKOUT");
//                } else {
//                    startWorkout = false;
//                    timerHandler.removeCallbacks(timerRunnable);
//                    oldTime = millis;
//                    buttonWorkout.setText("RESUME WORKOUT");
//                }
//            }
//        });


        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorManager.unregisterListener(stepDetector, sensor);

                Intent info = new Intent(MainActivity.this, Info.class);
                startActivity(info);
            }
        });







        switchTravelMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    walking = false;
                } else {
                    walking = true;
                }
            }
        });


//        buttonResetSteps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stepCount = 0;
//                textViewSteps.setText("Steps: "+ stepCount);
//                saveTotalSteps();
//            }
//        });

    }



    public void loadStepGoal (){
        FileInputStream fis = null;
        try {
            fis = openFileInput("stepGoal.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text="";


            while ((text = br.readLine()) != null){
                sb.append(text);
            }

            dailyStepGoal = Integer.parseInt(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    public void loadCalorieGoal (){
        FileInputStream fis = null;
        try {
            fis = openFileInput("calorieGoal.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text="";


            while ((text = br.readLine()) != null){
                sb.append(text);
            }

            dailyCalorieGoal = Integer.parseInt(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    public void loadDistanceGoal (){
        FileInputStream fis = null;
        try {
            fis = openFileInput("distanceGoal.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text="";


            while ((text = br.readLine()) != null){
                sb.append(text);
            }

            dailyDistanceGoal = Double.parseDouble(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    public void loadStrideLength (){
        FileInputStream fis = null;
        try {
            fis = openFileInput("strideLength.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text="";


            while ((text = br.readLine()) != null){
                sb.append(text);
            }

            strideLength = Double.parseDouble(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }





    public void loadWeight (){
        FileInputStream fis = null;
        try {
            fis = openFileInput("weight.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text="";


            while ((text = br.readLine()) != null){
                sb.append(text);
            }

            double temp = Double.parseDouble(sb.toString());
            weight = (int)temp;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }





    public void loadHeight1 (){
        FileInputStream fis = null;
        try {
            fis = openFileInput("height1.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text="";


            while ((text = br.readLine()) != null){
                sb.append(text);
            }
            double temp = Double.parseDouble(sb.toString());
            height1 = (int)temp;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    public void loadHeight2 (){
        FileInputStream fis = null;
        try {
            fis = openFileInput("height2.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text="";


            while ((text = br.readLine()) != null){
                sb.append(text);
            }

            double temp = Double.parseDouble(sb.toString());
            height2 = (int)temp;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }














    public void saveTotalSteps(){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("steps.txt", MODE_PRIVATE);
            fos.write((stepCount+"").getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos!= null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void loadSteps (){
        FileInputStream fis = null;
        try {
            fis = openFileInput("steps.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text="";


            while ((text = br.readLine()) != null){
                sb.append(text);
            }


            stepCount = Integer.parseInt(sb.toString());
            textViewSteps.setText("Steps: "+ stepCount);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }





    public void saveDistance(){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("distance.txt", MODE_PRIVATE);
            fos.write((distance+"").getBytes());
            System.out.println("DISTANCE SAVED: " + distance);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos!= null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void loadDistance (){
        FileInputStream fis = null;
        try {
            fis = openFileInput("distance.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text="";


            while ((text = br.readLine()) != null){
                sb.append(text);
            }


            distance = Double.parseDouble(sb.toString());
            System.out.println("LOADED DISTANCE: " + distance);
            double temp = (int)(distance*100);
            temp/=100;
            textViewDistance.setText("Distance: " + temp + " miles");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }






    public void saveCalories(){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("calories.txt", MODE_PRIVATE);
            fos.write((calories+"").getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos!= null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void loadCalories (){
        FileInputStream fis = null;
        try {
            fis = openFileInput("calories.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text="";


            while ((text = br.readLine()) != null){
                sb.append(text);
            }


            calories = Double.parseDouble(sb.toString());
            textViewCalories.setText("Calories: " + (int)calories);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



}