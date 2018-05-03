package com.rohangawade.stockwatch;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by rohan on 3/1/2018.
 */

public class StockData implements Serializable,Comparable<StockData>{

    String ticker;
    String stockName;
    double stockPrice;
    double changeValue;
    double percentChange;

    public StockData(){

    }
    public StockData(String ticker, String stockName) {
        this.ticker = ticker;
        this.stockName = stockName;
    }


    public StockData(String ticker, String stockName, double stockPrice, double changeValue, double percentChange) {
        this.ticker = ticker;
        this.stockName = stockName;
        this.stockPrice = stockPrice;
        this.changeValue = changeValue;
        this.percentChange = percentChange;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public double getChangeValue() {
        return changeValue;
    }

    public void setChangeValue(double changeValue) {
        this.changeValue = changeValue;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }

    protected StockData(Parcel in){readFromParcel(in);}

    private void readFromParcel(Parcel in) {
        ticker=in.readString();
        stockName = in.readString();
        stockPrice = in.readDouble();
        percentChange=in.readDouble();
        changeValue = in.readDouble();
        //  id=in.readInt();
    }

    @Override
    public String toString() {
        return "Stock{" +
                "shortName='" + ticker + '\'' +
                ", stockName='" + stockName + '\'' +
                ", stockPrice=" + stockPrice +
                ", percentChange=" + percentChange +
                ", change Value =" +changeValue +

                '}';
    }

    @Override
    public int compareTo(StockData stockData) {
        String name =((StockData) stockData).getTicker();
        return this.getTicker().compareTo(name);
    }
}
