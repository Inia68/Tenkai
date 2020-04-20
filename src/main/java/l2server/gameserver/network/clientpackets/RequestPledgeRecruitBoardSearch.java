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

package l2server.gameserver.network.clientpackets;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.ExPledgeRecruitBoardSearch;

/**
 * @author Pere
 */
public final class RequestPledgeRecruitBoardSearch extends L2GameClientPacket
{
	private int _level;
	private int _karma;
	private boolean _clanName;
	private String _name;
	private int _sortBy;
	private boolean _desc;
	private int _page;

	@Override
	protected void readImpl()
	{
		_level = readD();
		_karma = readD();
		_clanName = readD() == 1;
		_name = readS();
		_sortBy = readD();
		_desc = readD() == 1;
		_page = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (!getClient().getFloodProtectors().getServerBypass().tryPerformAction("clanRecruitSearch"))
		{
			return;
		}

		sendPacket(new ExPledgeRecruitBoardSearch(_level, _karma, _clanName, _name, _sortBy, _desc, _page));
	}
}