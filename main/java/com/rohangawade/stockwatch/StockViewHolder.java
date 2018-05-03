package com.rohangawade.stockwatch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by rohan on 3/1/2018.
 */

public class StockViewHolder extends RecyclerView.ViewHolder {
    public TextView stockname;
    public TextView stockprice;
    public TextView percentchange;
    public TextView ticker;
    public CardView stockcv;

    public StockViewHolder(View view){
        super(view);
        ticker = (TextView) view.findViewById(R.id.tickerName);
        stockname =  (TextView) view.findViewById(R.id.stockName);
        stockprice = (TextView) view.findViewById(R.id.stockPrice);
        percentchange = (TextView) view.findViewById(R.id.percentage);


    }
}
