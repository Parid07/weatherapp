package com.example.mainpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.textclassifier.TextLinks;
import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRl;
    private ProgressBar loadingPB;
    private TextInputLayout citynameTv;
    private TextView temperatureTv,citynamedis;
    private TextView conditionTv;
    private RecyclerView weatherRV;
    private Button button;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIv, searchIV;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityname,city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        loadingPB = findViewById(R.id.PBloading);
        homeRl = findViewById(R.id.idrlhome);
        citynamedis=findViewById(R.id.cityname);
        button=findViewById(R.id.button);
        citynameTv = (TextInputLayout) findViewById(R.id.idtilcity);
        temperatureTv = findViewById(R.id.idtvtemperature);
        conditionTv = findViewById(R.id.idtvcondition);
        weatherRV = findViewById(R.id.idrvweather);
        cityEdt = (TextInputEditText) findViewById(R.id.edtcity);
        backIV = findViewById(R.id.idivback);
        iconIv = findViewById(R.id.idivicon);
        searchIV = findViewById(R.id.idivsearch);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModelArrayList);
        weatherRV.setAdapter(weatherRVAdapter);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
       cityname = getcitName(location.getLongitude(), location.getLatitude());
        getweatherinfo(cityname);
        city=cityname;

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = cityEdt.getText().toString();
                if (city.Empty()) {
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                } else {
                     citynameTv.setSuffixText(cityname);
                    getweatherinfo(city);

                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String query = new StringBuilder()
                        .append("city").append("=").append(city).toString();


                Uri builtUri = Uri.parse("https://ayush1704.github.io/website?").buildUpon()
                        .encodedQuery(query)
                        .build();

                URL url = null;
                try {
                    url = new URL(builtUri.toString());
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url.toString()));
                    startActivity(intent);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMISSIONS GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "please provide permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getcitName(double longitude, double latitude) {
        String cityname = "not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityname = city;
                    } else {
                        Log.d("TAG", "CITY NOT FOUND");

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
        }
        Log.d("hiiiiiiiiiiiiiii","the city name "+cityname);
citynamedis.setText(cityname);
        return cityname;
    }
    private void getweatherinfo(String cityname) {
        String url = " https://api.weatherapi.com/v1/current.json?key=5f4445e6ef164ee5926115520212612&q=" + cityname + "&aqi=yes";
        citynameTv.setSuffixText(cityname);
        citynamedis.setText(cityname);
        RequestQueue requestqueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingPB.setVisibility(View.GONE);
                        homeRl.setVisibility(View.VISIBLE);

                        weatherRVModelArrayList.clear();

                        try {


                            String temperature = response.getJSONObject("current").getString("temp_c");
                            temperatureTv.setText(temperature + "℃");
                            int isday = response.getJSONObject("current").getInt("is_day");
                            String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                            String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                            Picasso.get().load("http:".concat(conditionIcon)).into(iconIv);
                            conditionTv.setText(condition);
                            if (isday == 1) {
                               Picasso.get().load("https://images.unsplash.com/photo-1535498730771-e735b998cd64?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8c2t5bGluZXxlbnwwfHwwfHw%3D&w=1000&q=80").into(backIV);
                                //morning
                            } else {
                                Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_3euApvfvEk55JtZnSINxxS1bGxFWQauj3g&usqp=CAU").into(backIV);

                            }
                            JSONObject forecastobj = response.getJSONObject("forecast");
                            JSONObject forecastO = forecastobj.getJSONArray("forecastday").getJSONObject(0);
                            JSONArray hourArray = forecastO.getJSONArray("hour");
                            for (int i = 0; i < hourArray.length(); i++) {
                                JSONObject hourobj = hourArray.getJSONObject(i);
                                String time = hourobj.getString("time");
                                String tempr = hourobj.getString("temp_c");
                                String img = hourobj.getJSONObject("condition").getString("icon");
                                String wind = hourobj.getString("wind_kph");
                                weatherRVModelArrayList.add(new WeatherRVModel(time, tempr, img, wind));
                            }
                            weatherRVAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("YOUR_APP_LOG_TAG", "I got an error", error);


                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });
        requestqueue.add(jsonObjectRequest);
    }}


