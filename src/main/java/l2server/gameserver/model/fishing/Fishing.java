/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2server.gameserver.model.fishing;


import l2server.Config;
import l2server.gameserver.GeoData;
import l2server.gameserver.GeoEngine;
import l2server.gameserver.Item;
import l2server.gameserver.ThreadPoolManager;
import l2server.gameserver.datatables.FishingData;
import l2server.gameserver.enums.ShotType;
import l2server.gameserver.instancemanager.ZoneManager;
import l2server.gameserver.model.L2Fishing;
import l2server.gameserver.model.L2ItemInstance;
import l2server.gameserver.model.Location;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.event.EventDispatcher;
import l2server.gameserver.model.interfaces.ILocational;
import l2server.gameserver.model.itemcontainer.Inventory;
import l2server.gameserver.model.zone.L2ZoneType;
import l2server.gameserver.model.zone.type.L2FishingZone;
import l2server.gameserver.model.zone.type.L2WaterZone;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.*;
import l2server.gameserver.templates.item.L2Item;
import l2server.gameserver.templates.item.L2WeaponType;
import l2server.gameserver.util.Util;
import l2server.util.Rnd;

import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;


/**
 * @author bit
 */
public class Fishing
{
	protected static final Logger LOGGER = Logger.getLogger(Fishing.class.getName());
	private ILocational _baitLocation = new Location(0, 0, 0);
	
	private final L2PcInstance _player;
	private ScheduledFuture<?> _reelInTask;
	private ScheduledFuture<?> _startFishingTask;
	private boolean _isFishing = false;
	
	public Fishing(L2PcInstance player)
	{
		_player = player;
	}
	
	public synchronized boolean isFishing()
	{
		return _isFishing;
	}
	
	public boolean isAtValidLocation()
	{
		// TODO: implement checking direction
		// if (calculateBaitLocation() == null)
		// {
		// return false;
		// }
		return _player.isInsideZone(L2Character.ZONE_FISHING);
	}
	
	public boolean canFish()
	{
		return !_player.isDead() && !_player.isAlikeDead()  && !_player.isSitting();
	}
	
	private FishingBait getCurrentBaitData()
	{
		final L2ItemInstance bait = _player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		return bait != null ? FishingData.getInstance().getBaitData(bait.getItemId()) : null;
	}
	
	private void cancelTasks()
	{
		if (_reelInTask != null)
		{
			_reelInTask.cancel(false);
			_reelInTask = null;
		}
		
		if (_startFishingTask != null)
		{
			_startFishingTask.cancel(false);
			_startFishingTask = null;
		}
	}
	
	public synchronized void startFishing()
	{
		if (_isFishing)
		{
			return;
		}
		_isFishing = true;
		castLine();
	}
	
	private void castLine()
	{
		cancelTasks();
		
		if (!canFish())
		{
			if (_isFishing)
			{
				_player.sendMessage("YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED");
			}
			stopFishing(ExFishingEnd.FishingEndType.ERROR);
			return;
		}
		
		final FishingBait baitData = getCurrentBaitData();
		if (baitData == null)
		{
			_player.sendMessage("YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH");
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(ExFishingEnd.FishingEndType.ERROR);
			return;
		}
		
		final int minPlayerLevel = baitData.getMinPlayerLevel();
		final int maxPLayerLevel = baitData.getMaxPlayerLevel();
		if ((_player.getLevel() < minPlayerLevel) && (_player.getLevel() > maxPLayerLevel))
		{
			_player.sendMessage("YOU_DO_NOT_MEET_THE_FISHING_LEVEL_REQUIREMENTS");
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(ExFishingEnd.FishingEndType.ERROR);
			return;
		}
		
		final L2ItemInstance rod = _player.getActiveWeaponInstance();
		if ((rod == null) || (rod.getItemType() != L2WeaponType.FISHINGROD))
		{
			_player.sendMessage("YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED");
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(ExFishingEnd.FishingEndType.ERROR);
			return;
		}
		
		final FishingRod rodData = FishingData.getInstance().getRodData(rod.getItemId());
		if (rodData == null)
		{
			_player.sendMessage("YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED");
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(ExFishingEnd.FishingEndType.ERROR);
			return;
		}
		
		if (_player.isTransformed() || _player.isInBoat())
		{
			_player.sendMessage("YOU_CANNOT_FISH_WHEN_TRANSFORMED_OR_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT_IT_S_AGAINST_THE_RULES");
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(ExFishingEnd.FishingEndType.ERROR);
			return;
		}
		
		if (_player.isInCraftMode() || _player.isInStoreMode())
		{
			_player.sendMessage("YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_WORKSHOP_OR_PRIVATE_STORE");
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(ExFishingEnd.FishingEndType.ERROR);
			return;
		}
		
		if (_player.isInsideZone(L2Character.ZONE_WATER) || _player.isInWater())
		{
			_player.sendMessage("YOU_CANNOT_FISH_WHILE_UNDER_WATER");
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(ExFishingEnd.FishingEndType.ERROR);
			return;
		}
		
		_baitLocation = calculateBaitLocation();
		if (!_player.isInsideZone(L2Character.ZONE_FISHING) || (_baitLocation == null))
		{
			if (_isFishing)
			{
				_player.sendMessage("YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED");
				_player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			else
			{
				_player.sendMessage("YOU_CAN_T_FISH_HERE_YOUR_CHARACTER_IS_NOT_FACING_WATER_OR_YOU_ARE_NOT_IN_A_FISHING_GROUND");
				_player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			stopFishing(ExFishingEnd.FishingEndType.ERROR);
			return;
		}
		
		if (!_player.isChargedShot(ShotType.FISH_SOULSHOTS))
		{
			_player.rechargeShots(false, false, true);
		}
		
		final long fishingTime = Math.max(Rnd.get(baitData.getTimeMin(), baitData.getTimeMax()) - rodData.getReduceFishingTime(), 1000);
		final long fishingWaitTime = Rnd.get(baitData.getWaitMin(), baitData.getWaitMax());
		_reelInTask = ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			_player.getFishing().reelInWithReward();
			_startFishingTask = ThreadPoolManager.getInstance().scheduleGeneral(() -> _player.getFishing().castLine(), fishingWaitTime);
		}, fishingTime);
		_player.stopMove(null);
		_player.setIsImmobilized(true);
		_player.broadcastPacket(new ExFishingStart(_player, -1, _baitLocation));
		_player.sendPacket(new ExUserInfoFishing(_player, true, _baitLocation));
		_player.sendPacket(new PlaySound("SF_P_01"));
		_player.sendMessage("YOU_CAST_YOUR_LINE_AND_START_TO_FISH");
	}
	
	public void reelInWithReward()
	{
		// Fish may or may not eat the hook. If it does - it consumes fishing bait and fishing shot.
		// Then player may or may not catch the fish. Using fishing shots increases chance to win.
		final FishingBait baitData = getCurrentBaitData();
		if (baitData == null)
		{
			reelIn(ExFishingEnd.FishingEndReason.LOSE, false);
			LOGGER.warning("Player " + _player + " is fishing with unhandled bait: " + _player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND));
			return;
		}
		
		double chance = baitData.getChance();
		if (_player.isChargedShot(ShotType.FISH_SOULSHOTS))
		{
			chance *= 2;
		}
		
		if (Rnd.get(100) <= chance)
		{
			reelIn(ExFishingEnd.FishingEndReason.WIN, true);
		}
		else
		{
			reelIn(ExFishingEnd.FishingEndReason.LOSE, true);
		}
	}
	
	private void reelIn(ExFishingEnd.FishingEndReason reason, boolean consumeBait)
	{
		if (!_isFishing)
		{
			return;
		}
		
		cancelTasks();
		
		try
		{
			final L2ItemInstance bait = _player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			if ((consumeBait && bait == null) || bait.getCount() <= 0)
			{
				reason = ExFishingEnd.FishingEndReason.LOSE; // no bait - no reward
				return;
			}
            _player.getInventory().destroyItemByItemId("fishing", bait.getItemId(), 1, _player, null);

            if ((reason == ExFishingEnd.FishingEndReason.WIN) && (bait != null))
			{
				final FishingBait baitData = FishingData.getInstance().getBaitData(bait.getItemId());
				final FishingCatch fishingCatchData = baitData.getRandom();
				if (fishingCatchData != null)
				{
					final FishingData fishingData = FishingData.getInstance();
					final double lvlModifier = (Math.pow(_player.getLevel(), 2.2) * fishingCatchData.getMultiplier());
					final long xp = (long) (Rnd.get((int)fishingData.getExpRateMin(), (int)fishingData.getExpRateMax()) * lvlModifier *  1);
					final long sp = (long) (Rnd.get((int)fishingData.getSpRateMin(), (int)fishingData.getSpRateMax()) * lvlModifier *  1);
					_player.addExpAndSp(xp, sp, true);
					_player.getInventory().addItem("Fishing Reward", fishingCatchData.getItemId(), 1, _player, null);
					SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S1);
					msg.addItemName(fishingCatchData.getItemId());
					_player.sendPacket(msg);
					_player.unchargeShot(ShotType.FISH_SOULSHOTS);
					_player.rechargeShots(false, false, true);
				}
				else
				{
					LOGGER.warning("Could not find fishing rewards for bait " + bait.getItemId());
				}
			}
			else if (reason == ExFishingEnd.FishingEndReason.LOSE)
			{
				_player.sendMessage("THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY");
			}
			
			if (consumeBait)
			{
			    // Daily mission
				// EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFishing(_player, reason), _player);
			}
		}
		finally
		{
			_player.broadcastPacket(new ExFishingEnd(_player, reason));
			_player.sendPacket(new ExUserInfoFishing(_player, false));
		}
	}
	
	public void stopFishing()
	{
		stopFishing(ExFishingEnd.FishingEndType.PLAYER_STOP);
	}
	
	public synchronized void stopFishing(ExFishingEnd.FishingEndType endType)
	{
		if (_isFishing)
		{
			reelIn(ExFishingEnd.FishingEndReason.STOP, false);
			_isFishing = false;
			switch (endType)
			{
				case PLAYER_STOP:
				{
					_player.sendMessage("YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING");
					_player.setIsImmobilized(false);
					break;
				}
				case PLAYER_CANCEL:
				{
					_player.sendMessage("YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED");
					break;
				}
			}
		}
	}
	
	public ILocational getBaitLocation()
	{
		return _baitLocation;
	}
	
	private Location calculateBaitLocation()
	{
        int rnd = Rnd.get(150) + 50;
        double angle = Util.convertHeadingToDegree(_player.getHeading());
        double radian = Math.toRadians(angle);
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        int x = _player.getX() + (int) (cos * rnd);
        int y = _player.getY() + (int) (sin * rnd);
        int z = _player.getZ() + 50;
        /*
         * ...and if the spot is in a fishing zone. If it is, it will then
         * position the hook on the water surface. If not, you have to be GM to
         * proceed past here... in that case, the hook will be positioned using
         * the old Z lookup method.
         */
        L2FishingZone aimingTo = null;
        L2WaterZone water = null;
        boolean canFish = false;
        for (L2ZoneType zone : ZoneManager.getInstance().getZones(x, y))
        {
            if (zone instanceof L2FishingZone)
            {
                aimingTo = (L2FishingZone) zone;
                continue;
            }
            if (zone instanceof L2WaterZone)
            {
                water = (L2WaterZone) zone;
            }
        }
        if (aimingTo != null)
        {
            // fishing zone found, we can fish here
            if (Config.GEODATA > 0)
            {
                // geodata enabled, checking if we can see end of the pole
                if (GeoData.getInstance().canSeeTarget(_player.getX(), _player.getY(), z, x, y, z))
                {
                    // finding z level for hook
                    if (water != null)
                    {
                        // water zone exist
                        if (GeoData.getInstance().getHeight(x, y, z) < water.getWaterZ())
                        {
                            // water Z is higher than geo Z
                            z = water.getWaterZ() + 10;
                            canFish = true;
                        }
                    }
                    else
                    {
                        // no water zone, using fishing zone
                        if (GeoData.getInstance().getHeight(x, y, z) < aimingTo.getWaterZ())
                        {
                            // fishing Z is higher than geo Z
                            z = aimingTo.getWaterZ() + 10;
                            canFish = true;
                        }
                    }
                }
            }
            else
            {
                // geodata disabled
                // if water zone exist using it, if not - using fishing zone
                if (water != null)
                {
                    z = water.getWaterZ() + 10;
                }
                else
                {
                    z = aimingTo.getWaterZ() + 10;
                }
                canFish = true;
            }
        }
		
		return new Location(x, y, z);
	}
	
	/**
	 * Computes the Z of the bait.
	 * @param player the player
	 * @param baitX the bait x
	 * @param baitY the bait y
	 * @param fishingZone the fishing zone
	 * @param waterZone the water zone
	 * @return the bait z or {@link Integer#MIN_VALUE} when you cannot fish here
	 */
	private static int computeBaitZ(L2PcInstance player, int baitX, int baitY, L2FishingZone fishingZone, L2WaterZone waterZone)
	{
		if ((fishingZone == null))
		{
			return Integer.MIN_VALUE;
		}
		
		if ((waterZone == null))
		{
			return Integer.MIN_VALUE;
		}
		
		// always use water zone, fishing zone high z is high in the air...
		final int baitZ = waterZone.getWaterZ();
		
		// if (!GeoEngine.getInstance().canSeeTarget(player.getX(), player.getY(), player.getZ(), baitX, baitY, baitZ))
		//
		// return Integer.MIN_VALUE;
		// }
		if (GeoEngine.getInstance().hasGeo(baitX, baitY))
		{
			if (GeoEngine.getInstance().getHeight(baitX, baitY, baitZ) > baitZ)
			{
				return Integer.MIN_VALUE;
			}
			
			if (GeoEngine.getInstance().getHeight(baitX, baitY, player.getZ()) > baitZ)
			{
				return Integer.MIN_VALUE;
			}
		}
		
		return baitZ;
	}
}
