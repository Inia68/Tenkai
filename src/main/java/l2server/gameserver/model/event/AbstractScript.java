package l2server.gameserver.model.event;

import com.sun.org.apache.bcel.internal.generic.L2I;
import l2server.gameserver.Item;
import l2server.gameserver.datatables.ItemTable;
import l2server.gameserver.datatables.NpcTable;
import l2server.gameserver.instancemanager.CastleManager;
import l2server.gameserver.instancemanager.FortManager;
import l2server.gameserver.instancemanager.InstanceManager;
import l2server.gameserver.instancemanager.ZoneManager;
import l2server.gameserver.model.L2Spawn;
import l2server.gameserver.model.SpawnGroup;
import l2server.gameserver.model.StatSet;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.entity.Castle;
import l2server.gameserver.model.entity.Fort;
import l2server.gameserver.model.entity.Instance;
import l2server.gameserver.model.event.annotations.*;
import l2server.gameserver.model.event.impl.IBaseEvent;
import l2server.gameserver.model.event.impl.creature.npc.OnAttackableKill;
import l2server.gameserver.model.event.listeners.*;
import l2server.gameserver.model.event.returns.AbstractEventReturn;
import l2server.gameserver.model.event.timers.IEventTimerCancel;
import l2server.gameserver.model.event.timers.IEventTimerEvent;
import l2server.gameserver.model.event.timers.TimerHolder;
import l2server.gameserver.model.olympiad.Olympiad;
import l2server.gameserver.model.zone.L2ZoneType;
import l2server.gameserver.scripting.ManagedScript;
import l2server.gameserver.templates.chars.L2NpcTemplate;
import l2server.gameserver.templates.item.L2Item;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractScript extends ManagedScript implements IEventTimerEvent<String>, IEventTimerCancel<String> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractScript.class.getName());
    private final Map<ListenerRegisterType, Set<Integer>> _registeredIds = new ConcurrentHashMap<>();
    private final Queue<AbstractEventListener> _listeners = new PriorityBlockingQueue<>();
    private TimerExecutor<String> _timerExecutor;

    public AbstractScript()
    {
        initializeAnnotationListeners();
    }

    @Override
    public void onTimerEvent(TimerHolder<String> holder)
    {
        onTimerEvent(holder.getEvent(), holder.getParams(), holder.getNpc(), holder.getPlayer());
    }

    @Override
    public void onTimerCancel(TimerHolder<String> holder)
    {
        onTimerCancel(holder.getEvent(), holder.getParams(), holder.getNpc(), holder.getPlayer());
    }

    public void onTimerEvent(String event, StatSet params, L2Npc npc, L2PcInstance player)
    {
        LOGGER.warning("[" + getClass().getSimpleName() + "]: Timer event arrived at non overriden onTimerEvent method event: " + event + " npc: " + npc + " player: " + player);
    }

    public void onTimerCancel(String event, StatSet params, L2Npc npc, L2PcInstance player)
    {
    }

    public TimerExecutor<String> getTimers()
    {
        if (_timerExecutor == null)
        {
            synchronized (this)
            {
                if (_timerExecutor == null)
                {
                    _timerExecutor = new TimerExecutor<>(this, this);
                }
            }
        }
        return _timerExecutor;
    }

    public boolean hasTimers()
    {
        return _timerExecutor != null;
    }

    private void initializeAnnotationListeners()
    {
        final List<Integer> ids = new ArrayList<>();
        for (Method method : getClass().getMethods())
        {
            if (method.isAnnotationPresent(RegisterEvent.class) && method.isAnnotationPresent(RegisterType.class))
            {
                final RegisterEvent listener = method.getAnnotation(RegisterEvent.class);
                final RegisterType regType = method.getAnnotation(RegisterType.class);
                final ListenerRegisterType type = regType.value();
                final EventType eventType = listener.value();
                if (method.getParameterCount() != 1)
                {
                    LOGGER.warning(getClass().getSimpleName() + ": Non properly defined annotation listener on method: " + method.getName() + " expected parameter count is 1 but found: " + method.getParameterCount());
                    continue;
                }
                else if (!eventType.isEventClass(method.getParameterTypes()[0]))
                {
                    LOGGER.warning(getClass().getSimpleName() + ": Non properly defined annotation listener on method: " + method.getName() + " expected parameter to be type of: " + eventType.getEventClass().getSimpleName() + " but found: " + method.getParameterTypes()[0].getSimpleName());
                    continue;
                }
                else if (!eventType.isReturnClass(method.getReturnType()))
                {
                    LOGGER.warning(getClass().getSimpleName() + ": Non properly defined annotation listener on method: " + method.getName() + " expected return type to be one of: " + Arrays.toString(eventType.getReturnClasses()) + " but found: " + method.getReturnType().getSimpleName());
                    continue;
                }

                int priority = 0;

                // Clear the list
                ids.clear();

                // Scan for possible Id filters
                for (Annotation annotation : method.getAnnotations())
                {
                    if (annotation instanceof Id)
                    {
                        final Id npc = (Id) annotation;
                        for (int id : npc.value())
                        {
                            ids.add(id);
                        }
                    }
                    else if (annotation instanceof Ids)
                    {
                        final Ids npcs = (Ids) annotation;
                        for (Id npc : npcs.value())
                        {
                            for (int id : npc.value())
                            {
                                ids.add(id);
                            }
                        }
                    }
                    else if (annotation instanceof Range)
                    {
                        final Range range = (Range) annotation;
                        if (range.from() > range.to())
                        {
                            LOGGER.warning(getClass().getSimpleName() + ": Wrong " + annotation.getClass().getSimpleName() + " from is higher then to!");
                            continue;
                        }

                        for (int id = range.from(); id <= range.to(); id++)
                        {
                            ids.add(id);
                        }
                    }
                    else if (annotation instanceof Ranges)
                    {
                        final Ranges ranges = (Ranges) annotation;
                        for (Range range : ranges.value())
                        {
                            if (range.from() > range.to())
                            {
                                LOGGER.warning(getClass().getSimpleName() + ": Wrong " + annotation.getClass().getSimpleName() + " from is higher then to!");
                                continue;
                            }

                            for (int id = range.from(); id <= range.to(); id++)
                            {
                                ids.add(id);
                            }
                        }
                    }
                    else if (annotation instanceof NpcLevelRange)
                    {
                        final NpcLevelRange range = (NpcLevelRange) annotation;
                        if (range.from() > range.to())
                        {
                            LOGGER.warning(getClass().getSimpleName() + ": Wrong " + annotation.getClass().getSimpleName() + " from is higher then to!");
                            continue;
                        }
                        else if (type != ListenerRegisterType.NPC)
                        {
                            LOGGER.warning(getClass().getSimpleName() + ": ListenerRegisterType " + type + " for " + annotation.getClass().getSimpleName() + " NPC is expected!");
                            continue;
                        }

                        for (int level = range.from(); level <= range.to(); level++)
                        {
                            final List<L2NpcTemplate> templates = Arrays.asList(NpcTable.getInstance().getAllOfLevel(level));
                            templates.forEach(template -> ids.add(Integer.parseInt(template.getXmlNpcId())));
                        }
                    }
                    else if (annotation instanceof NpcLevelRanges)
                    {
                        final NpcLevelRanges ranges = (NpcLevelRanges) annotation;
                        for (NpcLevelRange range : ranges.value())
                        {
                            if (range.from() > range.to())
                            {
                                LOGGER.warning(getClass().getSimpleName() + ": Wrong " + annotation.getClass().getSimpleName() + " from is higher then to!");
                                continue;
                            }
                            else if (type != ListenerRegisterType.NPC)
                            {
                                LOGGER.warning(getClass().getSimpleName() + ": ListenerRegisterType " + type + " for " + annotation.getClass().getSimpleName() + " NPC is expected!");
                                continue;
                            }

                            for (int level = range.from(); level <= range.to(); level++)
                            {
                                final List<L2NpcTemplate> templates = Arrays.asList(NpcTable.getInstance().getAllOfLevel(level));
                                templates.forEach(template -> ids.add(Integer.parseInt(template.getXmlNpcId())));
                            }
                        }
                    }
                    else if (annotation instanceof Priority)
                    {
                        final Priority p = (Priority) annotation;
                        priority = p.value();
                    }
                }

                if (!ids.isEmpty())
                {
                    _registeredIds.computeIfAbsent(type, k -> ConcurrentHashMap.newKeySet()).addAll(ids);
                }

                registerAnnotation(method, eventType, type, priority, ids);
            }
        }
    }

    /**
     * Unloads all listeners registered by this class.
     */
    @Override
    public boolean unload()
    {
        _listeners.forEach(AbstractEventListener::unregisterMe);
        _listeners.clear();
        if (_timerExecutor != null)
        {
            _timerExecutor.cancelAllTimers();
        }
        return true;
    }

    protected final List<AbstractEventListener> registerConsumer(Consumer<? extends IBaseEvent> callback, EventType type, ListenerRegisterType registerType, int... npcIds)
    {
        return registerListener(container -> new ConsumerEventListener(container, type, callback, this), registerType, npcIds);
    }

    /**
     * Method that registers Function type of listeners (Listeners that need parameters but doesn't return objects)
     * @param callback
     * @param type
     * @param registerType
     * @param npcIds
     * @return
     */
    protected final List<AbstractEventListener> registerConsumer(Consumer<? extends IBaseEvent> callback, EventType type, ListenerRegisterType registerType, Collection<Integer> npcIds)
    {
        return registerListener(container -> new ConsumerEventListener(container, type, callback, this), registerType, npcIds);
    }

    /**
     * Method that registers Function type of listeners (Listeners that need parameters and return objects)
     * @param callback
     * @param type
     * @param registerType
     * @param npcIds
     * @return
     */
    protected final List<AbstractEventListener> registerFunction(Function<? extends IBaseEvent, ? extends AbstractEventReturn> callback, EventType type, ListenerRegisterType registerType, int... npcIds)
    {
        return registerListener(container -> new FunctionEventListener(container, type, callback, this), registerType, npcIds);
    }

    /**
     * Method that registers Function type of listeners (Listeners that need parameters and return objects)
     * @param callback
     * @param type
     * @param registerType
     * @param npcIds
     * @return
     */
    protected final List<AbstractEventListener> registerFunction(Function<? extends IBaseEvent, ? extends AbstractEventReturn> callback, EventType type, ListenerRegisterType registerType, Collection<Integer> npcIds)
    {
        return registerListener(container -> new FunctionEventListener(container, type, callback, this), registerType, npcIds);
    }

    /**
     * Method that registers runnable type of listeners (Listeners that doesn't needs parameters or return objects)
     * @param callback
     * @param type
     * @param registerType
     * @param npcIds
     * @return
     */
    protected final List<AbstractEventListener> registerRunnable(Runnable callback, EventType type, ListenerRegisterType registerType, int... npcIds)
    {
        return registerListener(container -> new RunnableEventListener(container, type, callback, this), registerType, npcIds);
    }

    /**
     * Method that registers runnable type of listeners (Listeners that doesn't needs parameters or return objects)
     * @param callback
     * @param type
     * @param registerType
     * @param npcIds
     * @return
     */
    protected final List<AbstractEventListener> registerRunnable(Runnable callback, EventType type, ListenerRegisterType registerType, Collection<Integer> npcIds)
    {
        return registerListener(container -> new RunnableEventListener(container, type, callback, this), registerType, npcIds);
    }

    /**
     * Method that registers runnable type of listeners (Listeners that doesn't needs parameters or return objects)
     * @param callback
     * @param type
     * @param registerType
     * @param priority
     * @param npcIds
     * @return
     */
    protected final List<AbstractEventListener> registerAnnotation(Method callback, EventType type, ListenerRegisterType registerType, int priority, int... npcIds)
    {
        return registerListener(container -> new AnnotationEventListener(container, type, callback, this, priority), registerType, npcIds);
    }

    /**
     * Method that registers runnable type of listeners (Listeners that doesn't needs parameters or return objects)
     * @param callback
     * @param type
     * @param registerType
     * @param priority
     * @param npcIds
     * @return
     */
    protected final List<AbstractEventListener> registerAnnotation(Method callback, EventType type, ListenerRegisterType registerType, int priority, Collection<Integer> npcIds)
    {
        return registerListener(container -> new AnnotationEventListener(container, type, callback, this, priority), registerType, npcIds);
    }

    /**
     * Method that registers dummy type of listeners (Listeners doesn't gets notification but just used to check if their type present or not)
     * @param type
     * @param registerType
     * @param npcIds
     * @return
     */
    protected final List<AbstractEventListener> registerDummy(EventType type, ListenerRegisterType registerType, int... npcIds)
    {
        return registerListener(container -> new DummyEventListener(container, type, this), registerType, npcIds);
    }

    /**
     * Method that registers dummy type of listeners (Listeners doesn't gets notification but just used to check if their type present or not)
     * @param type
     * @param registerType
     * @param npcIds
     * @return
     */
    protected final List<AbstractEventListener> registerDummy(EventType type, ListenerRegisterType registerType, Collection<Integer> npcIds)
    {
        return registerListener(container -> new DummyEventListener(container, type, this), registerType, npcIds);
    }

    protected final List<AbstractEventListener> registerListener(Function<ListenersContainer, AbstractEventListener> action, ListenerRegisterType registerType, int... ids)
    {
        final List<AbstractEventListener> listeners = new ArrayList<>(ids.length > 0 ? ids.length : 1);
        if (ids.length > 0)
        {
            for (int id : ids)
            {
                switch (registerType)
                {
                    case NPC:
                    {
                        final L2NpcTemplate template = NpcTable.getInstance().getTemplate(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    case ZONE:
                    {
                        final L2ZoneType template = ZoneManager.getInstance().getZoneById(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    case ITEM:
                    {
                        final L2Item template = ItemTable.getInstance().getTemplate(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    case CASTLE:
                    {
                        final Castle template = CastleManager.getInstance().getCastleById(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    case FORTRESS:
                    {
                        final Fort template = FortManager.getInstance().getFortById(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    case INSTANCE:
                    {
                        final Instance template = InstanceManager.getInstance().getInstance(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    default:
                    {
                        LOGGER.warning(getClass().getSimpleName() + ": Unhandled register type: " + registerType);
                    }
                }

                _registeredIds.computeIfAbsent(registerType, k -> ConcurrentHashMap.newKeySet()).add(id);
            }
        }
        else
        {
            switch (registerType)
            {
                case OLYMPIAD:
                {
                    final Olympiad template = Olympiad.getInstance();
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
                case GLOBAL: // Global Listener
                {
                    final ListenersContainer template = Containers.Global();
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
                case GLOBAL_NPCS: // Global Npcs Listener
                {
                    final ListenersContainer template = Containers.Npcs();
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
                case GLOBAL_MONSTERS: // Global Monsters Listener
                {
                    final ListenersContainer template = Containers.Monsters();
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
                case GLOBAL_PLAYERS: // Global Players Listener
                {
                    final ListenersContainer template = Containers.Players();
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
            }
        }

        _listeners.addAll(listeners);
        return listeners;
    }

    /**
     * Generic listener register method
     * @param action
     * @param registerType
     * @param ids
     * @return
     */
    protected final List<AbstractEventListener> registerListener(Function<ListenersContainer, AbstractEventListener> action, ListenerRegisterType registerType, Collection<Integer> ids)
    {
        final List<AbstractEventListener> listeners = new ArrayList<>(!ids.isEmpty() ? ids.size() : 1);
        if (!ids.isEmpty())
        {
            for (int id : ids)
            {
                switch (registerType)
                {
                    case NPC:
                    {
                        final L2NpcTemplate template = NpcTable.getInstance().getTemplate(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    case ZONE:
                    {
                        final L2ZoneType template = ZoneManager.getInstance().getZoneById(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    case ITEM:
                    {
                        final L2Item template = ItemTable.getInstance().getTemplate(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    case CASTLE:
                    {
                        final Castle template = CastleManager.getInstance().getCastleById(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    case FORTRESS:
                    {
                        final Fort template = FortManager.getInstance().getFortById(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    case INSTANCE:
                    {
                        final Instance template = InstanceManager.getInstance().getInstance(id);
                        if (template != null)
                        {
                            listeners.add(template.addListener(action.apply(template)));
                        }
                        break;
                    }
                    default:
                    {
                        LOGGER.warning(getClass().getSimpleName() + ": Unhandled register type: " + registerType);
                    }
                }
            }

            _registeredIds.computeIfAbsent(registerType, k -> ConcurrentHashMap.newKeySet()).addAll(ids);
        }
        else
        {
            switch (registerType)
            {
                case OLYMPIAD:
                {
                    final Olympiad template = Olympiad.getInstance();
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
                case GLOBAL: // Global Listener
                {
                    final ListenersContainer template = Containers.Global();
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
                case GLOBAL_NPCS: // Global Npcs Listener
                {
                    final ListenersContainer template = Containers.Npcs();
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
                case GLOBAL_MONSTERS: // Global Monsters Listener
                {
                    final ListenersContainer template = Containers.Monsters();
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
                case GLOBAL_PLAYERS: // Global Players Listener
                {
                    final ListenersContainer template = Containers.Players();
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
            }
        }
        _listeners.addAll(listeners);
        return listeners;
    }

    public Set<Integer> getRegisteredIds(ListenerRegisterType type)
    {
        return _registeredIds.getOrDefault(type, Collections.emptySet());
    }

    public Queue<AbstractEventListener> getListeners()
    {
        return _listeners;
    }

    /**
     * -------------------------------------------------------------------------------------------------------
     */

    /**
     * @param template
     */
    public void onSpawnActivate(L2Spawn template)
    {
    }

    /**
     * @param
     */
    public void onSpawnDeactivate(L2Spawn template)
    {
    }

    /**
     * @param template
     * @param group
     * @param npc
     */
    public void onSpawnNpc(L2Spawn template, SpawnGroup group, L2Npc npc)
    {
    }

    /**
     * @param template
     * @param group
     * @param npc
     */
    public void onSpawnDespawnNpc(L2Spawn template, SpawnGroup group, L2Npc npc)
    {
    }

    /**
     * @param template
     * @param group
     * @param npc
     * @param killer
     */
    public void onSpawnNpcDeath(L2Spawn template, SpawnGroup group, L2Npc npc, L2Character killer) {

    }

    protected final List<AbstractEventListener> setAttackableKillId(Consumer<OnAttackableKill> callback, int... npcIds)
    {
        for (int id : npcIds)
        {
            if (NpcTable.getInstance().getTemplate(id) == null)
            {
                LOGGER.severe(super.getClass().getSimpleName() + ": Found addKillId for non existing NPC: " + id + "!");
            }
        }
        return registerConsumer(callback, EventType.ON_ATTACKABLE_KILL, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides callback operation when Attackable dies from a player.
     * @param callback
     * @param npcIds
     * @return
     */
    protected final List<AbstractEventListener> setAttackableKillId(Consumer<OnAttackableKill> callback, Collection<Integer> npcIds)
    {
        for (int id : npcIds)
        {
            if (NpcTable.getInstance().getTemplate(id) == null)
            {
                LOGGER.severe(super.getClass().getSimpleName() + ": Found addKillId for non existing NPC: " + id + "!");
            }
        }
        return registerConsumer(callback, EventType.ON_ATTACKABLE_KILL, ListenerRegisterType.NPC, npcIds);
    }

}
