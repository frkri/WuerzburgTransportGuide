package netzplan;

public class NetzplanApi {
  private Retrofit retrofit;
  private String baseApiUrl = "https://netzplan.vvm-info.de/api/";

  public NetzplanApi () {
    retrofit = new Retrofit.builder()
        .baseUrl(baseApiUrl)
        .build();
  }
  public void forceRefreshToken() {
    // TODO:
    // Fetch API, get xsrfToken from cookie
  }

}