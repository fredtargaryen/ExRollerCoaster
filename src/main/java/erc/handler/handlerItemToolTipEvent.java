package erc.handler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class handlerItemToolTipEvent {
	
	@SubscribeEvent
	public void onRenderItemText(ItemTooltipEvent event)
	{
			event.getToolTip().add(event.getItemStack().getItem().getUnlocalizedName());
	}
}
