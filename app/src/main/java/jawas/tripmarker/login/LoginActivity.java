package jawas.tripmarker.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import jawas.tripmarker.profile.ProfileActivity;
import jawas.tripmarker.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void signUp(View view){
        Intent goToSignUp = new Intent(this, SignUpActivity.class);
        startActivity(goToSignUp);
    }

    public void signIn(View view) {
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);

        if(!password.getText().toString().isEmpty() || !email.getText().toString().isEmpty() ) {
           Firebase database = new Firebase("https://tripmarker.firebaseio.com/");

             database.authWithPassword(email.getText().toString(),
                password.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Intent goToMap = new Intent(LoginActivity.this, ProfileActivity.class);
                        goToMap.putExtra( (String)getResources().getText(R.string.uid_var), authData.getUid());
                        LoginActivity.this.startActivity(goToMap);
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        String error = firebaseError.toString();

                        if(error.length()!=164)
                            Toast.makeText(getApplicationContext(), error.substring(15), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), "You don"+ "\'" + "t have access to the Internet", Toast.LENGTH_LONG).show();
                    }
                });
        }
        int i=0;
            if(password.getText().toString().isEmpty() && email.getText().toString().isEmpty())
            {
                Toast.makeText(getApplicationContext(), "You didn" + "\'" + "t type Email and Password", Toast.LENGTH_LONG).show();
                i=1;
            }

            if(email.getText().toString().isEmpty() && i==0)
                Toast.makeText(getApplicationContext(), "You didn" + "\'" + "t type Email", Toast.LENGTH_LONG).show();

            if(password.getText().toString().isEmpty() && i==0)
                Toast.makeText(getApplicationContext(), "You didn" + "\'" + "t type Password", Toast.LENGTH_LONG).show();

    }
}
