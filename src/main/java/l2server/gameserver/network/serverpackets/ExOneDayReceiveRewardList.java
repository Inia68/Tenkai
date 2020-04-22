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
    public ExOneDayReceiveRewardList(L2PcInstance player)
    {
        _player = player;
    }

	@Override
	public void writeImpl()
	{
        writeC(0x23);
        writeD(_player.getClassId());
        writeD(LocalDate.now().getDayOfWeek().ordinal()); // Day of week
        writeD(2); // reward size
        for (int i = 0; i < 2; i++)
        {
            writeH(4037); // Reward id
            writeC(0x01); // Status
            writeC(0x01); // getRequiredCompletitions 1 : 0
            writeD(40); // progress
            writeD(100); // getRequiredCompletions()
        }
	}
}
