package com.example.testvolley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    int REQUEST_CODE = 100;


   TextView nameTV,temperatureTV,humidityTV,conditionTV,currentDateTV;
   EditText editText;
   Button btn;
   ImageView imageView1,imageView2;

   RecyclerView mList;
   LinearLayoutManager linearLayoutManager;
   DividerItemDecoration dividerItemDecoration;
   List<ForeCastModel> foreCastModelList;
   RecyclerView.Adapter adapter;

   ProgressDialog progressDialog;
    String cityName;
    String url ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        nameTV=findViewById(R.id.nametxt);
        temperatureTV=findViewById(R.id.temptxt);
        humidityTV=findViewById(R.id.humiditytxt);
        conditionTV=findViewById(R.id.contxt);
        editText = findViewById(R.id.ed1);
        btn = findViewById(R.id.btn1);
        imageView1 = findViewById(R.id.imageView1);
        imageView2=findViewById(R.id.imageView2);
        currentDateTV = findViewById(R.id.dateTV);
        mList =findViewById(R.id.recview1);
        progressDialog = new ProgressDialog(this);





        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        lastLocation();

        Calendar calendar = Calendar.getInstance();
        String currDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        currentDateTV.setText(currDate);





        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                apiCallFunction();

            }
        });


    }

    private void lastLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            cityName = addresses.get(0).getSubAdminArea();

                            url = "https://api.weatherapi.com/v1/forecast.json?key=2320e2460d0b457aa53115525230207&q="+cityName+"&days=5&aqi=yes&alerts=yes";



                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {

                                        JSONObject location = response.getJSONObject("location");
                                        String name = location.getString("name");
                                        nameTV.setText(name);

                                        JSONObject current = response.getJSONObject("current");
                                        String temp = current.getString("temp_c");
                                        temperatureTV.setText(temp+"°C");

                                        JSONObject condition = current.getJSONObject("condition");
                                        String conText = condition.getString("text");
                                        humidityTV.setText(conText);
                                        String currentconditionIV = condition.getString("icon");
                                        Glide.with(MainActivity.this).load("https:"+currentconditionIV+"").into(imageView1);


                                        JSONObject forecast = response.getJSONObject("forecast");
                                        JSONArray forecastday = forecast.getJSONArray("forecastday");

                                        JSONObject forcastindex = forecastday.getJSONObject(0);
                                        JSONObject day = forcastindex.getJSONObject("day");
                                        JSONObject upcondition = day.getJSONObject("condition");
                                        String upcontext = upcondition.getString("text");
                                        conditionTV.setText(upcontext);
                                        String upconditionIV = upcondition.getString("icon");
                                        Glide.with(MainActivity.this).load("https:"+upconditionIV+"").into(imageView2);


                                        foreCastModelList = new ArrayList<>();
                                        adapter = new ForeCastAdapter(getApplicationContext(),foreCastModelList);
                                        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());
                                        mList.setHasFixedSize(true);
                                        mList.setLayoutManager(linearLayoutManager);
                                        mList.addItemDecoration(dividerItemDecoration);
                                        mList.setAdapter(adapter);




                                        for (int i=0; i<forecastday.length(); i++){
                                            JSONObject forcastindexarray = forecastday.getJSONObject(i);
                                            ForeCastModel foreCastModel = new ForeCastModel();

                                            foreCastModel.setDate(forcastindexarray.getString("date"));

                                            JSONObject dayarray = forcastindexarray.getJSONObject("day");

                                            String temptextsymbol = dayarray.getString("avgtemp_c");
                                            foreCastModel.setAvgtemp_c(""+temptextsymbol+"°C");

                                            JSONObject upconditionarray = dayarray.getJSONObject("condition");

                                            foreCastModel.setText(upconditionarray.getString("text"));


                                            String iconUrl = upconditionarray.getString("icon");
                                            foreCastModel.setIcon("https:"+iconUrl+"");


                                            foreCastModelList.add(foreCastModel);





                                        }
                                        adapter.notifyDataSetChanged();






                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Toast.makeText(MainActivity.this, ""+error+"", Toast.LENGTH_SHORT).show();


                                }
                            });
                            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                            requestQueue.add(jsonObjectRequest);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                    }
                }
            });
        }else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION} , REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                lastLocation();
            }else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void apiCallFunction() {
        progressDialog.setTitle("Load Data");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        cityName = editText.getText().toString();
        url = "https://api.weatherapi.com/v1/forecast.json?key=2320e2460d0b457aa53115525230207&q="+cityName+"&days=5&aqi=yes&alerts=yes";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject location = response.getJSONObject("location");
                    String name = location.getString("name");
                    nameTV.setText(name);

                    JSONObject current = response.getJSONObject("current");
                    String temp = current.getString("temp_c");
                    temperatureTV.setText(temp+"°C");

                    JSONObject condition = current.getJSONObject("condition");
                    String conText = condition.getString("text");
                    humidityTV.setText(conText);
                    String currentconditionIV = condition.getString("icon");
                    Glide.with(MainActivity.this).load("https:"+currentconditionIV+"").into(imageView1);


                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONArray forecastday = forecast.getJSONArray("forecastday");

                    JSONObject forcastindex = forecastday.getJSONObject(0);
                    JSONObject day = forcastindex.getJSONObject("day");
                    JSONObject upcondition = day.getJSONObject("condition");
                    String upcontext = upcondition.getString("text");
                    conditionTV.setText(upcontext);
                    String upconditionIV = upcondition.getString("icon");
                    Glide.with(MainActivity.this).load("https:"+upconditionIV+"").into(imageView2);


                    foreCastModelList = new ArrayList<>();
                    adapter = new ForeCastAdapter(getApplicationContext(),foreCastModelList);
                    linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());
                    mList.setHasFixedSize(true);
                    mList.setLayoutManager(linearLayoutManager);
                    mList.addItemDecoration(dividerItemDecoration);
                    mList.setAdapter(adapter);




                    for (int i=0; i<forecastday.length(); i++){
                        JSONObject forcastindexarray = forecastday.getJSONObject(i);
                        ForeCastModel foreCastModel = new ForeCastModel();

                        foreCastModel.setDate(forcastindexarray.getString("date"));

                        JSONObject dayarray = forcastindexarray.getJSONObject("day");

                        String temptextsymbol = dayarray.getString("avgtemp_c");
                        foreCastModel.setAvgtemp_c(""+temptextsymbol+"°C");

                        JSONObject upconditionarray = dayarray.getJSONObject("condition");

                        foreCastModel.setText(upconditionarray.getString("text"));


                        String iconUrl = upconditionarray.getString("icon");
                        foreCastModel.setIcon("https:"+iconUrl+"");


                        foreCastModelList.add(foreCastModel);





                    }
                    adapter.notifyDataSetChanged();



                    progressDialog.dismiss();


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, ""+error+"", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest);
    }



}