package org.gottfaust.WWDC.model;

import java.util.Vector;

public class Mod implements Comparable {

  /** The mod's name **/
  public String name = "";

  /** The mod's title **/
  public String title = "";

  /** The mod's effects **/
  public ModEffect[] effects;

  /** Private Vectors as a hold-over **/
  private Vector<String> effectsTypes;
  private Vector<Double> effectStrengths;

  
  /**
   * CTOR
   */
  public Mod(){
  }

  /**
   * Gets the effect types
   * @return
   */
  public Vector<String> getEffectTypes(){

    //Initializes the vectors
    initializeEffects();

    //Return the vector
    return effectsTypes;
  }

  /**
   * Gets the effect strengths
   * @return
   */
  public Vector<Double> getEffectStrengths(){

    //Initializes the vectors
    initializeEffects();

    //Return the vector
    return effectStrengths;
  }

  /**
   * Initializes the effect vectors
   */
  private void initializeEffects(){

    //If the vectors are null, initialize them
    if(effectsTypes == null || effectStrengths == null) {

      //Initialize the vectors
      effectsTypes = new Vector<>();
      effectStrengths = new Vector();

      //Populate the values
      for(ModEffect effect : effects){
        effectsTypes.add(effect.type);
        effectStrengths.add(effect.value);
      }
    }
  }

  /**
   * Compares this to other mods
   */
  public int compareTo(Object o) {
    if(o instanceof Mod){
        return this.name.compareTo(((Mod)o).name);
    }else{
      return 0;
    }
  }
}
