package fithou.duogwas.myheremap.API;

public class ApiUtils {
    public static final String BASE_URL = "https://autosuggest.search.hereapi.com/v1/";

    public static ApiService getApiService() {
        return RetrofitClient.getClient(BASE_URL).create(ApiService.class);
    }
}
