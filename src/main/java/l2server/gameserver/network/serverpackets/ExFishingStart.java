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

package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.interfaces.ILocational;

/**
 * Format (ch)ddddd
 *
 * @author -Wooden-
 */
public class ExFishingStart extends L2GameServerPacket
{
    private final L2PcInstance _player;
    private final int _fishType;
    private final ILocational _baitLocation;

    /**
     * @param player
     * @param fishType
     * @param baitLocation
     */
    public ExFishingStart(L2PcInstance player, int fishType, ILocational baitLocation)
    {
        _player = player;
        _fishType = fishType;
        _baitLocation = baitLocation;
    }

	/* (non-Javadoc)
	 * @see l2server.gameserver.serverpackets.ServerBasePacket#writeImpl()
	 */
	@Override
	protected final void writeImpl()
	{
        writeD(_player.getObjectId());
        writeC(_fishType);
        writeD(_baitLocation.getX());
        writeD(_baitLocation.getY());
        writeD(_baitLocation.getZ());
        writeC(0x01); // 0 = newbie, 1 = normal, 2 = night
    }
}
