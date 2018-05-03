package com.rohangawade.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by rohan on 3/1/2018.
 */

public class CompanyDataLoader extends AsyncTask<String,Integer,String> {
    private static final String TAG = "CompanyDataLoader";
    private MainActivity mainActivity;
    private int count;
    private String dataURL;
    String data;

    public CompanyDataLoader(MainActivity mainActivity, String data) {
        this.mainActivity = mainActivity;
        this.data = data;
        dataURL="http://d.yimg.com/aq/autoc?region=US&lang=en-US&query=".concat(data);
    }

    @Override
    protected void onPostExecute(String s) {
        ArrayList<StockData> stockDataList = parseJSON(s);
        mainActivity.updateData(stockDataList);
    }

    @Override
    protected String doInBackground(String... strings) {
        Uri dataUri = Uri.parse(dataURL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: " + urlToUse);
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        Log.d(TAG, "doInBackground: " + sb.toString());

        return sb.toString();

    }

    private ArrayList<StockData> parseJSON(String s) {
        ArrayList<StockData> stockDataList = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONObject jMain = jObjMain.getJSONObject("ResultSet");
            JSONArray result = jMain.getJSONArray("Result");
            for (int i = 0; i < result.length(); i++) {

                JSONObject jStock = (JSONObject) result.get(i);
                String type= jStock.getString("type");
                if(type.equals("S")) {
                    String stockName = jStock.getString("name");
                    String ticker = jStock.getString("symbol");
                   if (!ticker.contains(".")) {
                        stockDataList.add(new StockData(ticker, stockName));
                    }
               }
            }
            return stockDataList;
        }catch (Exception e) {
                Log.d(TAG, "parseJSON: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
}
