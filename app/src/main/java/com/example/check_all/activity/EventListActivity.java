package com.example.check_all.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.check_all.R;
import com.example.check_all.adapter.EventListAdapter;
import com.example.check_all.api.Api;
import com.example.check_all.api.JsonPlaceholderApi;
import com.example.check_all.constant.Constant;
import com.example.check_all.models.ChoiceChecker;
import com.example.check_all.models.Event;
import com.example.check_all.services.Service;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListActivity extends AppCompatActivity implements EventListAdapter.OnEventListListener{
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private EventListAdapter eventListAdapter;
    private ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        initComponent();
    }

    private void initComponent() {
        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.event_list_recycler_view);
        manager = new LinearLayoutManager(this);

        getUserEvents();
    }

    private int getUserEvents() {
        ProgressDialog progressDialog = new ProgressDialog(EventListActivity.this);
        progressDialog.setTitle("Récupération des données");
        progressDialog.setMessage("Un instant...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonPlaceholderApi jsonPlaceHolderApi = Api.getApi(Constant.URL).create(JsonPlaceholderApi.class);

        Call<ResponseBody> getEventsCall = jsonPlaceHolderApi.getUserEvents(
                sharedPreferences.getInt("idUser", 0),
                sharedPreferences.getString("token", "")
        );


        getEventsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    String body = null;
                    try {
                        body = response.body().string();
                        JsonArray eventsJsonArray = new Gson().fromJson(body, JsonObject.class).get("events").getAsJsonArray();

                        if(eventsJsonArray.size() != 0) {
                            ArrayList<Event> events = new ArrayList<>();

                            for(int i = 0; i < eventsJsonArray.size(); i++) {
                                JsonObject eventJsonObject = eventsJsonArray.get(i).getAsJsonObject();
                                Event event = Service.getEvent(eventJsonObject);
                                events.add(event);
                            }
                            EventListActivity.this.events = events;
                            eventListAdapter = new EventListAdapter(events, EventListActivity.this, EventListActivity.this);

                            recyclerView.setLayoutManager(manager);
                            recyclerView.setAdapter(eventListAdapter);
                            progressDialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                } else {
                    Toast.makeText(EventListActivity.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                System.out.println("Failure");
                t.printStackTrace();
            }
        });
        return 0;
    }

    @Override
    public void onEventClick(int position) {
        ChoiceChecker choiceChecker = new ChoiceChecker();
        int indexTools = getIntent().getIntExtra("tools_choice",1);
        int indexAction = getIntent().getIntExtra("action_choice",1);
        choiceChecker.setChoiceToolsIndex(indexTools);
        choiceChecker.setChoiceActionIndex(indexAction);
        Intent intent = new Intent(EventListActivity.this,choiceChecker.getClassActivity());
        intent.putExtra("name", choiceChecker.getChoiceActionName()+" | "+choiceChecker.getChoiceToolsName());
        intent.putExtra("action", choiceChecker.getChoiceActionName());
        intent.putExtra("idEvent", events.get(position).getId());

        startActivity(intent);
    }
}