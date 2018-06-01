package erc.proxy;

import net.minecraftforge.fml.common.FMLCommonHandler;
import erc._core.ERC_Core;
import erc.handler.ERC_TickEventHandler;

public class ERC_ServerProxy implements IProxy
{
	@Override
	public void preInit()
	{
		ERC_Core.tickEventHandler = new ERC_TickEventHandler();
		FMLCommonHandler.instance().bus().register(ERC_Core.tickEventHandler);
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}

	@Override
	public void registerModels() {

	}
}
