package com.example.chengchao.instantfix;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.instantlib.InstantFix;

import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnTest;
    Button btnPatch;
    Button btnGetPatch;
    Button btnRevertPath;
    Test t1 = new Test();

    private static MainActivity $change = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnTest = (Button)findViewById(R.id.btnTest);
        btnPatch = (Button)findViewById(R.id.btnPatch);
        btnGetPatch = (Button)findViewById(R.id.btnGetPatch);
        btnRevertPath = (Button)findViewById(R.id.btnRevertPatch);

        btnTest.setOnClickListener(this);
        btnPatch.setOnClickListener(this);
        btnGetPatch.setOnClickListener(this);
        btnRevertPath.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    Test t = new Test();
                    Snackbar.make(view, "The result is :" + t.getInfo(new Module()), Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if($change != null){
            return $change.onOptionsItemSelected(item);
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Snackbar.make(item.getActionView(), "Click Settings", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnPatch){

            if(InstantFix.patch(getApplicationContext())){
                Snackbar.make(view, "Apply the patch", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }else{
                Snackbar.make(view, "No patch or failed", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        } else if(view.getId() == R.id.btnGetPatch){
            InstantFix.getPatchFromAsset(getApplicationContext());
            Snackbar.make(view, "Get the patch", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if(view.getId() == R.id.btnRevertPatch){
            InstantFix.revertPatch(getApplicationContext());
            Snackbar.make(view, "Revert the patch", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if(view.getId() == R.id.btnTest){
            Snackbar.make(view, "The result is :" + t1.getInfo(new Module()), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }
}
