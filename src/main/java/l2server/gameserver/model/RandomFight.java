package l2server.gameserver.model;

import l2server.L2DatabaseFactory;
import l2server.gameserver.ThreadPoolManager;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.SkillCoolTime;
import l2server.gameserver.util.Broadcast;
import l2server.gameserver.util.NpcUtil;
import l2server.log.Log;
import l2server.util.Rnd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.logging.Level;


/**
 * @author Inia
 *
 */
public class RandomFight
{

    public static enum State{INACTIVE,REGISTER,LOADING,FIGHT}
    public static State state = State.INACTIVE;
    public static final int timeEach = 10;
    public static Vector<L2PcInstance> players = new Vector<>();

    protected void openRegistrations()
    {
        return;

    }

    protected void checkRegistrations()
    {
       return;
    }


    protected void pickPlayers()
    {
        if(players.isEmpty() || players.size() < 2)
        {
            Broadcast.announceToOnlinePlayers("Random Fight Event aborted : not enough participants.");
            clean();
            return;
        }


        int rnd1=Rnd.get(players.size());
        int rnd2=Rnd.get(players.size());

        while(rnd2==rnd1)
            rnd2=Rnd.get(players.size());


        L2PcInstance player1 = players.get(rnd1);
        L2PcInstance player2 = players.get(rnd2);

        players.clear();
        players.add(player1);
        players.add(player2);

        Broadcast.announceToOnlinePlayers("Players selected: "+players.firstElement().getName()+" vs "+players.lastElement().getName());
        Broadcast.announceToOnlinePlayers("Players will be teleported in 15 seconds");
        ThreadPoolManager.getInstance().scheduleGeneral(new teleportPlayers(), 15000);
    }

    protected void checkItem()
    {
        if (state != State.FIGHT)
            return;

        for (L2PcInstance player : players)
        {
            if (player == null)
            {
                continue;
            }
            player.checkItemRestriction();
            player.broadcastUserInfo();
        }


    }

    protected void teleport()
    {
        if(players.isEmpty() || players.size() < 2)
        {
            Broadcast.announceToOnlinePlayers("Random Fight Event aborted : not enough participants.");
            clean();
            return;
        }

        L2Npc bufferOne = NpcUtil.addSpawn(40002, -88900 + 50, -252849, -3330, 0, false, 15000, false, 0);
        L2Npc bufferTwo = NpcUtil.addSpawn(40002, -87322 + 50, -252849, -3332, 0, false, 15000, false, 0);
        bufferOne.spawnMe();
        bufferTwo.spawnMe();

        players.firstElement().heal();
        players.lastElement().heal();

        players.firstElement().teleToLocation(-88900,-252849,-3330);
        players.lastElement().teleToLocation(-87322,-252849,-3332);

        players.firstElement().setPvpFlag(0);
        players.lastElement().setPvpFlag(0);

        players.firstElement().setTeam(1);
        players.lastElement().setTeam(2);

        players.firstElement().setIsParalyzed(true);
        players.lastElement().setIsParalyzed(true);


        players.firstElement().sendMessage("Fight will begin in 15 seconds!");
        players.lastElement().sendMessage("Fight will begin in 15 seconds!");

        ThreadPoolManager.getInstance().scheduleGeneral(new fight(), 15000);
    }

    public int getRankedPoints(L2PcInstance player)
    {
        Connection get = null;

        try
        {
            get = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = get.prepareStatement(
                    "SELECT rankedPoints FROM characters WHERE charId = ?");
            statement.setInt(1, player.getObjectId());
            ResultSet rset = statement.executeQuery();

            if (rset.next())
            {
                int currentPoints = rset.getInt("rankedPoints");
                return (currentPoints);
            }
            rset.close();
            statement.close();
        }

        catch (Exception e)
        {
            Log.log(Level.WARNING, "Couldn't get current ranked points : " + e.getMessage(), e);
        }
        finally
        {
            L2DatabaseFactory.close(get);
        }
        return 0;
    }


    public void setRankedPoints(L2PcInstance player, int amount)
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();

            PreparedStatement statement =
                    con.prepareStatement("UPDATE characters SET rankedPoints=? WHERE charId=?");
            statement.setInt(1, amount);
            statement.setInt(2, player.getObjectId());

            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            Log.log(Level.SEVERE, "Failed updating Ranked Points", e);
        }
        finally
        {
            L2DatabaseFactory.close(con);
        }
    }

    public void onKillInia(L2PcInstance killer, L2PcInstance killed)
    {
        int killerCurrentPoints;
        int killedCurrentPoints;

        killer.setPvpFlag(0);
        killed.setPvpFlag(0);

        killerCurrentPoints = getRankedPoints(killer);
        killedCurrentPoints = getRankedPoints(killed);

        int totalPoints = ((killedCurrentPoints + 1) / (killerCurrentPoints + 1 )) + 2;
        if (totalPoints > 20)
            totalPoints = 20 + (int) Math.pow(totalPoints - 20, 0.55);

        int amount = totalPoints;
        if (amount > 5)
            amount = 5 + (int) Math.pow(amount - 5, 0.45);

        setRankedPoints(killer, getRankedPoints(killer) + totalPoints);
        setRankedPoints(killed, getRankedPoints(killed) - (totalPoints / 3));

        if (getRankedPoints(killed) < 0)
        {
            setRankedPoints(killed, 0);
        }


        killer.addItem("", 5899, amount,killer, true);

        killer.sendMessage("You won " + totalPoints + " points !");
        killer.sendMessage("Current points : " + getRankedPoints(killer));
        killed.sendMessage("You lost " + totalPoints / 3 + " points");
        killed.sendMessage("Current points : " + getRankedPoints(killed));

        revert();
        clean();

    }

    protected void startFight()
    {

        if(players.isEmpty() || players.size() < 2)
        {
            Broadcast.announceToOnlinePlayers("One of the players isn't online, event aborted we are sorry!");
            clean();
            return;
        }
        state = State.FIGHT;


        players.firstElement().setPvpFlag(1);
        players.lastElement().setPvpFlag(1);



        players.firstElement().setIsParalyzed(false);
        players.lastElement().setIsParalyzed(false);

        players.firstElement().sendMessage("Fight!");
        players.lastElement().sendMessage("Fight!");

        for (L2PcInstance player : players)
        {
            if (player == null)
            {
                continue;
            }
            for (L2Skill skill : player.getAllSkills())
            {
                if (skill.getReuseDelay() <= 3600000)
                {
                    player.enableSkill(skill);
                }

            }
            player.sendSkillList();
            player.sendPacket(new SkillCoolTime(player));
        }

        ThreadPoolManager.getInstance().scheduleGeneral(new checkLast(), 220000 );
    }

    protected void lastCheck()
    {
        if(state == State.FIGHT)
        {
            if(players.isEmpty() || players.size() < 2)
            {
                revert();
                clean();
                return;
            }

            int alive=0;
            for(L2PcInstance player : players)
            {
                if(!player.isDead())
                    alive++;
            }

            if(alive==2)
            {
                Broadcast.announceToOnlinePlayers("Random Fight ended tie!");
                revert();
                clean();

            }
        }
    }

    public static void revert()
    {
        if(!players.isEmpty())
            for(L2PcInstance p : players)
            {
                if(p == null)
                    continue;

                if(p.isDead())
                    p.doRevive();

                p.setCurrentHp(p.getMaxHp());
                p.setCurrentCp(p.getMaxCp());
                p.setCurrentMp(p.getMaxMp());
                p.broadcastUserInfo();
                p.teleToLocation(-114435,253417,-1546);

            }
    }

    public static void clean()
    {

        if(state == State.FIGHT)
            for(L2PcInstance p : players)
                p.setTeam(0);


        players.clear();
        state = State.INACTIVE;

    }

    protected RandomFight()
    {
        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new checki(),  500 ,  500);
        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Event(), 60000 * 10 , 60000 * 10);
    }

    public static l2server.gameserver.model.RandomFight getInstance()
    {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder
    {
        protected static final l2server.gameserver.model.RandomFight _instance = new l2server.gameserver.model.RandomFight();
    }

    protected class Event implements Runnable
    {
        @Override
        public void run()
        {
            if(state == State.INACTIVE)
                openRegistrations();
        }

    }

    protected class checkRegist implements Runnable
    {

        @Override
        public void run()
        {
            checkRegistrations();
        }

    }

    protected class pickPlayers implements Runnable
    {
        @Override
        public void run()
        {
            pickPlayers();
        }

    }

    protected class teleportPlayers implements Runnable
    {
        @Override
        public void run()
        {
            teleport();
        }

    }

    protected class fight implements Runnable
    {

        @Override
        public void run()
        {
            startFight();
        }

    }

    protected class checki implements  Runnable
    {
        @Override
        public void run() {checkItem();}
    }

    protected class checkLast implements Runnable
    {
        @Override
        public void run()
        {
            lastCheck();
        }

    }
}