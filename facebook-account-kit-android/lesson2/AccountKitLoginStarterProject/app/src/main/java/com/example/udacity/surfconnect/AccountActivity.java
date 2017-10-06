package com.example.udacity.surfconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

public class AccountActivity extends AppCompatActivity {

    TextView id;
    TextView infoLabel;
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));

        id = (TextView) findViewById(R.id.id);
        infoLabel = (TextView) findViewById(R.id.info_label);
        info = (TextView) findViewById(R.id.info);

        // once we logged in we have accountkit login information
        // we can get them, such as account id, phone number, email address
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                // in success get account info

                // get account id
                String accountId = account.getId();
                id.setText(accountId);

                // get phone number
                PhoneNumber phonenumber = account.getPhoneNumber();
                // check if phone number is available, i.e user logged in using phone number
                if (account.getPhoneNumber() != null){
                    // format number
                    String formattedPhoneNumber = formatPhoneNumber(phonenumber.toString());
                    // set text of textview to phone number
                    info.setText(formattedPhoneNumber);
                    infoLabel.setText(R.string.phone_label);
                }else{
                    // user logged in using email address
                    String email = account.getEmail();
                    info.setText(email);
                    infoLabel.setText(R.string.email_label);
                }
            }

            @Override
            public void onError(final AccountKitError accountKitError) {
                // in error show toast message that we have error
                String message = accountKitError.getErrorType().getMessage();
                Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onLogout(View view){
        AccountKit.logOut();
        launchLoginActivity();
    }


    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private String formatPhoneNumber(String phoneNumber) {
        // helper method to format the phone number for display
        try {
            PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber pn = pnu.parse(phoneNumber, Locale.getDefault().getCountry());
            phoneNumber = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        }
        catch (NumberParseException e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }

}
