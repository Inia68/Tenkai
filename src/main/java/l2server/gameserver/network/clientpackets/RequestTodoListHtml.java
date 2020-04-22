package l2server.gameserver.network.clientpackets;

import l2server.log.Log;

/**
 * @author MegaParzor!
 */
public class RequestTodoListHtml extends L2GameClientPacket
{
    @SuppressWarnings("unused")
    private int _tab;
    @SuppressWarnings("unused")
    private String _linkName;

	@Override
	public void readImpl()
	{
        _tab = readC();
        _linkName = readS();
	}

	@Override
	public void runImpl()
	{
	}
}
