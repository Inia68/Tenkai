package l2server.gameserver.network.serverpackets;

import l2server.gameserver.datatables.AttendanceTable;
import l2server.gameserver.datatables.DailyMissionData;
import l2server.gameserver.model.DailyMissionDataHolder;
import l2server.gameserver.model.DailyMissionStatus;
import l2server.gameserver.model.actor.L2Attackable;
import l2server.gameserver.model.actor.instance.L2PcInstance;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * @author MegaParzor!
 */
public class ExOneDayReceiveRewardList extends L2GameServerPacket
{
    final L2PcInstance _player;
    private final Collection<DailyMissionDataHolder> _rewards;

    public ExOneDayReceiveRewardList(L2PcInstance player)
    {
        _player = player;
        _rewards = DailyMissionData.getInstance().getDailyMissionData(player);
    }

    @Override
	public void writeImpl()
	{
        writeC(0x23);
        writeD(_player.getClassId());
        writeD(LocalDate.now().getDayOfWeek().ordinal()); // Day of week
        writeD(_rewards.size());
        for (DailyMissionDataHolder reward : _rewards)
        {
            writeH(reward.getId());
            writeC(reward.getStatus(_player));
            writeC(reward.getRequiredCompletions() > 0 ? 0x01 : 0x00);
            writeD(reward.getProgress(_player));
            writeD(reward.getRequiredCompletions());
        }
	}
}
