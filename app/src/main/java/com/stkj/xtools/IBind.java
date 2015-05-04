package com.stkj.xtools;

import android.view.View;

public interface IBind<I extends Object> {
	/**
	 * get the parent for retrieve views
	 * 
	 * @return
	 */
	I getClassOwner();

	/**
	 * the find view method
	 * 
	 * @param id
	 * @return
	 */
	View id(int id);
}
