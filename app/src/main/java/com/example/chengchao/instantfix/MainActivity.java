package com.example.chengchao.instantfix;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    Class<?> clazz = Class.forName(Test$override.class.getName());
                    IFunction newIns = (IFunction)clazz.newInstance();

                    Class<?> clazzOld = Class.forName(Test.class.getName());
                    Field stubField =  clazzOld.getDeclaredField("stub");
                    stubField.setAccessible(true);
                    stubField.set(null, newIns);


                    Class<?> clazz2 = Class.forName(Module$override.class.getName());
                    IModule newIns2 = (IModule)clazz2.newInstance();

                    Class<?> clazzOld2 = Class.forName(Module.class.getName());
                    Field stubField2 =  clazzOld2.getDeclaredField("stub");
                    stubField2.setAccessible(true);
                    stubField2.set(null, newIns2);

                    Test t = new Test();
                    Snackbar.make(view, "The result is :" + t.getInfo(new Module()), Snackbar.LENGTH_LONG)
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
}
