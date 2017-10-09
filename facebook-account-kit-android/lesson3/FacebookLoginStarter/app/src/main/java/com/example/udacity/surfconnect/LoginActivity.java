package com.example.udacity.surfconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    public static int APP_REQUEST_CODE = 1;
    private LoginButton fbLoginButton;
    // login callback manager: handle result of login intent
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));

        fbLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);

        // set read permissions to email on login button
        fbLoginButton.setReadPermissions("email");

        //initialize callback manager
        mCallbackManager = CallbackManager.Factory.create();

        // register callback for the login button
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // on success login into fb launch AccountActivity
                launchAccountActivity();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                // get error message
                String errorMessage = error.getMessage();
                // show toas with the error message
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

            }
        });


        // check for existing access token in fb login
        // we do this because when we close the app and then open it again it will open LoginActivity
        // despite the fact that we have already logged in. So, it should open AccountActivity
        // we get facebook access token like this (com.facebook.AccessToken) because Account Kit also has access token
        com.facebook.AccessToken loginToken = com.facebook.AccessToken.getCurrentAccessToken();

        // check for an existing access token for Account Kit
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        // check if one of the tokens is not null
        if (accessToken != null || loginToken != null) {
            // if previously logged in, proceed to the account activity
            launchAccountActivity();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // forward result to the callback manager for login button
        mCallbackManager.onActivityResult(requestCode, resultCode,data);

        // For Account Kit, confirm that this response matches your request
        if (requestCode == APP_REQUEST_CODE) {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {
                // display login error
                String toastMessage = loginResult.getError().getErrorType().getMessage();
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            } else if (loginResult.getAccessToken() != null) {
                // on successful login, proceed to the account activity
                launchAccountActivity();
            }
        }
    }

    private void onLogin(final LoginType loginType) {
        // create intent for the Account Kit activity
        final Intent intent = new Intent(this, AccountKitActivity.class);

        // configure login type and response type
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        loginType,
                        AccountKitActivity.ResponseType.TOKEN
                );
        final AccountKitConfiguration configuration = configurationBuilder.build();

        // launch the Account Kit activity
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    public void onPhoneLogin(View view) {
        onLogin(LoginType.PHONE);
    }

    public void onEmailLogin(View view) {
        onLogin(LoginType.EMAIL);
    }

    private void launchAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }

}
