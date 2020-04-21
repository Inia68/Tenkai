package l2server.gameserver.network.serverpackets;

/**
 * @author MegaParzor!
 */
public class ExConfirmVipAttendanceCheck extends L2GameServerPacket
{
	@Override
	public void writeImpl()
	{
        writeC(_available ? 0x01 : 0x00); // can receive reward today? 1 else 0
        writeC(_index); // active reward index
        writeD(0);
        writeD(0);
	}

    boolean _available;
    int _index;

    public ExConfirmVipAttendanceCheck(boolean rewardAvailable, int rewardIndex)
    {
        _available = rewardAvailable;
        _index = rewardIndex;
    }

}
