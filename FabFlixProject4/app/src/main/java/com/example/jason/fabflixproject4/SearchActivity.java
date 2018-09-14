package com.example.jason.fabflixproject4;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity implements OnItemClickListener{

    private SearchMovie mAuthTask = null;
    ArrayList<String> data = new ArrayList<String>();
    ArrayList<String> sort = new ArrayList<String>();
    ArrayAdapter<String> sd;
    ListView listView;
    Button back;
    Button next;
    int index = 0;
    public int NUM_ITEMS_PAGE   = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView = findViewById(R.id.movie_list);
        listView.setOnItemClickListener(this);

        back = (Button)findViewById(R.id.back_button);
        next = (Button)findViewById(R.id.next_button);
        //Btnfooter();

        //loadList(0);
       // CheckBtnBackGroud(0);
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Log.i("position", "clicked on position" + position);
        String clicked_movie = sort.get(position);
        Intent movie_intent = new Intent(getApplicationContext(), SingleMovieActivity.class);
        movie_intent.putExtra("info", clicked_movie);

        movie_intent.putExtra("position", position);
        startActivity(movie_intent);
    }

    public void searchMovies(View view)
    {
        new SearchMovie().execute();
    }

    public class SearchMovie extends AsyncTask<String, Void, JSONArray> {
        EditText et = findViewById(R.id.movie_title);
        String title = et.getText().toString();

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(SearchActivity.this, "", "Getting Data from server");
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            JSONArray json = null;

            try {
                URL url = new URL("https://ec2-18-220-242-149.us-east-2.compute.amazonaws.com:8443/project4/api/movielist?title=" + title);
                // Simulate network access.
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();
                String s_url = url.toString();
                // check url
                Log.d("url", s_url);
                int status = urlConnection.getResponseCode();
                // check connection status
                Log.d("status", Integer.toString(status));

                switch (status) {
                    case 200:
                        // for valid cases
                    case 201:
                        InputStream stream = urlConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                        //BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuffer buffer = new StringBuffer();
                        String line = "";

                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                            // check json object
                            Log.d("Response: ", "> " + line);
                        }
                        reader.close();
                        // put into JSON array, return
                        json = new JSONArray(sb.toString());
                        return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //ArrayList<String> result = new ArrayList<String>();
            //buildList(json, result);
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            //mAuthTask = null;
            super.onPostExecute(result);
            pd.dismiss();

            if (result != null) {
                try {
                    data.clear();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject JSONObject = result.getJSONObject(i);
                        String m_id = JSONObject.getString("movie_id");
                        String m_title = JSONObject.getString("movie_title");
                        String m_year = JSONObject.getString("movie_year");
                        String m_director = JSONObject.getString("movie_director");
                        String m_genre = JSONObject.getString("genre");
                        String stars = "";
                        JSONArray jsonStarArray = JSONObject.getJSONArray("star_name_array");
                        for (int j = 0; j < jsonStarArray.length(); j++) {
                            String star = jsonStarArray.getString(j) + ", ";
                            stars += star;
                        }
                        String m_full = "Title: " + m_title + "\n" +
                                "Year: " + m_year + "\n" +
                                "Director: " + m_director + "\n" +
                                "Genre(s): " + m_genre + "\n" +
                                "Star(s): " + stars;
                        data.add(m_full);
                    }

                    loadList(index);
/*
                    if (data.size() > 0) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivity.this,
                                android.R.layout.simple_list_item_1, data);
                        listView.setAdapter(adapter);
                        Log.d("success", "yay it worked!");
                    } else {
                        Toast.makeText(SearchActivity.this, "No Data Found", Toast.LENGTH_LONG).show();
                        Log.d("fail", "RIP");
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void goToBack(View view) {
        if (index == 0) {
            loadList(index);
        }
        else {
            index = index - 1;
            loadList(index);
        }
    }

    public void goToNext(View view) {
        index = index + 1;
        loadList(index);
    }

    private void loadList(int number)
    {
        sort.clear();
        int start = number * NUM_ITEMS_PAGE;
        for(int i = start;i < (start) + NUM_ITEMS_PAGE; i++)
        {
            if(i < data.size())
            {
                sort.add(data.get(i));
            }
            else
            {
                break;
            }
        }
        sd = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                sort);
        listView.setAdapter(sd);
    }
}