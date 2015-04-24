package rabun.oanda.rest.endpoints;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.joda.time.DateTime;
import rabun.oanda.rest.base.Endpoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import rabun.oanda.rest.models.*;
import rabun.oanda.rest.models.OandaTypes.*;

/**
 * RATE ENDPOINTS
 */
public class RateEndpoints extends Endpoint {

    private final String instrumentsRoute = "/v1/instruments";
    private final String priceRoute = "/v1/prices";
    private final String candleRoute = "/v1/candles";

    public RateEndpoints(String key, AccountType accountType) {
        super(key, accountType);
    }

    /**
     * Get an instrument list
     *
     * Get a list of tradeable instruments (currency pairs, CFDs, and commodities) that are available for trading with the account specified.
     *
     * @param accountId Required The account id to fetch the list of tradeable instruments for
     * @param fields Optional An URL encoded (%2C) comma separated list of instrument fields that are to be returned in the response.
     *               The instrument field will be returned regardless of the input to this query parameter.
     *               Please see the Response Parameters section below for a list of valid values.
     * @param instruments Optional An URL encoded (%2C) comma separated list of instruments that are to be returned in the response.
     *                    If the instruments option is not specified, all instruments will be returned.
     * @return list of instrument
     * @throws UnirestException
     */
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

    /**
     * Get current prices
     *
     * @param instruments Required An URL encoded (%2C) comma separated list of instruments to fetch prices for.
     *                    Values should be one of the available instrument from the /v1/instruments response.
     * @return list of prices
     * @throws UnirestException
     */
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

    /**
     * Retrieve instrument history
     *
     * Get historical information on an instrument
     *
     * @param instrument Required Name of the instrument to retrieve history for. The instrument should be one of the available instrument from the /v1/instruments response
     * @param granularity Optional The time range represented by each candlestick. The value specified will determine the alignment of the first candlestick.
     * @param count Optional The number of candles to return in the response.
     *              This parameter may be ignored by the server depending on the time range provided.
     *              See “Time and Count Semantics” below for a full description.
     *              If not specified, count will default to 500. The maximum acceptable value for count is 5000.
     *              Count should not be specified if both the start and end parameters are also specified.
     * @param start Optional The start timestamp for the range of candles requested. The value specified must be in a valid datetime format.
     * @param end Optional The end timestamp for the range of candles requested. The value specified must be in a valid datetime format.
     * @param candleFormat Optional Candlesticks representation (about candestick representation).
     *                     This can be one of the following: “midpoint” - Midpoint based candlesticks.
     *                     “bidask” - Bid/Ask based candlesticks The default for candleFormat is “bidask” if the candleFormat parameter is not specified.
     * @param includeFirst Optional A boolean field which may be set to “true” or “false”.
     *                     If it is set to “true”, the candlestick covered by the start timestamp will be returned.
     *                     If it is set to “false”, this candlestick will not be returned.
     *                     This field exists so clients may easily ensure that they can poll for all candles more recent than their last received candle.
     *                     The default for includeFirst is “true” if the includeFirst parameter is not specified.
     * @param dailyAlignment Optional The hour of day used to align candles with hourly, daily, weekly, or monthly granularity.
     *                       The value specified is interpretted as an hour in the timezone set through the
     *                       alignmentTimezone parameter and must be an integer between 0 and 23.
     *                        The default for dailyAlignment is 21 when Eastern Daylight Time is in effect and 22 when
     *                        Eastern Standard Time is in effect. This corresponds to 17:00 local time in New York.
     * @param weeklyAlignment Optional The timezone to be used for the dailyAlignment parameter.
     *                        This parameter does NOT affect the returned timestamp, the start or end parameters, these will always be in UTC.
     *                        The timezone format used is defined by the http://en.wikipedia.org/wiki/Tz_database, a full list of the
     *                        timezones supported by the REST API can be found http://developer.oanda.com/docs/timezones.txt.
     * @return candle
     * @throws Exception
     */
    public Candle GetCandles(String instrument, GranularityType granularity, int count, DateTime start, DateTime end,
                             CandleFormat candleFormat, Boolean includeFirst, Byte dailyAlignment,
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

    /**
     * Retrieve instrument history
     *
     * Get historical information on an instrument
     *
     * @param instrument Required Name of the instrument to retrieve history for. The instrument should be one of the available instrument from the /v1/instruments response
     * @return candle of CandleMid
     * @throws Exception
     */
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

    /**
     * Retrieve instrument history
     *
     * Get historical information on an instrument
     *
     * @param instrument Required Name of the instrument to retrieve history for. The instrument should be one of the available instrument from the /v1/instruments response
     * @param granularity Optional The time range represented by each candlestick. The value specified will determine the alignment of the first candlestick.
     * @return list of CandleMid
     * @throws Exception
     */
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

    /**
     * Retrieve instrument history
     *
     * Get historical information on an instrument
     *
     * @param instrument Required Name of the instrument to retrieve history for. The instrument should be one of the available instrument from the /v1/instruments response
     * @param granularity Optional The time range represented by each candlestick. The value specified will determine the alignment of the first candlestick.
     * @param count Optional The number of candles to return in the response.
     *              This parameter may be ignored by the server depending on the time range provided.
     *              See “Time and Count Semantics” below for a full description.
     *              If not specified, count will default to 500. The maximum acceptable value for count is 5000.
     *              Count should not be specified if both the start and end parameters are also specified.
     * @return list of CandleMid
     * @throws Exception
     */
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

    /**
     * Retrieve instrument history
     *
     * Get historical information on an instrument
     *
     * @param instrument Required Name of the instrument to retrieve history for. The instrument should be one of the available instrument from the /v1/instruments response
     * @return list of CandleBidAsk
     * @throws Exception
     */
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

    /**
     * Retrieve instrument history
     *
     * Get historical information on an instrument
     *
     * @param instrument Required Name of the instrument to retrieve history for. The instrument should be one of the available instrument from the /v1/instruments response
     * @param granularity Optional The time range represented by each candlestick. The value specified will determine the alignment of the first candlestick.
     * @return list of CandleBidAsk
     * @throws Exception
     */
    public Candle<CandleBidAsk> GetCandlesBidAsk(String instrument, GranularityType granularity) throws Exception {

        Candle<CandleBidAsk> candle = new Candle<>();
        String endpoint = makeEndpoint(accountType, candleRoute);

        Map<String, Object> fields = makeCandle(instrument, granularity, null, null, null, CandleFormat.bidask, null, null, null);

        HttpResponse<JsonNode> jsonResponse = this.Get(null, fields, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillCandle(jsonResponse, CandleFormat.bidask, candle);

        return candle;

    }

    /**
     * Retrieve instrument history
     *
     * Get historical information on an instrument
     *
     * @param instrument Required Name of the instrument to retrieve history for. The instrument should be one of the available instrument from the /v1/instruments response
     * @param granularity Optional The time range represented by each candlestick. The value specified will determine the alignment of the first candlestick.
     * @param count Optional The number of candles to return in the response.
     *              This parameter may be ignored by the server depending on the time range provided.
     *              See “Time and Count Semantics” below for a full description.
     *              If not specified, count will default to 500. The maximum acceptable value for count is 5000.
     *              Count should not be specified if both the start and end parameters are also specified.
     * @return list of CandleBidAsk
     * @throws Exception
     */
    public Candle<CandleBidAsk> GetCandlesBidAsk(String instrument, GranularityType granularity, int count) throws Exception {

        Candle<CandleBidAsk> candle = new Candle<>();
        String endpoint = makeEndpoint(accountType, candleRoute);

        Map<String, Object> fields = makeCandle(instrument, granularity, count, null, null, CandleFormat.bidask, null, null, null);

        HttpResponse<JsonNode> jsonResponse = this.Get(null, fields, endpoint);

        if (jsonResponse.getCode() != 200) {
            throw new UnirestException(jsonResponse.getBody().toString());
        }

        fillCandle(jsonResponse, CandleFormat.bidask, candle);

        return candle;

    }

    private Map<String, Object> makeCandle(String instrument, GranularityType granularity, Integer count, DateTime start, DateTime end,
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
            fields.put("start", start.toString());
        }

        if (start != null && end != null) {
            fields.put("start", start.toString());
            fields.put("end", end.toString());
        }

        if (candleFormat != null)
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

            List candles = candle.candles;

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
