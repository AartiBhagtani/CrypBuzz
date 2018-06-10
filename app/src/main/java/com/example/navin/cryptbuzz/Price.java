package com.example.navin.cryptbuzz;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Price extends AppCompatActivity {
    ListView fruitsList;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        int[] ids = new int[]{52, 328, 1, 2,999};
        for (int i = 0; i < ids.length; i++) {
            String url;
            if(i==4) {
                url = "https://api.coinmarketcap.com/v1/ticker/ethereum/";
                StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {
                        try{
                            JSONArray ar=new JSONArray(string);
                            JSONObject object = ar.getJSONObject(0);
                            String price=object.getString("price_usd");
                            TextView bit=(TextView) findViewById(R.id.textView12);
                            bit.setText(price);
                        }
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                RequestQueue rQueue = Volley.newRequestQueue(Price.this);
                rQueue.add(request);
            }
            else
                url = "https://api.coinmarketcap.com/v2/ticker/" + ids[i];
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String string) {
                    parseJsonData(string);
                }
            },
                    new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            RequestQueue rQueue = Volley.newRequestQueue(Price.this);
            rQueue.add(request);
        }
    }

    void parseJsonData(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);
            JSONObject obj1=object.getJSONObject("data");
            String name=obj1.getString("name");
            JSONObject obj2=obj1.getJSONObject("quotes");
            JSONObject obj3=obj2.getJSONObject("USD");
            String s=obj3.getString("price");

            TextView bit;

            if(name.equals("Bitcoin")){
                bit=(TextView) findViewById(R.id.textView3);
                bit.setText(s);
            }
            if(name.equals("Ripple")){
                bit=(TextView) findViewById(R.id.textView23);
                bit.setText(s);
            }
            if(name.equals("Monero")){
                bit=(TextView) findViewById(R.id.textView25);
                bit.setText(s);
            }
            if(name.equals("Litecoin")){
                bit=(TextView) findViewById(R.id.textView16);
                bit.setText(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.dismiss();
    }
}
