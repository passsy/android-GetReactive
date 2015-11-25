package com.pascalwelsch.getreactive;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.pascalwelsch.getreactive.databinding.ItemRepoBinding;
import com.pascalwelsch.getreactive.retrofit.GitApiInterface;
import com.pascalwelsch.getreactive.retrofit.GitHubResponse;
import com.pascalwelsch.getreactive.retrofit.Repository;
import com.pascalwelsch.getreactive.util.ArrayAdapter;
import com.pascalwelsch.getreactive.util.BindingViewHolder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.HttpException;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by pascalwelsch on 10/24/15.
 */
public class RepoListActivity extends AppCompatActivity {

    private static class RepoItemViewHolder extends BindingViewHolder<ItemRepoBinding> {

        public RepoItemViewHolder(final ItemRepoBinding viewBinding) {
            super(viewBinding);
        }
    }

    private static class RepoAdapter extends ArrayAdapter<Repository, RepoItemViewHolder> {

        public RepoAdapter() {
            super(null);
        }

        @Override
        public void onBindViewHolder(final RepoItemViewHolder holder, final int position) {
            final Repository repo = getItem(position);
            holder.getBinding().setRepo(repo);
        }

        @Override
        public RepoItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new RepoItemViewHolder(ItemRepoBinding.inflate(inflater, parent, false));
        }
    }

    private static final String TAG = RepoListActivity.class.getSimpleName();

    public static final String METHOD_THE_ANDROID_WAY = "METHOD_THE_ANDROID_WAY";

    public static final String METHOD_RX_BEGINNER = "METHOD_RX_BEGINNER";

    public static final String METHOD_RX_EXPERT = "METHOD_RX_EXPERT";

    private static final String METHOD_TYPE = "METHOD_TYPE";

    PublishSubject<CharSequence> mCallSubject = PublishSubject.create();

    private RepoAdapter mAdapter;

    private GitApiInterface mGitApiService;

    private String mMethod;

    private SearchView mSearchView;

    /**
     * @param method one of {@link #METHOD_THE_ANDROID_WAY}, {@link #METHOD_RX_BEGINNER}, {@link
     *               #METHOD_RX_EXPERT}
     */
    public static Intent newInstance(@NonNull final Context context,
            @Nullable final String method) {
        final Intent intent = new Intent(context, RepoListActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putString(METHOD_TYPE, method);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * support for onErrorResumeNext for Single because Single does not support Observable.empty()
     * which is crucial to consume the errors
     */
    public static <T> Observable<T> onErrorResumeNext(final Single<T> single,
            final Func1<Throwable, ? extends Observable<? extends T>> resumeFunction) {
        return single.toObservable().onErrorResumeNext(resumeFunction);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        registerToSearchViewEvents(mSearchView);
        return super.onCreateOptionsMenu(menu);
    }

    public void showRepositories(List<Repository> repositories) {
        mAdapter.swap(repositories);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMethod = getIntent().getStringExtra(METHOD_TYPE);
        if (mMethod == null) {
            throw new IllegalStateException("use #newInstance(Context, String)");
        }

        setContentView(R.layout.repo_list);
        setSupportActionBar(((Toolbar) findViewById(R.id.toolbar)));

        final RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RepoAdapter();
        list.setAdapter(mAdapter);

        final OkHttpClient okClient = new OkHttpClient();
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okClient.interceptors().add(logging);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClient)
                .build();
        mGitApiService = retrofit.create(GitApiInterface.class);
    }

    private void registerToSearchViewEvents(final SearchView searchView) {
        switch (mMethod) {
            case METHOD_THE_ANDROID_WAY:
                theAndroidWay(searchView);
                return;
            case METHOD_RX_BEGINNER:
                theRxBeginner(searchView);
                return;
            case METHOD_RX_EXPERT:
            default:
                theRxExpert(searchView);
        }
    }

    /**
     * search for repos and handle observable errors
     */
    @NonNull
    private Observable<GitHubResponse<Repository>> searchRepositories(
            final CharSequence charSequence) {
        return mGitApiService.searchRepositoriesObservable(charSequence.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(
                        throwable -> {
                            try {
                                throw throwable;
                            } catch (HttpException httpException) {
                                showEmptyErrorView(httpException.message());
                            } catch (Throwable other) {
                                showEmptyErrorView(other.getMessage());
                                other.printStackTrace();
                            }
                            return Observable.empty();
                        });
    }

    /**
     * search for repos using rx.Single for network request an handle errors
     */
    private Observable<GitHubResponse<Repository>> searchRepositoriesSingle(
            final CharSequence charSequence) {
        return onErrorResumeNext(
                mGitApiService.searchRepositoriesSingle(charSequence.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()),
                throwable -> {
                    try {
                        throw throwable;
                    } catch (HttpException httpException) {
                        showEmptyErrorView(httpException.message());
                    } catch (Throwable other) {
                        showEmptyErrorView(other.getMessage());
                        other.printStackTrace();
                    }
                    return Observable.empty();
                });
    }

    private void showEmptyErrorView(final String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * straight forward implementation of a search in plain Java/Android code
     * @param searchView
     */
    private void theAndroidWay(final SearchView searchView) {

        final Callback<GitHubResponse<Repository>> callback
                = new Callback<GitHubResponse<Repository>>() {
            @Override
            public void onFailure(final Throwable t) {
                showEmptyErrorView(t.getMessage());
            }

            @Override
            public void onResponse(
                    final Response<GitHubResponse<Repository>> response,
                    final Retrofit retrofit) {
                if (response.isSuccess()) {
                    showRepositories(response.body().getItems());
                } else {
                    try {
                        showEmptyErrorView(response.errorBody().string());
                    } catch (IOException e) {
                        showEmptyErrorView(response.message());
                    }
                }
            }
        };

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(final String query) {
                if (TextUtils.isEmpty(query)) {
                    return true;
                }
                mGitApiService.searchRepositories(query).enqueue(callback);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(final String query) {
                return false;
            }
        });
    }

    /**
     * working solution until an error occurs. Does not handle backpressure well
     * @param searchView
     */
    private void theRxBeginner(final SearchView searchView) {
        RxSearchView.queryTextChanges(searchView)
                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
                .flatMap(charSequence -> {
                    return mGitApiService.searchRepositoriesObservable(charSequence.toString())
                            .subscribeOn(Schedulers.io());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    showRepositories(response.getItems());
                }, throwable -> {
                    throwable.printStackTrace();
                    showEmptyErrorView(throwable.getMessage());
                    Toast.makeText(RepoListActivity.this,
                            "Completed Observable 'RxSearchView.queryTextChanges(searchView)' "
                                    + "with error! Should never happen",
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * with backpressure and error handling
     * @param searchView
     */
    private void theRxExpert(final SearchView searchView) {
        RxSearchView.queryTextChanges(searchView)
                .skip(1)
                .doOnNext(charSequence -> Log.v(TAG, "searching: " + charSequence))
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .debounce(200, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(charSequence -> {
                    final boolean empty = TextUtils.isEmpty(charSequence);
                    if (empty) {
                        Log.v(TAG, "empty view");
                        mAdapter.clear();
                    }
                    return !empty;
                })
                .concatMap(query -> {
                    Log.v(TAG, "requesting " + query);
                    // with rx.Observable
                    //return searchRepositories(query)

                    // with rx.Single (proof of concept)
                    return searchRepositoriesSingle(query);
                })
                .doOnNext(charSequence -> Log.v(TAG, "got data"))
                .subscribe(response -> {
                    showRepositories(response.getItems());
                }, throwable -> {
                    throwable.printStackTrace();
                    showEmptyErrorView(throwable.getMessage());
                });
    }
}
