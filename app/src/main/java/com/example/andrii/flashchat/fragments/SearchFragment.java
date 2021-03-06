package com.example.andrii.flashchat.fragments;

import android.support.v4.app.Fragment;


public class SearchFragment extends Fragment {
    /*private static final String TAG = "SearchFragment";
    public static final String IsSpeechOn_EXTRA = "IsSpeechOn_EXTRA";
    private boolean isSpeechOn = false;
    private Subscription subscription;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView tvEmpty;
    public static SearchFragment newInstance(boolean isSpeechOn){
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IsSpeechOn_EXTRA,isSpeechOn);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        setHasOptionsMenu(true);


        View v = inflater.inflate(R.layout.fragment_sexarch, container, false);
        mProgressBar = v.findViewById(R.id.progress_bar);
        tvEmpty = v.findViewById(R.id.empty_search_view);

        mRecyclerView = v.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<SearchItem> list = new ArrayList<>();
        mRecyclerView.setAdapter(new SearchListAdapter(getActivity(), list));




        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.item_search);

        SearchView searchView = (SearchView)item.getActionView();
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                tvEmpty.setVisibility(s.isEmpty()?View.VISIBLE:View.GONE);
                mRecyclerView.setVisibility(s.isEmpty()?View.GONE:View.VISIBLE);
                if (!s.isEmpty()){
                    showProgress(true);
                    if (subscription != null){
                        if (!subscription.isUnsubscribed()) {
                            subscription.unsubscribe();
                            Log.d(TAG,"UnSubscribe");
                        }
                    }

                    ActionSearch actionSearch = new ActionSearch(s, QueryPreferences.getActiveUserId(getActivity()));
                   Observable<String> observable = QueryAction.executeAnswerQuery(actionSearch);
                   subscription = observable.subscribe(new Observer<String>() {
                       @Override
                       public void onCompleted() {

                           showProgress(false);
                       }

                       @Override
                       public void onError(Throwable e) {
                           Toast.makeText(getActivity(),"Server error", LENGTH_LONG).show();
                           showProgress(false);
                       }

                       @Override
                       public void onNext(String answer) {
                           Log.d(TAG,"Answer:" + answer);
                           if (!answer.equals("error")){
                               Type listType = new TypeToken<List<SearchItem>>(){}.getType();
                               Gson gson = new Gson();
                               List<SearchItem> list = gson.fromJson(answer,listType);

                               mRecyclerView.setAdapter(new SearchListAdapter(getActivity(),list));
                           }else{
                               Toast.makeText(getActivity(),"Server error", LENGTH_LONG).show();
                           }

                       }
                   });

                }

                return true;
            }
        });

    }

    private void showProgress(boolean b) {
        mRecyclerView.setVisibility(b?View.GONE:View.VISIBLE);
        mProgressBar.setVisibility(b?View.VISIBLE:View.GONE);
    }*/
}
