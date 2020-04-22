package l2server.gameserver.network.serverpackets;

import l2server.gameserver.datatables.DailyMissionData;

/**
 * @author MegaParzor!
 */
public class ExConnectedTimeAndGetTableReward extends L2GameServerPacket
{

    public static final ExConnectedTimeAndGetTableReward STATIC_PACKET = new ExConnectedTimeAndGetTableReward();

	@Override
	public void writeImpl()
	{
        for (int i = 0; i < 16; i++)
        {
            writeD(0x00);
        }
        return;
	}
}
