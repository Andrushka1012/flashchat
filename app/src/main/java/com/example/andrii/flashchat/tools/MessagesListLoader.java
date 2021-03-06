package com.example.andrii.flashchat.tools;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.andrii.flashchat.data.Model.Person;
import com.example.andrii.flashchat.data.actions.ActionGetOnlinePersonData;
import com.example.andrii.flashchat.fragments.RecyclerViewFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class MessagesListLoader {
    private static String TAG = "MessagesListLoader";
    final Handler handler = new Handler();
    private Runnable runnable;
    private Subscription subscription;
    private boolean loading = false;

    public MessagesListLoader(Context context,Observable<RecyclerViewFragment> fragmentObservable) {

        runnable = () -> {
            if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();

            ActionGetOnlinePersonData action = new ActionGetOnlinePersonData(QueryPreferences.getActiveUserId(context));
            Subscription subscriptionRequest =  QueryAction.executeAnswerQuery(action)
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            setRenew();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG,"OnError:",e);
                            subscription = fragmentObservable.subscribe(recyclerViewFragment ->
                                    recyclerViewFragment.updateUi(null)
                            );
                            setRenew();
                        }

                        @Override
                        public void onNext(String s) {
                            Log.d(TAG,"onNext:" + s);
                            if (!s.equals("error")){
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Person>>(){}.getType();
                                List<Person> onlineList = gson.fromJson(s,listType);
                                subscription = fragmentObservable.subscribe(recyclerViewFragment -> {
                                    recyclerViewFragment.updateUi(onlineList);
                                });
                            }else {
                                onError(new Throwable("Request given answer:error"));
                            }
                        }
                    });
            QueryAction.addSubscription(subscriptionRequest);
        };
    }

    public boolean isLoading() {
        return loading;
    }

    public void startLoading(){
        handler.postDelayed(runnable,3000);
        loading = true;
    }

    public void stopLoading(){
        handler.removeCallbacks(runnable);
        if (subscription != null && !subscription.isUnsubscribed())subscription.unsubscribe();

    }

    private void setRenew(){
        loading = true;
        int delay = 10 * 1000;
        handler.postDelayed(runnable,delay);
    }

}
