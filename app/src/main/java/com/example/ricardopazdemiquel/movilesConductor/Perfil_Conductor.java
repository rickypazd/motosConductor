package com.example.ricardopazdemiquel.movilesConductor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import clienteHTTP.HttpConnection;
import clienteHTTP.MethodType;
import clienteHTTP.StandarRequestConfiguration;
import utiles.AppHelper;
import utiles.Contexto;
import utiles.VolleyMultipartRequest;
import utiles.VolleySingleton;

public class Perfil_Conductor extends AppCompatActivity implements View.OnClickListener {


    private TextView textNombre;
    private TextView textApellido;
    private TextView textTelefono;
    private TextView textEmail;
    private TextView textcredito;
    private ImageView btn_edit_fto;
    private com.mikhaellopez.circularimageview.CircularImageView img_photo;

    private LinearLayout Liner_nombre;
    private LinearLayout Liner_apellido;
    private LinearLayout Liner_telefono;
    private LinearLayout Liner_correo;
    private final int IMG_REQ=1;
    private Bitmap bm;

    @Override
    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_perfil_conductor);

        Toolbar toolbar = findViewById(R.id.toolbar3aa);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        textNombre = findViewById(R.id.text_nombreCliente);
        textApellido = findViewById(R.id.text_apellidoCliente);
        textTelefono = findViewById(R.id.text_numero_telefono);
        textEmail = findViewById(R.id.text_email_cliente);
        img_photo = findViewById(R.id.img_photo);
        btn_edit_fto=findViewById(R.id.btn_edit_fto);
        textcredito = findViewById(R.id.creditos);

        Liner_nombre = findViewById(R.id.Liner_nombre);
        Liner_apellido = findViewById(R.id.Liner_apellido);
        Liner_telefono = findViewById(R.id.Liner_telefono);
        Liner_correo = findViewById(R.id.Liner_correo);


        Liner_nombre.setOnClickListener(this);
        Liner_apellido.setOnClickListener(this);
        Liner_telefono.setOnClickListener(this);
        Liner_correo.setOnClickListener(this);


        final JSONObject usr_log = getUsr_log();
        if (usr_log != null) {
            try {
                new User_getPerfil(usr_log.getString("id")).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        btn_edit_fto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,IMG_REQ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQ && resultCode==RESULT_OK){
            Uri path = data.getData();
            try {
                bm= MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                img_photo.setImageBitmap(bm);
                saveProfileAccount();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Opcion para ir atras sin reiniciar el la actividad anterior de nuevo
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Perfil_Conductor.this , Editar_perfil_Activity.class);
        final JSONObject usr_log = getUsr_log();
        switch (view.getId()) {
            case R.id.Liner_nombre:
                if (usr_log != null) {
                    try {
                        String nombre = usr_log.getString("nombre");
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("tipo", "nombre_usuario");
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.Liner_apellido:
                if (usr_log != null) {
                    try {
                        String apellido_pa = usr_log.getString("apellido_pa");
                        String apellido_ma = usr_log.getString("apellido_ma");
                        intent.putExtra("apellido_pa", apellido_pa);
                        intent.putExtra("apellido_ma", apellido_ma);
                        intent.putExtra("tipo", "apellido_usuario");
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.Liner_telefono:
                if (usr_log != null) {
                    try {
                        String telefono = usr_log.getString("telefono");
                        intent.putExtra("telefono", telefono);
                        intent.putExtra("tipo", "telefono_usuario");
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.Liner_correo:
                if (usr_log != null) {
                    try {
                        String correo = usr_log.getString("correo");
                        intent.putExtra("correo", correo);
                        intent.putExtra("tipo", "correo_usuario");
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                break;

        }
    }

    private void cargarUsuario(){
        final JSONObject usr_log = getUsr_log();
        if (usr_log != null) {
            try {
                String nombre = usr_log.getString("nombre");
                String apellido_pa = usr_log.getString("apellido_pa");
                String apellido_ma = usr_log.getString("apellido_ma");
                String telefono= usr_log.getString("telefono");
                String correo = usr_log.getString("correo");
                DecimalFormat df = new DecimalFormat("#.00");
                String credito = df.format(Double.parseDouble(usr_log.getString("creditos")));
                textNombre.setText(nombre);
                textApellido.setText(apellido_pa+" "+apellido_ma);
                textTelefono.setText("+591 "+telefono);
                textEmail.setText(correo);
                textcredito.setText(credito);
                if(usr_log.getString("foto_perfil").length()>0){
                    new AsyncTaskLoadImage(img_photo).execute(getString(R.string.url_foto)+usr_log.getString("foto_perfil"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            finish();
        }
    }

    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
        String usr = preferencias.getString("usr_log", "");
        if (usr.length() <= 0) {
            return null;
        } else {
            try {
                JSONObject usr_log = new JSONObject(usr);
                return usr_log;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class User_getPerfil extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private final String id;
        User_getPerfil(String id_usr) {
            id = id_usr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Perfil_Conductor.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_usuario");
            parametros.put("id",id);
            String respuesta ="";
            try {
                respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            } catch (Exception ex) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(final String success) {
            super.onPostExecute(success);
            progreso.dismiss();
            if ( success == null){
                Toast.makeText(Perfil_Conductor.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else if (!success.isEmpty()){
                try {
                    JSONObject usr = new JSONObject(success);
                    if(usr.getString("exito").equals("si")){
                        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putString("usr_log", usr.toString());
                        editor.commit();
                        cargarUsuario();
                    }else{
                        Toast.makeText(Perfil_Conductor.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                        Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(Perfil_Conductor.this,"Error al obtener Datos", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    public class AsyncTaskLoadImage  extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";
        private ImageView imageView;
        public AsyncTaskLoadImage(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }


    private void saveProfileAccount() {
        // loading or check internet connection or something...
        // ... then
        String url = getString(R.string.url_servlet_admin);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                   if(resultResponse.equals("extio")){
                       Log.i("exito",
                               "cargar image.");
                   }else{
                       Log.i("Error",
                               "cargar image.");
                   }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("evento","subir_foto_perfil");
                    params.put("id_usr", getUsr_log().getInt("id")+"");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("archibo", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), img_photo.getDrawable()), "image/jpeg"));

                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }



}
