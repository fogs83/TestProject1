package fogs.testproject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    public static String LOG_TAG = "my_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ParseTask().execute();
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("https://www.quandl.com/api/v3/datasets/WIKI/AAPL.json?api_key=saJAsQiL5pp9r5TGgQYr");

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

            try {
                dataJsonObj = new JSONObject(strJson);
                JSONObject dataset = dataJsonObj.getJSONObject("dataset");
                JSONArray data = dataset.getJSONArray("data");
                JSONArray column_names = dataset.getJSONArray("column_names");
                String [] column_name = new String[column_names.length()];

                for (int i=0;i<column_names.length();i++){
                    column_name[i] = column_names.optString(i);
                }
                for (int j=0;j<10;j++){
                    JSONArray dataArray = data.getJSONArray(j);
                    for (int i = 0; i < 5; i++) {
                        Log.d(LOG_TAG,column_name[i]+": "+ dataArray.getString(i));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
