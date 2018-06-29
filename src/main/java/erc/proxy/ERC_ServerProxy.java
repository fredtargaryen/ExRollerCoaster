package erc.proxy;

import net.minecraftforge.common.MinecraftForge;
import erc._core.ERC_Core;
import erc.handler.ERC_TickEventHandler;

public class ERC_ServerProxy implements IProxy
{
	@Override
	public void preInit()
	{
		ERC_Core.tickEventHandler = new ERC_TickEventHandler();
		MinecraftForge.EVENT_BUS.register(ERC_Core.tickEventHandler);
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}

	@Override
	public void registerModels() {

	}
}
