package erc.tileEntity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DataTileEntityRail {
	public Vec3d vecPos;
	public Vec3d vecDir;
	public Vec3d vecUp;
	public float fUp;
	public float fDirTwist;
	public float Power;	// 	Power�̂�Base��Next�̊Ԃŋ���
	public int cx, cy, cz; // connection rail ����O���ǂ��Ɍq�����Ă��邩
	
	public DataTileEntityRail()
	{
		vecPos = new Vec3d(0, 0, 0);
		vecDir = new Vec3d(0, 0, 0);
		vecUp = new Vec3d(0, 0, 0);
		fUp = 0;
		fDirTwist = 0;
		Power = 25f;
		cx = cy = cz = -1;
	}
	
	public void SetData(Vec3d pos, Vec3d dir, Vec3d up, float fup, float fdir, float pow, int x, int y, int z)
	{
		vecPos = pos;                      
		vecDir = dir;                    
		vecUp = up;
		fUp = fup;
		fDirTwist = fdir;
		Power = pow;
		cx = x;
		cy = y;
		cz = z;
	}
	
	public void SetData(DataTileEntityRail src)
	{
		vecPos = src.vecPos;                      
		vecDir = src.vecDir;                    
		vecUp = src.vecUp;
		fUp = src.fUp;
		fDirTwist = src.fDirTwist;
		Power = src.Power;
		cx = src.cx;
		cy = src.cy;
		cz = src.cz;
	}
	
	public void SetPos(int x, int y, int z)
	{
		cx = x;
		cy = y;
		cz = z;
	}
	
	public void addFUp(float rot)
	{
		fUp += rot; if(fUp > 1f)fUp=1f; else if(fUp < -1f)fUp=-1f;
	}
	public void addTwist(float rot)
	{
		fDirTwist -= rot; if(fDirTwist > 1f)fDirTwist=1f; else if(fDirTwist < -1f)fDirTwist=-1f;
	}
	public void resetRot()
	{
		fUp = 0;
		fDirTwist = 0;
	}
	
	public Vec3d CalcVec3DIRxPOW(float pow)
	{
		return new Vec3d(
				(vecDir.x+vecUp.x*fUp)*pow,
				(vecDir.y+vecUp.y*fUp)*pow,
				(vecDir.z+vecUp.z*fUp)*pow);
	}
	public Vec3d CalcVec3UpTwist()
	{
		Vec3d vecpitch1 = vecUp.crossProduct(vecDir).normalize();
		return new Vec3d(
				vecUp.x+vecpitch1.x*fDirTwist,
				vecUp.y+vecpitch1.y*fDirTwist,
				vecUp.z+vecpitch1.z*fDirTwist).normalize();
	}
	
	public Wrap_TileEntityRail getConnectionTileRail(World world)
	{
		return (Wrap_TileEntityRail) world.getTileEntity(new BlockPos(cx, cy, cz));
	}
	
}
