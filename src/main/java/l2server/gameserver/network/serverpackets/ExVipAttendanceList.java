package l2server.gameserver.network.serverpackets;

import l2server.gameserver.datatables.AttendanceRewardData;
import l2server.gameserver.datatables.AttendanceTable;
import l2server.gameserver.datatables.ItemTable;
import l2server.gameserver.model.AttendanceInfoHolder;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.holder.ItemHolder;

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
        writeC(AttendanceRewardData.getInstance().getRewardsCount()); // reward size
        int rewardCounter = 0;
        for (ItemHolder item: AttendanceRewardData.getInstance().getRewards()) {
            rewardCounter++;
            writeD(item.getId());
            writeQ(item.getCount());
            writeC(0x01); // is unknown?
            writeC((rewardCounter % 7) == 0 ? 0x01 : 0x00); // is last in row?
        };
        writeC(0x00);
        writeD(0x00);
    }
}
