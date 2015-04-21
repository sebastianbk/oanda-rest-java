package rabun.oanda.rest.base;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Endpoint {
    public static enum AccountType {
        practice,
        real
    }

    protected String key;
    protected AccountType accountType;
    protected final String realEndpoint = "https://api-fxtrade.oanda.com";
    protected final String practiceEndpoint = "https://api-fxpractice.oanda.com";

    public Endpoint(String key, AccountType accountType) {
        this.key = key;
        this.accountType = accountType;
    }

    public String GetKey() {
        return this.key;
    }

    public AccountType GetAccountType() {
        return this.accountType;
    }

    protected String makeEndpoint(AccountType accountType, String route) {

        if (accountType == AccountType.practice)
            return String.format("%s%s", practiceEndpoint, route);

        if (accountType == AccountType.real)
            return String.format("%s%s", realEndpoint, route);

        else return null;
    }

    private HttpRequest setRouteParams(String endpoint, HttpRequest request, Map<String, String> routeParams){
        Pattern p = Pattern.compile("\\{\\w+}");
        Matcher matcher = p.matcher(endpoint);

        while(matcher.find()){
            String tmp = matcher.group();
            String spl = tmp.substring(1,tmp.length() -1);

            request.routeParam(spl, routeParams.get(spl));
        }

        return request;
    }

    public HttpResponse<JsonNode> Get(Map<String, String> routeParams, Map<String, Object> fields,
                                      String endpoint) throws UnirestException {

        GetRequest request = Unirest.get(endpoint);
        if (routeParams != null && routeParams.size() > 0)
            this.setRouteParams(endpoint, request, routeParams);


        return request
                .fields(fields)
                .header("Authorization", String.format("Bearer %s", this.key))
                .asJson();
    }

    public HttpResponse<JsonNode> Post(Map<String, String> routeParams, Map<String, Object> fields,
                                      String endpoint) throws UnirestException {

        HttpRequestWithBody request = Unirest.post(endpoint);
        if (routeParams != null && routeParams.size() > 0)
            this.setRouteParams(endpoint, request, routeParams);


        return request
                .header("Authorization", String.format("Bearer %s", this.key))
                .fields(fields)
                .asJson();
    }

    public HttpResponse<JsonNode> Patch(Map<String, String> routeParams, Map<String, Object> fields,
                                       String endpoint) throws UnirestException {

        HttpRequestWithBody request = Unirest.patch(endpoint);
        if (routeParams != null && routeParams.size() > 0)
            this.setRouteParams(endpoint, request, routeParams);


        return request
                .header("Authorization", String.format("Bearer %s", this.key))
                .fields(fields)
                .asJson();
    }

    public HttpResponse<JsonNode> Delete(Map<String, String> routeParams, String endpoint) throws UnirestException {

        HttpRequestWithBody request = Unirest.delete(endpoint);
        if (routeParams != null && routeParams.size() > 0)
            this.setRouteParams(endpoint, request, routeParams);


        return request
                .header("Authorization", String.format("Bearer %s", this.key))
                .asJson();
    }
}
