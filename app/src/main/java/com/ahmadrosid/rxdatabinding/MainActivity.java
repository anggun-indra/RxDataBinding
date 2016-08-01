package com.ahmadrosid.rxdatabinding;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.regex.Pattern;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private EditText input_email;
    private EditText input_password;
    private Button register;
    private TextView message_input_email;
    private TextView message_input_password;

    final Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        register = (Button) findViewById(R.id.register);
        message_input_email = (TextView) findViewById(R.id.message_input_email);
        message_input_password = (TextView) findViewById(R.id.message_input_password);


        compositeSubscription.add(checkEmail().map(new Func1<Boolean, Integer>() {
            @Override
            public Integer call(Boolean a) {
                return a ? Color.BLACK : Color.RED;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer color) {
                input_email.setTextColor(color);
                if (input_email.getText().length() > 1) {
                    if (color == Color.RED) {
                        message_input_email.setText("Email Salah!");
                    } else {
                        message_input_email.setText("Email Benar!");
                    }
                    message_input_email.setTextColor(color);
                }
            }
        }));

        compositeSubscription.add(checkPasswordValid().map(new Func1<Boolean, Integer>() {
            @Override
            public Integer call(Boolean a) {
                return a ? Color.BLACK : Color.RED;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer color) {
                input_password.setTextColor(color);
                if (input_password.getText().length() > 1) {
                    if (color == Color.RED) {
                        message_input_password.setText("Pasword Salah!");
                    } else {
                        message_input_password.setText("Password Benar!");
                    }
                    message_input_password.setTextColor(color);
                }

            }
        }));

        compositeSubscription.add(checkFormValid().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean btnEnabled) {
                register.setEnabled(btnEnabled);
            }
        }));
    }


    /**
     * check input email
     *
     * @return Boolean
     */
    private Observable<Boolean> checkEmail() {
        return RxTextView.textChanges(input_email).map(new Func1<CharSequence, Boolean>() {
            @Override
            public Boolean call(CharSequence charSequence) {
                return emailPattern.matcher(charSequence).matches();
            }
        });
    }

    /**
     * check input password
     *
     * @return Boolean
     */
    private Observable<Boolean> checkPasswordValid() {
        return RxTextView.textChanges(input_password).map(new Func1<CharSequence, Boolean>() {
            @Override
            public Boolean call(CharSequence charSequence) {
                return charSequence.length() > 4;
            }
        });
    }

    /**
     * check all from valid
     *
     * @return Boolean
     */
    private Observable<Boolean> checkFormValid() {
        return Observable.combineLatest(checkEmail(), checkPasswordValid(), new Func2<Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean a, Boolean b) {
                return a && b;
            }
        });
    }
}
