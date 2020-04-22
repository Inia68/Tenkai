package l2server.gameserver.network.clientpackets;

import l2server.Config;
import l2server.gameserver.datatables.PrimeShopData;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.itemcontainer.Inventory;
import l2server.gameserver.model.primeshop.PrimeShopGroup;
import l2server.gameserver.model.primeshop.PrimeShopItem;
import l2server.gameserver.network.serverpackets.ExBRBuyProduct;
import l2server.gameserver.network.serverpackets.ExBRGamePoint;
import l2server.gameserver.util.Util;
import l2server.log.Log;

import java.util.Calendar;

/**
 * @author MegaParzor!
 */
public class RequestBRBuyProduct extends L2GameClientPacket
{
    private static final int HERO_COINS = 23805;

    private int _brId;
    private int _count;

	@Override
	public void readImpl()
	{
        _brId = readD();
        _count = readD();
	}

	@Override
	public void runImpl()
	{
        final L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        {
            return;
        }

        if (player.isProcessingRequest())
        {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER_STATE));
            return;
        }


        final PrimeShopGroup item = PrimeShopData.getInstance().getItem(_brId);
        if (validatePlayer(item, _count, player))
        {
            final int price = (item.getPrice() * _count);
            final int paymentId = validatePaymentId(item, price);
            if (paymentId < 0)
            {
                player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
                return;
            }
            else if (paymentId > 0)
            {
                if (!player.destroyItemByItemId("PrimeShop-" + item.getBrId(), paymentId, price, player, true))
                {
                    player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
                    return;
                }
            }
            else if (paymentId == 0)
            {
                if (player.getPrimePoints() < price)
                {
                    player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
                    return;
                }
                player.setPrimePoints(player.getPrimePoints() - price);
            }

            for (PrimeShopItem subItem : item.getItems())
            {
                player.addItem("PrimeShop", subItem.getId(), subItem.getCount() * _count, player, true);
            }

            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SUCCESS));
            player.sendPacket(new ExBRGamePoint(player));
        }

	}

    private static boolean validatePlayer(PrimeShopGroup item, int count, L2PcInstance player)
    {
        final long currentTime = System.currentTimeMillis() / 1000;
        if (item == null)
        {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_PRODUCT));
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid brId from Prime", Config.DEFAULT_PUNISH);
            return false;
        }
        else if ((count < 1) || (count > 99))
        {
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid itemcount [" + count + "] from Prime", Config.DEFAULT_PUNISH);
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER_STATE));
            return false;
        }
        else if ((item.getMinLevel() > 0) && (item.getMinLevel() > player.getLevel()))
        {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER));
            return false;
        }
        else if ((item.getMaxLevel() > 0) && (item.getMaxLevel() < player.getLevel()))
        {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER));
            return false;
        }
        else if ((item.getMinBirthday() > 0) && (item.getMinBirthday() > player.checkBirthDay()))
        {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER_STATE));
            return false;
        }
        else if ((item.getMaxBirthday() > 0) && (item.getMaxBirthday() < player.checkBirthDay()))
        {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER_STATE));
            return false;
        }
        else if ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) & item.getDaysOfWeek()) == 0)
        {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.NOT_DAY_OF_WEEK));
            return false;
        }
        else if ((item.getStartSale() > 1) && (item.getStartSale() > currentTime))
        {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.BEFORE_SALE_DATE));
            return false;
        }
        else if ((item.getEndSale() > 1) && (item.getEndSale() < currentTime))
        {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.AFTER_SALE_DATE));
            return false;
        }

        final int weight = item.getWeight() * count;
        final long slots = item.getCount() * count;
        if (player.getInventory().validateWeight(weight))
        {
            if (!player.getInventory().validateCapacity(slots))
            {
                player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTROY_OVERFLOW));
                return false;
            }
        }
        else
        {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTROY_OVERFLOW));
            return false;
        }

        return true;
    }

    private static int validatePaymentId(PrimeShopGroup item, long amount)
    {
        switch (item.getPaymentType())
        {
            case 0: // Prime points
            {
                return 0;
            }
            case 1: // Adenas
            {
                return 57;
            }
            case 2: // Hero coins
            {
                return HERO_COINS;
            }
        }
        return -1;
    }
}
