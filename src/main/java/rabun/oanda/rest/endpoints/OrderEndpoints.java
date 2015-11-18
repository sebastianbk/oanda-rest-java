package rabun.oanda.rest.endpoints;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.joda.time.DateTime;
import rabun.oanda.rest.base.Endpoint;
import rabun.oanda.rest.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * ORDER ENDPOINTS
 */
public class OrderEndpoints extends Endpoint {

    private final String ordersRoute = "/v1/accounts/{account_id}/orders";
    private final String orderRoute = "/v1/accounts/{account_id}/orders/{order_id}";

    /**
     * @param key Secret access key required to access the api
     * @param accountType Type of account with which you work (training or real)
     */
    public OrderEndpoints(String key, AccountType accountType) {
        super(key, accountType);
    }

    /**
     * This will return all pending orders for an account.
     * Note: pending take profit or stop loss orders are recorded in the open trade object, and will not be returned in this request.
     *
     * Orders can be paginated with the count and maxId parameters.
     * At most, a maximum of 50 orders can be returned in one query.
     * If more orders exist than specified by the given or default count,
     * a URL with maxId set to the next unreturned order will be returned within the Link header.
     *
     * @param accountId account id
     * @param maxId Optional The server will return orders with id less than or equal to this, in descending order (for pagination).
     * @param count Optional Maximum number of open orders to return. Default: 50. Max value: 500.
     * @param instrument Optional Retrieve open orders for a specific instrument only. Default: all.
     * @param ids Optional An URL encoded comma (%2C) separated list of orders to retrieve. Maximum number of ids: 50. No other parameter may be specified with the ids parameter.
     * @return list of orders
     * @throws UnirestException
     */
    public List<Order> GetOrders(long accountId, Long maxId, Long count, String instrument, String ids) throws UnirestException {
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


        if (jsonResponse.getCode() > 299 || jsonResponse.getCode() < 200)
            throw new UnirestException(jsonResponse.getBody().toString());

        fillOrders(orders, jsonResponse);

        return orders;
    }

    /**
     * This will return all pending orders for an account.
     * Note: pending take profit or stop loss orders are recorded in the open trade object, and will not be returned in this request.
     *
     * Orders can be paginated with the count and maxId parameters.
     * At most, a maximum of 50 orders can be returned in one query.
     * If more orders exist than specified by the given or default count,
     * a URL with maxId set to the next unreturned order will be returned within the Link header.
     *
     * @param accountId account id
     * @return list of orders
     * @throws UnirestException
     */
    public List<Order> GetOrders(long accountId) throws UnirestException {
        List<Order> orders = new ArrayList<>();

        String endpoint = makeEndpoint(accountType, ordersRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        HttpResponse<JsonNode> jsonResponse = this.Get(routeParams, null, endpoint);

        if (jsonResponse.getCode() > 299 || jsonResponse.getCode() < 200)
            throw new UnirestException(jsonResponse.getBody().toString());

        fillOrders(orders, jsonResponse);


        return orders;
    }

    /**
     * Create a new order
     *
     * @param accountId account id
     * @param instrument Required Instrument to open the order on
     * @param units Required The number of units to open order for
     * @param side Required Direction of the order, either "buy" or "sell"
     * @param type Required The type of the order "limit", "stop", "marketIfTouched’ or "market"
     * @param expiry Required If order type is "limit", "stop", or "marketIfTouched". The order expiration time in UTC. The value specified must be in a valid datetime format
     * @param price Required If order type is "limit", "stop", or "marketIfTouched". The price where the order is set to trigger at
     * @param lowerBound Optional The minimum execution price
     * @param upperBound Optional The maximum execution price
     * @param takeProfit Optional The take profit price
     * @param trailingStop Optional The trailing stop distance in pips, up to one decimal place
     * @return created order
     * @throws UnirestException
     */
    public Order CreateOrder(long accountId, String instrument, long units, OandaTypes.Side side, OandaTypes.OrderType type,
                             DateTime expiry, Float price, Float lowerBound, Float upperBound,
                             Long takeProfit, Long trailingStop) throws UnirestException {

        String endpoint = makeEndpoint(accountType, ordersRoute);

        Map<String, Object> fields = new HashMap<>();

        fields.put("instrument", instrument);
        fields.put("units", units);
        fields.put("side", side.toString());
        fields.put("type", type.toString());

        if (type != OandaTypes.OrderType.market) {
            String dateString = expiry.toString();

            fields.put("expiry", dateString);
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
        HttpResponse<JsonNode> jsonResponse = this.Post(routeParams,fields,endpoint);

        if (jsonResponse.getCode() > 299 || jsonResponse.getCode() < 200)
            throw new UnirestException(jsonResponse.getBody().toString());

        return fillCreateOrder(jsonResponse);
    }

    /**
     * Get information for an order
     *
     * @param accountId account id
     * @param orderId order id
     * @return order
     * @throws UnirestException
     */
    public Order GetOrder(long accountId, long orderId) throws UnirestException {

        String endpoint = makeEndpoint(accountType, orderRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("order_id", String.valueOf(orderId));

        HttpResponse<JsonNode> jsonResponse = this.Get(routeParams, null, endpoint);

        if (jsonResponse.getCode() > 299 || jsonResponse.getCode() < 200)
            throw new UnirestException(jsonResponse.getBody().toString());

        return fillOrder(jsonResponse);
    }

    /**
     * Modify an existing order
     *
     * @param accountId account id
     * @param orderId order id
     * @param units Optional The number of units to open order for.
     * @param price Optional The price at which the order is set to trigger at.
     * @param expiry Optional The order expiration time in UTC. The value specified must be in a valid datetime format.
     * @param lowerBound Optional The minimum execution price.
     * @param upperBound Optional The maximum execution price.
     * @param stopLoss Optional The stop loss price.
     * @param takeProfit Optional The take profit price.
     * @param trailingStop Optional The trailing stop distance in pips, up to one decimal place.
     * @return order
     * @throws UnirestException
     */
    public Order UpdateOrder(long accountId, long orderId, Long units, Float price, DateTime expiry,
                             Float lowerBound, Float upperBound, Float stopLoss, Float takeProfit,
                             Long trailingStop) throws UnirestException {

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
            fields.put("expiry", expiry.toString());
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

        HttpResponse<JsonNode> jsonResponse = this.Patch(routeParams, fields, endpoint);

        if (jsonResponse.getCode() > 299 || jsonResponse.getCode() < 200)
            throw new UnirestException(jsonResponse.getBody().toString());

        return this.fillOrder(jsonResponse);
    }

    /**
     * Close an order
     *
     * @param accountId account id
     * @param orderId order id
     * @return order
     * @throws UnirestException
     */
    public Order CloseOrder(long accountId, long orderId) throws UnirestException {
        String endpoint = makeEndpoint(accountType, orderRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));
        routeParams.put("order_id", String.valueOf(orderId));

        HttpResponse<JsonNode> jsonResponse = this.Delete(routeParams, endpoint);

        if (jsonResponse.getCode() > 299 || jsonResponse.getCode() < 200)
            throw new UnirestException(jsonResponse.getBody().toString());

        return fillOrderClosed(jsonResponse);

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

                order.id = object.getLong("id");
                order.instrument = object.getString("instrument");
                order.price = (float) object.getDouble("price");
                order.side = OandaTypes.Side.valueOf(object.getString("side"));
                order.stopLoss = (float) object.getDouble("stopLoss");
                order.takeProfit = (float) object.getDouble("takeProfit");
                order.trailingStop = (float) object.getDouble("trailingStop");
                order.type = OandaTypes.OrderType.valueOf(object.getString("type"));
                order.units = object.getLong("units");
                order.time = object.getString("time");

                orders.add(order);
            } else {

                OrderMarketIfTouched order = new OrderMarketIfTouched();

                order.id = object.getLong("id");
                order.expiry = object.getString("expiry");
                order.instrument = object.getString("instrument");
                order.lowerBound = (float) object.getDouble("lowerBound");
                order.price = (float) object.getDouble("price");
                order.side = OandaTypes.Side.valueOf(object.getString("side"));
                order.stopLoss = (float) object.getDouble("stopLoss");
                order.takeProfit = (float) object.getDouble("takeProfit");
                order.trailingStop = (float) object.getDouble("trailingStop");
                order.type = OandaTypes.OrderType.valueOf(object.getString("type"));
                order.units = object.getLong("units");
                order.upperBound = (float) object.getDouble("upperBound");
                order.time = object.getString("time");

                orders.add(order);
            }
        }
    }

    private Order fillCreateOrder(HttpResponse<JsonNode> jsonResponse) {

        JSONObject jsonResult = jsonResponse.getBody().getObject();

        if (jsonResult.has("tradeOpened")) {

            JSONObject object = jsonResult.getJSONObject("tradeOpened");

            OrderMarket order = new OrderMarket();

            order.id = object.getLong("id");
            order.instrument = jsonResult.getString("instrument");
            order.price = (float) jsonResult.getDouble("price");
            order.side = OandaTypes.Side.valueOf(object.getString("side"));
            order.stopLoss = (float) object.getDouble("stopLoss");
            order.takeProfit = (float) object.getDouble("takeProfit");
            order.trailingStop = (float) object.getDouble("trailingStop");
            order.units = object.getLong("units");
            order.time = jsonResult.getString("time");
            order.type = OandaTypes.OrderType.market;
            return order;

        } else {

            JSONObject object = jsonResult.getJSONObject("orderOpened");
            OrderMarketIfTouched order = new OrderMarketIfTouched();


            order.id = object.getLong("id");
            order.expiry = object.getString("expiry");
            order.instrument = jsonResult.getString("instrument");
            order.lowerBound = (float) object.getDouble("lowerBound");
            order.price = (float) jsonResult.getDouble("price");
            order.side = OandaTypes.Side.valueOf(object.getString("side"));
            order.stopLoss = (float) object.getDouble("stopLoss");
            order.takeProfit = (float) object.getDouble("takeProfit");
            order.trailingStop = (float) object.getDouble("trailingStop");
            order.units = object.getLong("units");
            order.upperBound = (float) object.getDouble("upperBound");
            order.time = jsonResult.getString("time");
            order.type = OandaTypes.OrderType.marketIfTouched;

            return order;
        }


    }

    private Order fillOrder(HttpResponse<JsonNode> jsonResponse) {

        JSONObject jsonResult = jsonResponse.getBody().getObject();

        OandaTypes.OrderType type = OandaTypes.OrderType.valueOf(jsonResult.getString("type"));
        if (type == OandaTypes.OrderType.market) {

            OrderMarket order = new OrderMarket();

            order.instrument = jsonResult.getString("instrument");
            order.price = (float) jsonResult.getDouble("price");
            order.time = jsonResult.getString("time");
            order.id = jsonResult.getLong("id");
            order.side = OandaTypes.Side.valueOf(jsonResult.getString("side"));
            order.stopLoss = (float) jsonResult.getDouble("stopLoss");
            order.takeProfit = (float) jsonResult.getDouble("takeProfit");
            order.trailingStop = (float) jsonResult.getDouble("trailingStop");
            order.units = jsonResult.getLong("units");
            order.type = type;

            return order;
        } else {
            OrderMarketIfTouched order = new OrderMarketIfTouched();

            order.instrument = jsonResult.getString("instrument");
            order.price = (float) jsonResult.getDouble("price");
            order.time = jsonResult.getString("time");
            order.id = jsonResult.getLong("id");
            order.side = OandaTypes.Side.valueOf(jsonResult.getString("side"));
            order.stopLoss = (float) jsonResult.getDouble("stopLoss");
            order.takeProfit = (float) jsonResult.getDouble("takeProfit");
            order.trailingStop = (float) jsonResult.getDouble("trailingStop");
            order.units = jsonResult.getLong("units");
            order.expiry = jsonResult.getString("expiry");
            order.lowerBound = jsonResult.getInt("lowerBound");
            order.upperBound = jsonResult.getInt("lowerBound");
            order.type = type;

            return order;
        }
    }

    private Order fillOrderClosed(HttpResponse<JsonNode> jsonResponse){
        JSONObject jsonResult = jsonResponse.getBody().getObject();

        Order orderClosed = new Order();
        orderClosed.id = jsonResult.getLong("id");
        orderClosed.instrument = jsonResult.getString("instrument");
        orderClosed.price = (float)jsonResult.getDouble("price");
        orderClosed.side = OandaTypes.Side.valueOf(jsonResult.getString("side"));
        orderClosed.time = jsonResult.getString("time");

        return orderClosed;
    }

}
