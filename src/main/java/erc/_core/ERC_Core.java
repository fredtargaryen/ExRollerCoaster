/**
 * TODO v0.1
 * Riding the coaster
 * * The coremod erc.rewriteClass.loadingPlugin
 * Advanced models for sushi!
 * Check against 1.7.10 version
 * What was sound 1003
 * TODO v0.2
 * Smoothing makes all rails invisible rip
 * Rail lighting is patchy
 * Coaster spams the server when rails are not connected. Not great but Motty's decision.
 * * Commented out for now (2, ERC_EntityCoaster:745)
 * Coaster looks shocking!!
 * Rails stop rendering when moving far enough away from them and looking away from block
 * Lots of keys and colours not supported in .mtls
 * Movement not terribly smooth; might just be my laptop
 */
package erc._core;

import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
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
	public static final String VERSION = "1.5.1";

	
	//proxy////////////////////////////////////////
	@SidedProxy(clientSide = "erc.proxy.ERC_ClientProxy", serverSide = "erc.proxy.ERC_ServerProxy")
	public static IProxy proxy;
	
	//Blocks/////////////////////////////////////////
	public static Block railBranch = new blockRailBranch();
	public static Block railConst = new blockRailConstVelocity();
	public static Block railDetect = new blockRailDetector();
	public static Block railInvisible = new blockRailInvisible();
	public static Block railNonGravity = new BlockNonGravityRail();
	public static Block railNormal = new blockRailNormal();
	public static Block railRedAccel = new blockRailRedstoneAccelerator();
	
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
	//ITEMBLOCKS
	public static Item ItemRailBranch = new ItemBlock(railBranch);
	public static Item ItemRailConst = new ItemBlock(railConst);
	public static Item ItemRailDetect = new ItemBlock(railDetect);
	public static Item ItemRailInvisible = new ItemBlock(railInvisible);
	public static Item ItemRailNonGravity = new ItemBlock(railNonGravity);
	public static Item ItemRailNormal = new ItemBlock(railNormal);
	public static Item ItemRailRedAccel = new ItemBlock(railRedAccel);
	
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
		GameRegistry.registerTileEntity(TileEntityRailBase.class, new ResourceLocation("erc:tileentityrailbase"));
		GameRegistry.registerTileEntity(TileEntityRailNormal.class, new ResourceLocation("erc:tileentityrail"));
		GameRegistry.registerTileEntity(TileEntityRailRedstoneAccelerator.class, new ResourceLocation("erc:tileentityrailredacc"));
		GameRegistry.registerTileEntity(TileEntityRailConstVelosity.class, new ResourceLocation("erc:tileentityrailconstvel"));
		GameRegistry.registerTileEntity(TileEntityRailDetector.class, new ResourceLocation("erc:tileentityraildetector"));
		GameRegistry.registerTileEntity(TileEntityRailBranch2.class, new ResourceLocation("erc:tileentityrailbranch"));
		GameRegistry.registerTileEntity(TileEntityRailInvisible.class, new ResourceLocation("erc:tileentityinvisible"));
		GameRegistry.registerTileEntity(TileEntityNonGravityRail.class, new ResourceLocation( "erc:tileentitynongravity"));

		proxy.preInit();

		ERC_Logger.info("End preInit");
	}

	
	@EventHandler
	public void Init(FMLInitializationEvent e)
	{
		ERC_Logger.info("Start Init");

		proxy.init();
		
		//Register Entity
		int eid=100;
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":coaster"), ERC_EntityCoaster.class, "erc:coaster", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":coastermono"), ERC_EntityCoasterMonodentate.class, "erc:coaster:mono", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":coasterdouble"), ERC_EntityCoasterDoubleSeat.class, "erc:coaster:double", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":coasterseat"), ERC_EntityCoasterSeat.class, "erc:coaster:seat", eid++, this, 400, 20, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":coasterconnect"), ERC_EntityCoasterConnector.class, "erc:coaster:connect", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":sushi"), entitySUSHI.class, "erc:sushi", eid++, this, 200, 50, true);
		
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
		railNormal
			.setUnlocalizedName("railnormal")
				.setRegistryName("railnormal")
			.setCreativeTab(ERC_Tab);
		
		railRedAccel
			.setUnlocalizedName("railredaccel")
				.setRegistryName("railredaccel")
			.setCreativeTab(ERC_Tab);

		railConst
			.setUnlocalizedName("railconst")
				.setRegistryName("railconst")
			.setCreativeTab(ERC_Tab);

		railDetect
			.setUnlocalizedName("raildetector")
				.setRegistryName("raildetector")
			.setCreativeTab(ERC_Tab);

		railBranch
			.setUnlocalizedName("railbranch")
				.setRegistryName("railbranch")
			.setCreativeTab(ERC_Tab);

		railInvisible
			.setUnlocalizedName("railinvisible")
				.setRegistryName("railinvisible")
			.setCreativeTab(ERC_Tab);

		railNonGravity
            .setUnlocalizedName("railnongravity")
				.setRegistryName("railnongravity")
            .setCreativeTab(ERC_Tab);
	}
	
	private void InitItem_RC()
	{
		ItemBasePipe.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("railpipe")
		.setRegistryName("railpipe");

		ItemWrench.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("wrench_c1")
		.setRegistryName("wrench_c1");
		ItemWrench.setMaxStackSize(1);

		ItemStick.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("itemwrenchplaceblock")
		.setRegistryName("itemwrenchplaceblock");
		ItemStick.setMaxStackSize(1);

		ItemCoaster.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("coaster")
		.setRegistryName("coaster");
		ItemCoaster.setMaxStackSize(10);

		ItemCoasterConnector.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("coasterconnector")
		.setRegistryName("coasterconnector");
		ItemCoasterConnector.setMaxStackSize(10);

		ItemCoasterMono.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("coastermono")
		.setRegistryName("coastermono");
		ItemCoasterMono.setMaxStackSize(10);

		ItemSwitchRail.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("switchrailmodel")
		.setRegistryName("switchrailmodel");
		ItemSwitchRail.setMaxStackSize(1);

		ItemSUSHI.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("sushi")
		.setRegistryName("sushi");

		ItemSmoothAll.setCreativeTab(ERC_Tab)
		.setUnlocalizedName("itemsmoothall")
		.setRegistryName("itemsmoothall");

		//ITEMBLOCKS
		ItemRailBranch
				.setUnlocalizedName("railbranch")
				.setRegistryName("railbranch");
		ItemRailConst
				.setUnlocalizedName("railconst")
				.setRegistryName("railconst");
		ItemRailDetect
				.setUnlocalizedName("raildetector")
				.setRegistryName("raildetector");
		ItemRailInvisible
				.setUnlocalizedName("railinvisible")
				.setRegistryName("railinvisible");
		ItemRailNonGravity
				.setUnlocalizedName("railnongravity")
				.setRegistryName("railnongravity");
		ItemRailNormal
				.setUnlocalizedName("railnormal")
				.setRegistryName("railnormal");
		ItemRailRedAccel
				.setUnlocalizedName("railredaccel")
				.setRegistryName("railredaccel");
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> evt)
	{
		evt.getRegistry().registerAll(railBranch, railConst, railDetect, railInvisible, railNonGravity, railNormal, railRedAccel);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> evt)
	{
		evt.getRegistry().registerAll(ItemBasePipe, ItemWrench, ItemStick, ItemCoaster, ItemCoasterConnector,
				ItemCoasterMono, ItemSwitchRail, ItemSUSHI, ItemSmoothAll, ItemRailBranch, ItemRailConst,
				ItemRailDetect,ItemRailInvisible, ItemRailNonGravity, ItemRailNormal, ItemRailRedAccel);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		proxy.registerModels();
	}
}