package ru.SnowVolf.certgirl;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.util.HashMap;

/**
 * Created by Snow Volf on 15.07.2017, 18:55
 */

public class GirlFragment extends PreferenceFragment {
    SharedPreferences preferences;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        addPreferencesFromResource(R.xml.pref_screen);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_girl, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_compile: {
                if (Build.VERSION.SDK_INT >= 23){
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        RunitimeUtil.verifyStoragePermissions(getActivity());
                    } else compile();
                } else {
                    compile();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void compile(){
            try {
                if (!containsProperty("girl.filepath", " ")) {
                    throw new NullPointerException("FileName not found");
                }
                if (!containsProperty("girl.password", " ")) {
                    throw new NullPointerException("KeystorePassword not found");
                }
                if (!containsProperty("girl.alias", " ")) {
                    throw new NullPointerException("AliasName not found");
                }
                if (!containsProperty("girl.aliasPassword", " ")) {
                    throw new NullPointerException("AliasPassword not found");
                }
                final String name = getPrefValue("girl.filepath", " ");
                log(name);
                final String password = getPrefValue("girl.password", " ");
                log(password);
                final String alias = getPrefValue("girl.alias", " ");
                log(alias);
                final String aliasPass = getPrefValue("girl.aliasPassword", " ");
                log(aliasPass);
                final File file = new File(Environment.getExternalStorageDirectory().getPath() + "/MT2", name);
                log(file.getAbsolutePath());
                final File outDir = new File(Environment.getExternalStorageDirectory().getPath(), "/MT2/keys");
                log(outDir.getAbsolutePath());
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }
                final KeyStore keyStore = CerificateUtils.loadKeyStore(file, password);
                System.err.println("Output:");
                if (getPrefValue("girl.password", "").length() > 0) {
                    final String keyPass = getPrefValue("girl.password", "");
                    final File out = new File(outDir, CerificateUtils.getName(file.getName()) + ".aes");
                    CerificateUtils.encryptSplit(keyStore, out, keyPass, alias, aliasPass);
                }
                else {
                    CerificateUtils.split(keyStore, CerificateUtils.getName(file.getName()), outDir, alias, aliasPass);
                }
                System.err.println();
                System.out.println("succeed.");
                Toast.makeText(getActivity(), "Успешно", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                System.err.println();
                e.printStackTrace();
                Toast.makeText(getActivity(), "Ошибка!", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(getActivity())
                        .setMessage(e.getMessage() + "\n\n---\n\n" + e.getStackTrace().toString())
                        .setPositiveButton("ok", null)
                        .show();
            }
            try {
                System.in.read();
            }
            catch (IOException ex) {}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case RunitimeUtil.REQUEST_EXTERNAL_STORAGE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    compile();
                }
            }
        }
    }

    private void log(Object o){
        Log.e("CertGirl", "" + o);
    }

    private boolean containsProperty(String o, String o2){
        return !preferences.getString(o, o2).isEmpty();
    }

    private String getPrefValue(String key, String val){
        return preferences.getString(key, val);
    }
}
