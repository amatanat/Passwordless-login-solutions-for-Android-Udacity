package com.example.udacity.surfconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitActivity.ResponseType;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

public class LoginActivity extends AppCompatActivity {

    private static final int RESPONSE_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));

        // check for an existing access token
        final AccessToken accessToken = AccountKit.getCurrentAccessToken();
        // if user is already have access token then laucn AccountActivity
        if (accessToken != null){
            launchAccountActivity();
        } //else continue login flow
    }

    private void onLogin(final LoginType loginType){

        // create intent fo the AccountKitActivity
        Intent intent = new Intent(this, AccountKitActivity.class);

        //configure login type and response type
        AccountKitConfiguration.AccountKitConfigurationBuilder accountKitConfigurationBuilder =
            new AccountKitConfiguration.AccountKitConfigurationBuilder(
                loginType,

                // enable client access the token, so pass TOKEN response type
                // in case of own server autorization then pass CODE response type
                ResponseType.TOKEN
            );

        // build the configuration
        final AccountKitConfiguration accountKitConfiguration = accountKitConfigurationBuilder.build();
        // put accountKitConfiguration as extra to intent
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, accountKitConfiguration);
        startActivityForResult(intent, RESPONSE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESPONSE_CODE && resultCode == RESULT_OK){
            // get login result
            AccountKitLoginResult accountKitLoginResult = data.getParcelableExtra(
                AccountKitLoginResult.RESULT_KEY);

            // check if login was successfull
            if (accountKitLoginResult.getError() != null){
                // error in login
                String errorMessage = accountKitLoginResult.getError().getErrorType().getMessage();
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            } else if (accountKitLoginResult.getAccessToken() != null){
                // success in login
                launchAccountActivity();

            }

        }
    }

    private void launchAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }

    public void onPhoneLogin(View view){
        onLogin(LoginType.PHONE);
    }

    public void onEmailLogin(View view){
        onLogin(LoginType.EMAIL);
    }

}
