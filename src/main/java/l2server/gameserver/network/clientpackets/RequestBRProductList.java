package l2server.gameserver.network.clientpackets;

import l2server.gameserver.datatables.PrimeShopData;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.ExBrProductList;

/**
 * @author MegaParzor!
 */
public class RequestBRProductList extends L2GameClientPacket
{
    private int _type;

    @Override
	public void readImpl()
	{
		_type = readD();
	}

	@Override
	public void runImpl()
	{
        final L2PcInstance player = getClient().getActiveChar();
        if (player != null)
        {
            switch (_type)
            {
                case 0: // Home page
                {
                    player.sendPacket(new ExBrProductList(player, 0, PrimeShopData.getInstance().getPrimeItems().values()));
                    break;
                }
                case 1: // History
                {
                    break;
                }
                case 2: // Favorites
                {
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
	}
}
