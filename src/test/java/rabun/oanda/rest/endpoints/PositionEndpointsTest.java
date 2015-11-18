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

public class PositionEndpointsTest {

    public String key;
    public OrderEndpoints orderEndpoints;
    public PositionEndpoints positionEndpoints;
    public long accountId;

    @Before
    public void setUp() throws Exception {
        accountId = 5517316;
        key = "68845455388b640e79cb2a8da89db3a4-6aa7d276beb23ed544036b802a4bc1c7";
        orderEndpoints = new OrderEndpoints(key, Endpoint.AccountType.practice);
        positionEndpoints = new PositionEndpoints(key, Endpoint.AccountType.practice);
    }

    @After
    public void tearDown() throws Exception {
        positionEndpoints.ClosePosition(accountId, "EUR_USD");
    }

//    @Test
//    public void testGetPositions() throws Exception {
//        DateTime d = new DateTime(1447840660000L, DateTimeZone.UTC);
//        orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.marketIfTouched, d, 1.09f, 1.06f, 1.08f, null, null);
//
//        List<Position> positions = positionEndpoints.GetPositions(accountId);
//        assertNotNull(positions);
//        assertTrue(positions.size() > 0);
//
//    }

//    @Test
//    public void testGetPosition() throws Exception {
//        DateTime d = new DateTime(1447840660000L, DateTimeZone.UTC);
//        orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.marketIfTouched, d, 1.09f, 1.06f, 1.08f, null, null);
//        Position position = positionEndpoints.GetPosition(accountId, "EUR_USD");
//        assertNotNull(position);
//
//    }

    @Test
    public void testClosePosition() throws Exception {
        DateTime d = new DateTime(1447840660000L, DateTimeZone.UTC);
        orderEndpoints.CreateOrder(accountId, "EUR_USD", 100, OandaTypes.Side.buy, OandaTypes.OrderType.marketIfTouched, d, 1.09f, 1.06f, 1.08f, null, null);
        PositionClosed position = positionEndpoints.ClosePosition(accountId, "EUR_USD");
        assertNotNull(position);
    }
}