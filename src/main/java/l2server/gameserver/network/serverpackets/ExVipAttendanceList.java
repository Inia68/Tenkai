package l2server.gameserver.network.serverpackets;

import l2server.gameserver.datatables.AttendanceTable;
import l2server.gameserver.datatables.ItemTable;
import l2server.gameserver.model.AttendanceInfoHolder;
import l2server.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

public class ExVipAttendanceList  extends L2GameServerPacket
{
    boolean _available;
    int _index;

    public ExVipAttendanceList(L2PcInstance player)
    {
        final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
        _available = attendanceInfo.isRewardAvailable();
        _index = attendanceInfo.getRewardIndex();
    }

    /**
     */
    @Override
    protected final void writeImpl()
    {

        writeC(_available ? _index + 1 : _index); // index to receive?
        writeC(_index); // last received index?
        writeD(0x00);
        writeD(0x00);
        writeC(0x01);
        writeC(_available ? 0x01 : 0x00); // player can receive reward today?
        writeC(250);
        writeC(AttendanceTable.getInstance().getPizeCount()); // reward size
        int rewardCounter = 0;
        final List<AttendanceTable.AttendanceItem> reward = AttendanceTable.getInstance().getRewards(_index);
        for (AttendanceTable.AttendanceItem item: reward) {
            rewardCounter++;
            writeD(item.getId());
            writeQ(item.getAmount());
            writeC(0x01); // is unknown?
            writeC((rewardCounter % 7) == 0 ? 0x01 : 0x00); // is last in row?
        };
        writeC(0x00);
        writeD(0x00);
    }
}
