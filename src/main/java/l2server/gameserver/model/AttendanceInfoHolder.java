package l2server.gameserver.model;

public class AttendanceInfoHolder
{
    private final int _rewardIndex;
    private final boolean _rewardAvailable;

    public AttendanceInfoHolder(int rewardIndex, boolean rewardAvailable)
    {
        _rewardIndex = rewardIndex;
        _rewardAvailable = rewardAvailable;
    }

    public int getRewardIndex()
    {
        return _rewardIndex;
    }

    public boolean isRewardAvailable()
    {
        return _rewardAvailable;
    }
}