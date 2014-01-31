package sdp.strategy.test;

import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import sdp.communication.CommsClient;
import sdp.strategy.CommandHelper;
import sdp.vision.Vision;
import sdp.vision.WorldState;

public class CommandHelperTest {

	@Test
	public void moveTest1() throws IOException, InterruptedException{
		WorldState ws = mock(WorldState.class);
		CommandHelper underTest = new CommandHelper(mock(Vision.class), mock(CommsClient.class), ws);
		
		when(ws.getOurOrientation()).thenReturn((double) 0);
		when(ws.getOurX()).thenReturn((int) 100);
		when(ws.getOurY()).thenReturn((int) 100);
		
		assertEquals(0, underTest.ourAngleTo(120, 100), 0.0);
	}
	
	@Test
	public void moveTest2() throws IOException, InterruptedException{
		WorldState ws = mock(WorldState.class);
		CommandHelper underTest = new CommandHelper(mock(Vision.class), mock(CommsClient.class), ws);
		
		when(ws.getOurOrientation()).thenReturn((double) Math.PI/2);
		when(ws.getOurX()).thenReturn((int) 100);
		when(ws.getOurY()).thenReturn((int) 100);
		
		assertEquals(-Math.PI/2, underTest.ourAngleTo(120, 100), 0.1);
	}
	
	@Test
	public void moveTest3() throws IOException, InterruptedException{
		WorldState ws = mock(WorldState.class);
		CommandHelper underTest = new CommandHelper(mock(Vision.class), mock(CommsClient.class), ws);
		
		when(ws.getOurOrientation()).thenReturn((double) 3*Math.PI/2);
		when(ws.getOurX()).thenReturn((int) 100);
		when(ws.getOurY()).thenReturn((int) 100);
		
		assertEquals(Math.PI/2, underTest.ourAngleTo(120, 100), 0.1);
	}
	
	@Test
	public void moveTest4() throws IOException, InterruptedException{
		WorldState ws = mock(WorldState.class);
		CommandHelper underTest = new CommandHelper(mock(Vision.class), mock(CommsClient.class), ws);
		
		when(ws.getOurOrientation()).thenReturn((double) 0);
		when(ws.getOurX()).thenReturn((int) 238);
		when(ws.getOurY()).thenReturn((int) 228);		
		
		assertEquals(0, underTest.ourAngleTo(522, 254), 0.1);
	}
	
	@Test
	public void moveTest5() throws IOException, InterruptedException{
		WorldState ws = mock(WorldState.class);
		CommandHelper underTest = new CommandHelper(mock(Vision.class), mock(CommsClient.class), ws);
		
		when(ws.getOurOrientation()).thenReturn((double) 6.2831854820251465);
		when(ws.getOurX()).thenReturn((int) 162);
		when(ws.getOurY()).thenReturn((int) 225);		
		
		assertEquals(0, underTest.ourAngleTo(522, 254), 0.1);
	}
	//0.8026975
	
	
}
