package fithou.duogwas.myheremap.API;

import java.util.List;

import fithou.duogwas.myheremap.Model.ResponseResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("autosuggest")
    Call<ResponseResult> getAddressSearch(@Query("q") String q,
                                          @Query("apiKey") String apiKey,
                                          @Query("at") String at);
}
