package com.httprequest.httprequestprototype;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    private Button httpRequestButton;
    private TextView httpRequestResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpRequestButton = (Button)findViewById(R.id.scheduleDataButton);
        httpRequestResult = (TextView)findViewById(R.id.textView);
        httpRequestResult.setMovementMethod(new ScrollingMovementMethod());
        httpRequestButton.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            new RetrieveFeedTask().execute("http://softengbackend.cloudapp.net/UTA/ClassStatus?classNumbers=3330,2320");

        }
    };

    private void setData(String data){
        httpRequestResult.setText(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class RetrieveFeedTask extends AsyncTask<String, Void,String> {

        private Exception exception;
        private String result;
        public String getResult()
        {
            return result;
        }
        protected String doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL("http://softengbackend.cloudapp.net/UTA/ClassStatus?classNumbers=3330,2320");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            assert url != null;
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                try {

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    result = total.toString();
                    return total.toString();
                } catch (IOException ex) {


                }
            } finally {
                urlConnection.disconnect();
            }
            return "";
        }
        protected void onPostExecute(String result) {
            setData(result);
        }
    }
}
