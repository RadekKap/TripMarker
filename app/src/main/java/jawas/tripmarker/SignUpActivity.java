package jawas.tripmarker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void signUp(View view) {
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        EditText retypepass = (EditText) findViewById(R.id.retype_password);
        EditText name = (EditText) findViewById(R.id.name);

        if(password.getText().toString().equals(retypepass.getText().toString())==true
                && email.getText().toString().isEmpty()== false
                && name.getText().toString().isEmpty()== false
                && password.getText().toString().isEmpty()== false
                ) {
            Firebase firebase = new Firebase("https://tripmarker.firebaseio.com/");

            firebase.createUser(email.getText().toString(), password.getText().toString(),
                    new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> stringObjectMap) {
                            finish();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            String error = firebaseError.toString();
                            //Log.i("Firebase ERROR", error);

                            if(error.length()!=147)
                            Toast.makeText(getApplicationContext(), error.substring(15), Toast.LENGTH_LONG).show();
                            else
                            Toast.makeText(getApplicationContext(), "You don"+ "\'" + "t have access to the Internet", Toast.LENGTH_LONG).show();
                        }
                    });

        }   if(password.getText().toString().equals(retypepass.getText().toString())==false)
                Toast.makeText(getApplicationContext(), "Typed passwords are different", Toast.LENGTH_LONG).show();
            int i=0;
            if(email.getText().toString().isEmpty()== true && name.getText().toString().isEmpty()== true)
            {  Toast.makeText(getApplicationContext(), "You didn" + "\'" + "t type Email and Name", Toast.LENGTH_LONG).show();
                i=1;}

            if(email.getText().toString().isEmpty()== true && i==0)
                Toast.makeText(getApplicationContext(), "You didn" + "\'" + "t type Email", Toast.LENGTH_LONG).show();

            if(name.getText().toString().isEmpty()== true && i==0)
                Toast.makeText(getApplicationContext(), "You didn" + "\'" + "t type Name", Toast.LENGTH_LONG).show();

            if(password.getText().toString().isEmpty() == true && retypepass.getText().toString().isEmpty() == true)
                Toast.makeText(getApplicationContext(), "You didn" + "\'" + "t type Passwords", Toast.LENGTH_LONG).show();




    }
}
