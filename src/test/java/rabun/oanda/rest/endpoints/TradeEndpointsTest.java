package rabun.oanda.rest.endpoints;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rabun.oanda.rest.base.Endpoint;
import rabun.oanda.rest.models.*;

import java.util.List;

import static org.junit.Assert.*;

public class TradeEndpointsTest {

    public String key;
    public int accountId;
    public TradeEndpoints tradeEndpoints;
    public OrderEndpoints orderEndpoints;

    @Before
    public void setUp() throws Exception {
        accountId = 4905675;
        key = "019042a97b42ae9e1c0501e46cdb80fd-efe1760c46bfb65901418e8107e78827";
        tradeEndpoints = new TradeEndpoints(key, Endpoint.AccountType.practice);
        orderEndpoints = new OrderEndpoints(key, Endpoint.AccountType.practice);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetTrades() throws Exception {
        List<Trade> trades = tradeEndpoints.GetTrades(accountId, null, null, "EUR_USD", null);
        assertNotNull(trades);

    }

    @Test
    public void testGetTrade() throws Exception {
        OrderMarket order = (OrderMarket) orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.market, null, null, null, null, null, null);

        Trade trade = tradeEndpoints.GetTrade(accountId, order.id);
        assertNotNull(trade);

        tradeEndpoints.CloseTrade(accountId,order.id);
    }

    @Test
    public void testUpdateTrade() throws Exception {
        OrderMarket order = (OrderMarket) orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.market, null, null, null, null, null, null);

        Trade trade = tradeEndpoints.UpdateTrade(accountId, order.id, 1.07f, 1.09f, 22);
        assertNotNull(trade);

        tradeEndpoints.CloseTrade(accountId,order.id);
    }

    @Test
    public void testCloseTrade() throws Exception {
        OrderMarket order = (OrderMarket) orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.market, null, null, null, null, null, null);


        TradeClosed trade = tradeEndpoints.CloseTrade(accountId, order.id);
        assertNotNull(trade);
    }
}