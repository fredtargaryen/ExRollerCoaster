package erc.handler;

import org.lwjgl.input.Keyboard;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import erc._core.ERC_Reflection;
import erc.item.ERC_ItemSwitchingRailModel;
import erc.item.Wrap_ItemCoaster;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;

public class ERC_InputEventHandler {
	
	Minecraft mc;
	
	int wheelsum;
	
	public ERC_InputEventHandler(Minecraft minecraft)
	{
	    mc = minecraft;
	    wheelsum = 0;
	}
	
	@SubscribeEvent
	public void interceptMouseInput(MouseEvent event)
	{
	    if(Keyboard.isKeyDown(Keyboard.KEY_LMENU))
	    {
	    	wheelsum = event.getDwheel();
	    	ERC_Reflection.setMouseDHweel(0);
	    
		    if(wheelsum != 0)
		    {
				if(mc.player == null)return;
				ItemStack heldItemstack = mc.player.getHeldItemMainhand();
				if(heldItemstack == null)return;
				Item heldItem = heldItemstack.getItem();
				if(heldItem == null)return;
				if(heldItem instanceof Wrap_ItemCoaster)
				{
					((Wrap_ItemCoaster)heldItem).ScrollMouseHweel(wheelsum);
				}
				else if(heldItem instanceof ERC_ItemSwitchingRailModel)
				{
					((ERC_ItemSwitchingRailModel)heldItem).ScrollMouseHweel(wheelsum);
				}
		    }
	    }
	}
}
