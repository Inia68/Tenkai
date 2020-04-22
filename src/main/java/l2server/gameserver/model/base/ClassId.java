package l2server.gameserver.model.base;

import l2server.gameserver.model.interfaces.IIdentifiable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum ClassId implements IIdentifiable
{
    FIGHTER(0, false, Race.Human, null),

    WARRIOR(1, false, Race.Human, FIGHTER),
    GLADIATOR(2, false, Race.Human, WARRIOR),
    WARLORD(3, false, Race.Human, WARRIOR),
    KNIGHT(4, false, Race.Human, FIGHTER),
    PALADIN(5, false, Race.Human, KNIGHT),
    DARK_AVENGER(6, false, Race.Human, KNIGHT),
    ROGUE(7, false, Race.Human, FIGHTER),
    TREASURE_HUNTER(8, false, Race.Human, ROGUE),
    HAWKEYE(9, false, Race.Human, ROGUE),

    MAGE(10, true, Race.Human, null),
    WIZARD(11, true, Race.Human, MAGE),
    SORCERER(12, true, Race.Human, WIZARD),
    NECROMANCER(13, true, Race.Human, WIZARD),
    WARLOCK(14, true, true, Race.Human, WIZARD),
    CLERIC(15, true, Race.Human, MAGE),
    BISHOP(16, true, Race.Human, CLERIC),
    PROPHET(17, true, Race.Human, CLERIC),

    ELVEN_FIGHTER(18, false, Race.Elf, null),
    ELVEN_KNIGHT(19, false, Race.Elf, ELVEN_FIGHTER),
    TEMPLE_KNIGHT(20, false, Race.Elf, ELVEN_KNIGHT),
    SWORDSINGER(21, false, Race.Elf, ELVEN_KNIGHT),
    ELVEN_SCOUT(22, false, Race.Elf, ELVEN_FIGHTER),
    PLAINS_WALKER(23, false, Race.Elf, ELVEN_SCOUT),
    SILVER_RANGER(24, false, Race.Elf, ELVEN_SCOUT),

    ELVEN_MAGE(25, true, Race.Elf, null),
    ELVEN_WIZARD(26, true, Race.Elf, ELVEN_MAGE),
    SPELLSINGER(27, true, Race.Elf, ELVEN_WIZARD),
    ELEMENTAL_SUMMONER(28, true, true, Race.Elf, ELVEN_WIZARD),
    ORACLE(29, true, Race.Elf, ELVEN_MAGE),
    ELDER(30, true, Race.Elf, ORACLE),

    DARK_FIGHTER(31, false, Race.DarkElf, null),
    PALUS_KNIGHT(32, false, Race.DarkElf, DARK_FIGHTER),
    SHILLIEN_KNIGHT(33, false, Race.DarkElf, PALUS_KNIGHT),
    BLADEDANCER(34, false, Race.DarkElf, PALUS_KNIGHT),
    ASSASSIN(35, false, Race.DarkElf, DARK_FIGHTER),
    ABYSS_WALKER(36, false, Race.DarkElf, ASSASSIN),
    PHANTOM_RANGER(37, false, Race.DarkElf, ASSASSIN),

    DARK_MAGE(38, true, Race.DarkElf, null),
    DARK_WIZARD(39, true, Race.DarkElf, DARK_MAGE),
    SPELLHOWLER(40, true, Race.DarkElf, DARK_WIZARD),
    PHANTOM_SUMMONER(41, true, true, Race.DarkElf, DARK_WIZARD),
    SHILLIEN_ORACLE(42, true, Race.DarkElf, DARK_MAGE),
    SHILLIEN_ELDER(43, true, Race.DarkElf, SHILLIEN_ORACLE),

    ORC_FIGHTER(44, false, Race.Orc, null),
    ORC_RAIDER(45, false, Race.Orc, ORC_FIGHTER),
    DESTROYER(46, false, Race.Orc, ORC_RAIDER),
    ORC_MONK(47, false, Race.Orc, ORC_FIGHTER),
    TYRANT(48, false, Race.Orc, ORC_MONK),

    ORC_MAGE(49, true, Race.Orc, null),
    ORC_SHAMAN(50, true, Race.Orc, ORC_MAGE),
    OVERLORD(51, true, Race.Orc, ORC_SHAMAN),
    WARCRYER(52, true, Race.Orc, ORC_SHAMAN),

    DWARVEN_FIGHTER(53, false, Race.Dwarf, null),
    SCAVENGER(54, false, Race.Dwarf, DWARVEN_FIGHTER),
    BOUNTY_HUNTER(55, false, Race.Dwarf, SCAVENGER),
    ARTISAN(56, false, Race.Dwarf, DWARVEN_FIGHTER),
    WARSMITH(57, false, Race.Dwarf, ARTISAN),

    DUELIST(88, false, Race.Human, GLADIATOR),
    DREADNOUGHT(89, false, Race.Human, WARLORD),
    PHOENIX_KNIGHT(90, false, Race.Human, PALADIN),
    HELL_KNIGHT(91, false, Race.Human, DARK_AVENGER),
    SAGITTARIUS(92, false, Race.Human, HAWKEYE),
    ADVENTURER(93, false, Race.Human, TREASURE_HUNTER),
    ARCHMAGE(94, true, Race.Human, SORCERER),
    SOULTAKER(95, true, Race.Human, NECROMANCER),
    ARCANA_LORD(96, true, true, Race.Human, WARLOCK),
    CARDINAL(97, true, Race.Human, BISHOP),
    HIEROPHANT(98, true, Race.Human, PROPHET),

    EVA_TEMPLAR(99, false, Race.Elf, TEMPLE_KNIGHT),
    SWORD_MUSE(100, false, Race.Elf, SWORDSINGER),
    WIND_RIDER(101, false, Race.Elf, PLAINS_WALKER),
    MOONLIGHT_SENTINEL(102, false, Race.Elf, SILVER_RANGER),
    MYSTIC_MUSE(103, true, Race.Elf, SPELLSINGER),
    ELEMENTAL_MASTER(104, true, true, Race.Elf, ELEMENTAL_SUMMONER),
    EVA_SAINT(105, true, Race.Elf, ELDER),

    SHILLIEN_TEMPLAR(106, false, Race.DarkElf, SHILLIEN_KNIGHT),
    SPECTRAL_DANCER(107, false, Race.DarkElf, BLADEDANCER),
    GHOST_HUNTER(108, false, Race.DarkElf, ABYSS_WALKER),
    GHOST_SENTINEL(109, false, Race.DarkElf, PHANTOM_RANGER),
    STORM_SCREAMER(110, true, Race.DarkElf, SPELLHOWLER),
    SPECTRAL_MASTER(111, true, true, Race.DarkElf, PHANTOM_SUMMONER),
    SHILLIEN_SAINT(112, true, Race.DarkElf, SHILLIEN_ELDER),

    TITAN(113, false, Race.Orc, DESTROYER),
    GRAND_KHAVATARI(114, false, Race.Orc, TYRANT),
    DOMINATOR(115, true, Race.Orc, OVERLORD),
    DOOMCRYER(116, true, Race.Orc, WARCRYER),

    FORTUNE_SEEKER(117, false, Race.Dwarf, BOUNTY_HUNTER),
    MAESTRO(118, false, Race.Dwarf, WARSMITH),

    MALE_SOLDIER(123, false, Race.Kamael, null),
    FEMALE_SOLDIER(124, false, Race.Kamael, null),
    TROOPER(125, false, Race.Kamael, MALE_SOLDIER),
    WARDER(126, false, Race.Kamael, FEMALE_SOLDIER),
    BERSERKER(127, false, Race.Kamael, TROOPER),
    MALE_SOULBREAKER(128, false, Race.Kamael, TROOPER),
    FEMALE_SOULBREAKER(129, false, Race.Kamael, WARDER),
    ARBALESTER(130, false, Race.Kamael, WARDER),
    DOOMBRINGER(131, false, Race.Kamael, BERSERKER),
    MALE_SOUL_HOUND(132, false, Race.Kamael, MALE_SOULBREAKER),
    FEMALE_SOUL_HOUND(133, false, Race.Kamael, FEMALE_SOULBREAKER),
    TRICKSTER(134, false, Race.Kamael, ARBALESTER),
    INSPECTOR(135, false, Race.Kamael, WARDER),
    JUDICATOR(136, false, Race.Kamael, INSPECTOR),

    SIGEL_KNIGHT(139, false, null, null),
    TYRR_WARRIOR(140, false, null, null),
    OTHELL_ROGUE(141, false, null, null),
    YUL_ARCHER(142, false, null, null),
    FEOH_WIZARD(143, false, null, null),
    ISS_ENCHANTER(144, false, null, null),
    WYNN_SUMMONER(145, false, null, null),
    AEORE_HEALER(146, false, null, null),

    SIGEL_PHOENIX_KNIGHT(148, false, Race.Human, PHOENIX_KNIGHT),
    SIGEL_HELL_KNIGHT(149, false, Race.Human, HELL_KNIGHT),
    SIGEL_EVA_TEMPLAR(150, false, Race.Elf, EVA_TEMPLAR),
    SIGEL_SHILLIEN_TEMPLAR(151, false, Race.DarkElf, SHILLIEN_TEMPLAR),
    TYRR_DUELIST(152, false, Race.Human, DUELIST),
    TYRR_DREADNOUGHT(153, false, Race.Human, DREADNOUGHT),
    TYRR_TITAN(154, false, Race.Orc, TITAN),
    TYRR_GRAND_KHAVATARI(155, false, Race.Orc, GRAND_KHAVATARI),
    TYRR_MAESTRO(156, false, Race.Dwarf, MAESTRO),
    TYRR_DOOMBRINGER(157, false, Race.Kamael, DOOMBRINGER),
    OTHELL_ADVENTURER(158, false, Race.Human, ADVENTURER),
    OTHELL_WIND_RIDER(159, false, Race.Elf, WIND_RIDER),
    OTHELL_GHOST_HUNTER(160, false, Race.DarkElf, GHOST_HUNTER),
    OTHELL_FORTUNE_SEEKER(161, false, Race.Dwarf, FORTUNE_SEEKER),
    YUL_SAGITTARIUS(162, false, Race.Human, SAGITTARIUS),
    YUL_MOONLIGHT_SENTINEL(163, false, Race.Elf, MOONLIGHT_SENTINEL),
    YUL_GHOST_SENTINEL(164, false, Race.DarkElf, GHOST_SENTINEL),
    YUL_TRICKSTER(165, false, Race.Kamael, TRICKSTER),
    FEOH_ARCHMAGE(166, true, Race.Human, ARCHMAGE),
    FEOH_SOULTAKER(167, true, Race.Human, SOULTAKER),
    FEOH_MYSTIC_MUSE(168, true, Race.Elf, MYSTIC_MUSE),
    FEOH_STORM_SCREAMER(169, true, Race.DarkElf, STORM_SCREAMER),
    FEOH_SOUL_HOUND(170, true, Race.Kamael, MALE_SOUL_HOUND), // fix me ?
    ISS_HIEROPHANT(171, true, Race.Human, HIEROPHANT),
    ISS_SWORD_MUSE(172, false, Race.Elf, SWORD_MUSE),
    ISS_SPECTRAL_DANCER(173, false, Race.DarkElf, SPECTRAL_DANCER),
    ISS_DOMINATOR(174, true, Race.Orc, DOMINATOR),
    ISS_DOOMCRYER(175, true, Race.Orc, DOOMCRYER),
    WYNN_ARCANA_LORD(176, true, true, Race.Human, ARCANA_LORD),
    WYNN_ELEMENTAL_MASTER(177, true, true, Race.Elf, ELEMENTAL_MASTER),
    WYNN_SPECTRAL_MASTER(178, true, true, Race.DarkElf, SPECTRAL_MASTER),
    AEORE_CARDINAL(179, true, Race.Human, CARDINAL),
    AEORE_EVA_SAINT(180, true, Race.Elf, EVA_SAINT),
    AEORE_SHILLIEN_SAINT(181, true, Race.DarkElf, SHILLIEN_SAINT),

    ERTHEIA_FIGHTER(182, false, Race.Ertheia, null),
    ERTHEIA_WIZARD(183, true, Race.Ertheia, null),

    MARAUDER(184, false, Race.Ertheia, ERTHEIA_FIGHTER),
    CLOUD_BREAKER(185, true, Race.Ertheia, ERTHEIA_WIZARD),

    RIPPER(186, false, Race.Ertheia, MARAUDER),
    STRATOMANCER(187, true, Race.Ertheia, CLOUD_BREAKER),

    EVISCERATOR(188, false, Race.Ertheia, RIPPER),
    SAYHA_SEER(189, true, Race.Ertheia, STRATOMANCER);

    /** The Identifier of the Class */
    private final int _id;

    /** True if the class is a mage class */
    private final boolean _isMage;

    /** True if the class is a summoner class */
    private final boolean _isSummoner;

    /** The Race object of the class */
    private final Race _race;

    /** The parent ClassId or null if this class is a root */
    private final ClassId _parent;

    /** List of available Class for next transfer **/
    private final Set<ClassId> _nextClassIds = new HashSet<>(1);

    private static Map<Integer, ClassId> _classIdMap = new HashMap<>(ClassId.values().length);
    static
    {
        for (ClassId classId : ClassId.values())
        {
            _classIdMap.put(classId.getId(), classId);
        }
    }

    public static ClassId getClassId(int cId)
    {
        return _classIdMap.get(cId);
    }

    /**
     * Class constructor.
     * @param pId the class Id.
     * @param pIsMage {code true} if the class is mage class.
     * @param race the race related to the class.
     * @param pParent the parent class Id.
     */
    private ClassId(int pId, boolean pIsMage, Race race, ClassId pParent)
    {
        _id = pId;
        _isMage = pIsMage;
        _isSummoner = false;
        _race = race;
        _parent = pParent;

        if (_parent != null)
        {
            _parent.addNextClassId(this);
        }
    }

    /**
     * Class constructor.
     * @param pId the class Id.
     * @param pIsMage {code true} if the class is mage class.
     * @param pIsSummoner {code true} if the class is summoner class.
     * @param race the race related to the class.
     * @param pParent the parent class Id.
     */
    private ClassId(int pId, boolean pIsMage, boolean pIsSummoner, Race race, ClassId pParent)
    {
        _id = pId;
        _isMage = pIsMage;
        _isSummoner = pIsSummoner;
        _race = race;
        _parent = pParent;

        if (_parent != null)
        {
            _parent.addNextClassId(this);
        }
    }

    /**
     * Gets the ID of the class.
     * @return the ID of the class
     */
    @Override
    public int getId()
    {
        return _id;
    }

    /**
     * @return {code true} if the class is a mage class.
     */
    public boolean isMage()
    {
        return _isMage;
    }

    /**
     * @return {code true} if the class is a summoner class.
     */
    public boolean isSummoner()
    {
        return _isSummoner;
    }

    /**
     * @return the Race object of the class.
     */
    public Race getRace()
    {
        return _race;
    }

    /**
     * @param cid the parent ClassId to check.
     * @return {code true} if this Class is a child of the selected ClassId.
     */
    public boolean childOf(ClassId cid)
    {
        if (_parent == null)
        {
            return false;
        }

        if (_parent == cid)
        {
            return true;
        }

        return _parent.childOf(cid);
    }

    /**
     * @param cid the parent ClassId to check.
     * @return {code true} if this Class is equal to the selected ClassId or a child of the selected ClassId.
     */
    public boolean equalsOrChildOf(ClassId cid)
    {
        return (this == cid) || childOf(cid);
    }

    /**
     * @return the child level of this Class (0=root, 1=child leve 1...)
     */
    public int level()
    {
        if (_parent == null)
        {
            return 0;
        }

        return 1 + _parent.level();
    }

    /**
     * @return its parent Class Id
     */
    public ClassId getParent()
    {
        return _parent;
    }

    public ClassId getRootClassId()
    {
        if (_parent != null)
        {
            return _parent.getRootClassId();
        }
        return this;
    }

    /**
     * @return list of possible class transfer for this class
     */
    public Set<ClassId> getNextClassIds()
    {
        return _nextClassIds;
    }

    private final void addNextClassId(ClassId cId)
    {
        _nextClassIds.add(cId);
    }
}
