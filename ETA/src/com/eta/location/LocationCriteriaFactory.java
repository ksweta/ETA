package com.eta.location;

import android.location.Criteria;

/**
 * This class creates Criteria object based on the requirements.
 */
public class LocationCriteriaFactory {
   
   /**
    * This method creates Criteria for coarse locate. 
    * This criteria will settle for less accuracy, medium 
    * power, and low cost.
    * @return returns criteria object
    */
   public static Criteria createCoarseCriteria() {

     Criteria criteria = new Criteria();
     criteria.setAccuracy(Criteria.ACCURACY_COARSE);
     criteria.setAltitudeRequired(false);
     criteria.setBearingRequired(false);
     criteria.setSpeedRequired(false);
     criteria.setCostAllowed(true);
     criteria.setPowerRequirement(Criteria.POWER_LOW);
     return criteria;
   }

   /** 
    * This method creates Criteria for fine locate.
    * This criteria needs high accuracy, high power, and cost
    */
   public static Criteria createFineCriteria() {

     Criteria criteria = new Criteria();
     criteria.setAccuracy(Criteria.ACCURACY_FINE);
     criteria.setAltitudeRequired(false);
     criteria.setBearingRequired(false);
     criteria.setSpeedRequired(false);
     criteria.setCostAllowed(true);
     criteria.setPowerRequirement(Criteria.POWER_HIGH);
     return criteria;
   }
}
