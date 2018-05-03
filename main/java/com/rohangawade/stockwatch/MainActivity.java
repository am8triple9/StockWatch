package com.rohangawade.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener {
    CompanyDataLoader cd;
    FinanceDataLoader fd;
    String userText=null;

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ArrayList<StockData> stocksList=new ArrayList<>();
    private StockDataAdapter mAdapter;
    private SwipeRefreshLayout swiper;
    private DatabaseHandler databaseHandler;
    ArrayList<StockData> finalList= new ArrayList<>();
    private static String ticker;

    private ArrayList<StockData> tempstocksList=new ArrayList<>();
    private ArrayList<StockData> tempStartup = new ArrayList<>();
    private FinanceDataLoader fdStart;
    boolean refresh=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerstock);

        mAdapter = new StockDataAdapter(this,stocksList);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHandler = new DatabaseHandler(this);

        ArrayList<StockData> list = databaseHandler.loadStocks();
        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            if(list!=null)
                for(int i =0;i<list.size();i++)
                {
                    fdStart =new FinanceDataLoader(this,list.get(i));
                    fdStart.execute();
                }
        }
        else{
            Toast.makeText(this,"No active internet connection",Toast.LENGTH_SHORT).show();
            stocksList.addAll(list);
            Collections.sort(stocksList);
            mAdapter.notifyDataSetChanged();
        }
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Add:
                Log.d(TAG, "onOptionsItemSelected: on add");
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    final EditText et = new EditText(this);
                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                    et.setGravity(Gravity.CENTER_HORIZONTAL);
                    et.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                    builder.setView(et);
                    userText=et.getText().toString().trim();

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //databaseHandler.dumpLog();
                            cd = new CompanyDataLoader(MainActivity.this, et.getText().toString().trim());
                            cd.execute();

                            Date date = new Date();
                            Log.d(TAG, "onClick: " + tempstocksList.size() + " " + date + " " + cd.getStatus());

                        }

                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    builder.setTitle("Stock Selection");
                    builder.setMessage("Please enter a stock symbol");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("No Network Connection");
                    builder.setMessage("Stocks cannot be added without a network connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;

                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doRefresh() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            finalList.addAll(stocksList);
            Log.d(TAG, "onCreate: size: "+finalList.size());
            stocksList.clear();
            mAdapter.notifyDataSetChanged();
            if(finalList!=null) {
                for (int i = 0; i < finalList.size(); i++) {

                    fdStart = new FinanceDataLoader(this, finalList.get(i));
                    refresh=true;
                    fdStart.execute();
                }
                swiper.setRefreshing(false);
            }
            if(finalList!=null)
                finalList.clear();
        }
        else
        {
            swiper.setRefreshing(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks cannot be updated without a network connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }




    @Override
    public void onClick(View view) {
        final int pos = recyclerView.getChildLayoutPosition(view);
        if(stocksList.size()>0 && stocksList.get(pos)!=null) {
            String stockName = stocksList.get(pos).getTicker();
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                String url = "http://www.marketwatch.com/investing/stock/".concat(stockName);
                clickStock(url);

            } else {
                Toast.makeText(this, "You are NOT Connected to the Internet!", Toast.LENGTH_LONG).show();
                return;
            }

        }
    }

    public void clickStock(String urlStock) {
        String url = urlStock;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View view) {
        final int pos = recyclerView.getChildLayoutPosition(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                databaseHandler.deleteStock(stocksList.get(pos).getTicker());
                stocksList.remove(pos);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Delete Stock " + stocksList.get(pos).getStockName() + "?");
        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    public void updateData(ArrayList<StockData> cList) {
        if(cList == null) {
            Log.d("TAG", "cList");
        }
        if(cList.size()==0){
            Log.d(TAG,"");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Symbol Not found: "+userText);
            builder.setMessage("Data for stack symbol");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if(cList!=null){
            for(int i=0;i<cList.size();i++)
                Log.d(TAG, "updateData: "+cList.get(i));
            tempstocksList.addAll(cList);

            Date date=new Date();
            Log.d(TAG, "updateData: "+date);

            if(tempstocksList.size()>1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Make a selection");
                // builder.setIcon(R.drawable.icon2);
                final StockData[] stock = tempstocksList.toArray(new StockData[tempstocksList.size()]);
                final CharSequence[] stockArray = new CharSequence[tempstocksList.size()];
                for (int i = 0; i < tempstocksList.size(); i++)
                    stockArray[i] = tempstocksList.get(i).getTicker() + " - " + tempstocksList.get(i).getStockName();

                builder.setItems(stockArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fd = new FinanceDataLoader(MainActivity.this, tempstocksList.get(which));
                        fd.execute();
                    }
                });

                builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                       tempstocksList.clear();
                    }
                });
                AlertDialog dialog1 = builder.create();
                dialog1.show();
            }
            else{
                if(tempstocksList.size()==1) {
                    fd = new FinanceDataLoader(MainActivity.this, tempstocksList.get(0));
                    fd.execute();
                }
            }
        }
        else if(cList==null || cList.size()==0){
            Log.d(TAG,"");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Symbol Not found: "+userText);
            builder.setMessage("Data for stack symbol");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void addNewStock(StockData stock){

        // b=stocksList.contains(stock);
        for(int i =0;i<stocksList.size();i++)
            Log.d(TAG, "stock: "+stocksList.get(i).toString());

        if(stock!=null){
            for(int i =0;i<stocksList.size();i++)
                if (stocksList.get(i).getStockName().equals(stock.getStockName())){
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        builder.setTitle("Duplicate Stock");
                        builder.setMessage("Stock Symbol "+stock.getStockName()+" is already present");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        tempstocksList.clear();
                        return;
                    }
                }
        }

        if(stock!=null){

            stocksList.add(stock);

            Collections.sort(stocksList);
            Log.d(TAG, "addNewStock: "+refresh);
            if(refresh==false) {
                databaseHandler.addStock(stock);
                Log.d(TAG, "addNewStock: "+stock.getStockName()+" ");
            }
            else
                databaseHandler.updateStock(stock);
            mAdapter.notifyDataSetChanged();
            tempstocksList.clear();
        }

    }
}
