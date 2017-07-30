package org.gottfaust.WWDC.controller;

import org.gottfaust.WWDC.controller.interfaces.IWWDCController;
import org.gottfaust.WWDC.controller.services.CalculationService;
import org.gottfaust.WWDC.model.CalculationEntity;
import org.gottfaust.WWDC.model.Mod;
import org.gottfaust.WWDC.model.builders.CalculationResponseBuilder;
import org.gottfaust.WWDC.model.exceptions.WWDCAuthException;
import org.gottfaust.WWDC.model.factories.ResponseFactory;
import org.gottfaust.WWDC.model.submissions.CalculationSubmission;
import org.jboss.logging.Logger;
import org.gottfaust.WWDC.model.exceptions.WWDCDatabaseException;
import org.gottfaust.WWDC.model.exceptions.WWDCSubmissionException;
import org.gottfaust.WWDC.model.interfaces.IWWDCResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.text.DecimalFormat;
import java.util.Vector;

import static org.gottfaust.WWDC.constants.Constants.*;

@RestController
public class CalculationController implements IWWDCController {

    /** The Queue Service **/
    private CalculationService service = new CalculationService();

    /** The log4j Logger **/
    private static final Logger LOGGER = Logger.getLogger(CalculationController.class);

    @CrossOrigin
    @RequestMapping(value = "/api/calculate", method = RequestMethod.POST)
    public ResponseEntity getQueue(HttpServletRequest request,
                                   @RequestBody(required = false) CalculationSubmission submission)
            throws WWDCAuthException,
            WWDCSubmissionException,
            WWDCDatabaseException
    {
        //The IHateos self link
        String self = "/api/calculate";
    
        //Validate the submission
        if(submission != null && !submission.validate()){
            throw new WWDCSubmissionException(self, "The submission was invalid.");
        }
    
        //Setup the builder
        CalculationResponseBuilder builder = new CalculationResponseBuilder();
        builder.self = self;
        builder.results = runCalculations(submission);
    
        //Setup the final response
        IWWDCResponse response = ResponseFactory.buildResponse(builder);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Calculates and Returns the required data
     * @param submission
     * @return
     */
    protected String runCalculations(CalculationSubmission submission){

        //Setup the CalculationEntity to hold all of the values for this calculation iteration
        CalculationEntity calc = new CalculationEntity();

        //Get the base values from the selected weapon
        getBaseValues(submission, calc);

        //Calculate the final values based on weapon parameters and Active Mods
        calculateFinals(calc);

        //Calculate miscellaneous values
        calculateMiscValues(calc);

        //Calculate damage per shot
        calculateDamagePerShot(calc);

        //Calculate the damage per magazine
        calculateDamagePerIteration(calc);

        //Calculate the damage per minute
        calculateDamagePerMinute(calc);

        //Calculate the damage per second
        calculateDamagePerSecond(calc);

        //Calculate the burst damage per second
        calculateBurstDamagePerSecond(calc);

        //Return the formatted calculations
        return buildOutput(submission, calc);
    }

    /**
     * Gets the base values form the supplied submission and initializes the CalculationEntity with them
     * @param submission
     * @param calc
     */
    protected static void getBaseValues(CalculationSubmission submission, CalculationEntity calc){
        
        //Get the base values from the submission
        calc.weaponName = submission.getName();
        calc.weaponMode = submission.getWeaponMode();
        calc.damageType = submission.getDamageType();
        calc.chargeTime = submission.getChargeTime();
        calc.fireRate = submission.getFireRate();
        calc.reloadTime = submission.getReloadTime();
        calc.critChance = submission.getCritChance();
        calc.critMult = submission.getCritMultiplier();
        calc.projectileCount = submission.getProjectiles();
        calc.firstShotDamageMult = 1.0;
        calc.statusChance = submission.getStatusChance();
        calc.mag = submission.getMagSize();
        calc.ammoCap = submission.getTotalAmmo();
        calc.burstCount = submission.getBurstCount();

        //Setup the base damage from the submission
        switch(calc.damageType){
            case PHYSICAL_WEAPON_DAMAGE:
                calc.impact.base = submission.getImpactDamage();
                calc.puncture.base = submission.getPunctureDamage();
                calc.slash.base = submission.getSlashDamage();
                break;
            case FIRE_WEAPON_DAMAGE:
                calc.fire.base = submission.getBaseDamage();
                break;
            case ICE_WEAPON_DAMAGE:
                calc.ice.base = submission.getBaseDamage();
                break;
            case ELECTRIC_WEAPON_DAMAGE:
                calc.electric.base = submission.getBaseDamage();
                break;
            case TOXIN_WEAPON_DAMAGE:
                calc.toxin.base = submission.getBaseDamage();
                break;
            case BLAST_WEAPON_DAMAGE:
                calc.blast.base = submission.getBaseDamage();
                break;
            case MAGNETIC_WEAPON_DAMAGE:
                calc.magnetic.base = submission.getBaseDamage();
                break;
            case GAS_WEAPON_DAMAGE:
                calc.gas.base = submission.getBaseDamage();
                break;
            case RADIATION_WEAPON_DAMAGE:
                calc.radiation.base = submission.getBaseDamage();
                break;
            case CORROSIVE_WEAPON_DAMAGE:
                calc.corrosive.base = submission.getBaseDamage();
                break;
            case VIRAL_WEAPON_DAMAGE:
                calc.viral.base = submission.getBaseDamage();
                break;
            default:
                break;
        }

        //Setup the raw damage total
        calc.raw.base = calc.impact.base +
                calc.puncture.base +
                calc.slash.base +
                calc.fire.base +
                calc.ice.base +
                calc.electric.base +
                calc.toxin.base +
                calc.blast.base +
                calc.magnetic.base +
                calc.gas.base +
                calc.radiation.base +
                calc.corrosive.base +
                calc.viral.base;

        //Factor for multiple projectiles per shot
        if(calc.projectileCount > 1.0){
            calc.raw.base /= calc.projectileCount;
            calc.statusChance /= calc.projectileCount;
            calc.impact.base /= calc.projectileCount;
            calc.puncture.base /= calc.projectileCount;
            calc.slash.base /= calc.projectileCount;
            calc.fire.base /= calc.projectileCount;
            calc.ice.base /= calc.projectileCount;
            calc.electric.base /= calc.projectileCount;
            calc.toxin.base /= calc.projectileCount;
            calc.blast.base/= calc.projectileCount;
            calc.magnetic.base /= calc.projectileCount;
            calc.gas.base /= calc.projectileCount;
            calc.radiation.base /= calc.projectileCount;
            calc.corrosive.base /= calc.projectileCount;
            calc.viral.base /= calc.projectileCount;
        }

        //Calculations based on weapon type
        switch(calc.weaponMode){
            case CONTINUOUS:
                calc.continuousDrainRate = calc.fireRate;
                calc.fireRate = CONTINUOUS_MULT;
                calc.damageMult *= calc.continuousDrainRate;
                calc.statusChance /= CONTINUOUS_MULT;
                break;
            case CHARGE:
                double fireRateAddition = 60.0 / calc.chargeTime / 60.0;
                calc.fireRate += fireRateAddition;
                break;
            case BURST:
                calc.projectileCount *= calc.burstCount;
                break;
        }

        //Mod Vectors
        calc.activeMods = submission.getActiveMods();
    }

    /**
     * Calculates the final modded values
     * @param calc
     */
    private static void calculateFinals(CalculationEntity calc){
        //Initialize mod vectors
        Vector<Mod> combinedMods = new Vector<>();
        Vector<Double> magMods = new Vector<>();
        Vector<Double> ammoMods = new Vector<>();
        Vector<Double> critChanceMods = new Vector<>();
        Vector<Double> critMultMods = new Vector<>();
        Vector<Double> fireRateMods = new Vector<>();
        Vector<Double> reloadTimeMods = new Vector<>();
        Vector<Double> damageMultMods = new Vector<>();
        Vector<Double> impactDamageMods = new Vector<>();
        Vector<Double> punctureDamageMods = new Vector<>();
        Vector<Double> slashDamageMods = new Vector<>();
        Vector<Double> fireDamageMods = new Vector<>();
        Vector<Double> iceDamageMods = new Vector<>();
        Vector<Double> electricDamageMods = new Vector<>();
        Vector<Double> toxinDamageMods = new Vector<>();
        Vector<Double> blastDamageMods = new Vector<>();
        Vector<Double> magneticDamageMods = new Vector<>();
        Vector<Double> gasDamageMods = new Vector<>();
        Vector<Double> radiationDamageMods = new Vector<>();
        Vector<Double> corrosiveDamageMods = new Vector<>();
        Vector<Double> viralDamageMods = new Vector<>();
        Vector<Double> projectileCountMods = new Vector<>();
        Vector<Double> firstShotDamageMods = new Vector<>();
        Vector<Double> statusChanceMods = new Vector<>();
        Vector<Double> statusDurationMods = new Vector<>();
        Vector<Double> corpusMods = new Vector<>();
        Vector<Double> grineerMods = new Vector<>();
        Vector<Double> infestedMods = new Vector<>();
        Vector<Double> flatDamageMods = new Vector<>();
        Vector<Double> deadAimMods = new Vector<>();
        Vector<Double> flatStatusMods = new Vector<>();
        Vector<Double> flatMagMods = new Vector<>();

        //Check for combined elements
        Mod primeMod = null;
        String primeModType = "";
        for(Mod tempMod : calc.activeMods){
            if(primeMod == null){
                if(tempMod.getEffectTypes().contains(MOD_TYPE_FIRE_DAMAGE)){
                    double modPower = tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_FIRE_DAMAGE));
                    if(blastDamageMods.size() > 0){
                        blastDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else if(radiationDamageMods.size() > 0){
                        radiationDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else if(gasDamageMods.size() > 0){
                        gasDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else{
                        primeMod = tempMod;
                        primeModType = MOD_TYPE_FIRE_DAMAGE;
                    }
                }
                if(tempMod.getEffectTypes().contains(MOD_TYPE_ICE_DAMAGE)){
                    double modPower = tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_ICE_DAMAGE));
                    if(blastDamageMods.size() > 0){
                        blastDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else if(magneticDamageMods.size() > 0){
                        magneticDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else if(viralDamageMods.size() > 0){
                        viralDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else{
                        primeMod = tempMod;
                        primeModType = MOD_TYPE_ICE_DAMAGE;
                    }
                }
                if(tempMod.getEffectTypes().contains(MOD_TYPE_LIGHTNING_DAMAGE)){
                    double modPower = tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_LIGHTNING_DAMAGE));
                    if(corrosiveDamageMods.size() > 0){
                        corrosiveDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else if(magneticDamageMods.size() > 0){
                        magneticDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else if(radiationDamageMods.size() > 0){
                        radiationDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else{
                        primeMod = tempMod;
                        primeModType = MOD_TYPE_LIGHTNING_DAMAGE;
                    }
                }
                if(tempMod.getEffectTypes().contains(MOD_TYPE_TOXIN_DAMAGE)){
                    double modPower = tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_TOXIN_DAMAGE));
                    if(corrosiveDamageMods.size() > 0){
                        corrosiveDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else if(viralDamageMods.size() > 0){
                        viralDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else if(gasDamageMods.size() > 0){
                        gasDamageMods.add(modPower);
                        combinedMods.add(tempMod);
                    }else{
                        primeMod = tempMod;
                        primeModType = MOD_TYPE_TOXIN_DAMAGE;
                    }
                }
            }else{
                if(tempMod.getEffectTypes().contains(MOD_TYPE_FIRE_DAMAGE)){
                    switch (primeModType) {
                        case MOD_TYPE_FIRE_DAMAGE:
                            //Don't Combine
                            break;
                        case MOD_TYPE_ICE_DAMAGE:
                            if (magneticDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (viralDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (gasDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (radiationDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_FIRE_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                blastDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                        case MOD_TYPE_LIGHTNING_DAMAGE:
                            if (corrosiveDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (magneticDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (blastDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (gasDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_FIRE_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                radiationDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                        case MOD_TYPE_TOXIN_DAMAGE:
                            if (corrosiveDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (viralDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (blastDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (radiationDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_FIRE_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                gasDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                    }
                }else if(tempMod.getEffectTypes().contains(MOD_TYPE_ICE_DAMAGE)){
                    switch (primeModType) {
                        case MOD_TYPE_FIRE_DAMAGE:
                            if (gasDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (radiationDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (magneticDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (viralDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_ICE_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                blastDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                        case MOD_TYPE_ICE_DAMAGE:
                            //Don't Combine
                            break;
                        case MOD_TYPE_LIGHTNING_DAMAGE:
                            if (corrosiveDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (radiationDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (blastDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (viralDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_ICE_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                magneticDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                        case MOD_TYPE_TOXIN_DAMAGE:
                            if (corrosiveDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (gasDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (blastDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (magneticDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_ICE_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                viralDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                    }
                }else if(tempMod.getEffectTypes().contains(MOD_TYPE_LIGHTNING_DAMAGE)){
                    switch (primeModType) {
                        case MOD_TYPE_FIRE_DAMAGE:
                            if (gasDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (blastDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (corrosiveDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (magneticDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_LIGHTNING_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                radiationDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                        case MOD_TYPE_ICE_DAMAGE:
                            if (blastDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (viralDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (corrosiveDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (radiationDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_LIGHTNING_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                magneticDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                        case MOD_TYPE_LIGHTNING_DAMAGE:
                            //Don't Combine
                            break;
                        case MOD_TYPE_TOXIN_DAMAGE:
                            if (viralDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (gasDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (radiationDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (magneticDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_LIGHTNING_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                corrosiveDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                    }
                }else if(tempMod.getEffectTypes().contains(MOD_TYPE_TOXIN_DAMAGE)){
                    switch (primeModType) {
                        case MOD_TYPE_FIRE_DAMAGE:
                            if (blastDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (radiationDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (corrosiveDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (viralDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_TOXIN_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                gasDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                        case MOD_TYPE_ICE_DAMAGE:
                            if (blastDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (magneticDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (corrosiveDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (gasDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_TOXIN_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                viralDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                        case MOD_TYPE_LIGHTNING_DAMAGE:
                            if (magneticDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (radiationDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (gasDamageMods.size() > 0) {
                                //Don't Combine
                            } else if (viralDamageMods.size() > 0) {
                                //Don't Combine
                            } else {
                                combinedMods.add(primeMod);
                                combinedMods.add(tempMod);
                                double primeEffectPower = (primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType)));
                                double secondEffectPower = (tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_TOXIN_DAMAGE)));
                                double jointPower = primeEffectPower + secondEffectPower;
                                corrosiveDamageMods.add(jointPower);
                                primeMod = null;
                                primeModType = "";
                            }
                            break;
                        case MOD_TYPE_TOXIN_DAMAGE:
                            //Don't Combine
                            break;
                    }
                }
            }
        }
        //Combine the base
        boolean baseCombined = false;
        if(primeMod != null && !calc.damageType.equals(PHYSICAL_WEAPON_DAMAGE)){
            if( calc.damageType.equals(FIRE_WEAPON_DAMAGE)
                    &&(blastDamageMods.size() == 0)
                    &&(radiationDamageMods.size() == 0)
                    &&(gasDamageMods.size() == 0)){
                switch (primeModType) {
                    case MOD_TYPE_FIRE_DAMAGE:
                        //Don't Combine
                        break;
                    case MOD_TYPE_ICE_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        blastDamageMods.add(jointPower);
                        calc.blast.base = calc.fire.base;
                        calc.fire.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                    case MOD_TYPE_LIGHTNING_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        radiationDamageMods.add(jointPower);
                        calc.radiation.base = calc.fire.base;
                        calc.fire.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                    case MOD_TYPE_TOXIN_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        gasDamageMods.add(jointPower);
                        calc.gas.base = calc.fire.base;
                        calc.fire.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                }
            }else if( calc.damageType.equals(ICE_WEAPON_DAMAGE)
                    &&(blastDamageMods.size() == 0)
                    &&(magneticDamageMods.size() == 0)
                    &&(viralDamageMods.size() == 0)){
                switch (primeModType) {
                    case MOD_TYPE_FIRE_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        blastDamageMods.add(jointPower);
                        calc.blast.base = calc.ice.base;
                        calc.ice.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                    case MOD_TYPE_ICE_DAMAGE:
                        //Don't Combine
                        break;
                    case MOD_TYPE_LIGHTNING_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        magneticDamageMods.add(jointPower);
                        calc.magnetic.base = calc.ice.base;
                        calc.ice.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                    case MOD_TYPE_TOXIN_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        viralDamageMods.add(jointPower);
                        calc.viral.base = calc.ice.base;
                        calc.ice.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                }
            }else if( calc.damageType.equals(ELECTRIC_WEAPON_DAMAGE)
                    &&(radiationDamageMods.size() == 0)
                    &&(magneticDamageMods.size() == 0)
                    &&(corrosiveDamageMods.size() == 0)){
                switch (primeModType) {
                    case MOD_TYPE_FIRE_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        radiationDamageMods.add(jointPower);
                        calc.radiation.base = calc.electric.base;
                        calc.electric.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                    case MOD_TYPE_ICE_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        magneticDamageMods.add(jointPower);
                        calc.magnetic.base = calc.electric.base;
                        calc.electric.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                    case MOD_TYPE_LIGHTNING_DAMAGE:
                        //Don't Combine
                        break;
                    case MOD_TYPE_TOXIN_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        corrosiveDamageMods.add(jointPower);
                        calc.corrosive.base = calc.electric.base;
                        calc.electric.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                }
            }else if (calc.damageType.equals(TOXIN_WEAPON_DAMAGE)
                    &&(gasDamageMods.size() == 0)
                    &&(viralDamageMods.size() == 0)
                    &&(corrosiveDamageMods.size() == 0)){
                switch (primeModType) {
                    case MOD_TYPE_FIRE_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        gasDamageMods.add(jointPower);
                        calc.gas.base = calc.toxin.base;
                        calc.toxin.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                    case MOD_TYPE_ICE_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        viralDamageMods.add(jointPower);
                        calc.viral.base = calc.toxin.base;
                        calc.toxin.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                    case MOD_TYPE_LIGHTNING_DAMAGE: {
                        combinedMods.add(primeMod);
                        double jointPower = primeMod.getEffectStrengths().get(primeMod.getEffectTypes().indexOf(primeModType));
                        corrosiveDamageMods.add(jointPower);
                        calc.corrosive.base = calc.toxin.base;
                        calc.toxin.base = 0.0;
                        baseCombined = true;
                        break;
                    }
                    case MOD_TYPE_TOXIN_DAMAGE:
                        //Don't Combine
                        break;
                }
            }
        }
        if(!baseCombined){
            switch (calc.damageType) {
                case FIRE_WEAPON_DAMAGE:
                    if (blastDamageMods.size() > 0) {
                        calc.blast.base = calc.fire.base;
                        calc.fire.base = 0.0;
                    } else if (radiationDamageMods.size() > 0) {
                        calc.radiation.base = calc.fire.base;
                        calc.fire.base = 0.0;
                    } else if (gasDamageMods.size() > 0) {
                        calc.gas.base = calc.fire.base;
                        calc.fire.base = 0.0;
                    }
                    break;
                case ICE_WEAPON_DAMAGE:
                    if (blastDamageMods.size() > 0) {
                        calc.blast.base = calc.ice.base;
                        calc.ice.base = 0.0;
                    } else if (magneticDamageMods.size() > 0) {
                        calc.magnetic.base = calc.ice.base;
                        calc.ice.base = 0.0;
                    } else if (viralDamageMods.size() > 0) {
                        calc.viral.base = calc.ice.base;
                        calc.ice.base = 0.0;
                    }
                    break;
                case ELECTRIC_WEAPON_DAMAGE:
                    if (radiationDamageMods.size() > 0) {
                        calc.radiation.base = calc.electric.base;
                        calc.electric.base = 0.0;
                    } else if (magneticDamageMods.size() > 0) {
                        calc.magnetic.base = calc.electric.base;
                        calc.electric.base = 0.0;
                    } else if (corrosiveDamageMods.size() > 0) {
                        calc.corrosive.base = calc.electric.base;
                        calc.electric.base = 0.0;
                    }
                    break;
                case TOXIN_WEAPON_DAMAGE:
                    if (gasDamageMods.size() > 0) {
                        calc.gas.base = calc.toxin.base;
                        calc.toxin.base = 0.0;
                    } else if (viralDamageMods.size() > 0) {
                        calc.viral.base = calc.toxin.base;
                        calc.toxin.base = 0.0;
                    } else if (corrosiveDamageMods.size() > 0) {
                        calc.corrosive.base = calc.toxin.base;
                        calc.toxin.base = 0.0;
                    }
                    break;
            }
        }

        //Populate non-combined-element mod vectors
        for(int i = 0; i < calc.activeMods.size(); i++){
            Mod tempMod = calc.activeMods.get(i);
            if(!combinedMods.contains(tempMod)){
                if(tempMod.getEffectTypes().contains(MOD_TYPE_FIRE_DAMAGE)){
                    double modPower = tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_FIRE_DAMAGE));
                    if(blastDamageMods.size() > 0){
                        blastDamageMods.add(modPower);
                    }else if(radiationDamageMods.size() > 0){
                        radiationDamageMods.add(modPower);
                    }else if(gasDamageMods.size() > 0){
                        gasDamageMods.add(modPower);
                    }else{
                        fireDamageMods.add(modPower);
                    }
                }
                if(tempMod.getEffectTypes().contains(MOD_TYPE_ICE_DAMAGE)){
                    double modPower = tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_ICE_DAMAGE));
                    if(blastDamageMods.size() > 0){
                        blastDamageMods.add(modPower);
                    }else if(magneticDamageMods.size() > 0){
                        magneticDamageMods.add(modPower);
                    }else if(viralDamageMods.size() > 0){
                        viralDamageMods.add(modPower);
                    }else{
                        iceDamageMods.add(modPower);
                    }
                }
                if(tempMod.getEffectTypes().contains(MOD_TYPE_LIGHTNING_DAMAGE)){
                    double modPower = tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_LIGHTNING_DAMAGE));
                    if(corrosiveDamageMods.size() > 0){
                        corrosiveDamageMods.add(modPower);
                    }else if(magneticDamageMods.size() > 0){
                        magneticDamageMods.add(modPower);
                    }else if(radiationDamageMods.size() > 0){
                        radiationDamageMods.add(modPower);
                    }else{
                        electricDamageMods.add(modPower);
                    }
                }
                if(tempMod.getEffectTypes().contains(MOD_TYPE_TOXIN_DAMAGE)){
                    double modPower = tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_TOXIN_DAMAGE));
                    if(corrosiveDamageMods.size() > 0){
                        corrosiveDamageMods.add(modPower);
                    }else if(viralDamageMods.size() > 0){
                        viralDamageMods.add(modPower);
                    }else if(gasDamageMods.size() > 0){
                        gasDamageMods.add(modPower);
                    }else{
                        toxinDamageMods.add(modPower);
                    }
                }
                if(tempMod.getEffectTypes().contains(MOD_TYPE_IMPACT_DAMAGE)){
                    impactDamageMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_IMPACT_DAMAGE))));
                }
                if(tempMod.getEffectTypes().contains(MOD_TYPE_PUNCTURE_DAMAGE)){
                    punctureDamageMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_PUNCTURE_DAMAGE))));
                }
                if(tempMod.getEffectTypes().contains(MOD_TYPE_SLASH_DAMAGE)){
                    slashDamageMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_SLASH_DAMAGE))));
                }
            }
        }

        //Populate non-elemental mod vectors
        for(int i = 0; i < calc.activeMods.size(); i++){
            Mod tempMod = calc.activeMods.get(i);
            if(tempMod.getEffectTypes().contains(MOD_TYPE_MAG_CAP)){
                magMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_MAG_CAP))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_AMMO_CAP)){
                ammoMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_AMMO_CAP))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_CRIT_CHANCE)){
                critChanceMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_CRIT_CHANCE))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_CRIT_MULTIPLIER)){
                critMultMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_CRIT_MULTIPLIER))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_FIRE_RATE)){
                fireRateMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_FIRE_RATE))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_RELOAD_SPEED)){
                reloadTimeMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_RELOAD_SPEED))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_DAMAGE_BONUS)){
                damageMultMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_DAMAGE_BONUS))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_MULTISHOT)){
                projectileCountMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_MULTISHOT))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_FIRST_SHOT_DAMAGE)){
                firstShotDamageMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_FIRST_SHOT_DAMAGE))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_STATUS_CHANCE)){
                statusChanceMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_STATUS_CHANCE))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_STATUS_DURATION)){
                statusDurationMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_STATUS_DURATION))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_CORPUS_DAMAGE)){
                corpusMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_CORPUS_DAMAGE))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_GRINEER_DAMAGE)){
                grineerMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_GRINEER_DAMAGE))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_INFESTED_DAMAGE)){
                infestedMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_INFESTED_DAMAGE))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_FLAT_DAMAGE)){
                flatDamageMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_FLAT_DAMAGE))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_FLAT_STATUS)){
                flatStatusMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_FLAT_STATUS))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_FLAT_MAG)){
                flatMagMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_FLAT_MAG))));
            }
            if(tempMod.getEffectTypes().contains(MOD_TYPE_DEAD_AIM)){
                deadAimMods.add((tempMod.getEffectStrengths().get(tempMod.getEffectTypes().indexOf(MOD_TYPE_DEAD_AIM))));
            }
        }

        //Calculate finals
        calc.finalMag = calc.mag;
        for (Double magMod : magMods) {
            calc.finalMag += calc.mag * magMod;
        }
        for (Double flatMagMod : flatMagMods) {
            calc.finalMag += flatMagMod;
        }

        calc.finalAmmo = calc.ammoCap;
        for (Double ammoMod : ammoMods) {
            calc.finalAmmo += calc.ammoCap * ammoMod;
        }

        calc.finalCritChance = calc.critChance;
        for (Double critChanceMod : critChanceMods) {
            calc.finalCritChance += calc.critChance * critChanceMod;
        }

        calc.finalCritMult = calc.critMult;
        for (Double critMultMod : critMultMods) {
            calc.finalCritMult += calc.critMult * critMultMod;
        }

        calc.finalFlatDamageBonus = calc.flatDamageBonus;
        for (Double flatDamageMod : flatDamageMods) {
            calc.finalFlatDamageBonus += flatDamageMod;
        }

        calc.finalDeadAimMult = calc.deadAimMult;
        for (Double deadAimMod : deadAimMods) {
            calc.finalDeadAimMult += calc.deadAimMult * deadAimMod;
        }

        calc.finalDamageMult = calc.damageMult;
        for (Double damageMultMod : damageMultMods) {
            calc.finalDamageMult += calc.damageMult * damageMultMod;
        }

        if(calc.weaponMode.equals(CONTINUOUS)){
            calc.finalFireRate = calc.fireRate;
            double localContinuousDrainRate = calc.continuousDrainRate;
            for (Double fireRateMod : fireRateMods) {
                calc.finalDamageMult += calc.damageMult * fireRateMod;
                calc.continuousDrainRate += localContinuousDrainRate * fireRateMod;
            }
        }else{
            calc.finalFireRate = calc.fireRate;
            for (Double fireRateMod : fireRateMods) {
                calc.finalFireRate += calc.fireRate * fireRateMod;
            }
        }

        //This is completely retarded, but also the current case
        if(calc.weaponMode.equals(SEMI_AUTO)){
            if(calc.finalFireRate > 10.0){
                calc.finalFireRate = 10.0;
            }
        }

        calc.finalReloadTime = calc.reloadTime;
        double reloadSpeedMult = 1.0;
        for (Double reloadTimeMod : reloadTimeMods) {
            reloadSpeedMult += reloadTimeMod;
        }
        calc.finalReloadTime /= reloadSpeedMult;

        calc.finalProjectileCount = calc.projectileCount;
        for (Double projectileCountMod : projectileCountMods) {
            calc.finalProjectileCount += calc.projectileCount * projectileCountMod;
        }

        calc.finalFirstShotDamageMult = calc.firstShotDamageMult;
        for (Double firstShotDamageMod : firstShotDamageMods) {
            calc.finalFirstShotDamageMult += calc.firstShotDamageMult * firstShotDamageMod;
        }

        calc.finalStatusChance = calc.statusChance;
        for (Double statusChanceMod : statusChanceMods) {
            calc.finalStatusChance += calc.statusChance * statusChanceMod;
        }
        for (Double flatStatusMod : flatStatusMods) {
            double localStatus = flatStatusMod;
            if (calc.projectileCount > 1.0) {
                localStatus /= calc.projectileCount;
            }
            calc.finalStatusChance += localStatus;
        }

        calc.finalStatusDuration = calc.statusDuration;
        for (Double statusDurationMod : statusDurationMods) {
            calc.finalStatusDuration += calc.statusDuration * statusDurationMod;
        }

        if(calc.damageType.equals(PHYSICAL_WEAPON_DAMAGE)){

            calc.impact.finalBase = calc.impact.base;
            for(int i = 0; i < impactDamageMods.size(); i++){
                calc.impact.finalBase += calc.impact.base*impactDamageMods.size();
            }
            calc.impact.finalBase *= calc.finalDamageMult;

            calc.puncture.finalBase = calc.puncture.base;
            for(int i = 0; i < punctureDamageMods.size(); i++){
                calc.puncture.finalBase += calc.puncture.base*punctureDamageMods.size();
            }
            calc.puncture.finalBase *= calc.finalDamageMult;

            calc.slash.finalBase = calc.slash.base;
            for(int i = 0; i < slashDamageMods.size(); i++){
                calc.slash.finalBase += calc.slash.base*slashDamageMods.size();
            }
            calc.slash.finalBase *= calc.finalDamageMult;
        }

        calc.fire.finalBase = calc.fire.base;
        for (Double fireDamageMod : fireDamageMods) {
            calc.fire.finalBase += calc.raw.base * fireDamageMod;
        }
        calc.fire.finalBase *= calc.finalDamageMult;

        calc.ice.finalBase = calc.ice.base;
        for (Double iceDamageMod : iceDamageMods) {
            calc.ice.finalBase += calc.raw.base * iceDamageMod;
        }
        calc.ice.finalBase *= calc.finalDamageMult;

        calc.electric.finalBase = calc.electric.base;
        for (Double electricDamageMod : electricDamageMods) {
            calc.electric.finalBase += calc.raw.base * electricDamageMod;
        }
        calc.electric.finalBase *= calc.finalDamageMult;

        calc.toxin.finalBase = calc.toxin.base;
        for (Double toxinDamageMod : toxinDamageMods) {
            calc.toxin.finalBase += calc.raw.base * toxinDamageMod;
        }
        calc.toxin.finalBase *= calc.finalDamageMult;

        calc.blast.finalBase = calc.blast.base;
        for (Double blastDamageMod : blastDamageMods) {
            calc.blast.finalBase += calc.raw.base * blastDamageMod;
        }
        calc.blast.finalBase *= calc.finalDamageMult;

        calc.magnetic.finalBase = calc.magnetic.base;
        for (Double magneticDamageMod : magneticDamageMods) {
            calc.magnetic.finalBase += calc.raw.base * magneticDamageMod;
        }
        calc.magnetic.finalBase *= calc.finalDamageMult;

        calc.gas.finalBase = calc.gas.base;
        for (Double gasDamageMod : gasDamageMods) {
            calc.gas.finalBase += calc.raw.base * gasDamageMod;
        }
        calc.gas.finalBase *= calc.finalDamageMult;

        calc.radiation.finalBase = calc.radiation.base;
        for (Double radiationDamageMod : radiationDamageMods) {
            calc.radiation.finalBase += calc.raw.base * radiationDamageMod;
        }
        calc.radiation.finalBase *= calc.finalDamageMult;

        calc.corrosive.finalBase = calc.corrosive.base;
        for (Double corrosiveDamageMod : corrosiveDamageMods) {
            calc.corrosive.finalBase += calc.raw.base * corrosiveDamageMod;
        }
        calc.corrosive.finalBase *= calc.finalDamageMult;

        calc.viral.finalBase = calc.viral.base;
        for (Double viralDamageMod : viralDamageMods) {
            calc.viral.finalBase += calc.raw.base * viralDamageMod;
        }
        calc.viral.finalBase *= calc.finalDamageMult;

        calc.raw.finalBase = calc.impact.finalBase +
                calc.puncture.finalBase +
                calc.slash.finalBase +
                calc.slash.finalBase +
                calc.ice.finalBase +
                calc.electric.finalBase +
                calc.toxin.finalBase +
                calc.blast.finalBase +
                calc.magnetic.finalBase +
                calc.gas.finalBase +
                calc.radiation.finalBase +
                calc.corrosive.finalBase +
                calc.viral.finalBase;

        calc.finalCorpusMult = 1.0;
        for (Double corpusMod : corpusMods) {
            calc.finalCorpusMult += corpusMod;
        }

        calc.finalGrineerMult = 1.0;
        for (Double grineerMod : grineerMods) {
            calc.finalGrineerMult += grineerMod;
        }

        calc.finalInfestedMult = 1.0;
        for (Double infestedMod : infestedMods) {
            calc.finalInfestedMult += infestedMod;
        }

        if(calc.weaponMode.equals(BURST)){
            calc.finalCritShots = (calc.finalMag / calc.burstCount) * calc.finalCritChance;
            if(calc.finalCritShots > calc.finalMag){
                calc.finalCritShots = calc.finalMag;
            }

            calc.finalNormalShots = (calc.finalMag / calc.burstCount) - calc.finalCritShots;
            if(calc.finalNormalShots < 0.0){
                calc.finalNormalShots = 0.0;
            }
        }else{
            calc.finalCritShots = calc.finalMag * calc.finalCritChance;
            calc.finalNormalShots = calc.finalMag - calc.finalCritShots;
            if(calc.finalNormalShots < 0.0){
                calc.finalNormalShots = 0.0;
            }
        }

        switch (calc.weaponMode) {
            case CONTINUOUS:
                calc.finalNormalShots = (calc.finalNormalShots / calc.continuousDrainRate) * CONTINUOUS_MULT;
                calc.finalCritShots = (calc.finalCritShots / calc.continuousDrainRate) * CONTINUOUS_MULT;
                calc.finalIterationTime = (calc.finalMag / calc.continuousDrainRate) + calc.finalReloadTime;
                break;
            case BURST:
                calc.finalFireRate += calc.fireRate;
                double burstDelay = 0.05;
                double burstMS = (60.0 / calc.finalFireRate) / 60.0;
                double burstIterationMS = (burstMS * calc.burstCount) + burstDelay;
                calc.finalFireRate = (60.0 / burstIterationMS) / 60.0;
                if (calc.finalFireRate > 10.0) {
                    calc.finalFireRate = 10.0;
                }
                double numBursts = calc.finalMag / calc.burstCount;
                double rawFireTime = numBursts / calc.finalFireRate;
                calc.finalIterationTime = rawFireTime + calc.finalReloadTime;
                break;
            case FULL_AUTO_RAMP_UP:
                double baseFireDelay = 60.0 / calc.finalFireRate / 60.0;
                double firstFireDelay = baseFireDelay * 5;
                double secondFireDelay = baseFireDelay * 4;
                double thirdFireDelay = baseFireDelay * 3;
                double fourthFireDelay = baseFireDelay * 2;
                calc.finalIterationTime = (firstFireDelay + secondFireDelay + thirdFireDelay + fourthFireDelay + ((calc.finalMag - 4) * baseFireDelay)) + calc.finalReloadTime;
                break;
            default:
                calc.finalIterationTime = (calc.finalMag / calc.finalFireRate) + calc.finalReloadTime;
                break;
        }

        calc.finalIterationsPerMinute = 60.0 / calc.finalIterationTime;
    }

    /**
     * Calculates miscellaneous values
     * @param calc
     */
    private static void calculateMiscValues(CalculationEntity calc){
        if(calc.weaponMode.equals(CONTINUOUS)){
            calc.procsPerSecond = ((CONTINUOUS_MULT * ((calc.finalProjectileCount * calc.finalMag) / calc.continuousDrainRate)) * calc.finalStatusChance) * (60 / calc.finalIterationTime / 60);
            calc.burstProcsPerSecond = (CONTINUOUS_MULT * (calc.finalProjectileCount * calc.finalStatusChance));
        }else{
            calc.procsPerSecond = ((calc.finalProjectileCount * calc.finalMag) * calc.finalStatusChance) * (60 / calc.finalIterationTime / 60);
            calc.burstProcsPerSecond = ((calc.finalProjectileCount * calc.finalMag) * calc.finalStatusChance) * (60 / (calc.finalMag / calc.finalFireRate) / 60);
        }
        if(calc.slash.finalBase > 0.0){
            calc.slashStacks = calculateAverageStacks(calc, 6.0);
        }
        if(calc.fire.finalBase > 0.0){
            calc.fireStacks = calculateAverageStacks(calc, 6.0);
        }
        if(calc.toxin.finalBase > 0.0){
            calc.toxinStacks = calculateAverageStacks(calc, 8.0);
        }
        if(calc.gas.finalBase > 0.0){
            calc.gasStacks = calculateAverageStacks(calc, 8.0);
        }
    }

    /**
     * Calculates the damage per shot values
     * @param calc
     */
    private static void calculateDamagePerShot(CalculationEntity calc){

        //Calculate base damage per shot values
        calc.impact.perShot = (calc.impact.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.puncture.perShot = (calc.puncture.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.slash.perShot = (calc.slash.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.fire.perShot = (calc.fire.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.ice.perShot = (calc.ice.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.electric.perShot = (calc.electric.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.toxin.perShot = (calc.toxin.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.blast.perShot = (calc.blast.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.magnetic.perShot = (calc.magnetic.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.gas.perShot = (calc.gas.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.radiation.perShot = (calc.radiation.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.corrosive.perShot = (calc.corrosive.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.viral.perShot = (calc.viral.finalBase * calc.finalProjectileCount) * calc.finalDeadAimMult;
        calc.raw.perShot =  calc.impact.perShot +
                calc.puncture.perShot +
                calc.slash.perShot +
                calc.fire.perShot +
                calc.ice.perShot +
                calc.electric.perShot +
                calc.toxin.perShot +
                calc.blast.perShot +
                calc.magnetic.perShot +
                calc.gas.perShot +
                calc.radiation.perShot +
                calc.corrosive.perShot +
                calc.viral.perShot;

        //Surface-specific
        calc.corpus.perShot = calc.raw.perShot * calc.finalCorpusMult;
        calc.grineer.perShot = calc.raw.perShot * calc.finalGrineerMult;

        calc.infested.perShot += calc.impact.perShot;
        calc.infested.perShot += calc.puncture.perShot;
        calc.infested.perShot += calc.slash.perShot * 1.25;
        calc.infested.perShot += calc.fire.perShot * 1.25;
        calc.infested.perShot += calc.ice.perShot;
        calc.infested.perShot += calc.electric.perShot;
        calc.infested.perShot += calc.toxin.perShot;
        calc.infested.perShot += calc.blast.perShot;
        calc.infested.perShot += calc.magnetic.perShot;
        calc.infested.perShot += calc.gas.perShot * 1.75;
        calc.infested.perShot += calc.radiation.perShot * 0.5;
        calc.infested.perShot += calc.corrosive.perShot;
        calc.infested.perShot += calc.viral.perShot * 0.5;
        calc.infested.perShot *= calc.finalInfestedMult;

        calc.cloneFlesh.perShot += calc.impact.perShot * 0.75;
        calc.cloneFlesh.perShot += calc.puncture.perShot;
        calc.cloneFlesh.perShot += calc.slash.perShot * 1.25;
        calc.cloneFlesh.perShot += calc.fire.perShot * 1.25;
        calc.cloneFlesh.perShot += calc.ice.perShot;
        calc.cloneFlesh.perShot += calc.electric.perShot;
        calc.cloneFlesh.perShot += calc.toxin.perShot;
        calc.cloneFlesh.perShot += calc.blast.perShot;
        calc.cloneFlesh.perShot += calc.magnetic.perShot;
        calc.cloneFlesh.perShot += calc.gas.perShot * 0.5;
        calc.cloneFlesh.perShot += calc.radiation.perShot;
        calc.cloneFlesh.perShot += calc.corrosive.perShot;
        calc.cloneFlesh.perShot += calc.viral.perShot * 1.75;

        calc.ferrite.perShot += calc.impact.perShot;
        calc.ferrite.perShot += calc.puncture.perShot * 1.5;
        calc.ferrite.perShot += calc.slash.perShot * 0.85;
        calc.ferrite.perShot += calc.fire.perShot;
        calc.ferrite.perShot += calc.ice.perShot;
        calc.ferrite.perShot += calc.electric.perShot;
        calc.ferrite.perShot += calc.toxin.perShot * 1.25;
        calc.ferrite.perShot += calc.blast.perShot * 0.75;
        calc.ferrite.perShot += calc.magnetic.perShot;
        calc.ferrite.perShot += calc.gas.perShot;
        calc.ferrite.perShot += calc.radiation.perShot;
        calc.ferrite.perShot += calc.corrosive.perShot * 1.75;
        calc.ferrite.perShot += calc.viral.perShot;

        calc.alloy.perShot += calc.impact.perShot;
        calc.alloy.perShot += calc.puncture.perShot * 1.15;
        calc.alloy.perShot += calc.slash.perShot * 0.5;
        calc.alloy.perShot += calc.fire.perShot;
        calc.alloy.perShot += calc.ice.perShot * 1.25;
        calc.alloy.perShot += calc.electric.perShot * 0.5;
        calc.alloy.perShot += calc.toxin.perShot;
        calc.alloy.perShot += calc.blast.perShot;
        calc.alloy.perShot += calc.magnetic.perShot * 0.5;
        calc.alloy.perShot += calc.gas.perShot;
        calc.alloy.perShot += calc.radiation.perShot * 1.75;
        calc.alloy.perShot += calc.corrosive.perShot;
        calc.alloy.perShot += calc.viral.perShot;

        calc.mechanical.perShot += calc.impact.perShot * 1.25;
        calc.mechanical.perShot += calc.puncture.perShot;
        calc.mechanical.perShot += calc.slash.perShot;
        calc.mechanical.perShot += calc.fire.perShot;
        calc.mechanical.perShot += calc.ice.perShot;
        calc.mechanical.perShot += calc.electric.perShot * 1.5;
        calc.mechanical.perShot += calc.toxin.perShot * 0.75;
        calc.mechanical.perShot += calc.blast.perShot * 1.75;
        calc.mechanical.perShot += calc.magnetic.perShot;
        calc.mechanical.perShot += calc.gas.perShot;
        calc.mechanical.perShot += calc.radiation.perShot;
        calc.mechanical.perShot += calc.corrosive.perShot;
        calc.mechanical.perShot += calc.viral.perShot * 0.75;

        calc.corpusFlesh.perShot += calc.impact.perShot * 0.75;
        calc.corpusFlesh.perShot += calc.puncture.perShot;
        calc.corpusFlesh.perShot += calc.slash.perShot * 1.25;
        calc.corpusFlesh.perShot += calc.fire.perShot;
        calc.corpusFlesh.perShot += calc.ice.perShot;
        calc.corpusFlesh.perShot += calc.electric.perShot;
        calc.corpusFlesh.perShot += calc.toxin.perShot * 1.5;
        calc.corpusFlesh.perShot += calc.blast.perShot;
        calc.corpusFlesh.perShot += calc.magnetic.perShot;
        calc.corpusFlesh.perShot += calc.gas.perShot * 0.75;
        calc.corpusFlesh.perShot += calc.radiation.perShot;
        calc.corpusFlesh.perShot += calc.corrosive.perShot;
        calc.corpusFlesh.perShot += calc.viral.perShot * 1.5;

        calc.shield.perShot += calc.impact.perShot * 1.5;
        calc.shield.perShot += calc.puncture.perShot * 0.85;
        calc.shield.perShot += calc.slash.perShot;
        calc.shield.perShot += calc.fire.perShot;
        calc.shield.perShot += calc.ice.perShot * 1.5;
        calc.shield.perShot += calc.electric.perShot;
        calc.shield.perShot += calc.toxin.perShot;
        calc.shield.perShot += calc.blast.perShot;
        calc.shield.perShot += calc.magnetic.perShot * 1.75;
        calc.shield.perShot += calc.gas.perShot;
        calc.shield.perShot += calc.radiation.perShot * 0.75;
        calc.shield.perShot += calc.corrosive.perShot;
        calc.shield.perShot += calc.viral.perShot;

        calc.protoShield.perShot += calc.impact.perShot * 1.15;
        calc.protoShield.perShot += calc.puncture.perShot * 0.5;
        calc.protoShield.perShot += calc.slash.perShot;
        calc.protoShield.perShot += calc.fire.perShot * 0.5;
        calc.protoShield.perShot += calc.ice.perShot;
        calc.protoShield.perShot += calc.electric.perShot;
        calc.protoShield.perShot += calc.toxin.perShot * 1.25;
        calc.protoShield.perShot += calc.blast.perShot;
        calc.protoShield.perShot += calc.magnetic.perShot * 1.75;
        calc.protoShield.perShot += calc.gas.perShot;
        calc.protoShield.perShot += calc.radiation.perShot;
        calc.protoShield.perShot += calc.corrosive.perShot * 0.5;
        calc.protoShield.perShot += calc.viral.perShot;

        calc.robotic.perShot += calc.impact.perShot;
        calc.robotic.perShot += calc.puncture.perShot * 1.25;
        calc.robotic.perShot += calc.slash.perShot * 0.75;
        calc.robotic.perShot += calc.fire.perShot;
        calc.robotic.perShot += calc.ice.perShot;
        calc.robotic.perShot += calc.electric.perShot * 1.5;
        calc.robotic.perShot += calc.toxin.perShot * 0.75;
        calc.robotic.perShot += calc.blast.perShot;
        calc.robotic.perShot += calc.magnetic.perShot;
        calc.robotic.perShot += calc.gas.perShot;
        calc.robotic.perShot += calc.radiation.perShot * 1.25;
        calc.robotic.perShot += calc.corrosive.perShot;
        calc.robotic.perShot += calc.viral.perShot;

        calc.infestedFlesh.perShot += calc.impact.perShot;
        calc.infestedFlesh.perShot += calc.puncture.perShot;
        calc.infestedFlesh.perShot += calc.slash.perShot * 1.5;
        calc.infestedFlesh.perShot += calc.fire.perShot * 1.5;
        calc.infestedFlesh.perShot += calc.ice.perShot * 0.5;
        calc.infestedFlesh.perShot += calc.electric.perShot;
        calc.infestedFlesh.perShot += calc.toxin.perShot;
        calc.infestedFlesh.perShot += calc.blast.perShot;
        calc.infestedFlesh.perShot += calc.magnetic.perShot;
        calc.infestedFlesh.perShot += calc.gas.perShot * 1.5;
        calc.infestedFlesh.perShot += calc.radiation.perShot;
        calc.infestedFlesh.perShot += calc.corrosive.perShot;
        calc.infestedFlesh.perShot += calc.viral.perShot;

        calc.fossilized.perShot += calc.impact.perShot;
        calc.fossilized.perShot += calc.puncture.perShot;
        calc.fossilized.perShot += calc.slash.perShot * 1.15;
        calc.fossilized.perShot += calc.fire.perShot;
        calc.fossilized.perShot += calc.ice.perShot * 0.75;
        calc.fossilized.perShot += calc.electric.perShot;
        calc.fossilized.perShot += calc.toxin.perShot * 0.5;
        calc.fossilized.perShot += calc.blast.perShot * 1.5;
        calc.fossilized.perShot += calc.magnetic.perShot;
        calc.fossilized.perShot += calc.gas.perShot;
        calc.fossilized.perShot += calc.radiation.perShot * 0.25;
        calc.fossilized.perShot += calc.corrosive.perShot * 1.75;
        calc.fossilized.perShot += calc.viral.perShot;

        calc.sinew.perShot += calc.impact.perShot;
        calc.sinew.perShot += calc.puncture.perShot * 1.25;
        calc.sinew.perShot += calc.slash.perShot;
        calc.sinew.perShot += calc.fire.perShot;
        calc.sinew.perShot += calc.ice.perShot * 1.25;
        calc.sinew.perShot += calc.electric.perShot;
        calc.sinew.perShot += calc.toxin.perShot;
        calc.sinew.perShot += calc.blast.perShot * 0.5;
        calc.sinew.perShot += calc.magnetic.perShot;
        calc.sinew.perShot += calc.gas.perShot;
        calc.sinew.perShot += calc.radiation.perShot * 1.5;
        calc.sinew.perShot += calc.corrosive.perShot;
        calc.sinew.perShot += calc.viral.perShot;

        //Calculate crit damage per shot values
        calc.raw.critPerShot = calc.raw.perShot * calc.finalCritMult;
        calc.impact.critPerShot = calc.impact.perShot * calc.finalCritMult;
        calc.puncture.critPerShot = calc.puncture.perShot * calc.finalCritMult;
        calc.slash.critPerShot = calc.slash.perShot * calc.finalCritMult;
        calc.fire.critPerShot = calc.fire.perShot * calc.finalCritMult;
        calc.ice.critPerShot = calc.ice.perShot * calc.finalCritMult;
        calc.electric.critPerShot = calc.electric.perShot * calc.finalCritMult;
        calc.toxin.critPerShot = calc.toxin.perShot * calc.finalCritMult;
        calc.blast.critPerShot = calc.blast.perShot * calc.finalCritMult;
        calc.magnetic.critPerShot = calc.magnetic.perShot * calc.finalCritMult;
        calc.gas.critPerShot = calc.gas.perShot * calc.finalCritMult;
        calc.radiation.critPerShot = calc.radiation.perShot * calc.finalCritMult;
        calc.corrosive.critPerShot = calc.corrosive.perShot * calc.finalCritMult;
        calc.viral.critPerShot = calc.viral.perShot * calc.finalCritMult;
        calc.corpus.critPerShot = calc.corpus.perShot * calc.finalCritMult;
        calc.grineer.critPerShot = calc.grineer.perShot * calc.finalCritMult;
        calc.infested.critPerShot = calc.infested.perShot * calc.finalCritMult;
        calc.cloneFlesh.critPerShot = calc.cloneFlesh.perShot * calc.finalCritMult;
        calc.ferrite.critPerShot = calc.ferrite.perShot * calc.finalCritMult;
        calc.alloy.critPerShot = calc.alloy.perShot * calc.finalCritMult;
        calc.mechanical.critPerShot = calc.mechanical.perShot * calc.finalCritMult;
        calc.corpusFlesh.critPerShot = calc.corpusFlesh.perShot * calc.finalCritMult;
        calc.shield.critPerShot = calc.shield.perShot * calc.finalCritMult;
        calc.protoShield.critPerShot = calc.protoShield.perShot * calc.finalCritMult;
        calc.robotic.critPerShot = calc.robotic.perShot * calc.finalCritMult;
        calc.infestedFlesh.critPerShot = calc.infestedFlesh.perShot * calc.finalCritMult;
        calc.fossilized.critPerShot = calc.fossilized.perShot * calc.finalCritMult;
        calc.sinew.critPerShot = calc.sinew.perShot * calc.finalCritMult;


        //Calculate first-shot damage
        calc.raw.firstShot = calc.raw.critPerShot * calc.finalFirstShotDamageMult;
        calc.impact.firstShot = calc.impact.critPerShot * calc.finalFirstShotDamageMult;
        calc.puncture.firstShot = calc.puncture.critPerShot * calc.finalFirstShotDamageMult;
        calc.slash.firstShot = calc.slash.critPerShot * calc.finalFirstShotDamageMult;
        calc.fire.firstShot = calc.fire.critPerShot * calc.finalFirstShotDamageMult;
        calc.ice.firstShot = calc.ice.critPerShot * calc.finalFirstShotDamageMult;
        calc.electric.firstShot = calc.electric.critPerShot * calc.finalFirstShotDamageMult;
        calc.toxin.firstShot = calc.toxin.critPerShot * calc.finalFirstShotDamageMult;
        calc.blast.firstShot = calc.blast.critPerShot * calc.finalFirstShotDamageMult;
        calc.magnetic.firstShot = calc.magnetic.critPerShot * calc.finalFirstShotDamageMult;
        calc.gas.firstShot = calc.gas.critPerShot * calc.finalFirstShotDamageMult;
        calc.radiation.firstShot = calc.radiation.critPerShot * calc.finalFirstShotDamageMult;
        calc.corrosive.firstShot = calc.corrosive.critPerShot * calc.finalFirstShotDamageMult;
        calc.viral.firstShot = calc.viral.critPerShot * calc.finalFirstShotDamageMult;
        calc.corpus.firstShot = calc.corpus.critPerShot * calc.finalFirstShotDamageMult;
        calc.grineer.firstShot = calc.grineer.critPerShot * calc.finalFirstShotDamageMult;
        calc.infested.firstShot = calc.infested.critPerShot * calc.finalFirstShotDamageMult;
        calc.cloneFlesh.firstShot = calc.cloneFlesh.critPerShot * calc.finalFirstShotDamageMult;
        calc.ferrite.firstShot = calc.ferrite.critPerShot * calc.finalFirstShotDamageMult;
        calc.alloy.firstShot = calc.alloy.critPerShot * calc.finalFirstShotDamageMult;
        calc.mechanical.firstShot = calc.mechanical.critPerShot * calc.finalFirstShotDamageMult;
        calc.corpusFlesh.firstShot = calc.corpusFlesh.critPerShot * calc.finalFirstShotDamageMult;
        calc.shield.firstShot = calc.shield.critPerShot * calc.finalFirstShotDamageMult;
        calc.protoShield.firstShot = calc.protoShield.critPerShot * calc.finalFirstShotDamageMult;
        calc.robotic.firstShot = calc.robotic.critPerShot * calc.finalFirstShotDamageMult;
        calc.infestedFlesh.firstShot = calc.infestedFlesh.critPerShot * calc.finalFirstShotDamageMult;
        calc.fossilized.firstShot = calc.fossilized.critPerShot * calc.finalFirstShotDamageMult;
        calc.sinew.firstShot = calc.sinew.critPerShot * calc.finalFirstShotDamageMult;

    }

    /**
     * Calculates the total damage done over an entire magazine
     * @param calc
     */
    private static void calculateDamagePerIteration(CalculationEntity calc){
        calc.raw.perIteration = (calc.raw.perShot * calc.finalNormalShots) + (calc.raw.critPerShot * calc.finalCritShots) + calc.raw.firstShot;
        calc.impact.perIteration = (calc.impact.perShot * calc.finalNormalShots) + (calc.impact.critPerShot * calc.finalCritShots) + calc.impact.firstShot;
        calc.puncture.perIteration = (calc.puncture.perShot * calc.finalNormalShots) + (calc.puncture.critPerShot * calc.finalCritShots) + calc.puncture.firstShot;
        calc.slash.perIteration = (calc.slash.perShot * calc.finalNormalShots) + (calc.slash.critPerShot * calc.finalCritShots) + calc.slash.firstShot;
        calc.fire.perIteration = (calc.fire.perShot * calc.finalNormalShots) + (calc.fire.critPerShot * calc.finalCritShots) + calc.fire.firstShot;
        calc.ice.perIteration = (calc.ice.perShot * calc.finalNormalShots) + (calc.ice.critPerShot * calc.finalCritShots) + calc.ice.firstShot;
        calc.electric.perIteration = (calc.electric.perShot * calc.finalNormalShots) + (calc.electric.critPerShot * calc.finalCritShots) + calc.electric.firstShot;
        calc.toxin.perIteration = (calc.toxin.perShot * calc.finalNormalShots) + (calc.toxin.critPerShot * calc.finalCritShots) + calc.toxin.firstShot;
        calc.blast.perIteration = (calc.blast.perShot * calc.finalNormalShots) + (calc.blast.critPerShot * calc.finalCritShots) + calc.blast.firstShot;
        calc.magnetic.perIteration = (calc.magnetic.perShot * calc.finalNormalShots) + (calc.magnetic.critPerShot * calc.finalCritShots) + calc.magnetic.firstShot;
        calc.gas.perIteration = (calc.gas.perShot * calc.finalNormalShots) + (calc.gas.critPerShot * calc.finalCritShots) + calc.gas.firstShot;
        calc.radiation.perIteration = (calc.radiation.perShot * calc.finalNormalShots) + (calc.radiation.critPerShot * calc.finalCritShots) + calc.radiation.firstShot;
        calc.corrosive.perIteration = (calc.corrosive.perShot * calc.finalNormalShots) + (calc.corrosive.critPerShot * calc.finalCritShots) + calc.corrosive.firstShot;
        calc.viral.perIteration = (calc.viral.perShot * calc.finalNormalShots) + (calc.viral.critPerShot * calc.finalCritShots) + calc.viral.firstShot;
        calc.corpus.perIteration = (calc.corpus.perShot * calc.finalNormalShots) + (calc.corpus.critPerShot * calc.finalCritShots) + calc.corpus.firstShot;
        calc.grineer.perIteration = (calc.grineer.perShot * calc.finalNormalShots) + (calc.grineer.critPerShot * calc.finalCritShots) + calc.grineer.firstShot;
        calc.infested.perIteration = (calc.infested.perShot * calc.finalNormalShots) + (calc.infested.critPerShot * calc.finalCritShots) + calc.infested.firstShot;
        calc.cloneFlesh.perIteration = (calc.cloneFlesh.perShot * calc.finalNormalShots) + (calc.cloneFlesh.critPerShot * calc.finalCritShots) + calc.cloneFlesh.firstShot;
        calc.ferrite.perIteration = (calc.ferrite.perShot * calc.finalNormalShots) + (calc.ferrite.critPerShot * calc.finalCritShots) + calc.ferrite.firstShot;
        calc.alloy.perIteration = (calc.alloy.perShot * calc.finalNormalShots) + (calc.alloy.critPerShot * calc.finalCritShots) + calc.alloy.firstShot;
        calc.mechanical.perIteration = (calc.mechanical.perShot * calc.finalNormalShots) + (calc.mechanical.critPerShot * calc.finalCritShots) + calc.mechanical.firstShot;
        calc.corpusFlesh.perIteration = (calc.corpusFlesh.perShot * calc.finalNormalShots) + (calc.corpusFlesh.critPerShot * calc.finalCritShots) + calc.corpusFlesh.firstShot;
        calc.shield.perIteration = (calc.shield.perShot * calc.finalNormalShots) + (calc.shield.critPerShot * calc.finalCritShots) + calc.shield.firstShot;
        calc.protoShield.perIteration = (calc.protoShield.perShot * calc.finalNormalShots) + (calc.protoShield.critPerShot * calc.finalCritShots) + calc.protoShield.firstShot;
        calc.robotic.perIteration = (calc.robotic.perShot * calc.finalNormalShots) + (calc.robotic.critPerShot * calc.finalCritShots) + calc.robotic.firstShot;
        calc.infestedFlesh.perIteration = (calc.infestedFlesh.perShot * calc.finalNormalShots) + (calc.infestedFlesh.critPerShot * calc.finalCritShots) + calc.infestedFlesh.firstShot;
        calc.fossilized.perIteration = (calc.fossilized.perShot * calc.finalNormalShots) + (calc.fossilized.critPerShot * calc.finalCritShots) + calc.fossilized.firstShot;
        calc.sinew.perIteration = (calc.sinew.perShot * calc.finalNormalShots) + (calc.sinew.critPerShot * calc.finalCritShots) + calc.sinew.firstShot;

    }

    /**
     * Calculates the total damage dealt over a given minute.
     * @param calc
     */
    private static void calculateDamagePerMinute(CalculationEntity calc){
        calc.raw.perMinute = calc.raw.perIteration * calc.finalIterationsPerMinute;
        calc.impact.perMinute = calc.impact.perIteration * calc.finalIterationsPerMinute;
        calc.puncture.perMinute = calc.puncture.perIteration * calc.finalIterationsPerMinute;
        calc.slash.perMinute = calc.slash.perIteration * calc.finalIterationsPerMinute;
        calc.fire.perMinute = calc.fire.perIteration * calc.finalIterationsPerMinute;
        calc.ice.perMinute = calc.ice.perIteration * calc.finalIterationsPerMinute;
        calc.electric.perMinute = calc.electric.perIteration * calc.finalIterationsPerMinute;
        calc.toxin.perMinute = calc.toxin.perIteration * calc.finalIterationsPerMinute;
        calc.blast.perMinute = calc.blast.perIteration * calc.finalIterationsPerMinute;
        calc.magnetic.perMinute = calc.magnetic.perIteration * calc.finalIterationsPerMinute;
        calc.gas.perMinute = calc.gas.perIteration * calc.finalIterationsPerMinute;
        calc.radiation.perMinute = calc.radiation.perIteration * calc.finalIterationsPerMinute;
        calc.corrosive.perMinute = calc.corrosive.perIteration * calc.finalIterationsPerMinute;
        calc.viral.perMinute = calc.viral.perIteration * calc.finalIterationsPerMinute;
        calc.corpus.perMinute = calc.corpus.perIteration * calc.finalIterationsPerMinute;
        calc.grineer.perMinute = calc.grineer.perIteration * calc.finalIterationsPerMinute;
        calc.infested.perMinute = calc.infested.perIteration * calc.finalIterationsPerMinute;
        calc.cloneFlesh.perMinute = calc.cloneFlesh.perIteration * calc.finalIterationsPerMinute;
        calc.ferrite.perMinute = calc.ferrite.perIteration * calc.finalIterationsPerMinute;
        calc.alloy.perMinute = calc.alloy.perIteration * calc.finalIterationsPerMinute;
        calc.mechanical.perMinute = calc.mechanical.perIteration * calc.finalIterationsPerMinute;
        calc.corpus.perMinute = calc.corpusFlesh.perIteration * calc.finalIterationsPerMinute;
        calc.shield.perMinute = calc.shield.perIteration * calc.finalIterationsPerMinute;
        calc.protoShield.perMinute = calc.protoShield.perIteration * calc.finalIterationsPerMinute;
        calc.robotic.perMinute = calc.robotic.perIteration * calc.finalIterationsPerMinute;
        calc.infestedFlesh.perMinute = calc.infestedFlesh.perIteration * calc.finalIterationsPerMinute;
        calc.fossilized.perMinute = calc.fossilized.perIteration * calc.finalIterationsPerMinute;
        calc.sinew.perMinute = calc.sinew.perIteration * calc.finalIterationsPerMinute;
    }

    /**
     * Calculates the damage per second
     * @param calc
     */
    private static void calculateDamagePerSecond(CalculationEntity calc){
        //Calculate base DPS values
        calc.raw.perSecond = calc.raw.perMinute / 60.0;
        calc.impact.perSecond = calc.impact.perMinute / 60.0;
        calc.puncture.perSecond = calc.puncture.perMinute / 60.0;
        calc.slash.perSecond = calc.slash.perMinute / 60.0;
        calc.fire.perSecond = calc.fire.perMinute / 60.0;
        calc.ice.perSecond = calc.ice.perMinute / 60.0;
        calc.electric.perSecond = calc.electric.perMinute / 60.0;
        calc.toxin.perSecond = calc.toxin.perMinute / 60.0;
        calc.blast.perSecond = calc.blast.perMinute / 60.0;
        calc.magnetic.perSecond = calc.magnetic.perMinute / 60.0;
        calc.gas.perSecond = calc.gas.perMinute / 60.0;
        calc.radiation.perSecond = calc.radiation.perMinute / 60.0;
        calc.corrosive.perSecond = calc.corrosive.perMinute / 60.0;
        calc.viral.perSecond = calc.viral.perMinute / 60.0;
        calc.corpus.perSecond = calc.corpus.perMinute / 60.0;
        calc.grineer.perSecond = calc.grineer.perMinute / 60.0;
        calc.infested.perSecond = calc.infested.perMinute / 60.0;
        calc.cloneFlesh.perSecond = calc.cloneFlesh.perMinute / 60.0;
        calc.ferrite.perSecond = calc.ferrite.perMinute / 60.0;
        calc.alloy.perSecond = calc.alloy.perMinute / 60.0;
        calc.mechanical.perSecond = calc.mechanical.perMinute / 60.0;
        calc.corpusFlesh.perSecond = calc.corpus.perMinute / 60.0;
        calc.shield.perSecond = calc.shield.perMinute / 60.0;
        calc.protoShield.perSecond = calc.protoShield.perMinute / 60.0;
        calc.robotic.perSecond = calc.robotic.perMinute / 60.0;
        calc.infestedFlesh.perSecond = calc.infestedFlesh.perMinute / 60.0;
        calc.fossilized.perSecond = calc.fossilized.perMinute / 60.0;
        calc.sinew.perSecond = calc.sinew.perMinute / 60.0;

        //Add in DoTs
        double rawBase = ((calc.raw.base * calc.finalDamageMult) * calc.finalProjectileCount) * calc.finalDeadAimMult;
        double critBase = rawBase * calc.finalCritMult;
        double DoTBase = (((rawBase * calc.finalNormalShots) + (critBase * calc.finalCritShots) + calc.raw.firstShot) / calc.finalMag);
        double bleedDamage =  DoTBase * 0.35;
        double poisonDamage = DoTBase * 0.5;
        if(poisonDamage < 10.0){
            poisonDamage = 10.0;
        }
        double heatDamage = DoTBase * 0.5;
        double cloudDamage = DoTBase * 0.5;
        if(cloudDamage < 10.0){
            cloudDamage = 10.0;
        }
        double bleedDoTDPS = calc.slashStacks * bleedDamage;
        double poisonDoTDPS = calc.toxinStacks * poisonDamage;
        double heatDoTDPS = calc.fireStacks * heatDamage;
        double cloudDoTDPS = calc.gasStacks * cloudDamage;
        double electricProcDPS = calc.procsPerSecond * DoTBase;
        double DotTotal = bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS + electricProcDPS;
        calc.raw.perSecond += DotTotal;
        calc.corpus.perSecond += DotTotal;
        calc.grineer.perSecond += DotTotal;
        calc.infested.perSecond += DotTotal;
        calc.cloneFlesh.perSecond += DotTotal;
        calc.ferrite.perSecond += DotTotal;
        calc.alloy.perSecond += DotTotal;
        calc.mechanical.perSecond += DotTotal;
        calc.corpusFlesh.perSecond += DotTotal;
        calc.shield.perSecond += DotTotal;
        calc.protoShield.perSecond += DotTotal;
        calc.robotic.perSecond += DotTotal;
        calc.infestedFlesh.perSecond += DotTotal;
        calc.fossilized.perSecond += DotTotal;
        calc.sinew.perSecond += DotTotal;
    }

    /**
     * Calculates the burst damage per second
     * @param calc
     */
    private static void calculateBurstDamagePerSecond(CalculationEntity calc){
        //Calculate base Burst DPS values
        double burstTime = (60.0 / (calc.finalIterationTime - calc.finalReloadTime)) / 60.0;
        calc.raw.rawPerSecond = calc.raw.perIteration * burstTime;
        calc.impact.rawPerSecond = calc.impact.perIteration * burstTime;
        calc.puncture.rawPerSecond = calc.puncture.perIteration * burstTime;
        calc.slash.rawPerSecond = calc.slash.perIteration * burstTime;
        calc.fire.rawPerSecond = calc.fire.perIteration * burstTime;
        calc.ice.rawPerSecond = calc.ice.perIteration * burstTime;
        calc.electric.rawPerSecond = calc.electric.perIteration * burstTime;
        calc.toxin.rawPerSecond = calc.toxin.perIteration * burstTime;
        calc.blast.rawPerSecond = calc.blast.perIteration * burstTime;
        calc.magnetic.rawPerSecond = calc.magnetic.perIteration * burstTime;
        calc.gas.rawPerSecond = calc.gas.perIteration * burstTime;
        calc.radiation.rawPerSecond = calc.radiation.perIteration * burstTime;
        calc.corrosive.rawPerSecond = calc.corrosive.perIteration * burstTime;
        calc.viral.rawPerSecond = calc.viral.perIteration * burstTime;
        calc.corpus.rawPerSecond = calc.corpus.perIteration * burstTime;
        calc.grineer.rawPerSecond = calc.grineer.perIteration * burstTime;
        calc.cloneFlesh.rawPerSecond = calc.cloneFlesh.perIteration * burstTime;
        calc.infested.rawPerSecond = calc.infested.perIteration * burstTime;
        calc.ferrite.rawPerSecond = calc.ferrite.perIteration * burstTime;
        calc.alloy.rawPerSecond = calc.alloy.perIteration * burstTime;
        calc.mechanical.rawPerSecond = calc.mechanical.perIteration * burstTime;
        calc.corpusFlesh.rawPerSecond = calc.corpusFlesh.perIteration * burstTime;
        calc.shield.rawPerSecond = calc.shield.perIteration * burstTime;
        calc.protoShield.rawPerSecond = calc.protoShield.perIteration * burstTime;
        calc.robotic.rawPerSecond = calc.robotic.perIteration * burstTime;
        calc.infestedFlesh.rawPerSecond = calc.infestedFlesh.perIteration * burstTime;
        calc.fossilized.rawPerSecond = calc.fossilized.perIteration * burstTime;
        calc.sinew.rawPerSecond = calc.sinew.perIteration * burstTime;

        //Add in DoTs
        double rawBase = ((calc.raw.base * calc.finalDamageMult) * calc.finalProjectileCount) * calc.finalDeadAimMult;
        double critBase = rawBase * calc.finalCritMult;
        double DoTBase = (((rawBase * calc.finalNormalShots) + (critBase * calc.finalCritShots) + calc.raw.firstShot) / calc.finalMag);
        double bleedDamage =  DoTBase * 0.35;
        double poisonDamage = DoTBase * 0.5;
        if(poisonDamage < 10.0){
            poisonDamage = 10.0;
        }
        double heatDamage = DoTBase * 0.5;
        double cloudDamage = DoTBase * 0.5;
        if(cloudDamage < 10.0){
            cloudDamage = 10.0;
        }
        double bleedDoTDPS = calc.slashStacks * bleedDamage;
        double poisonDoTDPS = calc.toxinStacks * poisonDamage;
        double heatDoTDPS = calc.fireStacks * heatDamage;
        double cloudDoTDPS = calc.gasStacks * cloudDamage;
        double electricProcDPS = calc.procsPerSecond * DoTBase;
        double DotTotal = bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS + electricProcDPS;
        calc.raw.rawPerSecond += DotTotal;
        calc.corpus.rawPerSecond += DotTotal;
        calc.grineer.rawPerSecond += DotTotal;
        calc.infested.rawPerSecond += DotTotal;
        calc.cloneFlesh.rawPerSecond += DotTotal;
        calc.ferrite.rawPerSecond += DotTotal;
        calc.alloy.rawPerSecond += DotTotal;
        calc.mechanical.rawPerSecond += DotTotal;
        calc.corpusFlesh.rawPerSecond += DotTotal;
        calc.shield.rawPerSecond += DotTotal;
        calc.protoShield.rawPerSecond += DotTotal;
        calc.robotic.rawPerSecond += DotTotal;
        calc.infestedFlesh.rawPerSecond += DotTotal;
        calc.fossilized.rawPerSecond += DotTotal;
        calc.sinew.rawPerSecond += DotTotal;
    }

    /**
     * Calculates the average number of stacks of a given effect
     * @param calc
     * @param duration
     * @return
     */
    private static int calculateAverageStacks(CalculationEntity calc, double duration){

        double millisceondsPerShot = 1000.0 / calc.finalFireRate;
        double stacksPerShot = calc.finalStatusChance;
        double reloadTimeMilliseconds = calc.finalReloadTime * 1000.0;
        double stackTotal = 0.0;
        double moddedDuration = duration * calc.finalStatusDuration;
        int averageStacks = 0;
        int reloadTimeCounter = 0;
        int shotCounter = 0;
        int iterations = 0;
        boolean reloading = false;
        Vector<Double> stackVec = new Vector<>();
        Vector<Integer> stackCountVec = new Vector<>();

        //Run a 60 second simulation to calculate the average number of stacks
        for(int i=0; i < 60000; i++){
            //Add new stack
            if(!reloading){
                shotCounter++;
                //is it time to fire a new projectile?
                if(shotCounter >= millisceondsPerShot){
                    //Add stacks
                    for(int p = 0; p < calc.finalProjectileCount; p++){
                        stackTotal += stacksPerShot;
                        if(stackTotal > 1.0){
                            stackVec.add(moddedDuration);
                            stackTotal--;
                        }
                    }
                    shotCounter = 0;
                    //Have we unloaded the whole mag and need to reload?
                    iterations++;
                    if(iterations >= calc.finalMag){
                        reloading = true;
                        iterations = 0;
                    }
                }
            }else{
                //Are we still reloading?
                reloadTimeCounter++;
                if(reloadTimeCounter >= reloadTimeMilliseconds){
                    reloading = false;
                    reloadTimeCounter = 0;
                }
            }
            //Is this a whole second?
            if(i % 1000 == 0){
                //Decrement stack timers
                for(int j=0;j<stackVec.size();j++){
                    double temp = stackVec.get(j);
                    temp--;
                    stackVec.set(j, temp);
                }
                //Remove stacks that have expired
                for(int k=0;k<stackVec.size();k++){
                    if(stackVec.get(k) <= 0){
                        stackVec.remove(k);
                    }
                }
                //Add a new count to the stack counting vector
                stackCountVec.add(stackVec.size());
            }
        }

        for (Integer aStackCountVec : stackCountVec) {
            averageStacks += aStackCountVec;
        }
        averageStacks /= stackCountVec.size();
        return averageStacks;
    }

    /**
     * Appends the weapon information to the output text area
     */
    private static String buildOutput(CalculationSubmission submission, CalculationEntity calc){
        
        //Setup the output
        String output = "";
        
        //Append to Output
        DecimalFormat f = new DecimalFormat("#.###");
        output += "\n";
        output += "\n_____________________________________________________________";
        output += "\nName :: "+calc.weaponName;
        output += "\nMagazine Size :: "+calc.finalMag;
        output += "\nTotal Ammo :: "+(calc.finalMag+calc.finalAmmo);
        output += "\nCrit Chance :: "+f.format(calc.finalCritChance*100.0)+"%";
        output += "\nCrit Damage Multiplier :: "+f.format(calc.finalCritMult*100.0)+"%";
        String delimiter = "rounds";
        String mode = submission.getWeaponMode();
        if(mode.equals(BURST)){
            delimiter = "bursts";
        }else if(mode.equals(CONTINUOUS)){
            delimiter = "ammo drain";
        }
        output += "\nFire Rate :: "+f.format(calc.finalFireRate)+" "+delimiter+" per second";
        output += "\nReload Time :: "+f.format(calc.finalReloadTime)+" seconds";
        output += "\nStatus Chance :: "+f.format(calc.finalStatusChance*100.0)+"%";
        output += "\nProjectiles Per Shot :: "+f.format(calc.finalProjectileCount);
        output += "\nStatus Procs Per Second :: "+f.format(calc.procsPerSecond);
        output += "\nBurst Status Procs Per Second :: "+f.format(calc.burstProcsPerSecond);
        output += "\nTime to Empty magazine :: "+f.format(calc.finalIterationTime-calc.finalReloadTime)+" seconds";
        if(calc.slashStacks > 0){
            output += "\nAverage Bleed Stacks :: "+calc.slashStacks;
        }
        if(calc.toxinStacks > 0){
            output += "\nAverage Poison Stacks :: "+calc.toxinStacks;
        }
        if(calc.gasStacks > 0){
            output += "\nAverage Poison Cloud Stacks :: "+calc.gasStacks;
        }
        if(calc.fireStacks > 0){
            output += "\nAverage Burning Stacks :: "+calc.fireStacks;
        }
        output += "\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::";
        output += "\nRaw Damage Per Shot :: "+f.format(calc.raw.perShot);
        if(calc.impact.perShot > 0.0){
            output += "\nImpact Damage Per Shot :: "+f.format(calc.impact.perShot);
        }
        if(calc.puncture.perShot > 0.0){
            output += "\nPuncture Damage Per Shot :: "+f.format(calc.puncture.perShot);
        }
        if(calc.slash.perShot > 0.0){
            output += "\nSlash Damage Per Shot :: "+f.format(calc.slash.perShot);
        }
        if(calc.fire.perShot > 0.0){
            output += "\nFire Damage Per Shot :: "+f.format(calc.fire.perShot);
        }
        if(calc.ice.perShot > 0.0){
            output += "\nIce Damage Per Shot :: "+f.format(calc.ice.perShot);
        }
        if(calc.electric.perShot > 0.0){
            output += "\nElectric Damage Per Shot :: "+f.format(calc.electric.perShot);
        }
        if(calc.toxin.perShot > 0.0){
            output += "\nToxin Damage Per Shot :: "+f.format(calc.toxin.perShot);
        }
        if(calc.blast.perShot > 0.0){
            output += "\nBlast Damage Per Shot :: "+f.format(calc.blast.perShot);
        }
        if(calc.magnetic.perShot > 0.0){
            output += "\nMagnetic Damage Per Shot :: "+f.format(calc.magnetic.perShot);
        }
        if(calc.gas.perShot > 0.0){
            output += "\nGas Damage Per Shot :: "+f.format(calc.gas.perShot);
        }
        if(calc.radiation.perShot > 0.0){
            output += "\nRadiation Damage Per Shot :: "+f.format(calc.radiation.perShot);
        }
        if(calc.corrosive.perShot > 0.0){
            output += "\nCorrosive Damage Per Shot :: "+f.format(calc.corrosive.perShot);
        }
        if(calc.viral.perShot > 0.0){
            output += "\nViral Damage Per Shot :: "+f.format(calc.viral.perShot);
        }
        output += "\nDamage Per Shot to Clone Flesh :: "+f.format(calc.cloneFlesh.perShot);
        output += "\nDamage Per Shot to Ferrite Armor :: "+f.format(calc.ferrite.perShot);
        output += "\nDamage Per Shot to Alloy Armor :: "+f.format(calc.alloy.perShot);
        output += "\nDamage Per Shot to Mechanical :: "+f.format(calc.mechanical.perShot);
        output += "\nDamage Per Shot to Corpus Flesh :: "+f.format(calc.corpusFlesh.perShot);
        output += "\nDamage Per Shot to Shield :: "+f.format(calc.shield.perShot);
        output += "\nDamage Per Shot to Proto Shield :: "+f.format(calc.protoShield.perShot);
        output += "\nDamage Per Shot to Robotic :: "+f.format(calc.robotic.perShot);
        output += "\nDamage Per Shot to Infested Flesh :: "+f.format(calc.infestedFlesh.perShot);
        output += "\nDamage Per Shot to Fossilized :: "+f.format(calc.fossilized.perShot);
        output += "\nDamage Per Shot to Sinew :: "+f.format(calc.sinew.perShot);
        output += "\nDamage Per Shot to Corpus :: "+f.format(calc.corpus.perShot);
        output += "\nDamage Per Shot to Grineer :: "+f.format(calc.grineer.perShot);
        output += "\nDamage Per Shot to Infested :: "+f.format(calc.infested.perShot);
        output += "\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::";
        output += "\nRaw Crit Damage Per Shot :: "+f.format(calc.raw.critPerShot);
        if(calc.impact.critPerShot > 0.0){
            output += "\nImpact Crit Damage Per Shot :: "+f.format(calc.impact.critPerShot);
        }
        if(calc.puncture.critPerShot > 0.0){
            output += "\nPuncture Crit Damage Per Shot :: "+f.format(calc.puncture.critPerShot);
        }
        if(calc.slash.critPerShot > 0.0){
            output += "\nSlash Crit Damage Per Shot :: "+f.format(calc.slash.critPerShot);
        }
        if(calc.fire.critPerShot > 0.0){
            output += "\nFire Crit Damage Per Shot :: "+f.format(calc.fire.critPerShot);
        }
        if(calc.ice.critPerShot > 0.0){
            output += "\nIce Crit Damage Per Shot :: "+f.format(calc.ice.critPerShot);
        }
        if(calc.electric.critPerShot > 0.0){
            output += "\nElectric Crit Damage Per Shot :: "+f.format(calc.electric.critPerShot);
        }
        if(calc.toxin.critPerShot > 0.0){
            output += "\nToxin Crit Damage Per Shot :: "+f.format(calc.toxin.critPerShot);
        }
        if(calc.blast.critPerShot > 0.0){
            output += "\nBlast Crit Damage Per Shot :: "+f.format(calc.blast.critPerShot);
        }
        if(calc.magnetic.critPerShot > 0.0){
            output += "\nMagnetic Crit Damage Per Shot :: "+f.format(calc.magnetic.critPerShot);
        }
        if(calc.gas.critPerShot > 0.0){
            output += "\nGas Crit Damage Per Shot :: "+f.format(calc.gas.critPerShot);
        }
        if(calc.radiation.critPerShot > 0.0){
            output += "\nRadiation Crit Damage Per Shot :: "+f.format(calc.radiation.critPerShot);
        }
        if(calc.corrosive.critPerShot > 0.0){
            output += "\nCorrosive Crit Damage Per Shot :: "+f.format(calc.corrosive.critPerShot);
        }
        if(calc.viral.critPerShot > 0.0){
            output += "\nViral Crit Damage Per Shot :: "+f.format(calc.viral.critPerShot);
        }
        output += "\nCrit Damage Per Shot to Clone Flesh :: "+f.format(calc.cloneFlesh.critPerShot);
        output += "\nCrit Damage Per Shot to Ferrite Armor :: "+f.format(calc.ferrite.critPerShot);
        output += "\nCrit Damage Per Shot to Alloy Armor :: "+f.format(calc.alloy.critPerShot);
        output += "\nCrit Damage Per Shot to Mechanical :: "+f.format(calc.mechanical.critPerShot);
        output += "\nCrit Damage Per Shot to Corpus Flesh :: "+f.format(calc.corpusFlesh.critPerShot);
        output += "\nCrit Damage Per Shot to Shield :: "+f.format(calc.shield.critPerShot);
        output += "\nCrit Damage Per Shot to Proto Shield :: "+f.format(calc.protoShield.critPerShot);
        output += "\nCrit Damage Per Shot to Robotic :: "+f.format(calc.robotic.critPerShot);
        output += "\nCrit Damage Per Shot to Infested Flesh :: "+f.format(calc.infestedFlesh.critPerShot);
        output += "\nCrit Damage Per Shot to Fossilized :: "+f.format(calc.fossilized.critPerShot);
        output += "\nCrit Damage Per Shot to Sinew :: "+f.format(calc.sinew.critPerShot);
        output += "\nCrit Damage Per Shot to Corpus :: "+f.format(calc.corpus.critPerShot);
        output += "\nCrit Damage Per Shot to Grineer :: "+f.format(calc.grineer.critPerShot);
        output += "\nCrit Damage Per Shot to Infested :: "+f.format(calc.infested.critPerShot);
        if(calc.finalFirstShotDamageMult > 1.0){
            output += "\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::";
            output += "\nRaw First Shot Damage :: "+f.format(calc.raw.firstShot);
            if(calc.impact.firstShot > 0.0){
                output += "\nImpact First Shot Damage :: "+f.format(calc.impact.firstShot);
            }
            if(calc.puncture.firstShot > 0.0){
                output += "\nPuncture First Shot Damage :: "+f.format(calc.puncture.firstShot);
            }
            if(calc.slash.firstShot > 0.0){
                output += "\nSlash First Shot Damage :: "+f.format(calc.slash.firstShot);
            }
            if(calc.fire.firstShot > 0.0){
                output += "\nFire First Shot Damage :: "+f.format(calc.fire.firstShot);
            }
            if(calc.ice.firstShot > 0.0){
                output += "\nIce First Shot Damage :: "+f.format(calc.ice.firstShot);
            }
            if(calc.electric.firstShot > 0.0){
                output += "\nElectric First Shot Damage :: "+f.format(calc.electric.firstShot);
            }
            if(calc.toxin.firstShot > 0.0){
                output += "\nToxin First Shot Damage :: "+f.format(calc.toxin.firstShot);
            }
            if(calc.blast.firstShot > 0.0){
                output += "\nBlast First Shot Damage :: "+f.format(calc.blast.firstShot);
            }
            if(calc.magnetic.firstShot > 0.0){
                output += "\nMagnetic First Shot Damage :: "+f.format(calc.magnetic.firstShot);
            }
            if(calc.gas.firstShot > 0.0){
                output += "\nGas First Shot Damage :: "+f.format(calc.gas.firstShot);
            }
            if(calc.radiation.firstShot > 0.0){
                output += "\nRadiation First Shot Damage :: "+f.format(calc.radiation.firstShot);
            }
            if(calc.corrosive.firstShot > 0.0){
                output += "\nCorrosive First Shot Damage :: "+f.format(calc.corrosive.firstShot);
            }
            if(calc.viral.firstShot > 0.0){
                output += "\nViral First Shot Damage :: "+f.format(calc.viral.firstShot);
            }
            output += "\nFirst Shot Damage to Clone Flesh :: "+f.format(calc.cloneFlesh.firstShot);
            output += "\nFirst Shot Damage to Ferrite Armor :: "+f.format(calc.ferrite.firstShot);
            output += "\nFirst Shot Damage to Alloy Armor :: "+f.format(calc.alloy.firstShot);
            output += "\nFirst Shot Damage to Mechanical :: "+f.format(calc.mechanical.firstShot);
            output += "\nFirst Shot Damage to Corpus Flesh :: "+f.format(calc.corpusFlesh.firstShot);
            output += "\nFirst Shot Damage to Shield :: "+f.format(calc.shield.firstShot);
            output += "\nFirst Shot Damage to Proto Shield :: "+f.format(calc.protoShield.firstShot);
            output += "\nFirst Shot Damage to Robotic :: "+f.format(calc.robotic.firstShot);
            output += "\nFirst Shot Damage to Infested Flesh :: "+f.format(calc.infestedFlesh.firstShot);
            output += "\nFirst Shot Damage to Fossilized :: "+f.format(calc.fossilized.firstShot);
            output += "\nFirst Shot Damage to Sinew :: "+f.format(calc.sinew.firstShot);
            output += "\nFirst Shot Damage to Corpus :: "+f.format(calc.corpus.firstShot);
            output += "\nFirst Shot Damage to Grineer :: "+f.format(calc.grineer.firstShot);
            output += "\nFirst Shot Damage to Infested :: "+f.format(calc.infested.firstShot);
        }
        output += "\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::";
        output += "\nRaw Damage Per Second :: "+f.format(calc.raw.perSecond);
        output += "\nDamage Per Second to Clone Flesh :: "+f.format(calc.cloneFlesh.perSecond);
        output += "\nDamage Per Second to Ferrite Armor :: "+f.format(calc.ferrite.perSecond);
        output += "\nDamage Per Second to Alloy Armor :: "+f.format(calc.alloy.perSecond);
        output += "\nDamage Per Second to Mechanical :: "+f.format(calc.mechanical.perSecond);
        output += "\nDamage Per Second to Corpus Flesh :: "+f.format(calc.corpusFlesh.perSecond);
        output += "\nDamage Per Second to Shield :: "+f.format(calc.shield.perSecond);
        output += "\nDamage Per Second to Proto Shield :: "+f.format(calc.protoShield.perSecond);
        output += "\nDamage Per Second to Robotic :: "+f.format(calc.robotic.perSecond);
        output += "\nDamage Per Second to Infested Flesh :: "+f.format(calc.infestedFlesh.perSecond);
        output += "\nDamage Per Second to Fossilized :: "+f.format(calc.fossilized.perSecond);
        output += "\nDamage Per Second to Sinew :: "+f.format(calc.sinew.perSecond);
        output += "\nDamage Per Second to Corpus :: "+f.format(calc.corpus.perSecond);
        output += "\nDamage Per Second to Grineer :: "+f.format(calc.grineer.perSecond);
        output += "\nDamage Per Second to Infested :: "+f.format(calc.infested.perSecond);
        output += "\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::";
        output += "\nRaw Burst Damage Per Second :: "+f.format(calc.raw.rawPerSecond);
        output += "\nBurst Damage Per Second to Clone Flesh :: "+f.format(calc.cloneFlesh.rawPerSecond);
        output += "\nBurst Damage Per Second to Ferrite Armor :: "+f.format(calc.ferrite.rawPerSecond);
        output += "\nBurst Damage Per Second to Alloy Armor :: "+f.format(calc.alloy.rawPerSecond);
        output += "\nBurst Damage Per Second to Mechanical :: "+f.format(calc.mechanical.rawPerSecond);
        output += "\nBurst Damage Per Second to Corpus Flesh :: "+f.format(calc.corpusFlesh.rawPerSecond);
        output += "\nBurst Damage Per Second to Shield :: "+f.format(calc.shield.rawPerSecond);
        output += "\nBurst Damage Per Second to Proto Shield :: "+f.format(calc.protoShield.rawPerSecond);
        output += "\nBurst Damage Per Second to Robotic :: "+f.format(calc.robotic.rawPerSecond);
        output += "\nBurst Damage Per Second to Infested Flesh :: "+f.format(calc.infestedFlesh.rawPerSecond);
        output += "\nBurst Damage Per Second to Fossilized :: "+f.format(calc.fossilized.rawPerSecond);
        output += "\nBurst Damage Per Second to Sinew :: "+f.format(calc.sinew.rawPerSecond);
        output += "\nBurst Damage Per Second to Corpus :: "+f.format(calc.corpus.rawPerSecond);
        output += "\nBurst Damage Per Second to Grineer :: "+f.format(calc.grineer.rawPerSecond);
        output += "\nBurst Damage Per Second to Infested :: "+f.format(calc.infested.rawPerSecond);

        output += "\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::";
        output += submission.getModsOutput();
        
        //Rreturn
        return output;
    }
}
