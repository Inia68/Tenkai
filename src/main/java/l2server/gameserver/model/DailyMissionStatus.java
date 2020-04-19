package l2server.gameserver.model;

public enum DailyMissionStatus
{
    AVAILABLE(1),
    NOT_AVAILABLE(2),
    COMPLETED(3);

    private int _clientId;

    private DailyMissionStatus(int clientId)
    {
        _clientId = clientId;
    }

    public int getClientId()
    {
        return _clientId;
    }

    public static DailyMissionStatus valueOf(int clientId)
    {
        for (DailyMissionStatus type : values())
        {
            if (type.getClientId() == clientId)
            {
                return type;
            }
        }
        return null;
    }
}
