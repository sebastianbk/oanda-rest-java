package rabun.oanda.rest.endpoints;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import rabun.oanda.rest.base.Endpoint;
import rabun.oanda.rest.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TRANSACTION ENDPOINTS
 */
public class TransactionEndpoints extends Endpoint {

    private final String transactionsRoute = "/v1/accounts/{account_id}/transactions";

    public TransactionEndpoints(String key, AccountType accountType) {
        super(key, accountType);
    }

    /**
     * Get transaction history
     *
     * @param accountId account id
     * @param maxId Optional The first transaction to get.
     *              The server will return transactions with id less than or equal to this, in descending order.
     * @param minId Optional The last transaction to get.
     *              The server will return transactions with id greater or equal to this, in descending order.
     * @param count Optional The maximum number of transactions to return.
     *              The maximum value that can be specified is 500.
     *              By default, if count is not specified, a maximum of 50 transactions will be fetched.
     *              Note: Transactions requests with the count parameter specified is rate limited to 1 per every 60 seconds.
     * @param instrument Optional Retrieve transactions for a specific instrument only. Default: all
     * @param ids Optional An URL encoded comma (%2C) separated list of transaction ids to retrieve.
     *            Maximum number of ids: 50. No other parameter may be specified with the ids parameter.
     * @return list of trasactions
     * @throws UnirestException
     */
    public List<Transaction> GetTransactions(long accountId, long maxId, long minId, long count,
                                             String instrument, String ids) throws UnirestException {

        String endpoint = makeEndpoint(accountType, transactionsRoute);

        Map<String, String> routeParams = new HashMap<>();
        routeParams.put("account_id", String.valueOf(accountId));

        Map<String, Object> fields = new HashMap<>();
        fields.put("maxId", maxId);
        fields.put("minId", minId);
        fields.put("count", count);
        fields.put("instrument", instrument);
        fields.put("ids", ids);

        HttpResponse<JsonNode> response = this.Get(routeParams, fields, endpoint);
        return fillTransaction(response);
    }

    private List<Transaction> fillTransaction(HttpResponse<JsonNode> response) {
        JSONObject object = response.getBody().getObject();
        JSONArray array = object.getJSONArray("transactions");

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            OandaTypes.TransactionType transactionType = OandaTypes.TransactionType.valueOf(obj.getString("type"));

            switch (transactionType) {
                case MARKET_ORDER_CREATE: {
                    TransactionMarketOrderCreate transaction = new TransactionMarketOrderCreate();
                    transaction.id = obj.getInt("id");
                    transaction.accountId = obj.getInt("accountId");
                    transaction.time = obj.getString("time");
                    transaction.type = transactionType;
                    transaction.instrument = obj.getString("instrument");
                    transaction.units = obj.getInt("units");
                    transaction.side = OandaTypes.Side.valueOf(obj.getString("side"));
                    transaction.price = (float) obj.getDouble("price");
                    transaction.pl = obj.getInt("pl");
                    transaction.interest = (float) obj.getDouble("interest");
                    transaction.accountBalance = (float) obj.getDouble("accountBalance");

                    JSONObject toj = obj.getJSONObject("tradeOpened");
                    transaction.tradeOpened = new TradeOpened();
                    transaction.tradeOpened.id = toj.getInt("id");
                    transaction.tradeOpened.units = toj.getInt("units");

                    transactions.add(transaction);
                }

                case STOP_ORDER_CREATE: {
                    TransactionStopOrderCreate transaction = new TransactionStopOrderCreate();
                    transaction.id = obj.getInt("id");
                    transaction.accountId = obj.getInt("accountId");
                    transaction.time = obj.getString("time");
                    transaction.type = transactionType;
                    transaction.instrument = obj.getString("instrument");
                    transaction.units = obj.getInt("units");
                    transaction.side = OandaTypes.Side.valueOf(obj.getString("side"));
                    transaction.price = (float) obj.getDouble("price");
                    transaction.expiry = obj.getInt("expiry");
                    transaction.reason = OandaTypes.Reason.valueOf(obj.getString("reason"));

                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }


}
