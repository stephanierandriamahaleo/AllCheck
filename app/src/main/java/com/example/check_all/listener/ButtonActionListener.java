package com.example.check_all.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.check_all.R;
import com.example.check_all.activity.LoginActivity;

public class ButtonActionListener implements View.OnClickListener{

    RadioGroup radioGroup_tools;
    RadioGroup radioGroup_action;
    Context main_activity = null;
    Boolean isZebra;

    public ButtonActionListener(Boolean isZebra,RadioGroup radioGroup_tools,RadioGroup radioGroup_action,Context context) {
        this.isZebra = isZebra;
        this.radioGroup_tools = radioGroup_tools;
        this.radioGroup_action = radioGroup_action;
        this.main_activity = context;
    }

    @Override
    public void onClick(View view) {
        Button btn_click = (Button) view;
        String label_btn = btn_click.getText().toString();
        if(label_btn.equals(this.main_activity.getString(R.string.valider_btn))){
            Intent intentToLogin = new Intent(this.main_activity, LoginActivity.class);
            if(this.isZebra){
                int indexChoiceTools = 2;
                intentToLogin.putExtra("tools_choice",2);
                int indexChoiceAction = this.getChecked(this.radioGroup_action);
                if(indexChoiceAction==-1){
                    Toast.makeText(this.main_activity,R.string.error_no_choice_action, Toast.LENGTH_SHORT).show();
                }else if(indexChoiceAction>-1){
                    intentToLogin.putExtra("action_choice",indexChoiceAction);
                    this.main_activity.startActivity(intentToLogin);

                }
                System.out.println("Zebra exist index_tools: "+indexChoiceTools+" et index_action: "+indexChoiceAction);
            } else if(!this.isZebra){
                int indexChoiceTools = this.getChecked(this.radioGroup_tools);
                int indexChoiceAction = this.getChecked(this.radioGroup_action);
                if(indexChoiceTools==-1){
                    Toast.makeText(this.main_activity,R.string.error_no_choice_tools, Toast.LENGTH_SHORT).show();
                }
                if(indexChoiceAction==-1){
                    Toast.makeText(this.main_activity,R.string.error_no_choice_action, Toast.LENGTH_SHORT).show();
                }else if(indexChoiceTools>-1 && indexChoiceAction>-1){
                    intentToLogin.putExtra("tools_choice",indexChoiceTools);
                    intentToLogin.putExtra("action_choice",indexChoiceAction);
                    this.main_activity.startActivity(intentToLogin);
                }
                System.out.println("index_tools: "+indexChoiceTools+" et index_action: "+indexChoiceAction);
            }

        }
    }

    public int getChecked(RadioGroup radioGroup){
        int option = -1; // aucun check
        if(radioGroup.getCheckedRadioButtonId()==-1) {
            option = -1;
        }else{
            int childCount = radioGroup.getChildCount();
            int i;
            for(i=0;i<childCount;i++){
                RadioButton radio_btn = (RadioButton) radioGroup.getChildAt(i);
                if(radio_btn.isChecked()){
                    option = i;
                }
            }
        }
        return option;
    }
}
