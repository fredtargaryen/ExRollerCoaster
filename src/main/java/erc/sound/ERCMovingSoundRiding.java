package erc.sound;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erc.entity.ERC_EntityCoasterSeat;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

@SideOnly(Side.CLIENT)
public class ERCMovingSoundRiding extends MovingSound {
	private final EntityPlayer player;
	private final ERC_EntityCoasterSeat seat;

	public ERCMovingSoundRiding(EntityPlayer p_i45106_1_, ERC_EntityCoasterSeat p_i45106_2_)
	{
	    super(SoundEvents.ENTITY_MINECART_INSIDE, SoundCategory.NEUTRAL);
	    this.player = p_i45106_1_;
	    this.seat = p_i45106_2_;
	    this.attenuationType = ISound.AttenuationType.NONE;
	    this.repeat = true;
	    this.repeatDelay = 0;
	    //May be wrong - FT
	    this.pitch = 10.7f;
	}

	public void update() 
	{
		if ((!this.seat.isDead) && (this.player.isRiding()) && (this.player.getRidingEntity() == this.seat))
		{
			float f = seat.parent==null ? 0 : ((float) this.seat.parent.Speed);
			if (f >= 0.01D) 
			{
				this.volume = (MathHelper.clamp((float) Math.pow(Math.abs(f), 3.0D) * 0.5F, 0.0F, 1.0F));
				this.pitch = 1.3f;
			} 
			else 
			{
				this.volume = 0.0F;
			}
		} 
		else
		{
			this.donePlaying = true;
		}
	}
}