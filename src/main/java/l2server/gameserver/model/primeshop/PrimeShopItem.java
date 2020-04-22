package l2server.gameserver.model.primeshop;

import l2server.gameserver.model.holder.ItemHolder;

public class PrimeShopItem extends ItemHolder
{
    private final int _weight;
    private final int _isTradable;

    public PrimeShopItem(int itemId, int count, int weight, int isTradable)
    {
        super(itemId, count);
        _weight = weight;
        _isTradable = isTradable;
    }

    public int getWeight()
    {
        return _weight;
    }

    public int isTradable()
    {
        return _isTradable;
    }
}
