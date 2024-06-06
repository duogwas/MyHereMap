package fithou.duogwas.myheremap.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fithou.duogwas.myheremap.API.ApiService;
import fithou.duogwas.myheremap.API.ApiUtils;
import fithou.duogwas.myheremap.Adapter.SearchResultAdapter;
import fithou.duogwas.myheremap.Model.ResponseResult;
import fithou.duogwas.myheremap.Model.SearchResult;
import fithou.duogwas.myheremap.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LocationListener {
    SearchView searchView;
    RecyclerView rcvSearchResult;
    LocationManager locationManager;
    Double latitude, longitude;
    public static final String apiKey = "823tLZoHjBu6zAgxcS0_RRKDaXY7s-uz5RLC1KGgeHo";
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private long DEBOUNCE_TIME = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initView();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        getLocation();
    }

    private void initView() {
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        rcvSearchResult = findViewById(R.id.rcvSearchResult);
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchAddress(String keyWord) {
        String latlng = latitude + "," + longitude;
        ApiService apiService = ApiUtils.getApiService();
        Call<ResponseResult> call = apiService.getAddressSearch(keyWord, apiKey, latlng);
        call.enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                if (response.isSuccessful()) {
                    ResponseResult results = response.body();
                    List<SearchResult> search = results.getItems();
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    rcvSearchResult.setLayoutManager(linearLayoutManager);
                    SearchResultAdapter searchResultAdapter = new SearchResultAdapter(search, MainActivity.this, keyWord);
                    rcvSearchResult.setAdapter(searchResultAdapter);
                } else {
                    Log.e("notSuccessful", response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() == 0) {
            rcvSearchResult.setVisibility(View.GONE);
        } else {
            rcvSearchResult.setVisibility(View.VISIBLE);
            searchAddress(query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchHandler.removeCallbacks(searchRunnable);
        searchRunnable = new Runnable() {
            @Override
            public void run() {
                searchAddress(newText);
            }
        };
        searchHandler.postDelayed(searchRunnable, DEBOUNCE_TIME);
        return false;
    }
}