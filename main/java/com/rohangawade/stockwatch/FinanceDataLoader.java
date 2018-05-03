package com.rohangawade.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rohan on 3/1/2018.
 */

public class FinanceDataLoader extends AsyncTask<String,Integer,String>{

    private MainActivity mainActivity;
    private int count;
    private StockData stock= new StockData();
    private String dataURL=null;
    private static final String TAG = "FinanceDataLoader";

    public FinanceDataLoader(MainActivity ma, StockData stockData) {
        mainActivity = ma;
        stock=stockData;
        if(stockData!=null)
            dataURL = "https://api.iextrading.com/1.0/stock/".concat(stockData.getTicker())+"/quote";
    }

    @Override
    protected void onPostExecute(String s) {
        StockData stock = parseJSON(s);
        mainActivity.addNewStock(stock);
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
            String sub;
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains("//"))
                {   sub=line.substring(2);
                    sb.append(sub).append('\n');
                }
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


    private StockData parseJSON(String s) {
        StockData s1=null;
        try {
            JSONObject jStock = new JSONObject(s);
            String ticker = jStock.getString("symbol");
            String price = jStock.getString("latestPrice");
            String priceChange = jStock.getString("change");
            String priceChangePercent = jStock.getString("changePercent");
            double stockPrice=Double.parseDouble(price);
            double changeAmt=Double.parseDouble(priceChange);
            double changePercent=Double.parseDouble(priceChangePercent);
            s1=  new StockData(ticker,stock.getStockName(),stockPrice,changeAmt,changePercent);
            return s1;
        }
        catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


}
