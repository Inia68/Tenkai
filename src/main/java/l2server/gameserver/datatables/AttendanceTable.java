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

package l2server.gameserver.datatables;

import l2server.Config;
import l2server.gameserver.Reloadable;
import l2server.gameserver.ReloadableManager;
import l2server.gameserver.events.instanced.EventPrize;
import l2server.gameserver.events.instanced.EventPrize.EventPrizeCategory;
import l2server.gameserver.events.instanced.EventPrize.EventPrizeItem;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.log.Log;
import l2server.util.Rnd;
import l2server.util.xml.XmlDocument;
import l2server.util.xml.XmlNode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pere
 */
public class AttendanceTable implements Reloadable
{
	private final Map<String, List<AttendanceItem>> _prizes = new HashMap<>();

	private AttendanceTable()
	{
		ReloadableManager.getInstance().register("attendance", this);

		load();
	}

	public List<AttendanceItem> getRewards(int index) {
	    return _prizes.get(String.valueOf(index));
    }

	public void load()
	{
		_prizes.clear();

		XmlDocument doc = new XmlDocument(new File(Config.DATAPACK_ROOT, Config.DATA_FOLDER + "attendanceReward.xml"));
		int przCount = 0;


			for (XmlNode prizeNode : doc.getChildren())
			{
				if (!prizeNode.getName().equals("prizeCategory"))
				{
					continue;
				}

				String index = prizeNode.getString("index");
                List<AttendanceItem> list = _prizes.get(index);
                if (list == null)
                {
                    list = new ArrayList<>();
                    _prizes.put(index, list);
                }

                for (XmlNode node : prizeNode.getChildren())
				{
                    list.add(new AttendanceItem(node));
				}
			}

		Log.info("Attendance Prizes Table: loaded " + przCount + " prizes in " + _prizes.size() + " categories.");
	}

    public class AttendanceItem
    {
        private final int _id;
        private final int _amount;

        public AttendanceItem(XmlNode node)
        {
            _id = node.getInt("id");
            _amount = node.getInt("amount");
        }

        public int getId()
        {
            return _id;
        }

        public int getAmount()
        {
            return _amount;
        }

    }


	@Override
	public boolean reload()
	{
		load();
		return true;
	}

	@Override
	public String getReloadMessage(boolean success)
	{
		return "Attendance prizes reloaded";
	}

    public int getPizeCount() {
        return _prizes.size();
    }


    private static AttendanceTable _instance;

	public static AttendanceTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new AttendanceTable();
		}

		return _instance;
	}
}
