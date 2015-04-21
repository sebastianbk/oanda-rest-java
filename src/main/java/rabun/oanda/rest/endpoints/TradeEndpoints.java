package rabun.oanda.rest.endpoints;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import rabun.oanda.rest.base.Endpoint;
import rabun.oanda.rest.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeEndpoints extends Endpoint {

    private final String tradesRoute = "/v1/accounts/{account_id}/trades";
    private final String tradeRoute = "/v1/accounts/{account_id}/trades/{trade_id}";

    public TradeEndpoints(String key, AccountType accountType) {
        super(key, accountType);
    }

    public List<Trade> GetTrades(int accountId, Integer maxId, Integer count, String instrument, String ids) throws UnirestException {

        String endpoint = makeEndpoint(accountType, tradesRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));

        Map<String, Object> fields = new HashMap<>();
        if (maxId != null)
            fields.put("maxId", maxId);
        if (count != null)
            fields.put("count", count);
        if (instrument != null)
            fields.put("instrument", instrument);
        if (ids != null)
            fields.put("ids", ids);

        HttpResponse<JsonNode> response = this.Get(routeParams, fields, endpoint);

        return fillTrades(response);
    }

    public Trade GetTrade(int accountId, int tradeId) throws UnirestException {

        String endpoint = makeEndpoint(accountType, tradeRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("trade_id", String.valueOf(tradeId));

        HttpResponse<JsonNode> response = this.Get(routeParams, null, endpoint);

        return fillTrade(response);
    }

    public Trade UpdateTrade(int accountId, int tradeId, Float stopLoss, Float takeProfit, Integer trailingStop) throws UnirestException {
        String endpoint = makeEndpoint(accountType, tradeRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("trade_id", String.valueOf(tradeId));

        Map<String, Object> fields = new HashMap<>();
        fields.put("stopLoss", stopLoss);
        fields.put("takeProfit", takeProfit);
        fields.put("trailingStop", trailingStop);

        HttpResponse<JsonNode> response = this.Patch(routeParams,fields, endpoint);

        return fillTrade(response);
    }

    public TradeClosed CloseTrade(int accountId, int tradeId) throws UnirestException {

        String endpoint = makeEndpoint(accountType, tradeRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("trade_id", String.valueOf(tradeId));

        HttpResponse<JsonNode> response = this.Delete(routeParams, endpoint);

        return fillTradeClosed(response);
    }

    private List<Trade> fillTrades(HttpResponse<JsonNode> response) {
        JSONObject object = response.getBody().getObject();
        JSONArray array = object.getJSONArray("trades");

        List<Trade> trades = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Trade trade = new Trade();

            trade.id = obj.getInt("id");
            trade.units = obj.getInt("units");
            trade.side = OandaTypes.Side.valueOf(obj.getString("side"));
            trade.instrument = obj.getString("instrument");
            trade.time = obj.getString("time");
            trade.price = (float) obj.getDouble("price");
            trade.takeProfit = (float) obj.getDouble("takeProfit");
            trade.stopLoss = (float) obj.getDouble("stopLoss");
            trade.trailingStop = (float) obj.getDouble("trailingStop");
            trade.trailingAmount = (float) obj.getDouble("trailingAmount");

            trades.add(trade);
        }

        return trades;
    }

    private Trade fillTrade(HttpResponse<JsonNode> response) {
        JSONObject object = response.getBody().getObject();
        Trade trade = new Trade();

        trade.id = object.getInt("id");
        trade.units = object.getInt("units");
        trade.side = OandaTypes.Side.valueOf(object.getString("side"));
        trade.instrument = object.getString("instrument");
        trade.time = object.getString("time");
        trade.price = (float) object.getDouble("price");
        trade.takeProfit = (float) object.getDouble("takeProfit");
        trade.stopLoss = (float) object.getDouble("stopLoss");
        trade.trailingStop = (float) object.getDouble("trailingStop");
        trade.trailingAmount = (float) object.getDouble("trailingAmount");

        return trade;
    }

    private TradeClosed fillTradeClosed(HttpResponse<JsonNode> response){
        JSONObject object = response.getBody().getObject();
        TradeClosed trade = new TradeClosed();

        trade.id = object.getInt("id");
        trade.instrument = object.getString("instrument");
        trade.price = (float) object.getDouble("price");
        trade.profit = (float) object.getDouble("profit");
        trade.side = OandaTypes.Side.valueOf(object.getString("side"));
        trade.time = object.getString("time");

        return trade;
    }

}
