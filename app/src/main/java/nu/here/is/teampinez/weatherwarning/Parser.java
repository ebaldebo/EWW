package nu.here.is.teampinez.weatherwarning;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.github.goober.coordinatetransformation.positions.SWEREF99Position;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Parser extends AsyncTask<String, Void, String> {
    private final static String authid = "5fe4551a599447929a301bc183b83a26";

    private ProgressDialog progressDialog;
    MyCurrentLocationListener gps;

    Parser(Activity activity) {
        super();
        gps = new MyCurrentLocationListener(activity);
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        return getJson(500);
    }

    @Override
    protected void onPostExecute(String v) {
        progressDialog.dismiss();
        //TODO Remove debug logging
        Log.d("Post Execute", v);
    }

    private String getJson(int timeout) {
        HttpURLConnection c = null;
        try {
            URL url = new URL("http://api.trafikinfo.trafikverket.se/v1/data.json");
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setDoOutput(true);
            c.setDoInput(true);
            c.setRequestProperty("Content-Type", "text/xml");
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);

            OutputStream os = c.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            SWEREF99Position position = gps.getLoc();

            // writer.write(paramsRadius(authid, position, "10000"));
            writer.write(paramsCone(authid, gps.getTriangle()));
            writer.flush();
            writer.close();
            os.close();

            c.connect();

            int status = c.getResponseCode();

            switch (status) {
                case 200:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        } finally {
            if(c != null) {
                try {
                    c.disconnect();
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
            }
        }
        return null;
    }

    private String paramsRadius(String authid, SWEREF99Position position, String radius) {
        StringBuilder sb = new StringBuilder();
        sb.append("<REQUEST>");
        sb.append("<LOGIN authenticationkey='").append(authid).append("' />");
        sb.append("<QUERY objecttype='WeatherStation'>");

        /*
         * Values lat and long position will be swapped sometime soon in the API.
         */
        sb.append("<FILTER>").append("<WITHIN name='Geometry.SWEREF99TM' shape='center' value='").append(position.getLongitude()).append(" ").append(position.getLatitude()).append("' radius='").append(radius).append("'").append("/></FILTER>");

        /*
         * More datas?
         */
        sb.append("<INCLUDE>Measurement.MeasureTime</INCLUDE>");
        sb.append("<INCLUDE>Name</INCLUDE>");
        sb.append("<INCLUDE>Measurement.Air.Temp</INCLUDE>");
        sb.append("<INCLUDE>Measurement.Road.Temp</INCLUDE>");
        sb.append("<INCLUDE>Measurement.Wind.Force</INCLUDE>");
        sb.append("<INCLUDE>Geometry.WGS84</INCLUDE>");
        sb.append("<INCLUDE>Measurement.Wind.ForceMax</INCLUDE>");
        sb.append("<INCLUDE>Measurement.Wind.DirectionText</INCLUDE>");
        sb.append("<INCLUDE>ModifiedTime</INCLUDE>");
        sb.append("</QUERY></REQUEST>");

        //TODO Remove debug logging
        Log.d("Request", sb.toString());

        return sb.toString();
    }

    private String paramsCone(String authid, ArrayList<SWEREF99Position> positions) {
        StringBuilder sb =  new StringBuilder();
        sb.append("<REQUEST>");
        sb.append("<LOGIN authenticationkey='").append(authid).append("' />");
        sb.append("<QUERY objecttype='WeatherStation'>");
        sb.append("<FILTER>");
        sb.append("<WITHIN name='Geometry.SWEREF99TM' shape='polygon' value='").append(positions.get(0).getLongitude()).append(" ").append(positions.get(0).getLatitude()).append(",").append(positions.get(1).getLongitude()).append(" ").append(positions.get(1).getLatitude()).append(",").append(positions.get(2).getLongitude()).append(" ").append(positions.get(2).getLatitude()).append(",").append(positions.get(0).getLongitude()).append(" ").append(positions.get(0).getLatitude()).append("' /></FILTER>");

        /*
         * More datas?
         */
        sb.append("<INCLUDE>Measurement.MeasureTime</INCLUDE>");
        sb.append("<INCLUDE>Name</INCLUDE>");
        sb.append("<INCLUDE>Measurement.Air.Temp</INCLUDE>");
        sb.append("<INCLUDE>Measurement.Road.Temp</INCLUDE>");
        sb.append("<INCLUDE>Measurement.Wind.Force</INCLUDE>");
        sb.append("<INCLUDE>Geometry.WGS84</INCLUDE>");
        sb.append("<INCLUDE>Measurement.Wind.ForceMax</INCLUDE>");
        sb.append("<INCLUDE>Measurement.Wind.DirectionText</INCLUDE>");
        sb.append("<INCLUDE>ModifiedTime</INCLUDE>");
        sb.append("</QUERY></REQUEST>");

        //TODO Remove debug logging
        Log.d("Request", sb.toString());

        return sb.toString();
    }
}
