public class netzplanApi {

  private String xsrfToken;
  private String baseUrl;

  public netzplanApi(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getXsrfToken() {
    return xsrfToken;
  }

  public void forceRefreshToken() {
    // TODO:
    // Fetch API, get xsrfToken from cookie
  }

}