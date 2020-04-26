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
import l2server.gameserver.model.StatSet;
import l2server.gameserver.model.base.ClassId;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.holder.ItemHolder;
import l2server.log.Log;
import l2server.util.IXmlReader;
import l2server.util.xml.XmlDocument;
import l2server.util.xml.XmlNode;
import org.w3c.dom.Document;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class DailyMissionData implements IXmlReader
{
    private static final Logger LOGGER = Logger.getLogger(DailyMissionData.class.getName());
    private final Map<Integer, List<DailyMissionDataHolder>> _dailyMissionRewards = new LinkedHashMap<>();
    private boolean _isAvailable;

    protected DailyMissionData()
    {
        load();
    }

    @Override
    public void load()
    {
        _dailyMissionRewards.clear();
        parseDatapackFile("data/DailyMission.xml");
        _isAvailable = !_dailyMissionRewards.isEmpty();
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _dailyMissionRewards.size() + " one day rewards.");
    }

    @Override
    public void parseDocument(Document doc, File f)
    {
        forEach(doc, "list", listNode -> forEach(listNode, "reward", rewardNode ->
        {
            final StatSet set = new StatSet(parseAttributes(rewardNode));
            final List<ItemHolder> items = new ArrayList<>(1);
            forEach(rewardNode, "items", itemsNode -> forEach(itemsNode, "item", itemNode ->
            {
                final int itemId = parseInteger(itemNode.getAttributes(), "id");
                final int itemCount = parseInteger(itemNode.getAttributes(), "count");
                items.add(new ItemHolder(itemId, itemCount));
            }));

            set.set("items", items);

            final List<ClassId> classRestriction = new ArrayList<>(1);
            forEach(rewardNode, "classId", classRestrictionNode -> classRestriction.add(ClassId.getClassId(Integer.parseInt(classRestrictionNode.getTextContent()))));
            set.set("classRestriction", classRestriction);

            // Initial values in case handler doesn't exists
            set.set("handler", "");
            set.set("params", StatSet.EMPTY_STATSET);

            // Parse handler and parameters
            forEach(rewardNode, "handler", handlerNode ->
            {
                set.set("handler", parseString(handlerNode.getAttributes(), "name"));

                final StatSet params = new StatSet();
                set.set("params", params);
                forEach(handlerNode, "param", paramNode -> params.set(parseString(paramNode.getAttributes(), "name"), paramNode.getTextContent()));
            });

            final DailyMissionDataHolder holder = new DailyMissionDataHolder(set);
            _dailyMissionRewards.computeIfAbsent(holder.getId(), k -> new ArrayList<>()).add(holder);
        }));
    }

    public Collection<DailyMissionDataHolder> getDailyMissionData()
    {
        //@formatter:off
        return _dailyMissionRewards.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        //@formatter:on
    }

    public Collection<DailyMissionDataHolder> getDailyMissionData(L2PcInstance player)
    {
        //@formatter:off
        return _dailyMissionRewards.values()
                .stream()
                .flatMap(List::stream)
                .filter(o -> o.isDisplayable(player))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public Collection<DailyMissionDataHolder> getDailyMissionData(int id)
    {
        return _dailyMissionRewards.get(id);
    }

    public boolean isAvailable()
    {
        return _isAvailable;
    }

    /**
     * Gets the single instance of DailyMissionData.
     * @return single instance of DailyMissionData
     */
    public static DailyMissionData getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final DailyMissionData INSTANCE = new DailyMissionData();
    }
}
