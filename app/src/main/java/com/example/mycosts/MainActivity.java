package com.example.mycosts;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static SQLiteDatabase db;
    static ArrayList<Purchases> purchases;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DBHelper(this);
        purchases = new ArrayList<>();

        db = dbHelper.getWritableDatabase();
        notifyArrList(db,getApplicationContext());

        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        final RVAdapter adapter = new RVAdapter(purchases);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        //if ()
        rv.smoothScrollToPosition(adapter.getItemCount());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_dialog);
                final EditText name = (EditText)dialog.findViewById(R.id.name);
                final EditText cost = (EditText)dialog.findViewById(R.id.cost);
                Button add = (Button)dialog.findViewById(R.id.add);
                Button cancel = (Button)dialog.findViewById(R.id.cancel);
                dialog.show();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String s ="'" + new Date() + "'";
                        db.execSQL("INSERT INTO costs_table (date, name, cost, type) VALUES (" +
                                s + ", '" +
                                name.getText().toString() + "', " +
                                Integer.parseInt(cost.getText().toString()) + ", " +
                                "0);");
                        notifyArrList(db,getApplicationContext());
                        //purchases.add(new Purchases(name.getText().toString(), Integer.parseInt(cost.getText().toString())));
                        adapter.notifyItemInserted(purchases.size() - 1);
                        dialog.dismiss();

                    }
                });



           }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

   /* public void createDialogView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Новая покупка")
                .setCancelable(false)
                .setPositiveButton("ДОБАВИТЬ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

    }
    */
   public static void notifyArrList(SQLiteDatabase db,Context context){
       Cursor c = db.query("costs_table", null, null, null, null, null, null);
       if (c.moveToFirst()){
           int idColIndex = c.getColumnIndex("id");
           int nameColIndex = c.getColumnIndex("name");
           int costColIndex = c.getColumnIndex("cost");
           purchases.clear();
           do{
               purchases.add(new Purchases(c.getString(nameColIndex), c.getInt(costColIndex), c.getInt(idColIndex)));
               Log.d("TAG_LOG", c.getInt(idColIndex) + " " + c.getString(nameColIndex) + " " + c.getInt(costColIndex));
           }while (c.moveToNext());
       }else Toast.makeText(context, "В базе данных нет записей", Toast.LENGTH_SHORT).show();
       c.close();
   }
   public static void notifyArrList(SQLiteDatabase db,Context context,Calendar startDay){
       Cursor c = db.query("costs_table", null, null, null, null, null, null);
       if (c.moveToFirst()){
           int idColIndex = c.getColumnIndex("id");
           int nameColIndex = c.getColumnIndex("name");
           int costColIndex = c.getColumnIndex("cost");
           purchases.clear();
           do{
               purchases.add(new Purchases(c.getString(nameColIndex), c.getInt(costColIndex), c.getInt(idColIndex)));
               Log.d("TAG_LOG", c.getInt(idColIndex) + " " + c.getString(nameColIndex) + " " + c.getInt(costColIndex));
           }while (c.moveToNext());
       }else Toast.makeText(context, "В базе данных нет записей", Toast.LENGTH_SHORT).show();
       c.close();
   }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            int counter = 0;
            Cursor c = db.query("costs_table", null, null, null, null, null, null);
            if (c.moveToFirst()){
                int costColIndex = c.getColumnIndex("cost");
                do{
                    counter += c.getInt(costColIndex);
                }while (c.moveToNext());
            }else Toast.makeText(this, "В базе данных нет записей", Toast.LENGTH_SHORT).show();
            c.close();
            Snackbar.make(getCurrentFocus(), counter + " P", BaseTransientBottomBar.LENGTH_LONG).setAction("",null).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.today) {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.week) {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.month) {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        } 

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static class RVAdapter extends RecyclerView.Adapter<RVAdapter.PurchasesViewHolder>{

        ArrayList<Purchases> purchases;
        View v;

        public static class PurchasesViewHolder extends RecyclerView.ViewHolder{

            CardView cv;
            TextView name;
            TextView cost;

            public PurchasesViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.cv);
                name = (TextView) itemView.findViewById(R.id.cost_name);
                cost = (TextView) itemView.findViewById(R.id.cost_tv);
            }
        }

        public RVAdapter(ArrayList<Purchases> purchases){
            if (purchases != null){
                this.purchases = purchases;
            }
        }

        @Override
        public PurchasesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new PurchasesViewHolder(v);
        }

        @Override
        public void onBindViewHolder(PurchasesViewHolder holder, final int position) {
            holder.name.setText(purchases.get(position).name);
            holder.cost.setText(purchases.get(position).cost + " Р");
            holder.cv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Удалить запись")
                            .setMessage("Вы уверены?")
                            .setCancelable(false)
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.execSQL("DELETE FROM costs_table WHERE id=" + (purchases.get(position).id) + ";");
                                    notifyArrList(db,v.getContext());
                                    notifyDataSetChanged();
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                    return true;
                }
            });
        }


        @Override
        public int getItemCount() {
            return purchases.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }
    public static class Purchases{
        String name;
        int cost;
        int id;

        public Purchases(String name, int cost, int id){
            this.name = name;
            this.cost = cost;
            this.id = id;
        }
        public Purchases(String name, int cost){
            this.name = name;
            this.cost = cost;
        }
    }
}
