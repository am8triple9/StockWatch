package com.rohangawade.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by rohan on 3/1/2018.
 */

public class StockDataAdapter extends RecyclerView.Adapter<StockViewHolder>{

    private static final String TAG = "StockDataAdapter";
    private MainActivity mainActivity;
    private List<StockData>  stockDataList;
    StockData currentStock;
    View view;

    public StockDataAdapter(MainActivity mainActivity, List<StockData> stockDataList) {
        this.mainActivity = mainActivity;
        this.stockDataList = stockDataList;
    }



    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder");
        View stockView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_stock,parent,false);
        stockView.setOnClickListener(mainActivity);
        stockView.setOnLongClickListener(mainActivity);
        return new StockViewHolder(stockView);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        StockData stockData = stockDataList.get(position);
        if(stockData.getChangeValue()>0){
            holder.ticker.setTextColor(Color.GREEN);
            holder.ticker.setText(stockData.getTicker());

            holder.stockname.setTextColor(Color.GREEN);
            holder.stockname.setText(stockData.getStockName());

            holder.stockprice.setTextColor(Color.GREEN);
            holder.stockprice.setText(String.valueOf(stockData.getStockPrice()));

            holder.percentchange.setTextColor(Color.GREEN);
            holder.percentchange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_up, 0, 0, 0);
            holder.percentchange.setText(String.valueOf(stockData.getChangeValue()) + " " + "(" + String.valueOf(stockData.getPercentChange()) + "%)");

        }
        else{
            holder.percentchange.setTextColor(Color.RED);
            holder.percentchange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down, 0, 0, 0);
            holder.percentchange.setText(String.valueOf(stockData.getChangeValue()) + " " + "(" + String.valueOf(stockData.getPercentChange()) + "%)");

            holder.ticker.setTextColor(Color.RED);
            holder.ticker.setText(stockData.getTicker());

            holder.stockname.setTextColor(Color.RED);
            holder.stockname.setText(stockData.getStockName());

            holder.stockprice.setTextColor(Color.RED);
            holder.stockprice.setText(String.valueOf(stockData.getStockPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return stockDataList.size();
    }
}
