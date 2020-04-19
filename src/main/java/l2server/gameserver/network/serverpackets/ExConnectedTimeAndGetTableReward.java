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

        // OutgoingPackets.EX_CONNECTED_TIME_AND_GETTABLE_REWARD.writeId(packet);
        for (int i = 0; i < 16; i++) // TODO : Find what the hell it is
        {
            writeD(0x00);
        }
        return;
	}
}
