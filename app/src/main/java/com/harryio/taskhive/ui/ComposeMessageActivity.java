package com.harryio.taskhive.ui;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.Label;
import com.harryio.taskhive.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComposeMessageActivity extends AppCompatActivity {
    private static final String JOB_TYPE_OFFER = "offer";
    private static final String JOB_TYPE_REQUEST = "request";
    private static final String PAYMENT_METHOD_BITCOIN = "bitcoin";
    private static final String PAYMENT_METHOD_PAYPAL = "paypal";
    private static final String PAYMENT_METHOD_DOGECOIN = "dogecoin";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.offer_button)
    RadioButton offerButton;
    @BindView(R.id.request_button)
    RadioButton requestButton;
    @BindView(R.id.keyword_editText)
    EditText keywordEdittext;
    @BindView(R.id.keyword_view)
    AutoLabelUI keywordView;
    @BindView(R.id.task_description_editText)
    TextInputEditText taskDescriptionEditText;
    @BindView(R.id.task_category)
    TextInputEditText taskCategory;
    @BindView(R.id.bitcoin_checkbox)
    CheckBox bitcoinCheckbox;
    @BindView(R.id.paypal_checkbox)
    CheckBox paypalCheckbox;
    @BindView(R.id.dogecoin_checkbox)
    CheckBox dogecoinCheckbox;
    @BindView(R.id.add_keyword_button)
    Button addKeywordButton;
    @BindView(R.id.post_job_button)
    Button postJobButton;
    @BindView(R.id.task_description_textInputLayout)
    TextInputLayout taskDescriptionTextInputLayout;
    @BindView(R.id.task_category_textInputLayout)
    TextInputLayout taskCategoryTextInputLayout;

    private String taskType = JOB_TYPE_OFFER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        ButterKnife.bind(this);

        setUpToolbar();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @OnClick({R.id.offer_button, R.id.request_button, R.id.add_keyword_button, R.id.post_job_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.offer_button:
                offerButton.setChecked(true);
                taskType = JOB_TYPE_OFFER;
                break;

            case R.id.request_button:
                requestButton.setChecked(true);
                taskType = JOB_TYPE_REQUEST;
                break;

            case R.id.add_keyword_button:
                String keyword = keywordEdittext.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    keywordView.addLabel(keyword);
                    keywordEdittext.setText("");
                }
                break;

            case R.id.post_job_button:
                postJob();
                break;
        }
    }

    private void postJob() {
        taskDescriptionTextInputLayout.setErrorEnabled(false);
        taskDescriptionTextInputLayout.setError("");
        taskCategoryTextInputLayout.setErrorEnabled(false);
        taskCategoryTextInputLayout.setError("");

        String description = taskDescriptionEditText.getText().toString();
        String category = taskCategory.getText().toString();

        if (TextUtils.isEmpty(description)) {
            taskDescriptionTextInputLayout.setErrorEnabled(true);
            taskDescriptionTextInputLayout.setError("Task description cannot be null");
            return;
        } else if (TextUtils.isEmpty(category)) {
            taskCategoryTextInputLayout.setErrorEnabled(true);
            taskCategoryTextInputLayout.setError("Task category cannot be empty");
            return;
        }

        List<String> paymentMethods = new ArrayList<>(3);
        if (bitcoinCheckbox.isChecked()) {
            paymentMethods.add(PAYMENT_METHOD_BITCOIN);
        }

        if (paypalCheckbox.isChecked()) {
            paymentMethods.add(PAYMENT_METHOD_PAYPAL);
        }

        if (dogecoinCheckbox.isChecked()) {
            paymentMethods.add(PAYMENT_METHOD_DOGECOIN);
        }

        if (paymentMethods.size() == 0) {
            Toast.makeText(this, "Please select at least one payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Label> labels = keywordView.getLabels();
        if (labels.size() == 0) {
            Toast.makeText(this, "Please add at least one task keyword", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> keywords = new ArrayList<>(labels.size());
        for (Label label : labels) {
            keywords.add(label.getText());
        }
    }
}