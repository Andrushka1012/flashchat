package com.example.andrii.flashchat.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrii.flashchat.R;
import com.example.andrii.flashchat.data.SingletonConnection;
import com.example.andrii.flashchat.data.actions.Action;
import com.example.andrii.flashchat.data.actions.ActionRegister;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterFragment extends Fragment {
    private final String TAG = "RegisterFragment"; 
    private AutoCompleteTextView mName;
    private AutoCompleteTextView mBirthDate;
    private AutoCompleteTextView mEmail;
    private AutoCompleteTextView mNumber;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Switch mSwitch;

    private Button mRegisterButton;
    private TextView mLogin;

    private View mProgressView;
    private View mRegisterFormView;

    public static RegisterFragment newInstance(){
        return new RegisterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register,container,false);
        mName = v.findViewById(R.id.name);
        mBirthDate = v.findViewById(R.id.date_of_birth);
        mEmail = v.findViewById(R.id.email);
        mNumber = v.findViewById(R.id.phone_number);
        mPassword = v.findViewById(R.id.password);
        mConfirmPassword = v.findViewById(R.id.confirm_password);
        mSwitch = v.findViewById(R.id.gender_switch);
        mRegisterButton = v.findViewById(R.id.register_btn);
        mLogin = v.findViewById(R.id.login);
        mProgressView = v.findViewById(R.id.register_progress);
        mRegisterFormView = v.findViewById(R.id.register_layout);


        mRegisterButton.setOnClickListener(view ->{
            if (correctData()){
               String name = mName.getText().toString();
               String date = mBirthDate.getText().toString();
               String email = mEmail.getText().toString();
               String number = mNumber.getText().toString();
               String password = mPassword.getText().toString();
               String gender = mSwitch.isChecked() ? "male":"female";

               showProgress(true);


                ActionRegister action = new ActionRegister(name,date,email,number,password,gender);
                Observable<Action> serverConnectObservable = Observable.just(action);

                Observable<String> observable = Observable.empty();
                observable.mergeWith(serverConnectObservable.observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .map(act -> {
                            SingletonConnection.getInstance().connect();
                            SingletonConnection.getInstance().executeAction(act);

                            BufferedReader in = SingletonConnection.getInstance().getReader();
                            Log.d(TAG,"in = null:" + String.valueOf(in == null));
                            String answer = "";
                            try {
                                answer = in.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return answer;
                        }))
                        .timeout(5, TimeUnit.SECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @SuppressLint("ShowToast")
                            @Override
                            public void onCompleted() {
                                SingletonConnection.getInstance().close();
                                showProgress(false);
                            }

                            @SuppressLint("ShowToast")
                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getActivity(),"Error with connecting to server",Toast.LENGTH_LONG).show();
                                SingletonConnection.getInstance().close();
                                showProgress(false);
                            }

                            @Override
                            public void onNext(String answer) {
                                Log.d(TAG,"OnNext s = " + answer);
                                if (answer.equals("invalid")) Toast.makeText(getActivity(),"Invalid data",Toast.LENGTH_LONG).show();
                                else{
                                    Toast.makeText(getActivity(),"Your account was created",Toast.LENGTH_LONG).show();
                                    getActivity().onBackPressed();
                                }
                            }
                        });
            }
        });
        mLogin.setOnClickListener(view -> getActivity().onBackPressed());

        return v;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        mLogin.setClickable(!show);
    }

    private boolean correctData(){
        boolean valid = true;
        View view = null;
        if (mName.getText().toString().length() < 3){
            valid = false;
            mName.setError("Write correct name");
            view = mName;
        }
        if (!mEmail.getText().toString().contains("@")){
            valid = false;
            mEmail.setError("This email is invalided");
            view = mEmail;
        }
        if (mNumber.getText().toString().length()<8){
            valid = false;
            mNumber.setError("This number is invalided");
            view = mNumber;
        }
        if (mPassword.getText().toString().length() < 6){
            valid = false;
            mPassword.setError("Short password(must be more 6 symbols)");
            view = mPassword;
        }
        if (mConfirmPassword.getText().toString().isEmpty()
                || !mConfirmPassword.getText().toString().equals(mPassword.getText().toString())){
            valid = false;
            mConfirmPassword.setError("Invalid confirming password");
            view = mConfirmPassword;
        }

        if (view != null){
            view.requestFocus();
        }

        return valid;
    }
}
