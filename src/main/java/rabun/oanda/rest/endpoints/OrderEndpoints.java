package rabun.oanda.rest.endpoints;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import rabun.oanda.rest.base.Endpoint;
import rabun.oanda.rest.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class OrderEndpoints extends Endpoint {

    private final String ordersRoute = "/v1/accounts/{account_id}/orders";
    private final String orderRoute = "/v1/accounts/{account_id}/orders/{order_id}";

    public OrderEndpoints(String key, AccountType accountType) {
        super(key, accountType);
    }

    public List<Order> GetOrders(int accountId, Integer maxId, Integer count, String instrument, String ids) throws UnirestException {
        List<Order> orders = new ArrayList<>();

        String endpoint = makeEndpoint(accountType, ordersRoute);

        Map<String, Object> fields = new HashMap<>();

        if (maxId != null)
            fields.put("maxId", maxId);

        if (count != null)
            fields.put("count", count);

        if (instrument != null)
            fields.put("instrument", instrument);

        if (ids != null)
            fields.put("ids", ids);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        HttpResponse<JsonNode> jsonResponse = this.Get(routeParams, fields, endpoint);


        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillOrders(orders, jsonResponse);

        return orders;
    }

    public List<Order> GetOrders(int accountId) throws UnirestException {
        List<Order> orders = new ArrayList<>();

        String endpoint = makeEndpoint(accountType, ordersRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        HttpResponse<JsonNode> jsonResponse = this.Get(routeParams, null, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillOrders(orders, jsonResponse);


        return orders;
    }

    public Order CreateOrder(int accountId, String instrument, int units, OandaTypes.Side side, OandaTypes.OrderType type,
                             Date expiry, Float price, Float lowerBound, Float upperBound,
                             Integer takeProfit, Integer trailingStop) throws UnirestException {

        String endpoint = makeEndpoint(accountType, ordersRoute);

        Map<String, Object> fields = new HashMap<>();

        fields.put("instrument", instrument);
        fields.put("units", units);
        fields.put("side", side.toString());
        fields.put("type", type.toString());

        if (type != OandaTypes.OrderType.market) {
            fields.put("expiry", expiry);
            fields.put("price", price);
        }

        if (lowerBound != null)
            fields.put("lowerBound", lowerBound);
        if (upperBound != null)
            fields.put("upperBound", upperBound);
        if (takeProfit != null)
            fields.put("takeProfit", takeProfit);
        if (trailingStop != null)
            fields.put("trailingStop", trailingStop);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        HttpResponse<JsonNode> jsonResponse = this.Get(routeParams, fields, endpoint);

        return fillOrder(jsonResponse);
    }

    public Order GetOrder(int accountId, int orderId) throws UnirestException {

        String endpoint = makeEndpoint(accountType, orderRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("order_id", String.valueOf(orderId));

        HttpResponse<JsonNode> response = this.Get(routeParams, null, endpoint);

        return fillOrder(response);
    }

    public Order UpdateOrder(int accountId, int orderId, Integer units, Float price, String expiry,
                             Float lowerBound, Float upperBound, Float stopLoss, Float takeProfit,
                             Integer trailingStop) throws UnirestException {

        String endpoint = makeEndpoint(accountType, orderRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("order_id", String.valueOf(orderId));

        Map<String, Object> fields = new HashMap<>();

        if (units != null)
            fields.put("units", units);
        if (price != null)
            fields.put("price", price);
        if (expiry != null)
            fields.put("expiry", expiry);
        if (lowerBound != null)
            fields.put("lowerBound", lowerBound);
        if (upperBound != null)
            fields.put("upperBound", upperBound);
        if (stopLoss != null)
            fields.put("stopLoss", stopLoss);
        if (takeProfit != null)
            fields.put("takeProfit", takeProfit);
        if (trailingStop != null)
            fields.put("trailingStop", trailingStop);

        HttpResponse<JsonNode> response = this.Get(routeParams, fields, endpoint);

        return this.fillOrder(response);
    }

    public OrderClosed CloseOrder(int accountId, int orderId) throws UnirestException {
        String endpoint = makeEndpoint(accountType, orderRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("order_id", String.valueOf(orderId));

        HttpResponse<JsonNode> response = this.Delete(routeParams, endpoint);

        return fillOrderClosed(response);

    }

    private void fillOrders(List<Order> orders, HttpResponse<JsonNode> jsonResponse) {
        JSONObject jsonResult = jsonResponse.getBody().getObject();
        JSONArray array = jsonResult.getJSONArray("orders");

        if (array.length() == 0) return;

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            OandaTypes.OrderType orderType = OandaTypes.OrderType.valueOf(object.getString("type"));
            if (orderType == OandaTypes.OrderType.market) {

                OrderMarket order = new OrderMarket();

                order.id = object.getInt("id");
                order.instrument = object.getString("instrument");
                order.price = (float) object.getDouble("price");
                order.side = OandaTypes.Side.valueOf(object.getString("side"));
                order.stopLoss = (float) object.getDouble("stopLoss");
                order.takeProfit = (float) object.getDouble("takeProfit");
                order.trailingStop = (float) object.getDouble("trailingStop");
                order.type = OandaTypes.OrderType.valueOf(object.getString("type"));
                order.units = object.getInt("units");
                order.time = object.getString("time");

                orders.add(order);
            } else {

                OrderMarketIfTouched order = new OrderMarketIfTouched();

                order.id = object.getInt("id");
                order.expiry = object.getString("expiry");
                order.instrument = object.getString("instrument");
                order.lowerBound = (float) object.getDouble("lowerBound");
                order.price = (float) object.getDouble("price");
                order.side = OandaTypes.Side.valueOf(object.getString("side"));
                order.stopLoss = (float) object.getDouble("stopLoss");
                order.takeProfit = (float) object.getDouble("takeProfit");
                order.trailingStop = (float) object.getDouble("trailingStop");
                order.type = OandaTypes.OrderType.valueOf(object.getString("type"));
                order.units = object.getInt("units");
                order.upperBound = (float) object.getDouble("upperBound");
                order.time = object.getString("time");

                orders.add(order);
            }
        }
    }

    private Order fillOrder(HttpResponse<JsonNode> jsonResponse) {

        JSONObject jsonResult = jsonResponse.getBody().getObject();

        if (jsonResult.has("tradeOpened") || (jsonResult.has("type")
                && OandaTypes.OrderType.valueOf(jsonResult.getString("type")) == OandaTypes.OrderType.market)) {
            JSONObject oj = jsonResult.getJSONObject("tradeOpened");

            OrderMarket order = new OrderMarket();

            order.instrument = jsonResult.getString("instrument");
            order.price = (float) jsonResult.getDouble("price");
            order.time = jsonResult.getString("time");
            order.id = oj.getInt("Id");
            order.side = OandaTypes.Side.valueOf(oj.getString("side"));
            order.stopLoss = (float) oj.getDouble("stopLoss");
            order.takeProfit = (float) oj.getDouble("takeProfit");
            order.trailingStop = (float) oj.getDouble("trailingStop");
            order.units = oj.getInt("units");

            return order;
        } else {
            JSONObject oj = jsonResult.getJSONObject("orderOpened");

            OrderMarketIfTouched order = new OrderMarketIfTouched();

            order.instrument = jsonResult.getString("instrument");
            order.price = (float) jsonResult.getDouble("price");
            order.time = jsonResult.getString("time");
            order.id = oj.getInt("Id");
            order.side = OandaTypes.Side.valueOf(oj.getString("side"));
            order.stopLoss = (float) oj.getDouble("stopLoss");
            order.takeProfit = (float) oj.getDouble("takeProfit");
            order.trailingStop = (float) oj.getDouble("trailingStop");
            order.units = oj.getInt("units");
            order.expiry = oj.getString("expiry");
            order.lowerBound = oj.getInt("lowerBound");
            order.upperBound = oj.getInt("lowerBound");

            return order;
        }
    }

    private OrderClosed fillOrderClosed(HttpResponse<JsonNode> jsonResponse){
        JSONObject jsonResult = jsonResponse.getBody().getObject();

        OrderClosed orderClosed = new OrderClosed();
        orderClosed.id = jsonResult.getInt("id");
        orderClosed.instrument = jsonResult.getString("instrument");
        orderClosed.price = (float)jsonResult.getDouble("price");
        orderClosed.side = OandaTypes.Side.valueOf(jsonResult.getString("side"));
        orderClosed.time = jsonResult.getString("time");

        return orderClosed;
    }

}
