/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package l2server.gameserver.model.zone.type;

import l2server.gameserver.ThreadPoolManager;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.fishing.Fishing;
import l2server.gameserver.model.zone.L2ZoneType;
import l2server.gameserver.network.serverpackets.ExAutoFishAvailable;
import l2server.gameserver.network.serverpackets.NpcInfo;
import l2server.gameserver.network.serverpackets.ServerObjectInfo;

import java.lang.ref.WeakReference;
import java.util.Collection;

/**
 * A fishing zone
 *
 * @author durgus
 */
public class L2FishingZone extends L2ZoneType
{
	public L2FishingZone(int id)
	{
		super(id);
	}

	@Override
	protected void onEnter(L2Character character)
	{
        character.setInsideZone(L2Character.ZONE_FISHING, true);

        if (character instanceof L2PcInstance)
        {
            if (character.isTransformed() && !((L2PcInstance) character).isCursedWeaponEquipped())
            {
                character.stopTransformation(true);
                //((L2PcInstance) character).untransform();
            }
            // TODO: update to only send speed status when that packet is known
            else
            {
                ((L2PcInstance) character).broadcastUserInfo();
            }
        }

         if (character instanceof L2PcInstance) {
         ((L2PcInstance)character).sendMessage("You entered fishing zone !"); }
        final WeakReference<L2PcInstance> weakPlayer = new WeakReference<>(character.getActingPlayer());
        ThreadPoolManager.getInstance().executeTask(new Runnable()
        {
            @Override
            public void run()
            {
                final L2PcInstance player = weakPlayer.get();
                if (player != null)
                {
                    final Fishing fishing = player.getFishing();
                    if (player.isInsideZone(L2Character.ZONE_FISHING))
                    {
                        if (fishing.canFish() && !fishing.isFishing())
                        {
                            player.sendPacket(ExAutoFishAvailable.YES);
                        }
                    }
                    else
                    {
                        player.sendPacket(ExAutoFishAvailable.NO);
                    }
                }
            }
        });


    }

	@Override
	protected void onExit(L2Character character)
	{
        character.setInsideZone(L2Character.ZONE_FISHING, false);

		if (character instanceof L2PcInstance)
		{
			((L2PcInstance)character).sendMessage("You exited fishing zone!");
		}

        if (character instanceof L2PcInstance)
        {
            ((L2PcInstance) character).broadcastUserInfo();
            ((L2PcInstance) character).sendPacket(ExAutoFishAvailable.NO);
        }
    }

	@Override
	public void onDieInside(L2Character character, L2Character killer)
	{
	}

	@Override
	public void onReviveInside(L2Character character)
	{
	}

	/* getWaterZ() this added function returns the Z value for the water surface.
	 * In effect this simply returns the upper Z value of the zone. This required
	 * some modification of L2ZoneForm, and zone form extentions.
	 */
	public int getWaterZ()
	{
		return getZone().getHighZ();
	}
}
