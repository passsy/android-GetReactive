package com.pascalwelsch.getreactive;

import com.jakewharton.rxbinding.view.RxView;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by pascalwelsch on 10/25/15.
 */
public class OtherCodeFromPresentation extends Activity {

    private class RegistrationModel {

        private final String mFullName;

        private final String mPassword;

        private final String mUsername;

        public RegistrationModel(final CharSequence username,
                final CharSequence password, final CharSequence fullName) {
            mUsername = username.toString();
            mPassword = password.toString();
            mFullName = fullName.toString();
        }

        public Boolean isValid() {
            return !TextUtils.isEmpty(mUsername)
                    && !TextUtils.isEmpty(mPassword);
        }
    }

    private static final String TAG = OtherCodeFromPresentation.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final Button myButton = (Button) findViewById(R.id.myButton);
        /*myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                triggerAction();
            }
        });*/

        final Observable<Void> myButtonObservable =
                Observable.create(subscriber -> {
                    myButton.setOnClickListener(v -> {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
                        }
                    });
                });

        myButtonObservable
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe(v -> triggerAction());

        RxView.clicks(myButton)
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe(v -> triggerAction());
/*

        final TextView username_tv = (TextView) findViewById(R.id.username);
        final TextView password_tv = (TextView) findViewById(R.id.password);
        final TextView fullName_tv = (TextView) findViewById(R.id.fullName); // optional

        Observable<CharSequence> rxUsername = RxTextView.textChanges(username_tv);
        Observable<CharSequence> rxPassword = RxTextView.textChanges(password_tv);



        Observable<CharSequence> rxFullName = RxTextView.textChanges(fullName_tv)
            .mergeWith(Observable.just(""));

        Observable.combineLatest(
            rxUsername, rxPassword, rxFullName, RegistrationModel::new)
            .filter(RegistrationModel::isValid)
            .subscribe(LoginActivity::enableSubmitButton);
*/


    }

    private void triggerAction() {
        Log.v(TAG, "trigger action");
    }

}
