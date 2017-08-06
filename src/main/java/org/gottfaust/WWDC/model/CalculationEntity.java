package org.gottfaust.WWDC.model;

import org.gottfaust.WWDC.model.damage.Damage;
import org.gottfaust.WWDC.model.damage.SurfaceDamage;

import java.util.Vector;

public class CalculationEntity {

    /** Base Values **/
    public String weaponName = "";
    public String weaponMode = "";
    public String damageType = "";
    public double chargeTime = 0.0;
    public double fireRate = 0.0;
    public double reloadTime = 0.0;
    public double critChance = 0.0;
    public double critMult = 0.0;
    public double projectileCount = 0.0;
    public double firstShotDamageMult = 1.0;
    public double statusChance = 0.0;
    public double statusDuration = 1.0;
    public double damageMult = 1.0;
    public double deadAimMult = 1.0;
    public double flatDamageBonus = 0.0;
    public int mag = 0;
    public int ammoCap = 0;
    public int burstCount = 0;

    /** Active Mods **/
    public Vector<Mod> activeMods = new Vector<>();

    /** Calculated Values **/
    public int finalMag = 0;
    public int finalAmmo = 0;
    public double finalIterationTime = 0.0;
    public double finalIterationsPerMinute = 0.0;
    public double finalCritShots = 0.0;
    public double finalNormalShots = 0.0;
    public double finalCritChance = 0.0;
    public double finalCritMult = 0.0;
    public double finalFireRate = 0.0;
    public double finalReloadTime = 0.0;
    public double finalProjectileCount = 0.0;
    public double finalFirstShotDamageMult = 1.0;
    public double finalStatusChance = 0.0;
    public double finalStatusDuration = 1.0;
    public double finalDamageMult = 1.0;
    public double finalDeadAimMult = 1.0;
    public double finalFlatDamageBonus = 0.0;
    public double finalCorpusMult = 1.0;
    public double finalGrineerMult = 1.0;
    public double finalInfestedMult = 1.0;
    public double continuousDrainRate = 0.0;
    public double procsPerSecond = 0.0;
    public double burstProcsPerSecond = 0.0;
    public int slashStacks = 0;
    public int fireStacks = 0;
    public int toxinStacks = 0;
    public int gasStacks = 0;

    /** Damage Values **/
    public Damage raw = new Damage();
    public Damage impact = new Damage();
    public Damage puncture = new Damage();
    public Damage slash = new Damage();
    public Damage fire = new Damage();
    public Damage ice = new Damage();
    public Damage electric = new Damage();
    public Damage toxin = new Damage();
    public Damage blast = new Damage();
    public Damage magnetic = new Damage();
    public Damage gas = new Damage();
    public Damage radiation = new Damage();
    public Damage corrosive = new Damage();
    public Damage viral = new Damage();
    public SurfaceDamage corpus = new SurfaceDamage();
    public SurfaceDamage grineer = new SurfaceDamage();
    public SurfaceDamage infested = new SurfaceDamage();
    public SurfaceDamage cloneFlesh = new SurfaceDamage();
    public SurfaceDamage ferrite = new SurfaceDamage();
    public SurfaceDamage alloy = new SurfaceDamage();
    public SurfaceDamage mechanical = new SurfaceDamage();
    public SurfaceDamage corpusFlesh = new SurfaceDamage();
    public SurfaceDamage shield = new SurfaceDamage();
    public SurfaceDamage protoShield = new SurfaceDamage();
    public SurfaceDamage robotic = new SurfaceDamage();
    public SurfaceDamage infestedFlesh = new SurfaceDamage();
    public SurfaceDamage fossilized = new SurfaceDamage();
    public SurfaceDamage sinew = new SurfaceDamage();
}
