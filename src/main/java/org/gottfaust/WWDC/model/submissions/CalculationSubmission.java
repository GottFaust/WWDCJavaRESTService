package org.gottfaust.WWDC.model.submissions;

import org.gottfaust.WWDC.model.Mod;
import org.gottfaust.WWDC.model.Weapon;

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;

import static org.gottfaust.WWDC.constants.Constants.FULL_AUTO;
import static org.gottfaust.WWDC.constants.Constants.PHYSICAL_WEAPON_DAMAGE;

public class CalculationSubmission {

    /** The weapon **/
    public Weapon weapon;

    /** The mods **/
    public Mod[] mods;

    /**
     * Default CTOR
     */
    public CalculationSubmission() {
    }

    /**
     * Validates all of the relevant fields against the strict regex
     * @return valid/invalid
     */
    public boolean validate(){
        boolean isValid = true;
        return isValid;
    }

    /**
     * Validates that a string does not contain any non-word-non-digit characters
     * @param str String to valid
     * @return boolean valid/invalid
     */
    private boolean valid(String str){
        //Validate the string if it's not null, otherwise it's valid
        if(notNullOrEmpty(str)) {
            String regex = "^[a-zA-Z0-9\\s.\\-:\"',.;]+$";
            final Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(str).matches();
        }else{
            return true;
        }
    }

    /**
     * Checks to see if the string is not null or empty
     * @param str String to check
     * @return Boolean result
     */
    private boolean notNullOrEmpty(String str){
        return (str != null && !str.equals(""));
    }

    /**
     * Gets the weapon's active mods
     * @return
     */
    public Vector<Mod> getActiveMods(){
        Vector<Mod> modVec = new Vector<>(Arrays.asList(mods));
        return new Vector<>(modVec);
    }

    /**
     * Gets the weapon's mode of operation
     * @return mode
     */
    public String getWeaponMode(){
        return (weapon.mode != null) ? weapon.mode : FULL_AUTO;
    }

    /**
     * Gets the weapon's base damage type
     * @return type
     */
    public String getDamageType(){
        return (weapon.damageType != null) ? weapon.damageType : PHYSICAL_WEAPON_DAMAGE;
    }

    /**
     * Gets the name
     * @return name
     */
    public String getName(){
        return (weapon.name != null) ? weapon.name : "UNKNOWN";
    }

    /**
     * Gets the weapon's charge time
     * @return chargeTime
     */
    public double getChargeTime(){
        return (weapon.chargeTime != null) ? weapon.chargeTime : 0.0;
    }

    /**
     * Gets the weapon's burst count
     * @return burstCount
     */
    public int getBurstCount(){
        return (weapon.burstCount != null) ? weapon.burstCount.intValue() : 0;
    }

    /**
     * Gets the number of projectiles
     * @return projectiels
     */
    public double getProjectiles(){
        return (weapon.projectileCount != null) ? weapon.projectileCount : 0.0;
    }

    /**
     * Gets the status chance
     * @return status
     */
    public double getStatusChance(){
        return (weapon.statusChance != null) ? weapon.statusChance : 0.0;
    }

    /**
     * Gets the base damage
     * @return damage
     */
    public double getBaseDamage(){
        return (weapon.baseDamage != null) ? weapon.baseDamage : 0.0;
    }

    /**
     * Gets the impact damage
     * @return damage
     */
    public double getImpactDamage(){
        return (weapon.impactDamage != null) ? weapon.impactDamage : 0.0;
    }

    /**
     * Gets the puncture damage
     * @return damage
     */
    public double getPunctureDamage(){
        return (weapon.punctureDamage != null) ? weapon.punctureDamage : 0.0;
    }
    /**
     * Gets the slash damage
     * @return damage
     */
    public double getSlashDamage(){
        return (weapon.slashDamage != null) ? weapon.slashDamage : 0.0;
    }

    /**
     * Gets the fire rate
     * @return fireRate
     */
    public double getFireRate(){
        return (weapon.fireRate != null) ? weapon.fireRate : 0.0;
    }

    /**
     * Gets the mag size
     * @return magSize
     */
    public int getMagSize(){
        return (weapon.magSize != null) ? weapon.magSize.intValue() : 0;
    }

    /**
     * Gets the total ammo
     * @return totalAmmo
     */
    public int getTotalAmmo(){
        return (weapon.totalAmmo != null) ? weapon.totalAmmo.intValue() : 0;
    }

    /**
     * Gets the reload timer
     * @return reloadTime
     */
    public double getReloadTime(){
        return (weapon.reloadTime != null) ? weapon.reloadTime : 0.0;
    }

    /**
     * Gets the crit chance
     * @return critChance
     */
    public double getCritChance(){
        return (weapon.critChance != null) ? weapon.critChance : 0.0;
    }

    /**
     * Gets the crit multiplier
     * @return critMult
     */
    public double getCritMultiplier(){
        return (weapon.critDamage != null) ? weapon.critDamage : 0.0;
    }

    /**
     * Returns the active mods in a formatted string
     */
    public String getModsOutput(){
        String retStr = "";
        for(Mod mod : mods){
            String modStr = (mod.name == null || mod.name.equals("")) ? "UNKNOWN" : mod.name;
            retStr += "\n" + modStr + ",";
        }
        return retStr;
    }
}
