/**
 * TODO
 * What was sound 1003
 * Advanced models for sushi!
 * -FT
 */
package erc._core;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import erc.block.*;
import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;
import erc.entity.ERC_EntityCoasterDoubleSeat;
import erc.entity.ERC_EntityCoasterMonodentate;
import erc.entity.ERC_EntityCoasterSeat;
import erc.entity.entitySUSHI;
import erc.gui.ERC_GUIHandler;
import erc.handler.ERC_TickEventHandler;
import erc.item.ERC_ItemCoaster;
import erc.item.ERC_ItemCoasterConnector;
import erc.item.ERC_ItemCoasterMonodentate;
import erc.item.ERC_ItemSmoothAll;
import erc.item.ERC_ItemSwitchingRailModel;
import erc.item.ERC_ItemWrench;
import erc.item.ERC_ItemWrenchPlaceBlock;
import erc.item.itemSUSHI;
import erc.message.ERC_PacketHandler;
import erc.proxy.IProxy;
import erc.tileEntity.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

@Mod( 
		modid = ERC_Core.MODID, 
		name = "Ex Roller Coaster", 
		version = ERC_Core.VERSION,
		useMetadata = true
		)
@Mod.EventBusSubscriber
public class ERC_Core {
	public static final String MODID = ERC_CONST.DOMAIN;
	public static final String VERSION = "1.41";

	
	//proxy////////////////////////////////////////
	@SidedProxy(clientSide = "erc.proxy.ERC_ClientProxy", serverSide = "erc.proxy.ERC_ServerProxy")
	public static IProxy proxy;
	
	//Blocks/////////////////////////////////////////
	public static Block railNormal = new blockRailNormal();
	public static Block railRedAccel = new blockRailRedstoneAccelerator();
	public static Block railConst = new blockRailConstVelocity();
	public static Block railDetect = new blockRailDetector();
	public static Block railBranch = new blockRailBranch();
	public static Block railInvisible;
	public static Block railNonGravity = new BlockNonGravityRail();
	
	//special block renderer ID
	public static int blockRailRenderId;
//	public static int blockFerrisSupporterRenderID;
	
	// items /////////////////////////////////////////
	public static Item ItemBasePipe = new Item();
	public static Item ItemWrench = new ERC_ItemWrench();
	public static Item ItemCoaster = new ERC_ItemCoaster();
	public static Item ItemCoasterConnector = new ERC_ItemCoasterConnector();
	public static Item ItemCoasterMono = new ERC_ItemCoasterMonodentate();
	public static Item ItemSwitchRail = new ERC_ItemSwitchingRailModel();
	public static Item ItemSUSHI = new itemSUSHI();
	public static Item ItemStick = new ERC_ItemWrenchPlaceBlock();
	public static Item ItemSmoothAll = new ERC_ItemSmoothAll();
	
	//GUI/////////////////////////////////////////
	@Mod.Instance(ERC_Core.MODID)
    public static ERC_Core INSTANCE;
//    public static Item sampleGuiItem;
    public static final int GUIID_RailBase = 0;
//    public static final int GUIID_FerrisConstructor = 1;
//    public static final int GUIID_FerrisBasketConstructor = 2;
//    public static final int GUIID_FerrisCore = 3;
    
	////////////////////////////////////////////////////////////////
	//Creative Tab
	public static ERC_CreateCreativeTab ERC_Tab = new ERC_CreateCreativeTab("ExRC", ItemBasePipe);
	
	////////////////////////////////////////////////////////////////
	// TickEventProxy
	public static ERC_TickEventHandler tickEventHandler = null;
	
	////////////////////////////////////////////////////////////////
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		ERC_Logger.info("Start preInit");

		ERC_PacketHandler.init();

		//Register Items
		InitBlock_RC();
		InitItem_RC();
		
		// Register TileEntity
		GameRegistry.registerTileEntity(TileEntityRailBase.class, "ERC:TileEntityRailBase");
		GameRegistry.registerTileEntity(TileEntityRailNormal.class, "ERC:TileEntityRail");
		GameRegistry.registerTileEntity(TileEntityRailRedstoneAccelerator.class, "ERC:TileEntityRailRedAcc");
		GameRegistry.registerTileEntity(TileEntityRailConstVelosity.class, "ERC:TileEntityRailconstvel");
		GameRegistry.registerTileEntity(TileEntityRailDetector.class, "ERC:TileEntityRailDetector");
		GameRegistry.registerTileEntity(TileEntityRailBranch2.class, "ERC:TileEntityRailBranch");
		GameRegistry.registerTileEntity(TileEntityRailInvisible.class, "ERC:TileEntityInvisible");
		GameRegistry.registerTileEntity(TileEntityNonGravityRail.class, "ERC:TileEntityNonGravity");

		proxy.preInit();

		ERC_Logger.info("End preInit");
	}

	
	@EventHandler
	public void Init(FMLInitializationEvent e)
	{
		ERC_Logger.info("Start Init");

		proxy.init();
		proxy.registerModels();
		
		//Register Entity
		int eid=100;
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":coaster"), ERC_EntityCoaster.class, "erc:coaster", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":coastermono"), ERC_EntityCoasterMonodentate.class, "erc:coaster:mono", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":coasterdouble"), ERC_EntityCoasterDoubleSeat.class, "erc:coaster:double", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":coasterseat"), ERC_EntityCoasterSeat.class, "erc:coaster:seat", eid++, this, 400, 20, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":coasterconnect"), ERC_EntityCoasterConnector.class, "erc:coaster:connect", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":SUSHI"), entitySUSHI.class, "erc:SUSHI", eid++, this, 200, 50, true);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ERC_GUIHandler());
		
		ERC_Logger.info("End Init");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit();
	}
	////////////////////////////////////////////////////////////////

	private void InitBlock_RC()
	{
		railNormal = new blockRailNormal()
			.setUnlocalizedName("railnormal")
			.setRegistryName("railnormal")
			//.setBlockTextureName("iron_block")
			.setCreativeTab(ERC_Tab);
		
		railRedAccel
			.setUnlocalizedName("railRedAccel")
				.setRegistryName("ERC.RailAccel")
			//.setBlockTextureName("redstone_block")
			.setCreativeTab(ERC_Tab);

		railConst
			.setUnlocalizedName("railConstVelocity")
				.setRegistryName("ERC.RailConst")
			//.setBlockTextureName("obsidian")
			.setCreativeTab(ERC_Tab);

		railDetect
			.setUnlocalizedName("railDetector")
				.setRegistryName("ERC.RailDetector")
			//.setBlockTextureName("quartz_block_chiseled_top")
			.setCreativeTab(ERC_Tab);

		railBranch
			.setUnlocalizedName("railBranch")
				.setRegistryName("ERC.RailBranch")
			//.setBlockTextureName("lapis_block")
			.setCreativeTab(ERC_Tab);

		railInvisible = new blockRailInvisible()
			.setUnlocalizedName("railinvisible")
				.setRegistryName("railinvisible")
			//.setBlockTextureName("glass")
			.setCreativeTab(ERC_Tab);

		railNonGravity
            .setUnlocalizedName("railNonGravity")
				.setRegistryName("ERC.RailNonGravity")
            //.setBlockTextureName("portal")
            .setCreativeTab(ERC_Tab);
	}
	
	private void InitItem_RC()
	{
		ItemBasePipe.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("railpipe")
		.setRegistryName("railpipe");

		ItemWrench.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("wrench_c1")
		.setRegistryName("Wrench");
		ItemWrench.setMaxStackSize(1);

		ItemStick.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("wrench_p")
		.setRegistryName("ItemWrenchPlaceBlock");
		ItemStick.setMaxStackSize(1);

		ItemCoaster.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("coaster")
		.setRegistryName("Coaster");
		//ItemCoaster.setTextureName(MODID+":coaster");
		ItemCoaster.setMaxStackSize(10);

		ItemCoasterConnector.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("coasterconnector")
		.setRegistryName("CoasterConnector");
		//ItemCoasterConnector.setTextureName(MODID+":coaster_c");
		ItemCoasterConnector.setMaxStackSize(10);

		ItemCoasterMono.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("coastermono")
		.setRegistryName("CoasterMono");
		//ItemCoasterMono.setTextureName(MODID+":coaster");
		ItemCoasterMono.setMaxStackSize(10);

		ItemSwitchRail.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("switchrailmodel")
		.setRegistryName("SwitchRailModel");
//		ItemSwitchRail.setTextureName(MODID+":switchrail");
		ItemSwitchRail.setMaxStackSize(1);

		ItemSUSHI.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("sushi")
		.setRegistryName("ItemSUSHI");

		ItemSmoothAll.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("itemsmoothall")
		.setRegistryName("ItemSmoothAll");
		//ItemSmoothAll.setTextureName(MODID+":SmoothAll");
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> evt)
	{
		evt.getRegistry().registerAll(railNormal, railRedAccel, railConst, railDetect, railBranch, railInvisible);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> evt)
	{
		evt.getRegistry().registerAll(ItemBasePipe, ItemWrench, ItemStick, ItemCoaster, ItemCoasterConnector,
				ItemCoasterMono, ItemSwitchRail, ItemSUSHI, ItemSmoothAll);
	}
}