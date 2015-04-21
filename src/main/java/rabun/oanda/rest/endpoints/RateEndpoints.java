package rabun.oanda.rest.endpoints;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import rabun.oanda.rest.base.Endpoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import rabun.oanda.rest.models.*;
import rabun.oanda.rest.models.OandaTypes.*;

public class RateEndpoints extends Endpoint {

    private final String instrumentsRoute = "/v1/instruments";
    private final String priceRoute = "/v1/prices";
    private final String candleRoute = "/v1/candles";

    public RateEndpoints(String key, AccountType accountType) {
        super(key, accountType);
    }

    public List<Instrument> GetInstruments(int accountId, String fields, String instruments) throws UnirestException {
        List<Instrument> instrumentList = new ArrayList<>();

        String endpoint = makeEndpoint(accountType, instrumentsRoute);

        Map<String, Object> map = new HashMap<>();
        map.put("accountId", accountId);
        if (fields != null)
            map.put("fields", fields);
        if (instruments != null && instruments.length() > 0)
            map.put("instruments", instruments);


        HttpResponse<JsonNode> jsonResponse = this.Get(null, map, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        JSONObject obj = jsonResponse.getBody().getObject();
        JSONArray array = obj.getJSONArray("instruments");


        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Instrument instrument = new Instrument();

            instrument.displayName = object.getString("displayName");
            instrument.instrument = object.getString("instrument");
            instrument.maxTradeUnits = object.getInt("maxTradeUnits");
            instrument.pip = (float) object.getDouble("pip");

            instrumentList.add(instrument);
        }

        return instrumentList;
    }

    public List<Price> GetPrices(String instruments) throws UnirestException {
        List<Price> prices = new ArrayList<>();

        String endpoint = makeEndpoint(accountType, priceRoute);

        Map<String, Object> map = new HashMap<>();
        map.put("instruments", instruments);
        HttpResponse<JsonNode> jsonResponse = this.Get(null, map, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        JSONObject obj = jsonResponse.getBody().getObject();
        JSONArray array = obj.getJSONArray("prices");


        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Price price = new Price();

            price.ask = (float) object.getDouble("ask");
            price.bid = (float) object.getDouble("bid");
            price.instrument = object.getString("instrument");
            price.status = object.optString("status");
            price.time = object.optString("time");

            prices.add(price);
        }

        return prices;

    }

    public Candle GetCandles(String instrument, GranularityType granularity, int count, Date start, Date end,
                             CandleFormat candleFormat, boolean includeFirst, byte dailyAlignment,
                             WeeklyAlignment weeklyAlignment) throws Exception {

        Candle candle = new Candle();
        String endpoint = makeEndpoint(accountType, candleRoute);

        Map<String, Object> fields = makeCandle(instrument, granularity, count, start, end,
                candleFormat, includeFirst, dailyAlignment, weeklyAlignment);

        if (candleFormat != null && candleFormat == CandleFormat.midpoint) {
            candleFormat = CandleFormat.bidask;
        }

        HttpResponse<JsonNode> jsonResponse = this.Get(null, fields, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillCandle(jsonResponse, candleFormat, candle);

        return candle;

    }

    public Candle<CandleMid> GetCandlesMid(String instrument) throws Exception {

        Candle<CandleMid> candle = new Candle<>();

        String endpoint = makeEndpoint(accountType, candleRoute);

        Map<String, Object> fields = makeCandle(instrument, null, null, null, null, CandleFormat.midpoint, null, null, null);

        HttpResponse<JsonNode> jsonResponse = this.Get(null, fields, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillCandle(jsonResponse, CandleFormat.midpoint, candle);

        return candle;

    }

    public Candle<CandleMid> GetCandlesMid(String instrument, GranularityType granularity) throws Exception {

        Candle<CandleMid> candle = new Candle<>();
        String endpoint = makeEndpoint(accountType, candleRoute);

        Map<String, Object> fields = makeCandle(instrument, granularity, null, null, null, CandleFormat.midpoint, null, null, null);

        HttpResponse<JsonNode> jsonResponse = this.Get(null, fields, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillCandle(jsonResponse, CandleFormat.midpoint, candle);

        return candle;

    }

    public Candle<CandleMid> GetCandlesMid(String instrument, GranularityType granularity, int count) throws Exception {

        Candle<CandleMid> candle = new Candle<>();
        String endpoint = makeEndpoint(accountType, candleRoute);

        Map<String, Object> fields = makeCandle(instrument, granularity, count, null, null, CandleFormat.midpoint, null, null, null);

        HttpResponse<JsonNode> jsonResponse = this.Get(null, fields, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillCandle(jsonResponse, CandleFormat.midpoint, candle);

        return candle;

    }

    public Candle<CandleBidAsk> GetCandlesBidAsk(String instrument) throws Exception {

        Candle<CandleBidAsk> candle = new Candle<>();

        String endpoint = makeEndpoint(accountType, candleRoute);

        Map<String, Object> fields = makeCandle(instrument, null, null, null, null, CandleFormat.bidask, null, null, null);

        HttpResponse<JsonNode> jsonResponse = this.Get(null, fields, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillCandle(jsonResponse, CandleFormat.bidask, candle);

        return candle;

    }

    public Candle<CandleBidAsk> GetCandlesBidAsk(String instrument, GranularityType granularityType) throws Exception {

        Candle<CandleBidAsk> candle = new Candle<>();
        String endpoint = makeEndpoint(accountType, candleRoute);

        Map<String, Object> fields = makeCandle(instrument, granularityType, null, null, null, CandleFormat.bidask, null, null, null);

        HttpResponse<JsonNode> jsonResponse = this.Get(null, fields, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillCandle(jsonResponse, CandleFormat.bidask, candle);

        return candle;

    }

    public Candle<CandleBidAsk> GetCandlesBidAsk(String instrument, GranularityType granularityType, int count) throws Exception {

        Candle<CandleBidAsk> candle = new Candle<>();
        String endpoint = makeEndpoint(accountType, candleRoute);

        Map<String, Object> fields = makeCandle(instrument, granularityType, count, null, null, CandleFormat.bidask, null, null, null);

        HttpResponse<JsonNode> jsonResponse = this.Get(null, fields, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillCandle(jsonResponse, CandleFormat.bidask, candle);

        return candle;

    }

    private Map<String, Object> makeCandle(String instrument, GranularityType granularity, Integer count, Date start, Date end,
                                           CandleFormat candleFormat, Boolean includeFirst, Byte dailyAlignment,
                                           WeeklyAlignment weeklyAlignment) throws Exception {

        Map<String, Object> fields = new HashMap<>();

        if (instrument == null || instrument.length() == 0)
            throw new Exception("The instrument param can't be empty or null");

        fields.put("instrument", instrument);

        if (granularity == null)
            granularity = GranularityType.S5;
        fields.put("granularity", granularity.toString());

        if (count == null)
            count = 500;
        fields.put("count", count);

        if (start != null) {
            fields.put("start", start.getTime());
        }

        if (start != null && end != null) {
            fields.put("start", start.getTime());
            fields.put("end", end.getTime());
        }

        if (candleFormat == null)
            throw new Exception("candleFormat does not be null");
        fields.put("candleFormat", candleFormat.toString());

        if (includeFirst == null)
            includeFirst = true;

        if (start != null)
            fields.put("includeFirst", includeFirst);

        if (dailyAlignment == null) {
            dailyAlignment = 22;
        } else if (dailyAlignment > 23)
            throw new Exception("The dailyAlignment must be between 0 and 23");

        fields.put("dailyAlignment", dailyAlignment);

        if (weeklyAlignment == null)
            weeklyAlignment = WeeklyAlignment.Friday;
        fields.put("weeklyAlignment", weeklyAlignment.toString());

        return fields;

    }

    private void fillCandle(HttpResponse<JsonNode> jsonResponse, CandleFormat candleFormat, Candle candle) {
        JSONObject obj = jsonResponse.getBody().getObject();

        candle.granularity = GranularityType.valueOf(obj.getString("granularity"));
        candle.instrument = obj.getString("instrument");

        JSONArray array = obj.getJSONArray("candles");

        if (candleFormat == CandleFormat.bidask) {

            List<CandleBidAsk> candles = candle.candles;

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                CandleBidAsk candleBidAsk = new CandleBidAsk();

                candleBidAsk.closeAsk = (float) object.getDouble("closeAsk");
                candleBidAsk.closeBid = (float) object.getDouble("closeBid");
                candleBidAsk.highAsk = (float) object.getDouble("highAsk");
                candleBidAsk.highBid = (float) object.getDouble("highBid");
                candleBidAsk.lowAsk = (float) object.getDouble("lowAsk");
                candleBidAsk.lowBid = (float) object.getDouble("lowBid");
                candleBidAsk.openAsk = (float) object.getDouble("openAsk");
                candleBidAsk.openBid = (float) object.getDouble("openBid");
                candleBidAsk.complete = object.getBoolean("complete");
                candleBidAsk.time = object.getString("time");
                candleBidAsk.volume = object.getInt("volume");

                candles.add(candleBidAsk);
            }
        } else if (candleFormat == CandleFormat.midpoint) {
            List<CandleMid> candles = candle.candles;
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                CandleMid candleMid = new CandleMid();

                candleMid.closeMid = (float) object.getDouble("closeMid");
                candleMid.highMid = (float) object.getDouble("highMid");
                candleMid.lowMid = (float) object.getDouble("lowMid");
                candleMid.openMid = (float) object.getDouble("openMid");
                candleMid.volume = object.getInt("volume");
                candleMid.time = object.getString("time");
                candleMid.complete = object.getBoolean("complete");

                candles.add(candleMid);
            }
        }

    }

}
