package com.example.fitnessapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Info extends AppCompatActivity {

    private NumberPicker numberPickerHeight1, numberPickerHeight2;
    private TextView textViewStrideLength, textViewBMI;
    private EditText editTextWeight, editTextStepGoal, editTextCalorieGoal, editTextDistanceGoal;
    private Button buttonEnterWeight, buttonEnterStepGoal, buttonEnterCalorieGoal, buttonEnterDistanceGoal,buttonBack;

    private double BMI;
    private String BMIstr;

    private int height1 = 0;
    private int height2 = 0;
    private double weight = 0;

    private int dailyStepGoal, dailyCalorieGoal = 0;
    private double dailyDistanceGoal = 0;
    private double strideLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        numberPickerHeight1 = findViewById(R.id.numberPickerHeight1);
        numberPickerHeight2 = findViewById(R.id.numberPickerHeight2);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextStepGoal = findViewById(R.id.editTextStepGoal);
        editTextCalorieGoal = findViewById(R.id.editTextCalorieGoal);
        editTextDistanceGoal = findViewById(R.id.editTextDistanceGoal);

        buttonEnterWeight = findViewById(R.id.buttonEnterWeight);
        buttonEnterStepGoal = findViewById(R.id.buttonEnterStepGoal);
        buttonEnterCalorieGoal = findViewById(R.id.buttonEnterCalorieGoal);
        buttonEnterDistanceGoal = findViewById(R.id.buttonEnterDistanceGoal);
        buttonBack = findViewById(R.id.buttonBack);
        textViewStrideLength = findViewById(R.id.textViewStrideLength);
        textViewBMI = findViewById(R.id.textViewBMI);


        numberPickerHeight1.setMinValue(1);
        numberPickerHeight1.setMaxValue(9);

        numberPickerHeight2.setMinValue(0);
        numberPickerHeight2.setMaxValue(11);


        loadStepGoal();
        loadCalorieGoal();
        loadDistanceGoal();
        loadStrideLength();
        loadWeight();
        loadHeight1();
        loadHeight2();


        System.out.println(height1);
        System.out.println(height2);
        if (height1==0){
            numberPickerHeight1.setValue(1);
        } else {
            numberPickerHeight1.setValue((int)height1);
        }
        numberPickerHeight2.setValue((int)height2);

        if (dailyStepGoal!=0){
            editTextStepGoal.setText(dailyStepGoal+"");
        }
        if (dailyCalorieGoal!=0){
            editTextCalorieGoal.setText(dailyCalorieGoal+"");
        }
        if (dailyDistanceGoal!=0){
            editTextDistanceGoal.setText(""+dailyDistanceGoal+"");
        }
        if (weight!=0){
            editTextWeight.setText(weight+"");
        }

        if (strideLength!=0) {
            textViewStrideLength.setText(strideLength + " feet");
        } else {
            textViewStrideLength.setText("N/A feet");
        }

        if (weight!=0 && height1!=0 && height2!=0){

            BMI = (weight / (((height1 * 12) + height2) *  ((height1 * 12) + height2))) * 703;
            BMI = Math.round(BMI);

            if (BMI < 18.5) {
                BMIstr = "Underweight";
                textViewBMI.setTextColor(Color.BLUE);
            } else if (BMI >= 18.5 && BMI < 25) {
                BMIstr = "Healthy";
                textViewBMI.setTextColor(Color.GREEN);
            } else if (BMI >= 25 && BMI < 30) {
                BMIstr = "Overweight";
                textViewBMI.setTextColor(Color.YELLOW);
            } else if (BMI >= 30) {
                BMIstr = "Obese";
                textViewBMI.setTextColor(Color.RED);
            }
            textViewBMI.setText("BMI: " + (int)BMI + "\n" + BMIstr);

        }



        numberPickerHeight1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                height1 = newVal;
                strideLength = ((height1*12) + height2)*.413;
                strideLength/=12;
                strideLength = Math.round(strideLength*100);
                strideLength/=100;
                textViewStrideLength.setText(strideLength + " feet");


                if ((height1*12 + height2)!=0 && weight!=0) {
                    BMI = (weight / (((height1 * 12) + height2) *  ((height1 * 12) + height2))) * 703;
                    BMI = Math.round(BMI);
                    if (BMI < 18.5) {
                        BMIstr = "Underweight";
                        textViewBMI.setTextColor(Color.BLUE);
                    } else if (BMI >= 18.5 && BMI < 25) {
                        BMIstr = "Healthy";
                        textViewBMI.setTextColor(Color.GREEN);
                    } else if (BMI >= 25 && BMI < 30) {
                        BMIstr = "Overweight";
                        textViewBMI.setTextColor(Color.YELLOW);
                    } else if (BMI >= 30) {
                        BMIstr = "Obese";
                        textViewBMI.setTextColor(Color.RED);
                    }
                    textViewBMI.setText("BMI: " + (int)BMI + "\n" + BMIstr);
                }
            }
        });

        //(37.25 lbs / 41.5 in / 41.5 in) x 703 = 15.2

        numberPickerHeight2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                height2 = newVal;
                strideLength = ((height1*12) + height2)*.413;
                strideLength/=12;
                strideLength = Math.round(strideLength*100);
                strideLength/=100;
                textViewStrideLength.setText(strideLength + " feet");
                if ((height1*12 + height2)!=0 && weight!=0) {
                    BMI = (weight / (((height1 * 12) + height2) *  ((height1 * 12) + height2))) * 703;
                    BMI = Math.round(BMI);
                    if (BMI < 18.5) {
                        BMIstr = "Underweight";
                        textViewBMI.setTextColor(Color.BLUE);
                    } else if (BMI >= 18.5 && BMI < 25) {
                        BMIstr = "Healthy";
                        textViewBMI.setTextColor(Color.GREEN);
                    } else if (BMI >= 25 && BMI < 30) {
                        BMIstr = "Overweight";
                        textViewBMI.setTextColor(Color.YELLOW);
                    } else if (BMI >= 30) {
                        BMIstr = "Obese";
                        textViewBMI.setTextColor(Color.RED);
                    }
                    textViewBMI.setText("BMI: " + (int)BMI + "\n" + BMIstr);
                }
            }
        });

        buttonEnterWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight = Double.parseDouble(editTextWeight.getText().toString());
                if ((height1*12 + height2)!=0 && weight!=0) {

                    BMI = (weight / (((height1 * 12) + height2) *  ((height1 * 12) + height2))) * 703;

                    BMI = Math.round(BMI);
                    if (BMI < 18.5) {
                        BMIstr = "Underweight";
                        textViewBMI.setTextColor(Color.BLUE);
                    } else if (BMI >= 18.5 && BMI < 25) {
                        BMIstr = "Healthy";
                        textViewBMI.setTextColor(Color.GREEN);
                    } else if (BMI >= 25 && BMI < 30) {
                        BMIstr = "Overweight";
                        textViewBMI.setTextColor(Color.YELLOW);
                    } else if (BMI >= 30) {
                        BMIstr = "Obese";
                        textViewBMI.setTextColor(Color.RED);
                    }
                    textViewBMI.setText("BMI: " + (int)BMI + "\n" + BMIstr);
                }
            }
        });

        buttonEnterStepGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dailyStepGoal = Integer.parseInt(editTextStepGoal.getText().toString());
            }
        });

        buttonEnterCalorieGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dailyCalorieGoal = Integer.parseInt(editTextCalorieGoal.getText().toString());
            }
        });

        buttonEnterDistanceGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dailyDistanceGoal = Double.parseDouble(editTextDistanceGoal.getText().toString());
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(Info.this, MainActivity.class);
                saveStepGoal();
                saveCalorieGoal();
                saveDistanceGoal();
                saveStrideLength();
                saveWeight();
                saveHeight1();
                saveHeight2();
                startActivity(main);
            }
        });







    }



    public void saveStepGoal(){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("stepGoal.txt", MODE_PRIVATE);
            fos.write((dailyStepGoal+"").getBytes());

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



    public void saveCalorieGoal(){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("calorieGoal.txt", MODE_PRIVATE);
            fos.write((dailyCalorieGoal+"").getBytes());

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



    public void saveDistanceGoal(){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("distanceGoal.txt", MODE_PRIVATE);
            fos.write((dailyDistanceGoal+"").getBytes());

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



    public void saveStrideLength(){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("strideLength.txt", MODE_PRIVATE);
            fos.write((strideLength+"").getBytes());

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



    public void saveWeight(){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("weight.txt", MODE_PRIVATE);
            fos.write((weight+"").getBytes());

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

            weight = Double.parseDouble(sb.toString());
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



    public void saveHeight1(){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("height1.txt", MODE_PRIVATE);
            fos.write((height1+"").getBytes());

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



    public void saveHeight2 (){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput("height2.txt", MODE_PRIVATE);
            fos.write((height2+"").getBytes());

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

}