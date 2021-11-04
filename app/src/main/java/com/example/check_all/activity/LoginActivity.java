package com.example.check_all.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.check_all.R;
import com.example.check_all.api.Api;
import com.example.check_all.api.JsonPlaceholderApi;
import com.example.check_all.constant.Constant;
import com.example.check_all.constant.Data;
import com.example.check_all.models.ChoiceChecker;
import com.example.check_all.models.UserRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(R.string.login_btn);
        initComponent(this);

    }

    public void initComponent(Context context){
        LinearLayout layout = findViewById(R.id.loginLayout);
        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);
        //ImageView
        LinearLayout.LayoutParams params_img = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200);
        params_img.rightMargin = Data.margin;
        params_img.leftMargin = Data.margin;
        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.logo);
        img.setLayoutParams(params_img);


        //input email
        LinearLayout.LayoutParams params_email = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params_email.rightMargin = Data.margin;
        params_email.leftMargin = Data.margin;
        EditText input_email = new EditText(context);
        input_email.setLayoutParams(params_email);
        input_email.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        input_email.setHint(R.string.email_placeholder);


        //input password
        LinearLayout.LayoutParams params_password = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params_password.rightMargin = Data.margin;
        params_password.leftMargin = Data.margin;
        EditText input_password = new EditText(context);
        input_password.setLayoutParams(params_password);
        input_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input_password.setHint(R.string.password_placeholder);

        if(!sharedPreferences.getString("email", "").isEmpty()) {
            input_email.setText(sharedPreferences.getString("email", ""));
            input_password.setText(sharedPreferences.getString("password", ""));
        }


        //checkbox password
        LinearLayout.LayoutParams params_checkbox = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params_checkbox.rightMargin = Data.margin;
        params_checkbox.leftMargin = Data.margin;
        CheckBox checkBox = new CheckBox(context);
        checkBox.setText(R.string.pwd_chkbx);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()) {
                    input_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        checkBox.setLayoutParams(params_checkbox);

        //button login
        LinearLayout.LayoutParams params_login_btn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params_login_btn.rightMargin = Data.margin;
        params_login_btn.leftMargin = Data.margin;
        Button login_btn = new Button(context);
        login_btn.setText(R.string.login_btn);
        login_btn.setLayoutParams(params_login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoiceChecker choiceChecker = new ChoiceChecker();
                int indexTools = getIntent().getIntExtra("tools_choice",1);
                int indexAction = getIntent().getIntExtra("action_choice",1);
                String email = input_email.getText().toString();
                String password = input_password.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this,R.string.error_empty_login, Toast.LENGTH_SHORT).show();
                } else {
                    choiceChecker.setChoiceToolsIndex(indexTools);
                    choiceChecker.setChoiceActionIndex(indexAction);
                    choiceChecker.setEmail(email);
                    choiceChecker.setPassword(password);
                    checklogin(email, password, choiceChecker);
                }
            }
        });

        //ajout dans la view
        layout.addView(img);
        layout.addView(input_email);
        layout.addView(input_password);
        layout.addView(checkBox);
        layout.addView(login_btn);
    }

    private void saveSettings(String url, String token, int idUser, String email, String password, String action, String tool){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("url");
        editor.remove("token");
        editor.remove("idUser");
        editor.remove("email");
        editor.remove("password");
        editor.remove("action");
        editor.remove("tool");
        editor.putString("login_url", url + "/api/auth-tokens");
        editor.putString("api_url", url + "/api/android/event/billet/check");
        editor.putString("url", url);
        editor.putString("token", token);
        editor.putInt("idUser", idUser);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("action", action);
        editor.putString("tool", tool);
        editor.apply();
        editor.commit();
    }

    private boolean checklogin(String email, String password, ChoiceChecker choiceChecker) {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Connexion");
        progressDialog.setMessage("Connexion en cours...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //Authentification nom / mot de passe
        JsonPlaceholderApi jsonPlaceHolderApi = Api.getApi(Constant.URL).create(JsonPlaceholderApi.class);
        UserRequest user = new UserRequest(email, password, choiceChecker.getChoiceActionName());
        Call<ResponseBody> loginCall = jsonPlaceHolderApi.login(user);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 201) {
                    String body;
                    try {
                        body = response.body().string();
                        JsonObject jsonBody = new Gson().fromJson(body, JsonObject.class);

                        saveSettings(
                                Constant.URL,
                                jsonBody.get("token").getAsJsonObject().get("value").getAsString(),
                                jsonBody.get("user").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt(),
                                email,
                                password,
                                choiceChecker.getChoiceActionName(),
                                choiceChecker.getChoiceToolsName()

                        );

                        Intent intentToChoice = new Intent(LoginActivity.this,EventListActivity.class);
                        intentToChoice.putExtra("name", choiceChecker.getChoiceActionName()+" | "+choiceChecker.getChoiceToolsName());
                        intentToChoice.putExtra("action", choiceChecker.getChoiceActionName());
                        int indexTools = getIntent().getIntExtra("tools_choice",1);
                        int indexAction = getIntent().getIntExtra("action_choice",1);
                        intentToChoice.putExtra("tools_choice",indexTools);
                        intentToChoice.putExtra("action_choice",indexAction);
                        startActivity(intentToChoice);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Vérifiez les informations saisies", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Erreur de connexion au serveur", Toast.LENGTH_LONG).show();
            }
        });
        return false;
    }
}
