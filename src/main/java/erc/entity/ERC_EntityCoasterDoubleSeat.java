package erc.entity;

import erc.tileEntity.TileEntityRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ERC_EntityCoasterDoubleSeat extends ERC_EntityCoaster{

	ERC_EntityCoasterSeat secondseat;

	public ERC_EntityCoasterDoubleSeat(World world)
	{
		super(world);
		this.setSize(1.4f, 0.6f);
	}
	
	public ERC_EntityCoasterDoubleSeat(World world, TileEntityRailBase tile, double x, double y, double z) {
		super(world, tile, x, y, z);
	}
	
	@Override
	public boolean canBeRidden()
    {
        return true; // true : ����
    }
	
	// �E�N���b�N�ŏ�鏈�����ۂ�
    public boolean interactFirst(EntityPlayer player)
    {
    	if(!canBeRidden())return false;
    	Entity THIS = this;
    	Entity riddenbyentity = this.getControllingPassenger();
    	if(riddenbyentity != null)
    	{
    		THIS = secondseat;
    		riddenbyentity = secondseat.getControllingPassenger();
    	}
    	
    	//�@������ɉ�������Ă�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�v���C���[�̒N��������Ă���@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@��蕨�����������̂͏���Ă���l����Ȃ�
        if (riddenbyentity != null && riddenbyentity instanceof EntityPlayer && riddenbyentity != player)
        {
            return true; 
        }
        //����łȂ��Ă�	��������Ă�				�@�����v���C���[����Ȃ��Ȃɂ�������Ă�����
        else if (riddenbyentity != null && riddenbyentity != player)
        {
            return false; 
        }
        else
        {
            if (!this.world.isRemote)
            {
                player.startRiding(THIS); // �悹��
            }

            return true;
        }
    }
    
    @Override
    public void onUpdate()
    {
    	super.onUpdate();
    }

	@Override
	public Entity[] getParts() {
		// ��������Ԃ�Entity���S�ēo�^�����@�Z�J���h�V�[�g�o�^���A�����H
		Entity[] ret = new Entity[1];
		ret[0] = secondseat = new ERC_EntityCoasterSeat(this.world);
		return ret;
	}

	public double getYOffset() {
		return (this.height / 2.0F) - 0.3F;
	}
}
