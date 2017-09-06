package com.example.drhoom.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    private static final int BOOK_LOADER_ID = 1;

    private BookAdapter mAdapter;

    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        ListView bookListView = (ListView) findViewById(R.id.list);
        bookListView.setEmptyView(mEmptyStateTextView);
        bookListView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            String searchText = savedInstanceState.getString("SearchText");

            if (!searchText.isEmpty()) {
                View loadingIndicator = findViewById(R.id.loading_indicator);
                GetInfo(loadingIndicator, searchText);
            }
        }

        Button searchBtn = (Button) findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.destroyLoader(BOOK_LOADER_ID);
                mEmptyStateTextView.setText("");
                TextView searchTxtView = (TextView) findViewById(R.id.search_text);
                String searchTxt = searchTxtView.getText().toString();
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                GetInfo(loadingIndicator, searchTxt);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        TextView searchTxtView = (TextView) findViewById(R.id.search_text);
        String searchTxt = searchTxtView.getText().toString();
        savedInstanceState.putString("SearchText", searchTxt);
    }

    private void GetInfo(View loadingIndicator, String searchTxt) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            Bundle args = new Bundle();
            args.putString("searchQuery", searchTxt);
            loaderManager.initLoader(BOOK_LOADER_ID, args, MainActivity.this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        String searchQuery = args.getString("searchQuery");
        String searchURL = BOOKS_REQUEST_URL + searchQuery;
        return new BookLoader(this, searchURL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_books);
        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }
}
