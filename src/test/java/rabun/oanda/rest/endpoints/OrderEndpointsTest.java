package rabun.oanda.rest.endpoints;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import rabun.oanda.rest.base.Endpoint;
import rabun.oanda.rest.models.*;

import java.util.List;

import static org.junit.Assert.*;

public class OrderEndpointsTest {

    public String key;
    public OrderEndpoints orderEndpoints;
    public int accountId;

    @Before
    public void Init() {
        accountId = 4905675;
        key = "019042a97b42ae9e1c0501e46cdb80fd-efe1760c46bfb65901418e8107e78827";
        orderEndpoints = new OrderEndpoints(key, Endpoint.AccountType.practice);
    }

    @Test
    public void testGetOrders() throws Exception {
        List<Order> orders = orderEndpoints.GetOrders(accountId, null, null, "EUR_USD", null);
        assertNotNull(orders);
        assertTrue(orders.size() >= 0);
    }

    @Test
    public void testGetOrders1() throws Exception {
        List<Order> orders = orderEndpoints.GetOrders(accountId);
        assertNotNull(orders);
        assertTrue(orders.size() >= 0);
    }

    @Test
    public void testCreateOrderMarketIfTouched() throws Exception {
        DateTime d = new DateTime(1429696889000L, DateTimeZone.UTC);
        OrderMarketIfTouched order = (OrderMarketIfTouched)orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.marketIfTouched, d, 1.09f, 1.06f, 1.08f, null, null);

        assertNotNull(order);
    }

    @Test
    public void testGetOrder() throws Exception {
        Order order = orderEndpoints.GetOrder(accountId,111);
        assertNotNull(order);
    }

    @Test
    public void testUpdateOrder() throws Exception {

    }

    @Test
    public void testCloseOrder() throws Exception {

    }
}