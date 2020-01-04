package pe.edu.idat.apploginreserva;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        //Inicializando las preferencias del app
        SharedPreferences preferences =
                getSharedPreferences("appLogin",
                        MODE_PRIVATE);
        //Consulto si la preferencia tiene información
        //almacenada
        if(preferences.contains("idpersona")){
            startActivity(new Intent(
                    LoginActivity.this,
                    MainActivity.class));
        }

        mQueue = Volley.newRequestQueue(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsumirWS();
            }
        });
    }

    private void ConsumirWS(){
        String url = "http://luis.wordlatin.com/RestfulService/login.php";
        Map<String, String> params = new HashMap<>();
        params.put("usuario",
                etUsuario.getText().toString());
        params.put("password",
                etPassword.getText().toString());
        JSONObject paramJSON = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                paramJSON,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.getBoolean("rpta")){
                                //Almacenamos la información en la
                                //preferencia del APP
                                SharedPreferences.Editor editor
                                        = getSharedPreferences("appLogin",
                                        MODE_PRIVATE).edit();
                                editor.putString("idpersona",
                                        String.valueOf(
                                                response.getInt(
                                                        "idpersona")));
                                editor.apply();
                                startActivity(new Intent(
                                        LoginActivity.this,
                                        MainActivity.class));
                            }else{
                                Toast.makeText(
                                        getApplicationContext(),
                                        response.getString("mensaje"),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }catch (Exception ex){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        mQueue.add(request);

    }

}
