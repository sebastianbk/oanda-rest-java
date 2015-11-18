package rabun.oanda.rest.endpoints;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rabun.oanda.rest.base.Endpoint;
import rabun.oanda.rest.models.*;

import java.util.List;

import static org.junit.Assert.*;

public class RateEndpointsTest {

    public String key;
    public long accountId;
    public RateEndpoints rateEndpoints;

    @Before
    public void setUp() throws Exception {
        accountId = 5517316;
        key = "68845455388b640e79cb2a8da89db3a4-6aa7d276beb23ed544036b802a4bc1c7";
        rateEndpoints = new RateEndpoints(key, Endpoint.AccountType.practice);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetInstruments() throws Exception {
        List<Instrument> instruments = rateEndpoints.GetInstruments(accountId, null, null);
        assertNotNull(instruments);
        assertTrue(instruments.size() > 0);
    }

    @Test
    public void testGetPrices() throws Exception {
        List<Price> prices = rateEndpoints.GetPrices("EUR_USD");
        System.out.println(prices.get(0).bid);
        assertNotNull(prices);
        assertTrue(prices.size() > 0);
    }

    @Test
    public void testGetCandles() throws Exception {
        Candle candle = rateEndpoints.GetCandles("EUR_USD", OandaTypes.GranularityType.D, 100, null, null, null, null, null, null);
        assertNotNull(candle);
    }

    @Test
    public void testGetCandlesMid() throws Exception {
        Candle<CandleMid> candleMidCandle = rateEndpoints.GetCandlesMid("EUR_USD");
        assertNotNull(candleMidCandle);
    }

    @Test
    public void testGetCandlesMid1() throws Exception {
        Candle<CandleMid> candleMidCandle = rateEndpoints.GetCandlesMid("EUR_USD", OandaTypes.GranularityType.M);
        assertNotNull(candleMidCandle);
    }

    //TODO This method crash if set cout more than 500 or less. I don't understand it :)
    @Test
    public void testGetCandlesMid2() throws Exception {
        Candle<CandleMid> candleMidCandle = rateEndpoints.GetCandlesMid("EUR_USD", OandaTypes.GranularityType.M, 500);
        assertNotNull(candleMidCandle);
    }

    @Test
    public void testGetCandlesBidAsk() throws Exception {
        Candle<CandleBidAsk> candleBidAskCandle = rateEndpoints.GetCandlesBidAsk("EUR_USD");
        assertNotNull(candleBidAskCandle);
    }

    @Test
    public void testGetCandlesBidAsk1() throws Exception {
        Candle<CandleBidAsk> candleBidAskCandle = rateEndpoints.GetCandlesBidAsk("EUR_USD", OandaTypes.GranularityType.D);
        assertNotNull(candleBidAskCandle);
    }

    @Test
    public void testGetCandlesBidAsk2() throws Exception {
        Candle<CandleBidAsk> candleBidAskCandle = rateEndpoints.GetCandlesBidAsk("EUR_USD", OandaTypes.GranularityType.D, 1000);
        assertNotNull(candleBidAskCandle);
    }
}