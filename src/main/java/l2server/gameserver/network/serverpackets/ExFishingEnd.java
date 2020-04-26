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

/**
 * Format: (ch) dc
 * d: character object id
 * c: 1 if won 0 if failed
 *
 * @author -Wooden-
 */
public class ExFishingEnd extends L2GameServerPacket
{
    public enum FishingEndReason
    {
        LOSE(0),
        WIN(1),
        STOP(2);

        private final int _reason;

        FishingEndReason(int reason)
        {
            _reason = reason;
        }

        public int getReason()
        {
            return _reason;
        }
    }

    public enum FishingEndType
    {
        PLAYER_STOP,
        PLAYER_CANCEL,
        ERROR;
    }

    private final L2PcInstance _player;
    private final FishingEndReason _reason;

    public ExFishingEnd(L2PcInstance player, FishingEndReason reason)
    {
        _player = player;
        _reason = reason;
    }

	/* (non-Javadoc)
	 * @see l2server.gameserver.serverpackets.ServerBasePacket#writeImpl()
	 */
	@Override
	protected final void writeImpl()
	{
        writeD(_player.getObjectId());
        writeC(_reason.getReason());
	}
}
