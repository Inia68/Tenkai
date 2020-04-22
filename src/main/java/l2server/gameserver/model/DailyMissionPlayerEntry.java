package l2server.gameserver.model;

public class DailyMissionPlayerEntry
{
    private final int _objectId;
    private final int _rewardId;
    private DailyMissionStatus _status = DailyMissionStatus.NOT_AVAILABLE;
    private int _progress;
    private long _lastCompleted;

    public DailyMissionPlayerEntry(int objectId, int rewardId)
    {
        _objectId = objectId;
        _rewardId = rewardId;
    }

    public DailyMissionPlayerEntry(int objectId, int rewardId, int status, int progress, long lastCompleted)
    {
        this(objectId, rewardId);
        _status = DailyMissionStatus.valueOf(status);
        _progress = progress;
        _lastCompleted = lastCompleted;
    }

    public int getObjectId()
    {
        return _objectId;
    }

    public int getRewardId()
    {
        return _rewardId;
    }

    public DailyMissionStatus getStatus()
    {
        return _status;
    }

    public void setStatus(DailyMissionStatus status)
    {
        _status = status;
    }

    public int getProgress()
    {
        return _progress;
    }

    public void setProgress(int progress)
    {
        _progress = progress;
    }

    public int increaseProgress()
    {
        _progress++;
        return _progress;
    }

    public long getLastCompleted()
    {
        return _lastCompleted;
    }

    public void setLastCompleted(long lastCompleted)
    {
        _lastCompleted = lastCompleted;
    }
}
