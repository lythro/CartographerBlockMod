package openblocks.common.tileentity;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.MagnetWhitelists;
import openblocks.common.entity.EntityMiniMe;
import openmods.api.IBreakAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.entity.EntityBlock;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityGoldenEgg extends SyncedTileEntity implements IPlaceAwareTile, IBreakAwareTile {

	private static final float SPEED_CHANGE_RATE = 0.1f;
	private static final Random RANDOM = new Random();
	private static final String MR_GLITCH = "Mikeemoo";
	private static final int STAGE_CHANGE_TICK = 100;
	private static final int RISING_TIME = 400;
	private static final int FALLING_TIME = 10;
	public static final int MAX_HEIGHT = 5;
	private static final double STAGE_CHANGE_CHANCE = 0.8;

	public static enum State {
		INERT(0, 0, false) {
			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return target.tryRandomlyChangeState(STAGE_CHANGE_TICK, ROTATING_SLOW);
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target) {
				target.tickCounter++;
			}
		},
		ROTATING_SLOW(1, 0, false) {
			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return target.tryRandomlyChangeState(STAGE_CHANGE_TICK, ROTATING_MEDIUM);
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target) {
				target.tickCounter++;
			}
		},
		ROTATING_MEDIUM(10, 0, false) {
			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return target.tryRandomlyChangeState(STAGE_CHANGE_TICK, ROTATING_FAST);
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target) {
				target.tickCounter++;
			}
		},
		ROTATING_FAST(50, 0, false) {
			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return target.tryRandomlyChangeState(STAGE_CHANGE_TICK, FLOATING);
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target) {
				target.tickCounter++;
			}
		},
		FLOATING(100, 1.0f / RISING_TIME, true) {
			@Override
			public void onEntry(TileEntityGoldenEgg target) {
				target.tickCounter = RISING_TIME;
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target) {
				target.tickCounter--;
				if (RANDOM.nextInt(6) != 0) return;

				int posX = target.xCoord + RANDOM.nextInt(20) - 10;
				int posY = target.yCoord + RANDOM.nextInt(2) - 1;
				int posZ = target.zCoord + RANDOM.nextInt(20) - 10;
				boolean canMove = MagnetWhitelists.instance.testBlock(target.worldObj, posX, posY, posZ);
				if (canMove) target.pickUpBlock(posX, posY, posZ);
			}

			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return (target.tickCounter <= 0)? FALLING : null;
			}
		},
		FALLING(150, -1.0f / FALLING_TIME, true) {
			@Override
			public void onEntry(TileEntityGoldenEgg target) {
				target.tickCounter = FALLING_TIME;
				target.dropBlocks();
			}

			@Override
			public void onServerTick(TileEntityGoldenEgg target) {
				target.tickCounter--;
			}

			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return (target.tickCounter <= 0)? EXPLODING : null;
			}
		},
		EXPLODING(666, 0, true) {
			@Override
			public void onEntry(TileEntityGoldenEgg target) {
				target.explode();
			}

			@Override
			public State getNextState(TileEntityGoldenEgg target) {
				return null;
			}
		};

		public final float rotationSpeed;

		public final float progressSpeed;

		public final boolean specialEffects;

		public void onEntry(TileEntityGoldenEgg target) {}

		public void onServerTick(TileEntityGoldenEgg target) {}

		public abstract State getNextState(TileEntityGoldenEgg target);

		private State(float rotationSpeed, float riseSpeed, boolean specialEffects) {
			this.rotationSpeed = rotationSpeed;
			this.progressSpeed = riseSpeed;
			this.specialEffects = specialEffects;
		}

		private static final State[] STATES = values();
	}

	public int tickCounter;

	private float rotation;
	private float progress;

	private float rotationSpeed;
	private float progressSpeed;

	private List<EntityBlock> blocks = Lists.newArrayList();
	private SyncableInt stage;

	private String owner = "";

	public float getRotation(float partialTickTime) {
		return rotation + rotationSpeed * partialTickTime;
	}

	public float getProgress(float partialTickTime) {
		return progress + progressSpeed * partialTickTime;
	}

	public float getOffset(float partialTickTime) {
		return getProgress(partialTickTime) * MAX_HEIGHT;
	}

	public State tryRandomlyChangeState(int delay, State nextState) {
		return (tickCounter % delay == 0) && (RANDOM.nextDouble() < STAGE_CHANGE_CHANCE)? nextState : null;
	}

	@Override
	protected void createSyncedFields() {
		stage = new SyncableInt(State.INERT.ordinal());
	}

	private void pickUpBlock(int x, int y, int z) {
		EntityBlock block = EntityBlock.create(worldObj, x, y, z);
		if (block != null) {
			block.setHasAirResistance(false);
			block.setHasGravity(false);
			block.setShouldDrop(false);
			block.motionY = 0.1;
			blocks.add(block);
			worldObj.spawnEntityInWorld(block);
		}
	}

	private void dropBlocks() {
		for (EntityBlock block : blocks) {
			block.setShouldDrop(true);
			block.motionY = -0.9;
			block.setHasGravity(true);
		}

		blocks.clear();
	}

	private void explode() {
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		worldObj.createExplosion(null, 0.5 + xCoord, 0.5 + yCoord, 0.5 + zCoord, 2, true);
		if (Strings.isNullOrEmpty(owner)) owner = MR_GLITCH;
		EntityMiniMe miniMe = new EntityMiniMe(worldObj, owner);
		miniMe.setPositionAndRotation(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 0, 0);
		worldObj.spawnEntityInWorld(miniMe);
	}

	public State getState() {
		int stateId = stage.getValue();
		if (stateId < 0 || stateId >= State.STATES.length) return State.INERT;
		return State.STATES[stateId];
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		State state = getState();

		if (worldObj.isRemote) {
			rotationSpeed = (1 - SPEED_CHANGE_RATE) * rotationSpeed + SPEED_CHANGE_RATE * state.rotationSpeed;
			rotation += rotationSpeed;

			progressSpeed = (1 - SPEED_CHANGE_RATE) * progressSpeed + SPEED_CHANGE_RATE * state.progressSpeed;
			progress += progressSpeed;
		} else {
			state.onServerTick(this);

			State nextState = state.getNextState(this);
			if (nextState != null) {
				stage.setValue(nextState.ordinal());
				nextState.onEntry(this);
				sync();
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("owner", owner);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		owner = nbt.getString("owner");
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	@Override
	public void onBlockBroken() {
		dropBlocks();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		if (player != null) owner = player.username;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, -1024, zCoord, xCoord + 1, 1024, zCoord + 1);
	}

}
