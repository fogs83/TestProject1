package fogs.testproject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static String LOG_TAG = "my_log";
    public String wikiTiker ="";
    public final ArrayList <String> dataArrayList= new ArrayList<>();
    EditText wikiEditText;
    Button btnSearch;
    AsyncTask parse;
    ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //new ParseTask().execute();

        btnSearch = (Button) findViewById(R.id.btnSearch);
        wikiEditText = (EditText) findViewById(R.id.wikiEditText);
        listView = (ListView)findViewById(R.id.dataList);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Searching...", Toast.LENGTH_SHORT);
                toast.show();
                dataArrayList.removeAll(dataArrayList);
                wikiTiker = wikiEditText.getText().toString();
                parse= new ParseTask().execute();
                Log.d(LOG_TAG,parse.getStatus().toString());
            }
        });
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        ArrayList result =new ArrayList();

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("https://www.quandl.com/api/v3/datasets/WIKI/"+ wikiTiker +".json?api_key=saJAsQiL5pp9r5TGgQYr");
                Log.d(LOG_TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            Log.d(LOG_TAG, strJson);
            JSONObject dataJsonObj = null;
            JSONObject dataset= null;
            JSONArray data= null;
            JSONArray column_names= null;
            String [] column_name=  null;
            ArrayAdapter adapter=null;

            try {
                dataJsonObj = new JSONObject(strJson);
                dataset = dataJsonObj.getJSONObject("dataset");
                data = dataset.getJSONArray("data");
                column_names = dataset.getJSONArray("column_names");
                column_name = new String[column_names.length()];

                for (int i=0;i<column_names.length();i++){
                    column_name[i] = column_names.optString(i);
                }
                for (int j=0;j<100;j++){
                    JSONArray dataArray = data.getJSONArray(j);
                    for (int i = 0; i < 5; i++) {
                        result.add(dataArray.getString(i));
//                        Log.d(LOG_TAG,column_name[i]+": "+ dataArray.getString(i));
                    }
                    dataArrayList.add(result.toString());

                    result.removeAll(result);

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

             adapter= new ArrayAdapter<String>(getApplication(),
                    android.R.layout.simple_list_item_1, dataArrayList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        }

    }

}
