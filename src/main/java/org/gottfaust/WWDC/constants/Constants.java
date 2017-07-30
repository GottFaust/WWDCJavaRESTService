package org.gottfaust.WWDC.constants;

public class Constants {

    /** Calculation variables **/
    public static final double CONTINUOUS_MULT = 4.0;

    /** Mod Type Constants **/
    public static final String MOD_TYPE_DAMAGE_BONUS = "DamageBonus";
    public static final String MOD_TYPE_FIRE_DAMAGE = "FireDamage";
    public static final String MOD_TYPE_LIGHTNING_DAMAGE = "LightningDamage";
    public static final String MOD_TYPE_ICE_DAMAGE = "IceDamage";
    public static final String MOD_TYPE_PUNCTURE_DAMAGE = "PunctureDamage";
    public static final String MOD_TYPE_TOXIN_DAMAGE = "ToxinDamage";
    public static final String MOD_TYPE_IMPACT_DAMAGE = "ImpactDamage";
    public static final String MOD_TYPE_SLASH_DAMAGE = "SlashDamage";
    public static final String MOD_TYPE_MISC_DAMAGE = "MiscDamage";
    public static final String MOD_TYPE_MULTISHOT = "Multishot";
    public static final String MOD_TYPE_FIRE_RATE = "FireRate";
    public static final String MOD_TYPE_RELOAD_SPEED = "ReladSpeed";
    public static final String MOD_TYPE_MAG_CAP = "MagCap";
    public static final String MOD_TYPE_AMMO_CAP = "AmmoCap";
    public static final String MOD_TYPE_CORPUS_DAMAGE = "CorpusDamage";
    public static final String MOD_TYPE_GRINEER_DAMAGE = "GrineerDamage";
    public static final String MOD_TYPE_INFESTED_DAMAGE = "InfestedDamage";
    public static final String MOD_TYPE_CRIT_CHANCE = "CritChance";
    public static final String MOD_TYPE_CRIT_MULTIPLIER = "CritMultiplier";
    public static final String MOD_TYPE_STATUS_CHANCE = "StatusChance";
    public static final String MOD_TYPE_STATUS_DURATION = "StatusDuration";
    public static final String MOD_TYPE_FIRST_SHOT_DAMAGE = "FirstShotDamage";
    public static final String MOD_TYPE_ZOOM = "Zoom";
    public static final String MOD_TYPE_OBJECT_PIERCE = "ObjectPierce";
    public static final String MOD_TYPE_AMMO_MUTATOR = "AmmoMutator";
    public static final String MOD_TYPE_ACCURACY = "AccuracyBonus";
    public static final String MOD_TYPE_RECOIL = "RecoilBonus";
    public static final String MOD_TYPE_SPREAD = "SpreadBonus";
    public static final String MOD_TYPE_SILENCE = "Silence";
    public static final String MOD_TYPE_FLAT_DAMAGE = "FlatDamageBonus";
    public static final String MOD_TYPE_DEAD_AIM = "DeadAim";
    public static final String MOD_TYPE_FLAT_STATUS = "FlatStatusBonus";
    public static final String MOD_TYPE_FLAT_MAG = "FlatMagBonus";

    /** Damage Types **/
    public static final String PHYSICAL_WEAPON_DAMAGE = "Physical";
    public static final String IMPACT_WEAPON_DAMAGE = "Impact";
    public static final String PUNCTURE_WEAPON_DAMAGE = "Puncture";
    public static final String SLASH_WEAPON_DAMAGE = "Slash";
    public static final String FIRE_WEAPON_DAMAGE = "Fire";
    public static final String ICE_WEAPON_DAMAGE = "Ice";
    public static final String ELECTRIC_WEAPON_DAMAGE = "Electric";
    public static final String TOXIN_WEAPON_DAMAGE = "Toxin";
    public static final String BLAST_WEAPON_DAMAGE = "Blast";
    public static final String MAGNETIC_WEAPON_DAMAGE = "Magnetic";
    public static final String GAS_WEAPON_DAMAGE = "Gas";
    public static final String RADIATION_WEAPON_DAMAGE = "Radiation";
    public static final String CORROSIVE_WEAPON_DAMAGE = "Corrosive";
    public static final String VIRAL_WEAPON_DAMAGE = "Viral";

    public static final String NO_WEAPON_DAMAGE = "None";

    /** Weapon Modes **/
    public static final String BURST = "Burst";
    public static final String CHARGE = "Charge";
    public static final String CONTINUOUS = "Continuous";
    public static final String FULL_AUTO = "Full-Auto";
    public static final String FULL_AUTO_RAMP_UP = "Full-Auto (Ramp-up)";
    public static final String FULL_AUTO_BULLET_RAMP = "Full-Auto (Bullet-Ramp)";
    public static final String SEMI_AUTO = "Semi-Auto";

    /** Mod Polarities **/
    public static final String NONE = "None";
    public static final String DASH = "~";
    public static final String EQUALS = "=";
    public static final String D = "D";
    public static final String V = "V";

    /** UI Text Values **/
    public static final String MOD_LABEL = "Mod:";
    public static final String RANK_LABEL = "Rank:";
    public static final String SLOT_POLARITY_LABEL = ":Pol:";
    public static final String MOD_POLARITY_LABEL = "Mod-Pol:";
    public static final String COST_LABEL = ":Cost:";
    public static final String CUSTOM_WEAPON = "Custom";

    /** Enemy Values **/
    public static final String ENEMY_TYPE_INFESTED = "Infested";
    public static final String ENEMY_TYPE_CORPUS = "Corpus";
    public static final String ENEMY_TYPE_GRINEER = "Grineer";
    public static final String ENEMY_SURFACE_CLONE_FLESH = "CloneFlesh";
    public static final String ENEMY_SURFACE_FERRITE_ARMOR = "FerriteArmor";
    public static final String ENEMY_SURFACE_ALLOY_ARMOR = "AlloyArmor";
    public static final String ENEMY_SURFACE_MECHANICAL = "Mechanical";
    public static final String ENEMY_SURFACE_CORPUS_FLESH = "CorpusFlesh";
    public static final String ENEMY_SURFACE_PROTO_SHIELD = "ProtoShield";
    public static final String ENEMY_SURFACE_INFESTED_FLESH = "InfestedFlesh";
    public static final String ENEMY_SURFACE_FOSSILIZED = "Fossilized";
    public static final String ENEMY_SURFACE_SINEW = "Sinew";
    public static final String ENEMY_SURFACE_SHIELDS = "Shields";
    public static final String ENEMY_SURFACE_ROBOTIC = "Robotic";
    public static final String ENEMY_SURFACE_INFESTED = "Infested";

    /** Log prints **/
    public static final String REALLY_REALLY_BAD = "\n\n" +
            "                      ________ \n" +
            "                  , -'\"          ``~ , \n" +
            "               , -\"                 \"- , \n" +
            "             ,/                        \":, \n" +
            "           ,?                             , \n" +
            "          /                               ,} \n" +
            "         /                           ,:`^` } \n" +
            "        /                          ,:\"     / \n" +
            "       ?   __                     :`      / \n" +
            "       /__ (   \"~-,_             ,:`     / \n" +
            "      /(_  \"~,_    \"~,_        ,:`     _/ \n" +
            "      {_$;_   \"=,_    \"-,_ ,-~-,},~\";/  } \n" +
            "      ((   *~_    \"=- _   \";,, /` /\"   / \n" +
            "  ,,,___ `~,   \"~ ,          `   }    / \n" +
            "      (  `=-,,    `            (;_,,-\" \n" +
            "      / `~,   `-                   / \n" +
            "       `~ *-,                   |, /   ,__ \n" +
            ",,_     } >- _                  |       `=~-, \n" +
            "   `=~-,__   `,                 \n" +
            "          `=~-,, ,                \n" +
            "                `:,,              `  __ \n" +
            "                   `=-,          ,%`>--==`` \n" +
            "                       _   _,-%    ` \n" +
            "                                   , \n" +
            "      You've gone and done it now!\n\n";
}
