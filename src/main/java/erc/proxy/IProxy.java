package erc.proxy;

public interface IProxy{
	public void preInit();
	public void init();
	public void postInit();
	public void registerModels();
}