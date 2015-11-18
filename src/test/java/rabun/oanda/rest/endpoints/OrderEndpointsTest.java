package rabun.oanda.rest.endpoints;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rabun.oanda.rest.base.Endpoint;
import rabun.oanda.rest.models.*;

import java.util.List;

import static org.junit.Assert.*;

public class OrderEndpointsTest {

    public String key;
    public long accountId;
    public OrderEndpoints orderEndpoints;

    @Before
    public void Init() {
        accountId = 5517316;
        key = "YOUR-KEY";
        orderEndpoints = new OrderEndpoints(key, Endpoint.AccountType.practice);
    }

    @Test
    public void testCreateOrderMarketIfTouched() throws Exception {
        DateTime d = new DateTime(1448964000000L, DateTimeZone.UTC);
        OrderMarketIfTouched order = (OrderMarketIfTouched) orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.marketIfTouched, d, 1.09f, 1.06f, 1.08f, null, null);

        assertNotNull(order);
    }

    @Test
    public void testGetOrders() throws Exception {
        DateTime d = new DateTime(1448964000000L, DateTimeZone.UTC);
        OrderMarketIfTouched order = (OrderMarketIfTouched) orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.marketIfTouched, d, 1.09f, 1.06f, 1.08f, null, null);

        assertNotNull(order);

        List<Order> orders = orderEndpoints.GetOrders(accountId, null, null, "EUR_USD", null);
        assertNotNull(orders);
        assertTrue(orders.size() >= 0);
    }

    @Test
    public void testGetOrders1() throws Exception {
        DateTime d = new DateTime(1448964000000L, DateTimeZone.UTC);
        OrderMarketIfTouched order = (OrderMarketIfTouched) orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.marketIfTouched, d, 1.09f, 1.06f, 1.08f, null, null);

        assertNotNull(order);

        List<Order> orders = orderEndpoints.GetOrders(accountId);
        assertNotNull(orders);
        assertTrue(orders.size() >= 0);
    }

    @Test
    public void testGetOrder() throws Exception {
        DateTime d = new DateTime(1448964000000L, DateTimeZone.UTC);
        OrderMarketIfTouched order = (OrderMarketIfTouched) orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.marketIfTouched, d, 1.09f, 1.06f, 1.08f, null, null);

        assertNotNull(order);

        Order o = orderEndpoints.GetOrder(accountId, order.id);
        assertNotNull(o);
    }

    @Test
    public void testUpdateOrder() throws Exception {
        DateTime d = new DateTime(1429804625000L, DateTimeZone.UTC);
        OrderMarketIfTouched order = (OrderMarketIfTouched) orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.marketIfTouched, d, 1.09f, 1.06f, 1.08f, null, null);


        Order upOrder = orderEndpoints.UpdateOrder(accountId, order.id, 200L, 1.12f, null, null, null, null, null, null);
        assertNotNull(upOrder);
    }

    @Test
    @After
    public void testCloseOrder() throws Exception {
        List<Order> orders = orderEndpoints.GetOrders(accountId);

        for (Order order : orders) {
            Order o = orderEndpoints.CloseOrder(accountId, order.id);
            assertNotNull(o);
        }

    }
}