package com.example.check_all;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.check_all.constant.Data;
import com.example.check_all.listener.ButtonActionListener;

public class MainActivity extends AppCompatActivity {

    String[] listToolsBtn = Data.NAME_TOOLS_BTN;
    String[] listActionBtn = Data.NAME_ACTION_BTN;
    RadioGroup radioGroup_tools = null;
    RadioGroup radioGroup_action = null;
    Boolean isZebraTechno = true;
    private final int margin = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!(Build.MANUFACTURER.equals("Zebra Technologies"))) {
            initChoiceTerminalComponent(this);
            isZebraTechno = false;
        }
        initChoiceActionComponent(this);
        ButtonActionListener btn_listener = new ButtonActionListener(this.isZebraTechno,this.getRadioGroup_tools(),this.getRadioGroup_action(),this);
        createButton(this,btn_listener);
    }

    public Boolean getZebraTechno() {
        return isZebraTechno;
    }

    public void initChoiceTerminalComponent(Context context){
        LinearLayout layout = this.findViewById(R.id.mainLayout);

        TextView label_choice_tools = new TextView(context);
        label_choice_tools.setText(R.string.label_tool_choice);
        LinearLayout.LayoutParams params_label = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_label.leftMargin = this.margin;
        params_label.rightMargin = this.margin;
        label_choice_tools.setLayoutParams(params_label);
        label_choice_tools.setTextSize(18);
        layout.addView(label_choice_tools);

        LinearLayout layout_tools = new LinearLayout(context);

        LinearLayout.LayoutParams params_layout_tools = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        params_layout_tools.leftMargin = this.margin;
        params_layout_tools.rightMargin = this.margin;
        layout_tools.setLayoutParams(params_layout_tools);

        RadioGroup radioGroup = new RadioGroup(context);
        LinearLayout.LayoutParams params_radio_grp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        radioGroup.setLayoutParams(params_radio_grp);
        int i;
        for(i=0;i<listToolsBtn.length;i++){
            RadioButton radioBtn = new RadioButton(context);
            LinearLayout.LayoutParams params_radio_btn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params_radio_btn.leftMargin = this.margin;
            label_choice_tools.setLayoutParams(params_radio_btn);
            radioBtn.setText(listToolsBtn[i]);
            radioGroup.addView(radioBtn);
        }
        this.setRadioGroup_tools(radioGroup);
        layout_tools.addView(radioGroup);

        ScrollView scroll_radio_btn = new ScrollView(context);
        scroll_radio_btn.setVerticalScrollBarEnabled(true);
        LinearLayout.LayoutParams params_scroll_radio_grp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        params_scroll_radio_grp.leftMargin = this.margin;
        params_scroll_radio_grp.rightMargin = this.margin;
        scroll_radio_btn.setLayoutParams(params_scroll_radio_grp);
        scroll_radio_btn.addView(layout_tools);

        layout.addView(scroll_radio_btn);
    }

    public void initChoiceActionComponent(Context context){
        LinearLayout layout = this.findViewById(R.id.mainLayout);

        TextView label_choice_tools = new TextView(context);
        label_choice_tools.setText(R.string.label_action_choice);
        LinearLayout.LayoutParams params_label = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_label.leftMargin = this.margin;
        params_label.rightMargin = this.margin;
        label_choice_tools.setLayoutParams(params_label);
        label_choice_tools.setTextSize(18);
        layout.addView(label_choice_tools);

        ScrollView scroll_layout = new ScrollView(context);
        scroll_layout.setVerticalScrollBarEnabled(true);
        LinearLayout.LayoutParams params_scroll_layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
        params_scroll_layout.leftMargin = this.margin;
        params_scroll_layout.rightMargin = this.margin;
        scroll_layout.setLayoutParams(params_scroll_layout);

        LinearLayout layout_action_btn = new LinearLayout(context);

        LinearLayout.LayoutParams params_layout_action_btn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_layout_action_btn.leftMargin = this.margin;
        params_layout_action_btn.rightMargin = this.margin;
        layout_action_btn.setLayoutParams(params_layout_action_btn);

        RadioGroup radioGroup = new RadioGroup(context);
        LinearLayout.LayoutParams params_radio_grp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        radioGroup.setLayoutParams(params_radio_grp);

        int i;
        for(i=0;i<listActionBtn.length;i++){
            RadioButton radioBtn = new RadioButton(context);
            radioBtn.setText(listActionBtn[i]);
            LinearLayout.LayoutParams params_action_btn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            radioBtn.setLayoutParams(params_action_btn);
            radioGroup.addView(radioBtn);
        }

        this.setRadioGroup_action(radioGroup);
        layout_action_btn.addView(radioGroup);
        scroll_layout.addView(layout_action_btn);
        layout.addView(scroll_layout);
    }

    public void createButton(Context context, ButtonActionListener btn_listener){
        LinearLayout layout = this.findViewById(R.id.mainLayout);
        Button btn = new Button(context);
        btn.setText(R.string.valider_btn);
        btn.setOnClickListener(btn_listener);
        LinearLayout.LayoutParams params_btn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_btn.leftMargin = this.margin;
        params_btn.rightMargin = this.margin;

        btn.setLayoutParams(params_btn);

        layout.addView(btn);
    }

    public void setRadioGroup_tools(RadioGroup radioGroup_tools) {
        this.radioGroup_tools = radioGroup_tools;
    }

    public RadioGroup getRadioGroup_tools() {
        return radioGroup_tools;
    }

    public void setRadioGroup_action(RadioGroup radioGroup_action) {
        this.radioGroup_action = radioGroup_action;
    }

    public RadioGroup getRadioGroup_action() {
        return radioGroup_action;
    }
}