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
import l2server.gameserver.model.DailyMissionDataHolder;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.log.Log;
import l2server.util.xml.XmlDocument;
import l2server.util.xml.XmlNode;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LasTravel
 * @author Pere
 */

public class DailyMissionData implements Reloadable
{
	public class MissionTemplate
	{
		private int _id;
		private Map<Integer, DailyMissionInfo> _missions = new HashMap<>();
		public MissionTemplate(int id)
		{
			_id = id;
		}

		public int getId()
		{
			return _id;
		}

		public Map<Integer, DailyMissionInfo> getHairStyles()
		{
			return _missions;
		}
	}

	public class DailyMissionInfo
	{
		private int _id;
		private int _rewardId;
		private String _name;

		private DailyMissionInfo(int id, int rewardId, String name)
		{
			_id = id;
			_rewardId = rewardId;
			_name = name;
		}

		public int getId()
		{
			return _id;
		}

        public int getRewardId()
        {
            return _rewardId;
        }

        public String getName()
        {
            return _name;
        }
	}

	private Map<Integer, MissionTemplate> _missionsTable = new HashMap<>();
    private Collection<MissionTemplate> _miss;

	private DailyMissionData()
	{
		if (!Config.IS_CLASSIC)
		{
			reload();
			ReloadableManager.getInstance().register("DailyMission", this);
		}
	}

	@Override
	public boolean reload()
	{
		File file = new File(Config.DATAPACK_ROOT, Config.DATA_FOLDER + "DailyMission.xml");

		XmlDocument doc = new XmlDocument(file);
        _missionsTable.clear();
        MissionTemplate template = new MissionTemplate(0);
        for (XmlNode d : doc.getChildren())
        {
            if (d.getName().equalsIgnoreCase("reward"))
            {
                int id = d.getInt("id");
                int rewardId = d.getInt("reward_id");
                String name = d.getString("name");

                DailyMissionInfo info = new DailyMissionInfo(id, rewardId, name);
            }
        }

        _missionsTable.put(0, template);
        _miss.add(template);
        Log.info("BeautyTable: Loaded " + template.getHairStyles().size() + " missions");

		return false;
	}

	@Override
	public String getReloadMessage(boolean success)
	{
		return "DailyMission Table reloaded";
	}

	public MissionTemplate getTemplate(int id)
	{
		return _missionsTable.get(id);
	}

    public Collection<MissionTemplate> getMissions()
    {
        return _miss;
    }

	public static DailyMissionData getInstance()
	{
		return SingletonHolder._instance;
	}


	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final DailyMissionData _instance = new DailyMissionData();
	}
}
