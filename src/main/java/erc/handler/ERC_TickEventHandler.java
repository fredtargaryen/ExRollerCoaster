package erc.handler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ERC_TickEventHandler {
	
	private static int tickcounter = 0;
	@SubscribeEvent
	public void onTickEvent(TickEvent.ServerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START) 
		{
			setTickcounter(getTickcounter() + 1);
//			ERC_ManagerPrevTickCoasterSeatSetPos_server.update();
		}
		if (event.phase == TickEvent.Phase.END)
		{
			
		}
	}

	public static int getTickcounter() {
		return tickcounter;
	}

	public static void setTickcounter(int tickcounter) {
		ERC_TickEventHandler.tickcounter = tickcounter;
	}
	
}
