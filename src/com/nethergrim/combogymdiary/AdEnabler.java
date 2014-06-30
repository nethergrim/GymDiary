package com.nethergrim.combogymdiary;

public  class AdEnabler {
	
	private static boolean isPaid = false;
	
	public static void setPaid (boolean paid){
		isPaid = paid;
	}
	
	public static boolean IsPaid(){
		return isPaid;
	}
	

}
