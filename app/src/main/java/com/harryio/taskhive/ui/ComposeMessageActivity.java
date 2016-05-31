package com.harryio.taskhive.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.harryio.taskhive.R;

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

    private String jobType = JOB_TYPE_OFFER;
    private String paymentMethod = PAYMENT_METHOD_BITCOIN;

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

    @OnClick({R.id.offer_button, R.id.request_button, R.id.add_keyword_button, R.id.post_job_button,
            R.id.bitcoin_button, R.id.paypal_button, R.id.dogecoin_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.offer_button:
                offerButton.setChecked(true);
                jobType = JOB_TYPE_OFFER;
                break;

            case R.id.request_button:
                requestButton.setChecked(true);
                jobType = JOB_TYPE_REQUEST;
                break;

            case R.id.bitcoin_button:
                paymentMethod = PAYMENT_METHOD_BITCOIN;
                break;

            case R.id.paypal_button:
                paymentMethod = PAYMENT_METHOD_PAYPAL;
                break;

            case R.id.dogecoin_button:
                paymentMethod = PAYMENT_METHOD_DOGECOIN;
                break;

            case R.id.add_keyword_button:
                String keyword = keywordEdittext.getText().toString();
                keywordView.addLabel(keyword);
                keywordEdittext.setText("");
                break;

            case R.id.post_job_button:
                postJob();
                break;
        }

    }

    private void postJob() {

    }
}