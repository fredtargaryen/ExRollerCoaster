package erc.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;

public abstract class Wrap_RailRenderer extends ModelBase{
	
	public abstract void setModelNum(int PosNum_org); // ���[���\���̍ŏ���1��Ă�
	public abstract void construct(int idx, Vec3d Pos, Vec3d Dir, Vec3d Cross, float exParam);
	public abstract void render(Tessellator tess);
}
